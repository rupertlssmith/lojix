/*
 * Copyright The Sett Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thesett.mlang.parser;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.DoubleLiteral;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.IntLiteral;
import com.thesett.aima.logic.fol.NumericType;
import com.thesett.aima.logic.fol.OpSymbol;
import static com.thesett.aima.logic.fol.OpSymbol.Associativity.FX;
import static com.thesett.aima.logic.fol.OpSymbol.Associativity.XFX;
import static com.thesett.aima.logic.fol.OpSymbol.Associativity.XFY;
import static com.thesett.aima.logic.fol.OpSymbol.Associativity.YFX;
import com.thesett.aima.logic.fol.Predicate;
import com.thesett.aima.logic.fol.StringLiteral;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.VariableAndFunctorInternerImpl;
import com.thesett.aima.logic.fol.isoprologparser.CandidateOpSymbol;
import com.thesett.aima.logic.fol.isoprologparser.DynamicOperatorParser;
import com.thesett.aima.logic.fol.isoprologparser.OperatorTable;
import com.thesett.common.parsing.SourceCodeException;
import com.thesett.common.parsing.SourceCodePosition;
import com.thesett.common.parsing.SourceCodePositionImpl;
import com.thesett.common.util.Source;
import com.thesett.common.util.TraceIndenter;
import com.thesett.mlang.aterm.Actor;
import com.thesett.mlang.aterm.Function;
import com.thesett.mlang.aterm.Program;
import com.thesett.mlang.aterm.ProgramBody;

/**
 * MlangParser is a recursive descent parser for the language Mlang.
 *
 * <p/>A deferred decision parser, {@link DynamicOperatorParser}, is used to parse sequences of terms possibly involving
 * operators. This can be achieved with a recursive descent parser, but involves hard-coding the precedence rules into
 * the parser. PICAT does not allow user defined operators, or ambiguous operators precedences, but
 * {@link DynamicOperatorParser} is still used to handle its operators, as it keeps the precedence rules out of this
 * parser which makes it simpler. See {@link DynamicOperatorParser} for the details.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Create a parser for Mlang on a token source. <td> {@link Source}
 * <tr><td> Parse a sentence in Mlang with dynamic operators. <td> {@link DynamicOperatorParser}.
 * <tr><td> Parse a sequence of sentences followed by an end of file.
 * <tr><td> Intern all Mlang built in functors and operators. <td> {@link VariableAndFunctorInterner}.
 * <tr><th> Table all Mlang build in operators. <td> {@link OperatorTable}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class MlangParser implements MlangParserConstants
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(MlangParser.class.getName()); */

    /** Used for logging to the console. */
    private static final java.util.logging.Logger console =
        java.util.logging.Logger.getLogger("CONSOLE." + MlangParser.class.getName());

    /** Holds the variable scoping context for the current sentence. */
    protected Map<Integer, Variable> variableContext = new HashMap<Integer, Variable>();

    /** Holds the byte code machine to compile into, if using compiled mode. */
    protected VariableAndFunctorInterner interner =
        new VariableAndFunctorInternerImpl("Mlang_Variable_Namespace", "Mlang_Functor_Namespace");

    /** Holds the dynamic operator parser for parsing terms involving operators. */
    protected DynamicOperatorParser operatorParser = new DynamicOperatorParser();

    /** Holds the table of operators. */
    protected OperatorTable operatorTable = operatorParser;

    /** Holds the tokenizer that supplies the next token on demand. */
    protected Source<Token> tokenSource;

    /** Holds the indenter to provide neatly indent execution traces. */
    protected TraceIndenter indenter = new TraceIndenter(true);

    /**
     * Builds a Mlang parser on a token source to be parsed.
     *
     * @param tokenSource The token source to be parsed.
     * @param interner    The interner for variable and functor names.
     */
    public MlangParser(Source<Token> tokenSource, VariableAndFunctorInterner interner)
    {
        /** Set this parser up to use the supplied interner. */
        this.interner = interner;

        /** Clears the variable scoping context for the first sentence. */
        variableContext.clear();

        /** Intern all of the built in symbols and functors in the language. */
        initializeBuiltIns();

        this.tokenSource = tokenSource;
    }

    /**
     * Parses statements and print the parse tree to the console for quick interactive testing of the parser.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args)
    {
        try
        {
            SimpleCharStream inputStream = new SimpleCharStream(System.in, null, 1, 1);
            MlangParserTokenManager tokenManager = new MlangParserTokenManager(inputStream);
            Source<Token> tokenSource = new TokenSource(tokenManager);

            MlangParser parser =
                new MlangParser(tokenSource,
                    new VariableAndFunctorInternerImpl("Mlang_Variable_Namespace", "Mlang_Functor_Namespace"));

            while (true)
            {
                // Parse the next sentence or directive.
                Object nextParsing = parser.program();

                console.info(nextParsing.toString());
            }
        }
        catch (Exception e)
        {
            console.log(Level.SEVERE, e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Changes the token source the parser is consuming from.
     *
     * @param tokenSource The new token source the parser is to consume from.
     */
    public void setTokenSource(Source<Token> tokenSource)
    {
        this.tokenSource = tokenSource;
    }

    public Program program() throws SourceCodeException
    {
        Program program = new Program();
        Token nextToken = tokenSource.peek();

        // Parse the optional module declaration.
        if (nextToken.kind == MODULE)
        {
            moduleDecl(program);
            nextToken = tokenSource.peek();
        }

        while (nextToken.kind == IMPORT)
        {
            importDecl(program);
            nextToken = tokenSource.peek();
        }

        List<ProgramBody> results = program.getProgramBody();

        // Loop consuming program body definitions until EOF is encountered.
        while (true)
        {
            if (peekAndConsumeEof())
            {
                break;
            }
            else
            {
                results.add(programBody());
            }
        }

        return program;
    }

    public ProgramBody programBody() throws SourceCodeException
    {
        return null;
    }

    public Predicate<Clause> predicate() throws SourceCodeException
    {
        return null;
    }

    public Function function() throws SourceCodeException
    {
        return null;
    }

    public Actor actor() throws SourceCodeException
    {
        return null;
    }

    /**
     * Parses a single atom in first order logic. If the operator has been set up which has the same name as the atom
     * then the atom may actually be a functor expressed as a prefix, postfix or infix operator. If this is the case the
     * value returned by this method will be a {@link CandidateOpSymbol}. Otherwise it will be a {@link Functor} of
     * arity zero.
     *
     * @return A {@link CandidateOpSymbol} or a {@link Functor} or arity zero.
     *
     * @throws SourceCodeException If the token sequence does not parse as a valid atom.
     */
    public Term atom() throws SourceCodeException
    {
        Token name = consumeToken(ATOM);

        Term result;

        // Used to build the possible set of operators that this symbol could be parsed as.
        EnumMap<OpSymbol.Fixity, OpSymbol> possibleOperators =
            operatorTable.getOperatorsMatchingNameByFixity(name.image);

        // Check if the symbol mapped onto any candidate operators and if not create a constant for it.
        if ((possibleOperators == null) || possibleOperators.isEmpty())
        {
            int nameId = interner.internFunctorName(name.image, 0);
            result = new Functor(nameId, null);
        }
        else
        {
            // Set the possible associativities of the operator on the candidate.
            result = new CandidateOpSymbol(name.image, possibleOperators);
        }

        // Set the position that the name was parsed from.
        SourceCodePosition position =
            new SourceCodePositionImpl(name.beginLine, name.beginColumn, name.endLine, name.endColumn);
        result.setSourceCodePosition(position);

        return result;
    }

    /**
     * Parses a variable in first order logic. Variables are scoped within the current sentence being parsed, so if the
     * variable has been seen previously in the sentence it is returned rather than a new one being created.
     *
     * @return A variable.
     *
     * @throws SourceCodeException If the next token in the sequence is not a variable.
     */
    public Variable variable() throws SourceCodeException
    {
        Token name = consumeToken(VAR);

        // Intern the variables name.
        int nameId = interner.internVariableName(name.image);

        // Check if the variable already exists in this scope, or create a new one if it does not.
        // If the variable is the unidentified anonymous variable '_', a fresh one will always be created.
        Variable var = null;

        if (!"_".equals(name.image))
        {
            var = variableContext.get(nameId);
        }

        if (var != null)
        {
            return var;
        }
        else
        {
            var = new Variable(nameId, null, name.image.startsWith("_"));
            variableContext.put(nameId, var);

            return var;
        }
    }

    /**
     * Parses an integer literal.
     *
     * @return An integer literal.
     *
     * @throws SourceCodeException If the next token in the sequence is not an integer literal.
     */
    public NumericType intLiteral() throws SourceCodeException
    {
        Token valToken = consumeToken(INTEGER_LITERAL);

        NumericType result = new IntLiteral(Integer.parseInt(valToken.image));

        // Set the position that the literal was parsed from.
        SourceCodePosition position =
            new SourceCodePositionImpl(valToken.beginLine, valToken.beginColumn, valToken.endLine, valToken.endColumn);
        result.setSourceCodePosition(position);

        return result;
    }

    /**
     * Parses a real number literal.
     *
     * @return A real number literal.
     *
     * @throws SourceCodeException If the next token in the sequence is not a real number literal.
     */
    public NumericType doubleLiteral() throws SourceCodeException
    {
        Token valToken = consumeToken(REAL_LITERAL);

        NumericType result = new DoubleLiteral(Double.parseDouble(valToken.image));

        // Set the position that the literal was parsed from.
        SourceCodePosition position =
            new SourceCodePositionImpl(valToken.beginLine, valToken.beginColumn, valToken.endLine, valToken.endColumn);
        result.setSourceCodePosition(position);

        return result;
    }

    /**
     * Parses a string literal.
     *
     * @return A string literal.
     *
     * @throws SourceCodeException If the next token in the sequence is not a string literal.
     */
    public StringLiteral stringLiteral() throws SourceCodeException
    {
        Token valToken = consumeToken(STRING_LITERAL);

        String valWithQuotes = valToken.image;

        StringLiteral result = new StringLiteral(valWithQuotes.substring(1, valWithQuotes.length() - 1));

        // Set the position that the literal was parsed from.
        SourceCodePosition position =
            new SourceCodePositionImpl(valToken.beginLine, valToken.beginColumn, valToken.endLine, valToken.endColumn);
        result.setSourceCodePosition(position);

        return result;
    }

    /**
     * Peeks at the next token to see if it is {@link #EOF} and if it is conumes it. If the symbol is consumed then the
     * return value indicates that this has happened. This method is intended to be usefull for interactive interpreters
     * to detect when to exit at the end of input.
     *
     * @return <tt>true</tt> if the next token is EOF and it is consumed.
     */
    public boolean peekAndConsumeEof()
    {
        Token nextToken = tokenSource.peek();

        if (nextToken.kind == EOF)
        {
            try
            {
                consumeToken(EOF);
            }
            catch (SourceCodeException e)
            {
                // If the peek ahead kind can not be consumed then something strange has gone wrong so report this
                // as a bug rather than try to recover from it.
                throw new RuntimeException(e);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Peeks at the next token to see if it is an {@link #ATOM} which is equal to ";" and if it is consumes it. If the
     * symbol is consumed then the return value indicates that this has happened. This is intended to be usefull for
     * interactive interpreters when querying the user to see if they want more solutions to be found.
     *
     * @return <tt>true</tt> if the next token is ";" and it is consumed.
     */
    public boolean peekAndConsumeMore()
    {
        Token nextToken = tokenSource.peek();

        if ((nextToken.kind == ATOM) && ";".equals(nextToken.image))
        {
            try
            {
                consumeToken(ATOM);
            }
            catch (SourceCodeException e)
            {
                // If the peek ahead kind can not be consumed then something strange has gone wrong so report this
                // as a bug rather than try to recover from it.
                throw new RuntimeException(e);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Interns an operators name as a functor of appropriate arity for the operators fixity, and sets the operator in
     * the operator table.
     *
     * @param operatorName  The name of the operator to create.
     * @param priority      The priority of the operator, zero unsets it.
     * @param associativity The operators associativity.
     */
    public void internOperator(String operatorName, int priority, OpSymbol.Associativity associativity)
    {
        int arity;

        if ((associativity == XFY) | (associativity == YFX) | (associativity == XFX))
        {
            arity = 2;
        }
        else
        {
            arity = 1;
        }

        int name = interner.internFunctorName(operatorName, arity);
        operatorTable.setOperator(name, operatorName, priority, associativity);
    }

    /**
     * Interns a list of operator names as functors of appropriate arity for the operators fixity, and sets the operator
     * in the operator table.
     *
     * @param operatorNames The names of the operator to create.
     * @param priority      The priority of the operator, zero unsets it.
     * @param associativity The operators associativity.
     */
    public void internOperator(String[] operatorNames, int priority, OpSymbol.Associativity associativity)
    {
        for (String operatorName : operatorNames)
        {
            internOperator(operatorName, priority, associativity);
        }
    }

    /** Interns and inserts into the operator table all of the built in operators and functors in Mlang. */
    protected void initializeBuiltIns()
    {
        // Initializes the operator table with the standard Mlang built-in operators.
        internOperator(new String[] { ".", "@" }, 2100, XFX);
        internOperator("**", 2000, XFX);
        internOperator(new String[] { "+", "-" }, 1900, FX);
        internOperator(new String[] { "*", "/", "//", "/>", "/<", "div", "mod", "rem" }, 1800, XFX);
        internOperator(new String[] { "+", "-" }, 1700, XFX);
        internOperator(new String[] { ">>", "<<" }, 1600, XFX);
        internOperator("/\\", 1500, XFX);
        internOperator("^", 1400, XFX);
        internOperator("\\/", 1300, XFX);
        internOperator("..", 1200, XFX);
        internOperator("++", 1100, XFX);
        internOperator(
            new String[]
            {
                "=", "!=", ":=", "==", ">", ">=", "<", "=<", "<=", "::", "in", "notin", "#=", "#!=", "#>", "#>=", "#<",
                "#=<", "#<=", "@<", "@=<", "@<=", "@>", "@>="
            }, 1000, XFX);
        internOperator("#~", 900, XFX);
        internOperator("#/\\", 800, XFX);
        internOperator("#^", 700, XFX);
        internOperator("#\\/", 600, XFX);
        internOperator("#=>", 500, XFX);
        internOperator("#<=>", 400, XFX);
        internOperator(new String[] { "not", "once" }, 300, XFX);
        internOperator(new String[] { ",", "&&" }, 200, XFX);
        internOperator(new String[] { ";", "||" }, 100, XFX);

        // Intern all built in functors.
        // For example: interner.internFunctorName("nil", 0);
    }

    /**
     * Consumes a token of the expected kind from the token sequence. If the next token in the sequence is not of the
     * expected kind an error will be raised.
     *
     * @param  kind The kind of token to consume.
     *
     * @return The consumed token of the expected kind.
     *
     * @throws SourceCodeException If the next token in the sequence is not of the expected kind.
     */
    protected Token consumeToken(int kind) throws SourceCodeException
    {
        Token nextToken = tokenSource.peek();

        if (nextToken.kind != kind)
        {
            throw new SourceCodeException("Was expecting " + tokenImage[kind] + " but got " +
                tokenImage[nextToken.kind] + ".", null, null, null,
                new SourceCodePositionImpl(nextToken.beginLine, nextToken.beginColumn, nextToken.endLine,
                    nextToken.endColumn));
        }
        else
        {
            nextToken = tokenSource.poll();

            return nextToken;
        }
    }

    protected void moduleDecl(Program program)
    {
    }

    protected void importDecl(Program program)
    {
    }
}
