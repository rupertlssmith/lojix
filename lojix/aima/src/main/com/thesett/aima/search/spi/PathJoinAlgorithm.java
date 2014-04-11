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
package com.thesett.aima.search.spi;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;

/**
 * PathJoinAlgorithms are used to stitch together the forward and reverse portions of a path from start to goal in a
 * bi-directional search.
 *
 * <p>Once a match between the forward and reverse fringes of such a search has been found it is necessary to walk
 * backwards along the node found on the reverse fringe adding all its states and operations to the forward search path.
 * If the operations in the reverse half of the path have been stored in the direction of travel away from the goal
 * state then they must be reversed when copying them into the forward path.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Join a forward and a reverse {@link SearchNode} together into a complete forward path.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface PathJoinAlgorithm<O, T extends Traversable<O>>
{
    /**
     * Joins the forward and reverse portions of a search path. The operation in the reverse path may need to be
     * reversed and different implentations for different kinds of {@link com.thesett.aima.search.Traversable}s will
     * often be needed to provide the know how to do this. It is also possible that different operator sets were used on
     * {@link com.thesett.aima.search.Traversable}s for forward and reverse searches over the the same
     * {@link com.thesett.aima.search.GoalState}s or even completely different state implementations and implemtations
     * of this algorithm may need to convert the paths to extract the complete end to end solution.
     *
     * @param  forwardPath A node corresponding to the path from the start node to where the forward and reverse paths
     *                     met in a search solution.
     * @param  reversePath A node corresponging to the path from the goal node to where the forward and reverse paths
     *                     met in a search solution.
     *
     * @return The goal node such that following its parents will take you back to the start node.
     */
    public SearchNode<O, T> joinBothPaths(SearchNode<O, T> forwardPath, SearchNode<O, T> reversePath);
}
