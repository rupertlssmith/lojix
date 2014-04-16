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
package com.thesett.text.api.model;

/**
 * TextGrid defines a model describing a buffer of text that uses a monospaced font, and is addressable as a 2d grid.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface TextGrid
{
    int getWidth();

    int getHeight();

    int insert(char character, int x, int y);

    //void caretAt(int row, int column);

    //void insert(char character);

    //void backspace();

    //void newline();

    //Source<Row> updates();

    //Source<Row> full();
}
