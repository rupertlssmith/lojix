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
package com.thesett.aima.search;

/**
 * Bundles together a new traversable state, the operator used to get there, and the cost of the operation. This is
 * really a convenience class to encapsulate multiple return parameters from the {@link Traversable#successors} method
 * that reports all the states that are reachable successors of a traversable state.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Bundle the reachable successor state of a traversable state with the operator and cost of traversing to
 *          that state.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public final class Successor<O>
{
    /** Successor State. */
    protected Traversable<O> state;

    /** Operation to reach successor. */
    protected Operator<O> operator;

    /** Cost of operation. */
    protected float cost;

    /**
     * Constructor sets all values of successor object.
     *
     * @param state    The successor state.
     * @param operator The operator used to get to this state.
     * @param cost     The cost of the operation to get to this state.
     */
    public Successor(Traversable<O> state, Operator operator, float cost)
    {
        this.state = state;
        this.operator = operator;
        this.cost = cost;
    }

    /**
     * Returns string describing operation.
     *
     * @return The operator used to get to this successor state.
     */
    public Operator<O> getOperator()
    {
        return operator;
    }

    /**
     * Returns cost of performing operation.
     *
     * @return The cost of getting to this successor state.
     */
    public float getCost()
    {
        return cost;
    }

    /**
     * Returns the successor state.
     *
     * @return The successor state.
     */
    public Traversable<O> getState()
    {
        return state;
    }
}
