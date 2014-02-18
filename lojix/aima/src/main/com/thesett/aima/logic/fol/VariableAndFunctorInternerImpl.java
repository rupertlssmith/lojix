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
package com.thesett.aima.logic.fol;

import com.thesett.aima.attribute.impl.IdAttribute;

/**
 * VariableAndFunctorInternerImpl provides interners for variable and functor names in specified namespaces.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide symbol table for functors names.
 * <tr><td> Provide symbol table for variable names.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VariableAndFunctorInternerImpl implements VariableAndFunctorInterner
{
    /** Holds the interner that turns variable names into indexed integers. */
    private IdAttribute.IdAttributeFactory<String> variableInterningFactory;

    /** Holds the interner that turns functor names and arities into indexed integers. */
    private IdAttribute.IdAttributeFactory<FunctorName> functorInterningFactory;

    /**
     * Creates an interner for variable and functor names, with the names created under the specified name spaces.
     *
     * @param variableNameSpace The name space for variables.
     * @param functorNameSpace  The name space for functors.
     */
    public VariableAndFunctorInternerImpl(String variableNameSpace, String functorNameSpace)
    {
        variableInterningFactory = IdAttribute.getFactoryForClass(variableNameSpace);
        functorInterningFactory = IdAttribute.getFactoryForClass(functorNameSpace);
    }

    /** {@inheritDoc} */
    public IdAttribute.IdAttributeFactory<FunctorName> getFunctorInterner()
    {
        return functorInterningFactory;
    }

    /** {@inheritDoc} */
    public IdAttribute.IdAttributeFactory<String> getVariableInterner()
    {
        return variableInterningFactory;
    }

    /** {@inheritDoc} */
    public int internFunctorName(String name, int numArgs)
    {
        FunctorName functorName = new FunctorName(name, numArgs);

        return getFunctorInterner().createIdAttribute(functorName).ordinal();
    }

    /** {@inheritDoc} */
    public int internFunctorName(FunctorName name)
    {
        return getFunctorInterner().createIdAttribute(name).ordinal();
    }

    /** {@inheritDoc} */
    public int internVariableName(String name)
    {
        return getVariableInterner().createIdAttribute(name).ordinal();
    }

    /** {@inheritDoc} */
    public String getVariableName(int name)
    {
        return getVariableInterner().getAttributeFromInt(name).getValue();
    }

    /** {@inheritDoc} */
    public String getVariableName(Variable variable)
    {
        return getVariableName(variable.getName());
    }

    /** {@inheritDoc} */
    public FunctorName getFunctorFunctorName(int name)
    {
        return getFunctorInterner().getAttributeFromInt(name).getValue();
    }

    /** {@inheritDoc} */
    public String getFunctorName(int name)
    {
        return getFunctorInterner().getAttributeFromInt(name).getValue().getName();
    }

    /** {@inheritDoc} */
    public int getFunctorArity(int name)
    {
        return getFunctorInterner().getAttributeFromInt(name).getValue().getArity();
    }

    /** {@inheritDoc} */
    public FunctorName getFunctorFunctorName(Functor functor)
    {
        return getFunctorFunctorName(functor.getName());
    }

    /** {@inheritDoc} */
    public String getFunctorName(Functor functor)
    {
        return getFunctorName(functor.getName());
    }

    /** {@inheritDoc} */
    public int getFunctorArity(Functor functor)
    {
        return getFunctorArity(functor.getName());
    }
}
