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

import com.thesett.aima.search.util.informed.Heuristic;
import com.thesett.aima.search.util.informed.HeuristicSearchNode;

/**
 * Implements the manhattan distance heuristic for the eight puzzle. If there is a parent heuristic search node, which
 * there will be for all states other than the start state, then it calculates the manhattan heuristic incrementally
 * from it by only altering the parent heuristic for the one tile that was moved.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities <th> Collaborations
 * <tr><td> Calculate the manhattan distance of all tiles from the goal state.
 *     <td> {@link HeuristicSearchNode}, {@link EightPuzzleState}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ManhattanHeuristic implements Heuristic<String, EightPuzzleState>
{
    /**
     * Returns heuristic evaluation of an eight puzzle board position as the manhattan distance of all tiles from their
     * correct positions.
     *
     * @param  state      The EightPuzzleState to calculate the heuristic for.
     * @param  searchNode The heuristic search node for the state in some heuristic search.
     *
     * @return The heuristic estimation of the cost to the goal from this board position.
     */
    public float computeH(EightPuzzleState state, HeuristicSearchNode searchNode)
    {
        // Get the parent heuristic search node.
        HeuristicSearchNode parentNode = (HeuristicSearchNode) searchNode.getParent();

        // Check if there is no parent, in which case this is the start state so the complete heuristic needs
        // to be calculated.
        if (parentNode == null)
        {
            // Used to hold the running total.
            int h = 0;

            // Loop over the whole board.
            for (int j = 0; j < 3; j++)
            {
                for (int i = 0; i < 3; i++)
                {
                    char nextTile = state.getTileAt(i, j);

                    // Look up the board position of the tile in the solution.
                    int goalX = state.getGoalXForTile(nextTile);
                    int goalY = state.getGoalYForTile(nextTile);

                    // Compute the manhattan distance and add it to the total.
                    int diffX = goalX - i;

                    diffX = (diffX < 0) ? -diffX : diffX;

                    int diffY = goalY - j;

                    diffY = (diffY < 0) ? -diffY : diffY;

                    h += diffX + diffY;
                }
            }

            // Convert the result to a float and return it
            return (float) h;
        }

        // There is a parent node so calculate the heuristic incrementally from it.
        else
        {
            // Get the parent board state.
            EightPuzzleState parentState = (EightPuzzleState) parentNode.getState();

            // Get the parent heurstic value.
            float h = parentNode.getH();

            // Get the move that was played.
            char playedMove = ((String) searchNode.getAppliedOp().getOp()).charAt(0);

            // Get the position of the empty tile on the parent board.
            int emptyX = parentState.getEmptyX();
            int emptyY = parentState.getEmptyY();

            // Work out which tile has been moved, this is the tile that now sits where the empty tile was.
            char movedTile = state.getTileAt(emptyX, emptyY);

            // The tile has either moved one step closer to its goal location or one step further away, decide which it
            // is. Calculate the X or Y position that the tile moved from.
            int oldX = 0;
            int oldY = 0;

            switch (playedMove)
            {
            case 'L':
            {
                oldX = emptyX - 1;
                break;
            }

            case 'R':
            {
                oldX = emptyX + 1;
                break;
            }

            case 'U':
            {
                oldY = emptyY - 1;
                break;
            }

            case 'D':
            {
                oldY = emptyY + 1;
                break;
            }

            default:
            {
                throw new RuntimeException("Unkown operator: " + playedMove + ".");
            }
            }

            // Calculate the change in heuristic.
            int change = 0;

            switch (playedMove)
            {
            // Catch the case where a horizontal move was made.
            case 'L':
            case 'R':
            {
                // Get the X position of the tile in the goal state and current state
                int goalX = state.getGoalXForTile(movedTile);
                int newX = emptyX;

                // Calculate the change in the heuristic
                int oldDiffX = oldX - goalX;

                oldDiffX = (oldDiffX < 0) ? -oldDiffX : oldDiffX;

                int newDiffX = newX - goalX;

                newDiffX = (newDiffX < 0) ? -newDiffX : newDiffX;

                change = newDiffX - oldDiffX;
                break;
            }

            // Catch the case where a vertical move was made.
            case 'U':
            case 'D':
            {
                // Get the Y position of the tile in the goal state and current state
                int goalY = state.getGoalYForTile(movedTile);
                int newY = emptyY;

                // Calculate the change in the heuristic
                int oldDiffY = oldY - goalY;

                oldDiffY = (oldDiffY < 0) ? -oldDiffY : oldDiffY;

                int newDiffY = newY - goalY;

                newDiffY = (newDiffY < 0) ? -newDiffY : newDiffY;

                change = newDiffY - oldDiffY;

                break;
            }

            default:
            {
                throw new RuntimeException("Unkown operator: " + playedMove + ".");
            }
            }

            // Return the parent heuristic plus or minus one.
            return (change > 0) ? (h + 1.0f) : (h - 1.0f);
        }
    }
}
