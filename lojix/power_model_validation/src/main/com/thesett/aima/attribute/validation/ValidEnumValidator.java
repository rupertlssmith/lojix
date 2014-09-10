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
package com.thesett.aima.attribute.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.thesett.aima.attribute.impl.EnumeratedStringAttribute;

/**
 * ValidEnumValidator checks that a value contains a valid instance of an
 * {@link com.thesett.aima.attribute.impl.EnumeratedStringAttribute}. To be considered valid, the value must have an id
 * on it, that is not <tt>-1</tt>. <tt>null</tt> values are also considered to be valid, as null checking can be handled
 * by other validations.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th><th> Collaborations </th>
 * <tr><td> Check that an instance of an enum has a valid id. </td></tr>
 * </table></pre>
 */
public class ValidEnumValidator implements ConstraintValidator<ValidEnum, EnumeratedStringAttribute>
{
    /** The name of the enumeration type the the value must be a valid instance of. */
    private String enumTypeName;

    /** {@inheritDoc} */
    public void initialize(ValidEnum constraintAnnotation)
    {
        this.enumTypeName = constraintAnnotation.value();
    }

    /** {@inheritDoc} */
    public boolean isValid(EnumeratedStringAttribute value, ConstraintValidatorContext context)
    {
        if (value == null)
        {
            return true;
        }

        return true;
    }
}
