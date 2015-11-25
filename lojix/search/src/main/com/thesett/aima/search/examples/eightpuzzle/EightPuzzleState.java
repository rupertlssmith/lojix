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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.thesett.aima.search.GoalState;
import com.thesett.aima.search.Operator;
import com.thesett.aima.search.TraversableState;
import com.thesett.aima.search.util.OperatorImpl;

/**
 * An implementation of the eight puzzle. This is a puzzle with nine squares arranged in a 3x3 grid. One of the squares
 * is empty and the others contain tiles with the numbers 1 to 8 on them, initially randomly arranged. The object of the
 * game is to slide tiles into the empty square until all the tiles are arranged in order, reading left to right, top to
 * bottom, with the empty square in the last position.
 *
 * <p>For example (E denotes the empty square):
 *
 * <p>Random start state:
 *
 * <pre><p/><table>
 * <tr><td>5<td>E<td>3
 * <tr><td>6<td>1<td>7
 * <tr><td>8<td>2<td>4
 * </table></pre>
 *
 * <p>Goal state:
 *
 * <pre><p/><table>
 * <tr><td>1<td>2<td>3
 * <tr><td>4<td>5<td>6
 * <tr><td>7<td>8<td>E
 * </table></pre>
 *
 * <p>Instances of this state class represent a single board position. This class also provides for each state, a set of
 * operators corresponding to the legal moves on a state, that result in new legal board states.
 *
 * <p>For efficient matching of states this state representation provides the {@link #equals} and {@link #hashCode}
 * methods. The hash function is computed by transforming the board position into a string consisting of its tiles in
 * order (using the numeric characters plus 'E' for the empty square) and then taking the string hash of this.
 *
 * <p>Their are four possible moves; up, down, left and right. These correspond to moving the empty tile up, down, left
 * and right and are represented by the operation strings "U", "D", "L" and "R".
 *
 * <p>There is a static convenience method that generates a random solvable board position. Not all board positions are
 * solvable. Unsolvable positions can get close to a solution but will end up with two neighbouring tiles that need to
 * be swapped but can't be. Tiles can only swap position with the empty tile. There are effectively two sets of board
 * states for each puzzle, the legal states and the illegal ones. To get from one state set to the other simply swap two
 * adjacent non-empty tiles. This is called an illegal swap. To check for solvability the number of illegal swaps needed
 * to get to the goal position are counted. If this is even then they cancel out so the board position belongs to the
 * set of legal states. If its odd then they don't cancel out so the board belongs to the set of illegal states and is
 * not solvable with respect to the goal position.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Generate a random starting position that is solvable.
 * <tr><td> Generate a state corresponding to the goal state.
 * <tr><td> Check if a board position is the goal state.
 * <tr><td> Supply the valid moves for a board position.
 * <tr><td> Calculate the cost of a move (always one).
 * <tr><td> Apply a move to generate a new board position.
 * <tr><td> Report the goal coordinates for a tile.
 * <tr><td> Report the coordinates for a tile.
 * <tr><td> Report the position of the empty tile.
 * <tr><td> Report the tile at a given position.
 * <tr><td> Supply equals and hashCode for efficient hashing on puzzle states.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class EightPuzzleState extends TraversableState<String> implements GoalState, Cloneable
{
    /**
     * Defines the goal state. Changeing this constant allows different goal states to be defined. Another popular goal
     * state is "1238E4765".
     */
    private static final String GOAL_STRING = "12345678E";

    /** Used to hold the board position. */
    char[][] board = new char[3][3];

    /** Used to hold the X position of the empty tile. */
    int emptyX;

    /** Used to hold the Y position of the empty tile. */
    int emptyY;

    /**
     * Generates a puzzle state from a string.
     *
     * @param  s The board as a string of 9 characters.
     *
     * @return The board as an EightPuzzleState.
     */
    public static EightPuzzleState getStateFromString(String s)
    {
        // Turn the string into a list of characters and then transform this into a proper state object.
        return charListToState(stringToCharList(s));
    }

    /**
     * Generates the goal state.
     *
     * @return The goal state as an EightPuzzleState.
     */
    public static EightPuzzleState getGoalState()
    {
        // Turn the goal string into a list of characters and then transform this into a proper state object.
        return charListToState(stringToCharList(GOAL_STRING));
    }

    /**
     * Generates a random starting position.
     *
     * @return A random board state.
     */
    public static EightPuzzleState getRandomStartState()
    {
        EightPuzzleState newState;

        // Turn the goal string into a list of characters.
        List<Character> charList = stringToCharList(GOAL_STRING);

        // Generate random puzzles until a solvable one is found.
        do
        {
            // Shuffle the list.
            Collections.shuffle(charList);

            // Turn the shuffled list into a proper eight puzzle state object.
            newState = charListToState(charList);

            // Check that the puzzle is solvable and if not then repeat the shuffling process.
        }
        while (!isSolvable(newState));

        return newState;
    }

    /**
     * To check for solvability the empty tile is moved to its goal position and then the number of swaps needed to put
     * the other tiles in position is counted. For an odd number of rows on a square puzzle there must be an even number
     * of swaps, for an even number of rows an odd number of swaps.
     *
     * @param  state A puzzle state to test for solvability.
     *
     * @return True if the puzzle is solvable, false if it is not.
     */
    public static boolean isSolvable(EightPuzzleState state)
    {
        // Take a copy of the puzzle to check. This is done because this puzzle will be updated in-place and the
        // original is to be preserved.
        EightPuzzleState checkState;

        try
        {
            checkState = (EightPuzzleState) state.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new IllegalStateException("Puzzle state could not be cloned.", e);
        }

        // Create the goal state to check against when swapping tiles into position.
        EightPuzzleState goalState = getGoalState();

        // Count the number of illegal swaps needed to put the puzzle in order.
        int illegalSwaps = 0;

        // Loop over the whole board, left to right, to to bottom
        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < 3; i++)
            {
                // Find out from the goal state what tile should be at this position.
                char t = goalState.getTileAt(i, j);

                // Swap the tile into its goal position keeping count of the total number of illegal swaps.
                illegalSwaps += checkState.swapTileToLocationCountingIllegal(t, i, j);
            }
        }

        // Check if the number of illegal swaps is even in which case the puzzle is solvable, or odd in which case it
        // is not solvable.
        return (illegalSwaps % 2) == 0;
    }

    /**
     * This helper function calculates the goal X position for a tile.
     *
     * @param  t The tile to check.
     *
     * @return The goal X position for the tile.
     */
    public int getGoalXForTile(char t)
    {
        // Find the position of the tile in the goal string and take the remainder after dividing by 3 and
        // this is the X position.
        return GOAL_STRING.indexOf(t) % 3;
    }

    /**
     * This helper function calcualtes the goal Y position for a tile.
     *
     * @param  t The tile to check.
     *
     * @return The goal Y position for the tile.
     */
    public int getGoalYForTile(char t)
    {
        // Find the position of the tile in the goal string and integer divide by 3 to get the Y position.
        return GOAL_STRING.indexOf(t) / 3;
    }

    /**
     * This helper function calculates the X position for a tile.
     *
     * @param  t The tile to check.
     *
     * @return The current X position of the tile.
     */
    public int getXForTile(char t)
    {
        // Find the X position of the tile by finding its position in the string representation and taking the
        // remainder after dividing by 3.
        return toString().indexOf(t) % 3;
    }

    /**
     * This helper function calculates the Y position for a tile.
     *
     * @param  t The tile to check.
     *
     * @return The current X position of the tile.
     */
    public int getYForTile(char t)
    {
        // Find the Y position of the tile by finding its position in the string representation and dividing by 3.
        return toString().indexOf(t) / 3;
    }

    /**
     * Returns the tile at the specified X, Y position.
     *
     * @param  x The X position.
     * @param  y The Y position.
     *
     * @return The tile at X, Y.
     */
    public char getTileAt(int x, int y)
    {
        return board[y][x];
    }

    /**
     * Returns the X position of the empty tile.
     *
     * @return The X position of the empty tile.
     */
    public int getEmptyX()
    {
        return emptyX;
    }

    /**
     * Returns the Y position of the empty tile.
     *
     * @return The Y position of the empty tile.
     */
    public int getEmptyY()
    {
        return emptyY;
    }

    /**
     * Checks if a board position is the goal state.
     *
     * @return True if this board is in the goal position, false otherwise.
     */
    public boolean isGoal()
    {
        return toString().equals(GOAL_STRING);
    }

    /**
     * Applies a move to generate a new board position. This creates a new state object and updates its board position.
     * The board position in this object is not changed.
     *
     * @param  op The move to apply to this board; "U" for up, "D" for down, "L" for left and "R" for right.
     *
     * @return A new instance of EightPuzzleState with the operator applied to it.
     */
    public EightPuzzleState getChildStateForOperator(Operator op)
    {
        // Create a copy of the existing board state
        EightPuzzleState newState;

        try
        {
            newState = (EightPuzzleState) clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new IllegalStateException("Puzzle state could not be cloned.", e);
        }

        // Update the new board state using the in-place operator application
        newState.updateWithOperator(op);

        return newState;
    }

    /**
     * Calculates the cost of a move (always one).
     *
     * @param  op The move to calculate the cost of.
     *
     * @return All moves always have a cost of 1.0f.
     */
    public float costOf(Operator op)
    {
        return 1.0f;
    }

    /**
     * Supplies the valid moves for a board position.
     *
     * @param  reverse Don't care about reverse orderings. Ignored.
     *
     * @return An iterator over all the moves which are valid for this board position. This can be all 4, 3 or just 2
     *         moves, depending if the empty tile is in the middle, edge or corner positions.
     */
    public Iterator<Operator<String>> validOperators(boolean reverse)
    {
        // Used to hold a list of valid moves
        List<Operator<String>> moves = new ArrayList<Operator<String>>(4);

        // Check if the up move is valid
        if (emptyY != 0)
        {
            moves.add(new OperatorImpl<String>("U"));
        }

        // Check if the down move is valid
        if (emptyY != 2)
        {
            moves.add(new OperatorImpl<String>("D"));
        }

        // Check if the left move is valid
        if (emptyX != 0)
        {
            moves.add(new OperatorImpl<String>("L"));
        }

        // Check if the right move is valid
        if (emptyX != 2)
        {
            moves.add(new OperatorImpl<String>("R"));
        }

        return moves.iterator();
    }

    /**
     * Computes a hash code of the board position for efficient hashing of board states. This uses the string hash
     * function on the board position made by concatenating all the tiles into a string.
     *
     * <p>Another hashing strategy that could be tried is simply to concatenate all the tiles together into a decimal
     * number modulo the maximum integer.
     *
     * @return A hash code for this board position calculated from the position information.
     */
    public int hashCode()
    {
        return toString().hashCode();
    }

    /**
     * Checks if two board positions are equal. This is computed by using the toString method to concatenate all the
     * tiles togheter into a string and then using string equality.
     *
     * <p>A faster solution may simply be to iterate over the two boards until a mismatch is found. If there is no
     * mismatch then they are equal.
     *
     * @param  o The object to compare this to.
     *
     * @return True if the comparison object is a board position identical to this one, false otherwise.
     */
    public boolean equals(Object o)
    {
        return toString().equals(o.toString());
    }

    /**
     * Converts the board position into a string representation by walking over the board left-to-right, top-to-bottom
     * using 'E' for the empty tile and the number characters for the numbered tiles.
     *
     * @return A string representation of this board position.
     */
    public String toString()
    {
        char[] result = new char[9];
        int resultCounter = 0;

        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < 3; i++)
            {
                result[resultCounter++] = board[j][i];
            }
        }

        return new String(result);
    }

    /**
     * Pretty prints the board as 3 lines of characters with a space for the empty square.
     *
     * @return A pretty printed string representation of this board position.
     */
    public String prettyPrint()
    {
        String result = "";

        for (int j = 0; j < 3; j++)
        {
            result += new String(board[j]) + "\n";
        }

        result = result.replace('E', ' ');

        return result;
    }

    /**
     * Repeatedly swaps a tile with its neighbours until it reaches the specified location. If the tile is swapped with
     * the empty tile then this is a legal move. If the tile is swapped with another non-empty tile then this is an
     * illegal move and the total number of illegal moves is counted.
     *
     * <p>This method updates the board position array in-place, rather than generating a new state.
     *
     * @param  t the tile to move.
     * @param  x the X position to move the tile to.
     * @param  y the Y position to move the tile to.
     *
     * @return the number of illegal swaps performed.
     */
    protected int swapTileToLocationCountingIllegal(char t, int x, int y)
    {
        // Used to hold the count of illegal swaps
        int illegal = 0;

        // Find out where the tile to move is
        int tileX = getXForTile(t);
        int tileY = getYForTile(t);

        // Shift the tile into the correct column by repeatedly moving it left or right.
        while (tileX != x)
        {
            if ((tileX - x) > 0)
            {
                if (swapTiles(tileX, tileY, tileX - 1, tileY))
                {
                    illegal++;
                }

                tileX--;
            }
            else
            {
                if (swapTiles(tileX, tileY, tileX + 1, tileY))
                {
                    illegal++;
                }

                tileX++;
            }
        }

        // Shift the tile into the correct row by repeatedly moving it up or down.
        while (tileY != y)
        {
            // Commented out because tiles never swap down the board during the solvability test because tiles are
            // swapped into place left to right, top to bottom. The top row is always filled first so tiles cannot be
            // swapped down into it. Then the next row is filled but ones from the row above are never swapped down
            // into it because they are alrady in place and never move again and so on.
            /* if (tileY - y > 0)
             *{*/
            if (swapTiles(tileX, tileY, tileX, tileY - 1))
            {
                illegal++;
            }

            tileY--;
            /*}
             * else { if (swapTiles(tileX, tileY, tileX, tileY + 1)) illegal++; tileY++;}*/
        }

        return illegal;
    }

    /**
     * Applies a move to the board position. This changes the board position stored in this object. This is different
     * from the {@link #getChildStateForOperator} method which updates the board position in a new object.
     *
     * @param op The move to apply to this board; "U" for up, "D" for down, "L" for left and "R" for right.
     */
    protected void updateWithOperator(Operator op)
    {
        // Get the operator as a character by taking the first character of the operator string
        char opc = ((String) op.getOp()).charAt(0);

        // Move the empty tile according to the specified operation
        switch (opc)
        {
        // Swap the empty tile with the one above it.
        case 'U':
        {
            swapTiles(emptyX, emptyY, emptyX, emptyY - 1);
            break;
        }

        // Swap the empty tile with the one below it.
        case 'D':
        {
            swapTiles(emptyX, emptyY, emptyX, emptyY + 1);
            break;
        }

        // Swap the empty tile with the one to the left of it.
        case 'L':
        {
            swapTiles(emptyX, emptyY, emptyX - 1, emptyY);
            break;
        }

        // Swap the empty tile with the one to the right of it.
        case 'R':
        {
            swapTiles(emptyX, emptyY, emptyX + 1, emptyY);
            break;
        }

        default:
        {
            throw new IllegalStateException("Unkown operator: " + opc + ".");
        }
        }
    }

    /**
     * Swaps the two tiles at the specified coordinates. One of the tiles may be the empty tile and the empty tile
     * position will be correctly updated. If neither of the tiles is empty then this is an illegal swap in which case
     * the method returns true.
     *
     * @param  x1 the X position of tile 1
     * @param  y1 the Y position of tile 1
     * @param  x2 the X position of tile 2
     * @param  y2 the Y position of tile 2
     *
     * @return True if it is an illegal swap.
     */
    protected boolean swapTiles(int x1, int y1, int x2, int y2)
    {
        // Used to indicate that one of the swapped tiles was the empty tile
        boolean swappedEmpty = false;

        // Get the tile at the first position
        char tile1 = board[y1][x1];

        // Store the tile from the second position at the first position
        char tile2 = board[y2][x2];

        board[y1][x1] = tile2;

        // Store the first tile in the second position
        board[y2][x2] = tile1;

        // Check if the first tile was the empty tile and update the empty tile coordinates if so
        if (tile1 == 'E')
        {
            emptyX = x2;
            emptyY = y2;
            swappedEmpty = true;
        }

        // Else check if the second tile was the empty tile and update the empty tile coordinates if so
        else if (tile2 == 'E')
        {
            emptyX = x1;
            emptyY = y1;
            swappedEmpty = true;
        }

        return !swappedEmpty;
    }

    /**
     * Creates a deep clone of the puzzle state. That is one where the board position is copied into a new array. The
     * empty tile position is also copied.
     *
     * @return A deep clone of this object.
     *
     * @throws CloneNotSupportedException If cloning fails.
     */
    protected Object clone() throws CloneNotSupportedException
    {
        // Create a new state and copy the existing board position into it
        EightPuzzleState newState = (EightPuzzleState) super.clone();

        newState.board = new char[3][3];

        for (int j = 0; j < 3; j++)
        {
            System.arraycopy(board[j], 0, newState.board[j], 0, 3);
        }

        newState.emptyX = emptyX;
        newState.emptyY = emptyY;

        return newState;
    }

    /**
     * Turns a string representation of the board into a list of characters.
     *
     * @param  boardString The board as a string of nine characters.
     *
     * @return The board as a List of characters.
     */
    private static List<Character> stringToCharList(String boardString)
    {
        // Turn the goal state into a list of characters
        char[] chars = new char[9];

        boardString.getChars(0, 9, chars, 0);

        List<Character> charList = new ArrayList<Character>();

        for (int l = 0; l < 9; l++)
        {
            charList.add(chars[l]);
        }

        return charList;
    }

    /**
     * Turns a list of characters representation of the board into a proper state.
     *
     * @param  charList The board as a list of 9 characters.
     *
     * @return The board as an EightPuzzleState.
     */
    private static EightPuzzleState charListToState(List<Character> charList)
    {
        // Create a new empty puzzle state
        EightPuzzleState newState = new EightPuzzleState();

        // Loop over the board inserting the characters into it from the character list
        Iterator<Character> k = charList.iterator();

        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < 3; i++)
            {
                char nextChar = k.next();

                // Check if this is the empty tile and if so then take note of its position
                if (nextChar == 'E')
                {
                    newState.emptyX = i;
                    newState.emptyY = j;
                }

                newState.board[j][i] = nextChar;
            }
        }

        return newState;
    }
}
