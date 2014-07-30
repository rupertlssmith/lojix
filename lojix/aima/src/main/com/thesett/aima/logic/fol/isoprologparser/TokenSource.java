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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import com.thesett.common.util.Source;

/**
 * TokenSource wraps a token manager generated by JavaCC and presents it as a {@link Source} of tokens. This provides a
 * clean interface onto the generated code to write a parser around.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Take tokens from a queue. <td> {@link PrologParserTokenManager}.
 * <tr><td> Allow the next token in the queue to be peeked at. <td> {@link PrologParserTokenManager}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TokenSource implements Source<Token>
{
    /** Holds the current token. */
    public Token token;

    /** Holds the tokenizer that supplies the next token on demand. */
    public PrologParserTokenManager tokenManager;

    /**
     * Builds a token source around the specified token manager.
     *
     * @param tokenManager The token manager to use to feed this source.
     */
    protected TokenSource(PrologParserTokenManager tokenManager)
    {
        this.tokenManager = tokenManager;

        // The first token is initialized to be empty, so that the first call to poll returns the first token.
        token = new Token();
    }

    /**
     * Creates a token source on a string.
     *
     * @param  stringToTokenize The string to tokenize.
     *
     * @return A token source.
     */
    public static TokenSource getTokenSourceForString(String stringToTokenize)
    {
        SimpleCharStream inputStream = new SimpleCharStream(new StringReader(stringToTokenize), 1, 1);
        PrologParserTokenManager tokenManager = new PrologParserTokenManager(inputStream);

        return new TokenSource(tokenManager);
    }

    /**
     * Creates a token source on a file.
     *
     * @param  file The file to tokenize.
     *
     * @return A token source.
     *
     * @throws FileNotFoundException If the file cannot be found.
     */
    public static TokenSource getTokenSourceForFile(File file) throws FileNotFoundException
    {
        // Create a token source to load the model rules from.
        Reader ins = new FileReader(file);
        SimpleCharStream inputStream = new SimpleCharStream(ins, 1, 1);
        PrologParserTokenManager tokenManager = new PrologParserTokenManager(inputStream);

        return new TokenSource(tokenManager);
    }

    /**
     * Creates a token source on an input stream.
     *
     * @param  in The input stream to tokenize.
     *
     * @return A token source.
     */
    public static TokenSource getTokenSourceForInputStream(InputStream in)
    {
        SimpleCharStream inputStream = new SimpleCharStream(in, 1, 1);
        PrologParserTokenManager tokenManager = new PrologParserTokenManager(inputStream);

        return new TokenSource(tokenManager);
    }

    /**
     * Retrieves and removes the head token, or <tt>null</tt> if there are no more tokens.
     *
     * @return The head token, or <tt>null</tt> if there are no more tokens.
     */
    public Token poll()
    {
        if (token.next == null)
        {
            token.next = tokenManager.getNextToken();
        }

        token = token.next;

        return token;
    }

    /**
     * Retrieves, but does not remove, the head token, returning <tt>null</tt> if there are no more tokens.
     *
     * @return The head token, returning <tt>null</tt> if there are no more tokens.
     */
    public Token peek()
    {
        if (token.next == null)
        {
            token.next = tokenManager.getNextToken();
        }

        return token.next;
    }
}
