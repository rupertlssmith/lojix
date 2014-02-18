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
package com.thesett.aima.attribute.impl;

import java.math.BigDecimal;

import com.thesett.aima.state.Type;

/**
 * BigDecimalType defines the type of big decimals with a precision and a scale.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Define a big decimal type with a precision and a scale.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface BigDecimalType extends Type<BigDecimal>
{
    /**
     * Provices the precision of this big decimal.
     *
     * @return The precision of this big decimal.
     */
    int getPrecision();

    /**
     * Provides the scale of this big decimal.
     *
     * @return The scale of this big decimal.
     */
    int getScale();
}
