/*
 * Copyright The Sett Ltd, 2005 to 2014.
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
package com.thesett.aima.logic.fol.isoprologparser;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.Cons;
import com.thesett.aima.logic.fol.DoubleLiteral;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.IntLiteral;
import com.thesett.aima.logic.fol.Nil;
import com.thesett.aima.logic.fol.NumericType;
import com.thesett.aima.logic.fol.OpSymbol;
import static com.thesett.aima.logic.fol.OpSymbol.Associativity.FX;
import static com.thesett.aima.logic.fol.OpSymbol.Associativity.FY;
import static com.thesett.aima.logic.fol.OpSymbol.Associativity.XFX;
import static com.thesett.aima.logic.fol.OpSymbol.Associativity.XFY;
import static com.thesett.aima.logic.fol.OpSymbol.Associativity.YFX;
import com.thesett.aima.logic.fol.StringLiteral;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.TermUtils;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.VariableAndFunctorInternerImpl;
import com.thesett.common.parsing.SourceCodeException;
import com.thesett.common.parsing.SourceCodePosition;
import com.thesett.common.parsing.SourceCodePositionImpl;
import com.thesett.common.util.Source;
import com.thesett.common.util.TraceIndenter;

/**
 * PrologParser is a recursive descent parser for the language Prolog, that parses its input into first order logic
 * {@link Term}s or {@link Clause}s. The {@link #sentence()}s generated by this parser are Terms in first order logic,
 * but not necessarily sentences in Prolog. A sentence in Prolog consists of either a Horn clause, to be added to the
 * current knowledge base, or a query, which is a Horn clause with no head, for immediate resolution against the current
 * knowledge base. Sentences in Prolog are Horn clauses and may be parsed through the {@link #clause()} method.
 *
 * <p/>A deffered decision parser, {@link DynamicOperatorParser}, is used to parse sequences of terms possibly involving
 * operators. This cannot easily be achieved with a recursive descent parser. See {@link DynamicOperatorParser} for the
 * details.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Create a parser for Prolog on a token source. <td> {@link Source}
 * <tr><td> Parse a sentence in first order logic with dynamic operators. <td> {@link DynamicOperatorParser}.
 * <tr><td> Parse a sequence of sentences followed by an end of file.
 * <tr><td> Intern all Prolog built in functors and operators. <td> {@link VariableAndFunctorInterner}.
 * <tr><th> Table all Prolog build in operators. <td> {@link OperatorTable}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PrologParser implements PrologParserConstants
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(PrologParser.class.getName()); */

    /** Used for logging to the console. */
    private static final java.util.logging.Logger console =
        java.util.logging.Logger.getLogger("CONSOLE." + PrologParser.class.getName());

    /** Lists the tokens expected to begin a term expression as a string. */
    private static final String BEGIN_TERM_TOKENS =
        Arrays.toString(
            new String[]
            {
                tokenImage[FUNCTOR], tokenImage[LSQPAREN], tokenImage[VAR], tokenImage[INTEGER_LITERAL],
                tokenImage[FLOATING_POINT_LITERAL], tokenImage[STRING_LITERAL], tokenImage[ATOM], tokenImage[LPAREN]
            });

    /** Describes the possible system directives in interactive mode. */
    public enum Directive
    {
        Trace, Info, User, File
    }

    /** Holds the variable scoping context for the current sentence. */
    protected Map<Integer, Variable> variableContext = new HashMap<Integer, Variable>();

    /** Holds the byte code machine to compile into, if using compiled mode. */
    protected VariableAndFunctorInterner interner =
        new VariableAndFunctorInternerImpl("Prolog_Variable_Namespace", "Prolog_Functor_Namespace");

    /** Holds the dynamic operator parser for parsing terms involving operators. */
    protected DynamicOperatorParser operatorParser = new DynamicOperatorParser();

    /** Holds the table of operators. */
    protected OperatorTable operatorTable = operatorParser;

    /** Holds the tokenizer that supplies the next token on demand. */
    protected Source<Token> tokenSource;

    /** Holds the indenter to provide neatly indendet execution traces. */
    protected TraceIndenter indenter = new TraceIndenter(true);

    /**
     * Builds a prolog parser on a token source to be parsed.
     *
     * @param tokenSource The token source to be parsed.
     * @param interner    The interner for variable and functor names.
     */
    public PrologParser(Source<Token> tokenSource, VariableAndFunctorInterner interner)
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
            PrologParserTokenManager tokenManager = new PrologParserTokenManager(inputStream);
            Source<Token> tokenSource = new TokenSource(tokenManager);

            PrologParser parser =
                new PrologParser(tokenSource,
                    new VariableAndFunctorInternerImpl("Prolog_Variable_Namespace", "Prolog_Functor_Namespace"));

            while (true)
            {
                // Parse the next sentence or directive.
                Object nextParsing = parser.clause();

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

    /**
     * Parses a single terms, or atom (a functor with arity zero), as a sentence in first order logic. The sentence will
     * be parsed in a fresh variable context, to ensure its variables are scoped to within the term only. The sentence
     * does not have to be terminated by a full stop. This method is not generally used by Prolog, but is provided as a
     * convenience to languages over terms, rather than clauses.
     *
     * @return A term parsed in a fresh variable context.
     *
     * @throws SourceCodeException If the token sequence does not parse into a valid term sentence.
     */
    public Term termSentence() throws SourceCodeException
    {
        // Each new sentence provides a new scope in which to make variables unique.
        variableContext.clear();

        Term sentence = term();

        return sentence;

    }

    /**
     * Parses a single horn clause as a sentence in first order logic. A sentence consists of a clause followed by a
     * full stop.
     *
     * @return A horn clause sentence in first order logic.
     *
     * @throws SourceCodeException If the token sequence does not parse into a valid sentence.
     */
    public Clause sentence() throws SourceCodeException
    {
        Clause sentence = clause();

        consumeToken(PERIOD);

        return sentence;
    }

    /**
     * Parses many consecutive sentences, until an <EOF> is reached. This method is intended to aid with consulting
     * files.
     *
     * @return A list of parsed clauses.
     *
     * @throws SourceCodeException If the token sequence does not parse into a sequence of clauses terminted by <EOF>.
     */
    public List<Clause> sentences() throws SourceCodeException
    {
        List<Clause> results = new LinkedList<Clause>();

        // Loop consuming clauses until end of file is encounterd.
        while (true)
        {
            if (peekAndConsumeEof())
            {
                break;
            }
            else
            {
                results.add(sentence());
            }
        }

        return results;
    }

    /**
     * Parses a single sentence in first order logic. A sentence consists of a term followed by a full stop.
     *
     * @return A sentence in first order logic.
     *
     * @throws SourceCodeException If the token sequence does not parse into a valid sentence.
     */
    public Clause clause() throws SourceCodeException
    {
        // Each new sentence provides a new scope in which to make variables unique.
        variableContext.clear();

        Term term = term();

        Clause clause = TermUtils.convertToClause(term, interner);

        if (clause == null)
        {
            throw new SourceCodeException("Only queries and clauses are valid sentences in Prolog, not " + term + ".",
                null, null, null, term.getSourceCodePosition());
        }

        return clause;
    }

    /**
     * Parses multiple sequential terms, and if more than one is encountered then the flat list of terms encountered
     * must contain operators in order to be valid Prolog syntax. In that case the flat list of terms is passed to the
     * {@link DynamicOperatorParser#parseOperators(Term[])} method for 'deferred decision parsing' of dynamic operators.
     *
     * @return A single first order logic term.
     *
     * @throws SourceCodeException If the sequence of tokens does not form a valid syntactical construction as a first
     *                             order logic term.
     */
    public Term term() throws SourceCodeException
    {
        List<Term> terms;
        terms = terms(new LinkedList<Term>());

        Term[] flatTerms = terms.toArray(new Term[terms.size()]);

        if (flatTerms.length > 1)
        {
            return operatorParser.parseOperators(flatTerms);
        }
        else
        {
            Term result = flatTerms[0];

            // If a single candidate op symbol has been parsed, promote it to a constant.
            if (result instanceof CandidateOpSymbol)
            {
                CandidateOpSymbol candidate = (CandidateOpSymbol) result;

                int nameId = interner.internFunctorName(candidate.getTextName(), 0);
                result = new Functor(nameId, null);
            }

            return result;
        }
    }

    /**
     * Recursively parses terms, which may be functors, atoms, variables, literals or operators, into a flat list in the
     * order in which they are encountered.
     *
     * @param  terms A list of terms to accumulate in.
     *
     * @return The list of terms encountered in order.
     *
     * @throws SourceCodeException If the sequence of tokens does not form a valid syntactical construction as a list of
     *                             first order logic terms.
     */
    public List<Term> terms(List<Term> terms) throws SourceCodeException
    {
        Term term;

        Token nextToken = tokenSource.peek();

        switch (nextToken.kind)
        {
        case FUNCTOR:
            term = functor();
            break;

        case LSQPAREN:
            term = listFunctor();
            break;

        case VAR:
            term = variable();
            break;

        case INTEGER_LITERAL:
            term = intLiteral();
            break;

        case FLOATING_POINT_LITERAL:
            term = doubleLiteral();
            break;

        case STRING_LITERAL:
            term = stringLiteral();
            break;

        case ATOM:
            term = atom();
            break;

        case LPAREN:
            consumeToken(LPAREN);

            term = term();

            // Mark the term as bracketed to ensure that this is its final parsed form. In particular the
            // #arglist method will not break it up if it contains commas.
            term.setBracketed(true);

            consumeToken(RPAREN);
            break;

        default:
            throw new SourceCodeException("Was expecting one of " + BEGIN_TERM_TOKENS + " but got " +
                tokenImage[nextToken.kind] + ".", null, null, null,
                new SourceCodePositionImpl(nextToken.beginLine, nextToken.beginColumn, nextToken.endLine,
                    nextToken.endColumn));
        }

        terms.add(term);

        switch (tokenSource.peek().kind)
        {
        case LPAREN:
        case LSQPAREN:
        case INTEGER_LITERAL:
        case FLOATING_POINT_LITERAL:
        case STRING_LITERAL:
        case VAR:
        case FUNCTOR:
        case ATOM:
            terms(terms);
            break;

        default:
        }

        return terms;
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
     * Parses a single functor in first order logic with its arguments.
     *
     * @return A single functor in first order logic with its arguments.
     *
     * @throws SourceCodeException If the token sequence does not parse as a valid functor.
     */
    public Term functor() throws SourceCodeException
    {
        Token name = consumeToken(FUNCTOR);
        Term[] args = arglist();
        consumeToken(RPAREN);

        int nameId =
            interner.internFunctorName((args == null) ? name.image : name.image.substring(0, name.image.length() - 1),
                (args == null) ? 0 : args.length);

        Functor result = new Functor(nameId, args);

        SourceCodePosition position =
            new SourceCodePositionImpl(name.beginLine, name.beginColumn, name.endLine, name.endColumn);
        result.setSourceCodePosition(position);

        return result;
    }

    /**
     * Parses a list expressed as a sequence of functors in first order logic. The empty list consists of the atom 'nil'
     * and a non-empty list consists of the functor 'cons' with arguments the head of the list and the remainder of the
     * list.
     *
     * <p/>A list can be empty '[]', contains a sequence of terms seperated by commas '[a,...]', contain a sequence of
     * terms seperated by commas consed onto another term '[a,...|T]'. In the case where a term is consed onto the end,
     * if the term is itself a list the whole will form a list with a cons nil terminator, otherwise the term will be
     * consed onto the end as the list terminal.
     *
     * @return A list expressed as a sequence of functors in first order.
     *
     * @throws SourceCodeException If the token sequence does not parse as a valid list.
     */
    public Term listFunctor() throws SourceCodeException
    {
        // Get the interned names of the nil and cons functors.
        int nilId = interner.internFunctorName("nil", 0);
        int consId = interner.internFunctorName("cons", 2);

        // A list always starts with a '['.
        Token leftDelim = consumeToken(LSQPAREN);

        // Check if the list contains any arguments and parse them if so.
        Term[] args = null;

        Token nextToken = tokenSource.peek();

        switch (nextToken.kind)
        {
        case LPAREN:
        case LSQPAREN:
        case INTEGER_LITERAL:
        case FLOATING_POINT_LITERAL:
        case STRING_LITERAL:
        case VAR:
        case FUNCTOR:
        case ATOM:
            args = arglist();
            break;

        default:
        }

        // Work out what the terminal element in the list is. It will be 'nil' unless an explicit cons '|' has
        // been used to specify a different terminal element. In the case where cons is used explciitly, the
        // list prior to the cons must not be empty.
        Term accumulator;

        if (tokenSource.peek().kind == CONS)
        {
            if (args == null)
            {
                throw new SourceCodeException("Was expecting one of " + BEGIN_TERM_TOKENS + " but got " +
                    tokenImage[nextToken.kind] + ".", null, null, null,
                    new SourceCodePositionImpl(nextToken.beginLine, nextToken.beginColumn, nextToken.endLine,
                        nextToken.endColumn));
            }

            consumeToken(CONS);

            accumulator = term();
        }
        else
        {
            accumulator = new Nil(nilId, null);
        }

        // A list is always terminated with a ']'.
        Token rightDelim = consumeToken(RSQPAREN);

        // Walk down all of the lists arguments joining them together with cons/2 functors.
        if (args != null) // 'args' will contain one or more elements if not null.
        {
            for (int i = args.length - 1; i >= 0; i--)
            {
                Term previousAccumulator = accumulator;

                //accumulator = new Functor(consId.ordinal(), new Term[] { args[i], previousAccumulator });
                accumulator = new Cons(consId, new Term[] { args[i], previousAccumulator });
            }
        }

        // Set the position that the list was parsed from, as being the region between the '[' and ']' brackets.
        SourceCodePosition position =
            new SourceCodePositionImpl(leftDelim.beginLine, leftDelim.beginColumn, rightDelim.endLine,
                rightDelim.endColumn);
        accumulator.setSourceCodePosition(position);

        // The cast must succeed because arglist must return at least one argument, therefore the cons generating
        // loop must have been run at least once. If arglist is not called at all because an empty list was
        // encountered, then the accumulator will contain the 'nil' constant which is a functor of arity zero.
        return (Functor) accumulator;
    }

    /**
     * Parses a sequence of terms as a comma seperated argument list. The ',' operator in prolog can be used as an
     * operator, when it behaves as a functor of arity 2, or it can be used to separate a sequence of terms that are
     * arguments to a functor or list. The sequence of functors must first be parsed as a term, using the operator
     * precedence of "," to form the term. This method takes such a term and flattens it back into a list of terms,
     * breaking it only on a sequence of commas. Terms that have been parsed as a bracketed expression will not be
     * broken up.
     *
     * <p/>For example, 'a, b, c' is broken into the list { a, b, c}. The example, 'a, (b, c), d' is broken into the
     * list { a, (b, c), d} and so on.
     *
     * @return A sequence of terms parsed as a term, then flattened back into a list seperated on commas.
     *
     * @throws SourceCodeException If the token sequence is not a valid term.
     */
    public Term[] arglist() throws SourceCodeException
    {
        Term term = term();
        List<Term> result = TermUtils.flattenTerm(term, Term.class, ",", interner);

        return result.toArray(new Term[result.size()]);
    }

    /**
     * Parses a variable in first order logic. Variables are scoped within the current sentence being parsed, so if the
     * variable has been seen previously in the sentence it is returned rather than a new one being created.
     *
     * @return A variable.
     *
     * @throws SourceCodeException If the next token in the sequence is not a variable.
     */
    public Term variable() throws SourceCodeException
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
            var = new Variable(nameId, null, name.image.equals("_"));
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
    public Term intLiteral() throws SourceCodeException
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
    public Term doubleLiteral() throws SourceCodeException
    {
        Token valToken = consumeToken(FLOATING_POINT_LITERAL);

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
    public Term stringLiteral() throws SourceCodeException
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
     * Peeks at the next token to see if it is {@link #EOF} and if it is consumes it. If the symbol is consumed then the
     * return value indicates that this has happened. This method is intended to be useful for interactive interpreters
     * to detect when to exit at the end of input.
     *
     * @return <tt>true</tt> if the next token is EOF and it is consumed.
     */
    public boolean peekAndConsumeEof()
    {
        return peekAndConsume(EOF);
    }

    /**
     * Peeks at the next token to see if it is the system trace directive, and consumes it if it is.
     *
     * @return <tt>true</tt> iff the next token is the trace directive.
     */
    public boolean peekAndConsumeTrace()
    {
        return peekAndConsume(TRACE) && peekAndConsume(PERIOD);
    }

    /**
     * Peeks at the next token to see if it is the system info directive, and consumes it if it is.
     *
     * @return <tt>true</tt> iff the next token is the info directive.
     */
    public boolean peekAndConsumeInfo()
    {
        return peekAndConsume(INFO) && peekAndConsume(PERIOD);
    }

    /**
     * Peeks at the next token to see if it is the system user directive, and consumes it if it is.
     *
     * @return <tt>true</tt> iff the next token is the user directive.
     */
    public boolean peekAndConsumeUser()
    {
        return peekAndConsume(USER) && peekAndConsume(PERIOD);
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
                throw new IllegalStateException(e);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Peeks and consumes the next interactive system directive.
     *
     * @return The directive, or <tt>null</tt> if none is found.
     *
     * @throws SourceCodeException If the source being parsed does not match the grammar.
     */
    public Directive peekAndConsumeDirective() throws SourceCodeException
    {
        if (peekAndConsumeTrace())
        {
            return Directive.Trace;
        }

        if (peekAndConsumeInfo())
        {
            return Directive.Info;
        }

        if (peekAndConsumeUser())
        {
            return Directive.User;
        }

        return null;
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

    /** Interns and inserts into the operator table all of the built in operators and functors in Prolog. */
    protected void initializeBuiltIns()
    {
        // Initializes the operator table with the standard ISO prolog built-in operators.
        internOperator(":-", 1200, XFX);
        internOperator(":-", 1200, FX);
        internOperator("-->", 1200, XFX);
        internOperator("?-", 1200, FX);

        internOperator(";", 1100, XFY);
        internOperator("->", 1050, XFY);
        internOperator(",", 1000, XFY);
        internOperator("\\+", 900, FY);

        internOperator("=", 700, XFX);
        internOperator("\\=", 700, XFX);
        internOperator("==", 700, XFX);
        internOperator("\\==", 700, XFX);
        internOperator("@<", 700, XFX);
        internOperator("@=<", 700, XFX);
        internOperator("@>", 700, XFX);
        internOperator("@>=", 700, XFX);
        internOperator("=..", 700, XFX);
        internOperator("is", 700, XFX);
        internOperator("=:=", 700, XFX);
        internOperator("=\\=", 700, XFX);
        internOperator("<", 700, XFX);
        internOperator("=<", 700, XFX);
        internOperator(">", 700, XFX);
        internOperator(">=", 700, XFX);

        internOperator("+", 500, YFX);
        internOperator("-", 500, YFX);

        internOperator("\\/", 500, YFX);
        internOperator("/\\", 500, YFX);

        internOperator("/", 400, YFX);
        internOperator("//", 400, YFX);
        internOperator("*", 400, YFX);
        internOperator(">>", 400, YFX);
        internOperator("<<", 400, YFX);
        internOperator("rem", 400, YFX);
        internOperator("mod", 400, YFX);

        internOperator("-", 200, FY);
        internOperator("^", 200, YFX);
        internOperator("**", 200, YFX);
        internOperator("\\", 200, FY);

        // Intern all built in functors.
        interner.internFunctorName("nil", 0);
        interner.internFunctorName("cons", 2);
        interner.internFunctorName("true", 0);
        interner.internFunctorName("fail", 0);
        interner.internFunctorName("!", 0);
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

    /**
     * Peeks ahead for the given token type, and if one is foudn with that type, it is consumed.
     *
     * @param  kind The token kind to look for.
     *
     * @return <tt>true</tt> iff the token was found and consumed.
     */
    private boolean peekAndConsume(int kind)
    {
        Token nextToken = tokenSource.peek();

        if (nextToken.kind == kind)
        {
            try
            {
                consumeToken(kind);
            }
            catch (SourceCodeException e)
            {
                // If the peek ahead kind can not be consumed then something strange has gone wrong so report this
                // as a bug rather than try to recover from it.
                throw new IllegalStateException(e);
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
