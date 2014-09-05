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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.NDC;

/**
 * TraversibleStateTest is a pure unit test class for the abstract class {@link TraversableState}. It provides a
 * concrete implementation of this abstract class and fully tests all its non-abstract methods. It also provides a
 * constructor that can be called with an instance of a TraversableState to be subjected to the same tests.
 *
 * <p>The {@link TraversableState#successors} method should, for all valid operators provided by the traversable state,
 * generate an iterator over all of the {@link Successor} states that are formed by applying the valid operators to the
 * traversable state. There should be exactly one successor state for every valid operator.
 *
 * <pre><p/><table id="crc">
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that there are no duplicates amongst the valid operators.
 * <tr><td> Check that there are no duplicates amongst the operators reported as having been applied by the successors.
 * <tr><td> Check that getChildStateForOperator returns non-null for all valid operators.
 * <tr><td> Check that exactly one non-null successor state is returned for each valid operator.
 * <tr><td> Check that successor state costs match calculated costs.
 * <tr><td> Check that successor states match states reached by applying all valid operators.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   The operator reported in the successor nodes and the cost reported to get to the successor nodes must match
 *         the operator applied and the cost of applying that operator. Move these tests into the test class for
 *         {@link SearchNode}
 */
public class TraversableStateTest extends TestCase
{
    /** Used for debugging. */
    /* private static final Logger log = Logger.getLogger(TraversableStateTest.class.getName()); */

    /** The {@link TraversableState} object to test. */
    TraversableState testTraversableState;

    /**
     * Default constructor that will result in the tests being run on this classes own inner implementation of a
     * traversable state. This is used to check the sanity of the tests being run and the implementation of the
     * non-absrtact traversable state method.
     */
    public TraversableStateTest(String testName)
    {
        super(testName);

        // Create the test traversable state to use
        testTraversableState = new TestTraversableState();
    }

    /**
     * Builds the tests to be run on a supplied traversable state. This allows the default test methods to be applied to
     * arbitrary implementation of traversable state in sub-classes of this test class.
     */
    public TraversableStateTest(String testName, TraversableState testTraversableState)
    {
        super(testName);

        // Keep reference to the traversable state to subject to testing
        this.testTraversableState = testTraversableState;
    }

    /** Check that getChildStateForOperator returns non-null for all valid operators. */
    public void testApplyOperatorWorksAllValidOperators() throws Exception
    {
        String errorMessage = "";

        // Loop over all the valid operators.
        for (Iterator<Operator> i = testTraversableState.validOperators(false); i.hasNext();)
        {
            Operator next = i.next();

            // Check that applying the operator return a non-null state.
            if (testTraversableState.getChildStateForOperator(next) == null)
            {
                // The state was null so add it to the error message
                errorMessage += "Operator " + next + " resulted in a null state when applied.\n";
            }
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that exactly one non-null successor state is returned for each valid operator. */
    public void testExactlyOneSuccessorStateForEachValidOperator() throws Exception
    {
        String errorMessage = "";
        Map<Operator, Successor> successors = new HashMap<Operator, Successor>();

        // Get all the successors states and loop over them.
        for (Iterator<Successor> i = testTraversableState.successors(false); i.hasNext();)
        {
            Successor nextSuccessor = i.next();
            Operator nextOperator = nextSuccessor.getOperator();

            // Copy the next successor states into a hash map and key it by the operator.
            // It is assumed from the testNoDuplicateOperatorsInSuccessors test that there will be no duplicates.
            successors.put(nextOperator, nextSuccessor);
        }

        // Loop over all the valid operators.
        for (Iterator<Operator> i = testTraversableState.validOperators(false); i.hasNext();)
        {
            Operator next = i.next();

            // Try to find the matching successor state in the successors hash map.
            if (!successors.containsKey(next))
            {
                // No matching successor state could be found in the hash map so add this as an error message.
                errorMessage += "No successor could be found for the operator: " + next + "\n";
            }

            // A matching successor was found for the operator.
            else
            {
                // Remove the matching successor from the hash map.
                successors.remove(next);
            }
        }

        // Check if the hash map of successors is empty, that is that they were all removed by scanning over all the
        // operators, so it is known that there are no successor states generated for which there were no operators.
        if (!successors.isEmpty())
        {
            // The hash map is not empty so add this to the error message.
            errorMessage += "There were successor states for which there were no valid operators to produce them:\n";

            // List the invalid successor state in the error message too.
            for (Successor next : successors.values())
            {
                errorMessage +=
                    "Invalid successor: operator = " + next.getOperator() + ", state = " + next.getState() + "\n";
            }
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that there are no duplicates amongst the valid operators. */
    public void testNoDuplicateOperators()
    {
        String errorMessage = "";
        Set<Operator> operatorSet = new HashSet<Operator>();

        // Get all the operators and loop over them.
        for (Iterator<Operator> i = testTraversableState.validOperators(false); i.hasNext();)
        {
            Operator next = i.next();

            // Check that the operator does not already exist in the hash set.
            if (operatorSet.contains(next))
            {
                // A duplicate operator has been found so add it to the error message.
                errorMessage += "Duplicate found for operator: " + next + "\n";
            }

            // Copy the next operator into the hash set for further checking against duplicates.
            operatorSet.add(next);
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that there are no duplicates amongst the operators reported as having been applied by the successors. */
    public void testNoDuplicateOperatorsInSuccessors()
    {
        String errorMessage = "";
        Set<Operator> operatorSet = new HashSet<Operator>();

        // Get all the successors and loop over them.
        for (Iterator<Successor> i = testTraversableState.successors(false); i.hasNext();)
        {
            // Get the reported applied operator from the next successor
            Operator next = i.next().getOperator();

            // Check that the operator does not already exist in the hash set.
            if (operatorSet.contains(next))
            {
                // A duplicate operator has been found so add it to the error message.
                errorMessage += "Duplicate found for operator: " + next + "\n";
            }

            // Copy the next operator into the hash set for further checking against duplicates.
            operatorSet.add(next);
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that successor state costs match calculated costs. */
    public void testSuccessorStateCostsMatchAppliedOperators() throws Exception
    {
        String errorMessage = "";
        Map<Operator, Successor> successors = new HashMap<Operator, Successor>();

        // Get all the successors states and loop over them.
        for (Iterator<Successor> i = testTraversableState.successors(false); i.hasNext();)
        {
            Successor nextSuccessor = i.next();
            Operator nextOperator = nextSuccessor.getOperator();

            // Copy the next successor states into a hash map and key it by the operator.
            // It is assumed from the testNoDuplicateOperatorsInSuccessors test that there will be no duplicates.
            successors.put(nextOperator, nextSuccessor);
        }

        // Loop over all the valid operations
        for (Iterator<Operator> i = testTraversableState.validOperators(false); i.hasNext();)
        {
            Operator next = i.next();

            // Find the matching successor state in the successors hash map, this should always succeed if the
            // testExactlyOneSuccessorStateForEachValidOperator test has passed so failure is not checked for.
            Successor nextSuccessor = successors.get(next);

            // Check that the declared cost of the operator matches the cost reported in the successor.
            if (testTraversableState.costOf(next) != nextSuccessor.getCost())
            {
                // The costs don't match so add this to the error message.
                errorMessage +=
                    "The test TraversableSate reports the cost of operator " + next + " as " +
                    testTraversableState.costOf(next) + " but the successor state reports it as " +
                    nextSuccessor.getCost();
            }
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that successor states match states reached by applying all valid operators. */
    public void testSuccessorStatesMatchAppliedOperatorStates() throws Exception
    {
        String errorMessage = "";
        Map<Operator, Successor> successors = new HashMap<Operator, Successor>();

        // Get all the successors states and loop over them.
        for (Iterator<Successor> i = testTraversableState.successors(false); i.hasNext();)
        {
            Successor nextSuccessor = i.next();
            Operator nextOperator = nextSuccessor.getOperator();

            // Copy the next successor states into a hash map and key it by the operator.
            // It is assumed from the testNoDuplicateOperatorsInSuccessors test that there will be no duplicates.
            successors.put(nextOperator, nextSuccessor);
        }

        // Loop over all the valid operations
        for (Iterator<Operator> i = testTraversableState.validOperators(false); i.hasNext();)
        {
            Operator next = i.next();

            // Find the matching successor state in the successors hash map, this should always succeed if the
            // testExactlyOneSuccessorStateForEachValidOperator test has passed so failure is not checked for.
            Successor nextSuccessor = successors.get(next);

            // Check that the suceesor state matches the state reached by applying the operator.
            if (!testTraversableState.getChildStateForOperator(next).equals(nextSuccessor.getState()))
            {
                // The states don't match so add this to the error message.
                errorMessage +=
                    "The test TraversableSate reports the state of applied operator " + next + " as " +
                    testTraversableState.getChildStateForOperator(next) + " but the successor state reports it as " +
                    nextSuccessor.getState();
            }
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** @throws Exception */
    protected void setUp() throws Exception
    {
        NDC.push(getName());
    }

    /** @throws Exception */
    protected void tearDown() throws Exception
    {
        NDC.pop();
    }
}
