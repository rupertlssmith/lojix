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
package com.thesett.aima.search.examples.eightpuzzle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

import com.thesett.aima.search.Operator;
import com.thesett.aima.search.TraversableStateTest;

/**
 * EightPuzzleStateTest is a pure unit test class for the {@link EightPuzzleState} class.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that the empty tile is reported consistently by the getEmpty and getForTile methods.
 * <tr><td> Check that the applied operators correctly move the empty tile.
 * <tr><td> Check that the applied operators correctly move the non-empty tile.
 * <tr><td> Check that the goal state passes the goal test.
 * <tr><td> Check that non-goal test states do not pass the goal test.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class EightPuzzleStateTest extends TestCase
{
    /**  */
    /* private static final Logger log = Logger.getLogger(EightPuzzleStateTest.class.getName()); */

    /** The states to test. */
    private static Collection<EightPuzzleState> testStates;

    /** Default constructor that will result in the tests being run on the {@link EightPuzzleState} class. */
    public EightPuzzleStateTest(String testName)
    {
        super(testName);

        // Create the test states to use
        generateTestStates();
    }

    /**
     * Generates a collection of test states to run the tests on. This consists of one state for each position of the
     * empty tile.
     *
     * @todo Could randomize the positions of the non-empty tiles too.
     */
    public static void generateTestStates()
    {
        testStates = new ArrayList<EightPuzzleState>(9);

        // Loop over all the tile positions generating a puzzle with the empty tile in each.
        for (int i = 0; i < 9; i++)
        {
            // Used to keep track of the current tile
            int tile = 1;

            // Used to build the puzzle string in
            char[] chars = new char[9];

            // Generate the characters up to i and place them in the string
            for (int j = 0; j < i; j++)
            {
                chars[j] = (new Integer(tile++)).toString().charAt(0);
            }

            // Add in the empty tile in the string
            chars[i] = 'E';

            // Generate the charactars after i and place them in the string
            for (int k = i + 1; k < 9; k++)
            {
                chars[k] = (new Integer(tile++)).toString().charAt(0);
            }

            String puzzleString = new String(chars);

            testStates.add(EightPuzzleState.getStateFromString(puzzleString));
        }
    }

    /** Compile all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("EightPuzzleState Tests");

        // Add all tests defined in the TraversableStateTest class for each puzzles with the empty square in all
        // possible board positions
        generateTestStates();

        for (EightPuzzleState nextState : testStates)
        {
            suite.addTest(new TraversableStateTest("testNoDuplicateOperators", nextState));
            suite.addTest(new TraversableStateTest("testNoDuplicateOperatorsInSuccessors", nextState));
            suite.addTest(new TraversableStateTest("testApplyOperatorWorksAllValidOperators", nextState));
            suite.addTest(new TraversableStateTest("testExactlyOneSuccessorStateForEachValidOperator", nextState));
            suite.addTest(new TraversableStateTest("testSuccessorStateCostsMatchAppliedOperators", nextState));
            suite.addTest(new TraversableStateTest("testSuccessorStatesMatchAppliedOperatorStates", nextState));
        }

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(EightPuzzleStateTest.class);

        return suite;
    }

    /**
     * Check that the empty tile 'E' is reported to be in the location where it is consistently by the getEmpty
     * getForTile methods.
     */
    public void testGetEmptyAndGetForTileConsistent() throws Exception
    {
        String errorMessage = "";

        // Loop over the whole set of test puzzles
        for (EightPuzzleState nextState : testStates)
        {
            // Get the empty tile coordinates using the getEmpty methods.
            int emptyX = nextState.getEmptyX();
            int emptyY = nextState.getEmptyY();

            // Get the empty tile coordinates using the getForTile methods.
            int getForTileX = nextState.getXForTile('E');
            int getForTileY = nextState.getYForTile('E');

            // Check that they match and add to the error message if not.
            if ((emptyX != getForTileX) || (emptyY != getForTileY))
            {
                errorMessage +=
                    "The coordinates reported by the getEmpty and getForTile methods do not match.\n" +
                    "getForTileX  = " + getForTileX + ", emptyX = " + emptyX + ", getForTileY  = " + getForTileY +
                    ", emptyY = " + emptyY + " for puzzle board:\n" + nextState.prettyPrint();
            }

            // Get the tile at the reported empty tile location and check it really is the empty tile.
            if (nextState.getTileAt(emptyX, emptyY) != 'E')
            {
                errorMessage +=
                    "The repored empty tile location, X = " + emptyX + ", Y = " + emptyY +
                    ", does not return the empty tile using getTileAt.\n" + " for puzzle board:\n" +
                    nextState.prettyPrint();
            }
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that the applied operators correctly move the empty tile. */
    public void testOperatorsMoveEmptyTile() throws Exception
    {
        String errorMessage = "";

        // Loop over the whole set of test puzzles
        for (EightPuzzleState nextState : testStates)
        {
            // Get the current empty tile position.
            int emptyX = nextState.getEmptyX();
            int emptyY = nextState.getEmptyY();

            // Loop over all of the valid operators on the puzzle
            for (Iterator<Operator<String>> j = nextState.validOperators(false); j.hasNext();)
            {
                Operator operator = j.next();
                char op = ((String) operator.getOp()).charAt(0);

                // Work out where it should be after the operator is applied.
                int newX = emptyX;
                int newY = emptyY;

                switch (op)
                {
                case 'U':
                {
                    newY--;
                    break;
                }

                case 'D':
                {
                    newY++;
                    break;
                }

                case 'L':
                {
                    newX--;
                    break;
                }

                case 'R':
                {
                    newX++;
                    break;
                }
                }

                // Apply the operator to get a new state.
                EightPuzzleState newState = (EightPuzzleState) nextState.getChildStateForOperator(operator);

                // Check that empty tile has really moved to the correct location.
                if ((newState.getEmptyX() != newX) || (newState.getEmptyY() != newY))
                {
                    errorMessage +=
                        "After applying the operator, " + operator +
                        ", the empty tile does not move to its expected location, X = " + newX + ", Y = " + newY +
                        ", but moves instead to, X = " + newState.getEmptyX() + ", Y = " + newState.getEmptyY() +
                        " for puzzle board, X = " + emptyX + ", Y = " + emptyY + ":\n" + nextState.prettyPrint();
                }
            }
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that the applied operators correctly move the non-empty tile. */
    public void testOperatorsMoveNonEmptyTile() throws Exception
    {
        String errorMessage = "";

        // Loop over the whole set of test puzzles
        for (EightPuzzleState nextState : testStates)
        {
            // Get the current empty tile position.
            int emptyX = nextState.getEmptyX();
            int emptyY = nextState.getEmptyY();

            // Loop over all of the valid operators on the puzzle
            for (Iterator<Operator<String>> j = nextState.validOperators(false); j.hasNext();)
            {
                Operator operator = j.next();
                char op = ((String) operator.getOp()).charAt(0);

                // Work out where it should be after the operator is applied.
                // Work out where it should be after the operator is applied.
                int newX = emptyX;
                int newY = emptyY;

                switch (op)
                {
                case 'U':
                {
                    newY--;
                    break;
                }

                case 'D':
                {
                    newY++;
                    break;
                }

                case 'L':
                {
                    newX--;
                    break;
                }

                case 'R':
                {
                    newX++;
                    break;
                }
                }

                // Get the tile at that location.
                char t = nextState.getTileAt(newX, newY);

                // Apply the operators to get a new state.
                EightPuzzleState newState = (EightPuzzleState) nextState.getChildStateForOperator(operator);

                // Check that the displaced tile has been moved to the empty tiles old location.
                if (t != newState.getTileAt(emptyX, emptyY))
                {
                    errorMessage +=
                        "After applying the operator, " + operator +
                        ", the displaced tile does not move to its expected location, X = " + emptyX + ", Y = " +
                        emptyY + ", but this location contains the tile, " + newState.getTileAt(emptyX, emptyY) +
                        ", instead " + " for puzzle board:\n" + nextState.prettyPrint();
                }
            }
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /**
     * Check that the goal state passes the goal test and that the positions of all tiles matches the positions reported
     * for them by the getGoalForTile methods.
     */
    public void testGoalStateIsGoalState() throws Exception
    {
        String errorMessage = "";

        // Generate the goal state.
        EightPuzzleState goalState = EightPuzzleState.getGoalState();

        // Check that it really does pass the isGoal test.
        assertTrue("The goal state does not pass the isGoal test.", goalState.isGoal());

        // Check all tiles are really in their getGoalForTile positions.
        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < 3; i++)
            {
                // Get the tile at the current location.
                char t = goalState.getTileAt(i, j);

                // Get the goal location for that tile.
                int goalX = goalState.getGoalXForTile(t);
                int goalY = goalState.getGoalYForTile(t);

                // Check it matches its current location.
                if ((i != goalX) || (j != goalY))
                {
                    errorMessage +=
                        "Tile at " + i + ", " + j + " reports goal location at " + goalX + ", " + goalY +
                        " for a board position that passes the isGoal test.";
                }
            }
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that non-goal test states do not pass the goal test. */
    public void testNonGoalStateIsNotGoalState() throws Exception
    {
        String errorMessage = "";

        // Generate the goal state.
        EightPuzzleState goalState = EightPuzzleState.getGoalState();

        // Take a copy of the test puzzles and add some random solvable puzzles to it.
        ArrayList<EightPuzzleState> expandedTestStates = new ArrayList<EightPuzzleState>(testStates);

        for (int j = 0; j < 50; j++)
        {
            expandedTestStates.add(EightPuzzleState.getRandomStartState());
        }

        // Loop over the whole expanded set of test puzzles
        for (EightPuzzleState nextState : expandedTestStates)
        {
            // Check if the state is not equal to the goal state.
            if (!nextState.equals(goalState))
            {
                // Check that the state does not pass the isGoal test.
                assertFalse("Non-goal state passed the isGoal test for puzzle board:\n" + nextState.prettyPrint(),
                    nextState.isGoal());
            }
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Checks that the hashCode method runs ok. */
    public void testHashCode() throws Exception
    {
        // Generate the goal state.
        EightPuzzleState goalState = EightPuzzleState.getGoalState();

        // Get its hash code
        goalState.hashCode();
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
