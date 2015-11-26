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
package com.thesett.aima.learning.decisiontree.prototype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.thesett.aima.learning.AbstractLearningMethod;
import com.thesett.aima.learning.ClassifyingMachine;
import com.thesett.aima.learning.LearningFailureException;
import com.thesett.aima.math.InformationTheory;
import com.thesett.aima.state.Attribute;
import com.thesett.aima.state.OrdinalAttribute;
import com.thesett.aima.state.State;
import com.thesett.common.util.SimpleTree;
import com.thesett.common.util.Tree;

/**
 * ProtoDTLearningMethod builds decision trees from a set of examples. This is a prototype decision tree learner and is
 * not built to be efficient.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Build a decision tree from input data and return a machine that classifies with it.
 *     <td> {@link DecisionTree}, {@link ProtoDTMachine}, {@link OrdinalAttribute},
 *          {@link Assignment}, {@link Pending}, {@link Decision}
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Add method to extract learned machine as a compiled down java program.
 * @todo   How to cope with missing data from the input set.
 * @todo   How to convert continous real values attributes into ranges (optimally)? There are discretization methods in
 *         the literature to use, many are based on maximal information.
 * @todo   Decide how to manage memory and time efficiently during learning. Some ideas: The examples are held in an
 *         array. Every decision partitions this array into disjoint sets. Begin by sorting the array by the property to
 *         split on. This way each sub-set of examples can now be identified as a pair of indexes into the array
 *         identifying where the sub-set begins and ends. This is good because passing examples by value or creating
 *         large data structures containing many references to individual examples will be very wasteful compared with
 *         this approach. Typically the sort contains very few values and many items with the same value. Choose the
 *         sort algorithm that works fastest on this kind of data (range sort in O(n) time). Only sort when needed and
 *         keep the results of a sort so it never needs to be repeated.
 * @todo   Where the example set is very large, might need to manage this array on disk? Create an interface for array
 *         access and a caching mechanism to manage the disk array, bringing bits into memory as they are needed into a
 *         fixed size LRU cache. Don't know if learning sets that big are feasable to learn on anyway.
 * @todo   Manage the whole example set as a two dimensional array, rather than as an array of States. Can easily
 *         provide a State class that takes an index and pretends to be an object holding those properties by looking
 *         them up in the array. Or manage the example set as a bunch of arrays of primitive data values (attribute
 *         lists, rather than one table).
 */
public class ProtoDTLearningMethod extends AbstractLearningMethod
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(ProtoDTLearningMethod.class.getName()); */

    /**
     * Used to hold the value of the classification that all of a set of examples have. The
     * {@link #allHaveSameClassification} method tests if all of a set of examples have the same classification and if
     * they do, leaves that classification in this variable at the end of the method.
     */
    private OrdinalAttribute allClassification;

    /**
     * Builds a decision tree by repeatedly selecting the best property of the data examples to split on. The best
     * property is chosen to be the one that reveals the most information about the target property to learn.
     *
     * <p>The constructed decision tree is then used to create a classifying machine for that tree which is returned.
     *
     * @return A classifying machine built from the learned decision tree.
     *
     * @throws LearningFailureException If there is not exactly one output property to learn for. This algorithm can
     *                                  only handle a single output property.
     */
    public ClassifyingMachine learn() throws LearningFailureException
    {
        /*log.fine("public ClassifyingMachine learn(): called");*/

        // Call the initialize method to ensure that input and output properties are correctly set up.
        initialize();

        // Check that there is only one output property to learn for. Decision trees can only output a single
        // property classification (although mutliple trees could be built for multiple properties).
        if (outputProperties.size() != 1)
        {
            throw new LearningFailureException(
                "Decision trees can only learn a classification for a single property, " + "but " +
                outputProperties.size() + " have been set as outputs.", null);
        }

        // Extract the single output property to learn for.
        String outputProperty = outputProperties.iterator().next();

        // Check that the output property to be learnt is one with a finite number of values. Decision trees
        // cannot learn infinite valued attributes, such as real numbers etc.
        int numValues =
            inputExamples.iterator().next().getComponentType().getPropertyType(outputProperty).getNumPossibleValues();

        if (numValues < 1)
        {
            throw new LearningFailureException("Decision trees can only learn a classification for a " +
                "property with a finite number of values. The property, " + outputProperty +
                " can have an infinite number of values and should be " +
                "discretized prior to being learned by decision trees.", null);
        }

        // Used to queue nodes that are pending construction.
        Queue<DecisionTree> pendingNodes = new LinkedList<DecisionTree>();

        // Create a pending node,(an empty tree, the examples, the attributes, the default classification).
        Pending startNode =
            new Pending(inputExamples, inputProperties, getMajorityClassification(outputProperty, inputExamples), null);

        // Create the root of the decision tree out of this start pending node.
        DecisionTree decisionTree = new DecisionTree(startNode);

        // Push this new pending node onto the construction queue.
        pendingNodes.offer(decisionTree);

        // Loop until the queue of nodes pending construction is empty.
        while (!pendingNodes.isEmpty())
        {
            /*log.fine("Got next pending node.");*/

            // Get the tree fragment for the next pending node. The data element should always be safe to cast to a
            // Pending node as this algorithm only pushes pending nodes onto the queue.
            DecisionTree currentTreePendingNode = pendingNodes.remove();
            DecisionTreeElement currentTreePendingNodeElement = currentTreePendingNode.getElement();
            Pending currentNode = (Pending) currentTreePendingNodeElement;

            // Extract the examples, the attributes, the default classification, and the attribute to be matched,
            // for this pending node.
            Collection<State> examples = currentNode.getExamples();
            Collection<String> undecidedProperties = currentNode.getUndecidedProperties();
            OrdinalAttribute defaultAttribute = currentNode.getDefault();
            OrdinalAttribute matchingAttribute = currentNode.getAttributeValue();

            /*log.fine("Pending node corresponds to " + examples.size() + " examples.");*/
            /*log.fine("Pending node has " + undecidedProperties.size() + " undecided properties left.");*/

            // Used to hold the decision tree fragment (decision node, pending node or assignment leaf) that will
            // be inserted into the decision tree to replace the current pending node.
            DecisionTree newTreeFragment = null;

            // If the set of examples is empty then set the default as the leaf.
            if (examples.isEmpty())
            {
                /*log.fine("Examples is empty.");*/
                newTreeFragment = new DecisionTree(new Assignment(outputProperty, defaultAttribute, matchingAttribute));
            }

            // Else if undecided properties is empty then set the majority value of the classification of
            // examples as the leaf.
            else if (undecidedProperties.isEmpty())
            {
                /*log.fine("No undecided properties left.");*/

                // Work out what the majority classification is and create a leaf with that assignment.
                OrdinalAttribute majority = getMajorityClassification(outputProperty, inputExamples);

                newTreeFragment = new DecisionTree(new Assignment(outputProperty, majority, matchingAttribute));
            }

            // Else if all the examples have the same classification then set that classification as the leaf.
            else if (allHaveSameClassification(outputProperty, examples))
            {
                /*log.fine("All examples have the same classification.");*/
                newTreeFragment =
                    new DecisionTree(new Assignment(outputProperty, allClassification, matchingAttribute));
            }

            // Else choose the best attribute (with the largest estimated information gain) on the classification.
            else
            {
                /*log.fine("Choosing the best property to split on.");*/

                String bestProperty = chooseBestPropertyToDecideOn(outputProperty, examples, inputProperties);

                // Check if a best property could not be found, in which case behave as if there are no
                // input properties left to work with.
                if (bestProperty == null)
                {
                    /*log.fine("Couldn't find a best property to split on.");*/

                    // Put the pending node back onto the construction queue but with zero input properties.
                    Pending newPendingNode =
                        new Pending(examples, new ArrayList<String>(), defaultAttribute, matchingAttribute);

                    newTreeFragment = new DecisionTree(newPendingNode);
                    pendingNodes.offer(newTreeFragment);
                }
                else
                {
                    // Extract an attribute with this property name from the first example and use this to get
                    // a listing of all the possible values of that attribute.
                    Attribute bestAttribute =
                        (OrdinalAttribute) examples.iterator().next().getProperty(bestProperty);

                    /*log.fine("bestProperty = " + bestProperty);*/
                    /*log.fine("bestAttribute = " + bestAttribute);*/

                    // Create a decision node that decides on the best property. New pending nodes will be created
                    // as children of this node and added to it.
                    newTreeFragment =
                        new DecisionTree(new Decision(bestProperty, bestAttribute.getType().getNumPossibleValues(),
                                matchingAttribute));

                    // Produce a cut down input property set equal to the old one but with the selected best property
                    // removed as a decision node is to be created for it.
                    Set<String> newInputProperties = new HashSet<String>(inputProperties);

                    newInputProperties.remove(bestProperty);

                    // For each possible value of the best attribute.
                    for (Iterator<? extends Attribute> i = bestAttribute.getType().getAllPossibleValuesIterator();
                            i.hasNext();)
                    {
                        OrdinalAttribute nextAttribute = (OrdinalAttribute) i.next();

                        // Extract just those examples with the current value. To implement this efficiently first sort
                        // the examples by their attribute value for the best property to split on using a range sort
                        // (O(n) time as num possible values is limited and they are already indexed by their order).
                        // Create the new example collection as simply a pair of indexes into the sorted list.

                        Collection<State> matchingExamples = new ArrayList<State>();

                        for (State example : examples)
                        {
                            // Extract the attribute value for the property to decide on.
                            OrdinalAttribute testAttribute = (OrdinalAttribute) example.getProperty(bestProperty);

                            // Check if it matches the current value being extracted and add it to the collection of
                            // extracted examples if so.
                            if (nextAttribute.equals(testAttribute))
                            {
                                matchingExamples.add(example);
                            }
                        }

                        // Push onto the queue, a child node for this value, the subset of examples (or the value to
                        // select that subset on), attributes without the best one (or an exclusion list of attributes
                        // used so far), default majority-value of classification of these examples.
                        Pending newPendingNode =
                            new Pending(matchingExamples, newInputProperties,
                                getMajorityClassification(outputProperty, matchingExamples), nextAttribute);

                        DecisionTree newPendingNodeTreeFragment = new DecisionTree(newPendingNode);

                        pendingNodes.offer(newPendingNodeTreeFragment);

                        /*log.fine("Created new pending node below the split, for " + matchingExamples.size() +
                            " examples and " + newInputProperties.size() + " undecided properties.");*/

                        // Add the new pending node as a child of the parent decision node for the best property.
                        newTreeFragment.addChild(newPendingNodeTreeFragment);
                    }
                }
            }

            // Remove the current pending node from the decision tree and replace it with the new element that the
            // algorithm has selected.
            Tree.Node parentTree = (DecisionTree) currentTreePendingNode.getParent();

            // Check if the current pending node has a parent (if not it is the real tree root and the first pending
            // node created).
            if (parentTree != null)
            {
                // Remove the pending node from its parents child list.
                parentTree.getChildren().remove(currentTreePendingNode);

                // Replace the pending node with the newly built tree fragment in its parent child list.
                parentTree.addChild(newTreeFragment);
            }
            else
            {
                // Update the root tree pointer to point to the newly built tree fragment.
                decisionTree = newTreeFragment;
            }

            /*log.fine("There are now " + pendingNodes.size() + " pending nodes on the construction queue.");*/
        }

        // Loop over the whole decision tree initializing its quick lookup tables for all the decisions it contains.
        for (Iterator<SimpleTree<DecisionTreeElement>> i = decisionTree.iterator(Tree.IterationOrder.PreOrder);
                i.hasNext();)
        {
            DecisionTree nextTreeFragment = (DecisionTree) i.next();
            DecisionTreeElement nextElement = nextTreeFragment.getElement();

            // Check if it is a decision and build its lookup table if so.
            if (nextElement instanceof Decision)
            {
                Decision decision = (Decision) nextElement;

                decision.initializeLookups(nextTreeFragment);
            }
        }

        // Create a new decisition tree machine out of the fully constructed decision tree.
        ProtoDTMachine dtMachine = new ProtoDTMachine();

        dtMachine.setDecisionTree(decisionTree);

        // Return the trained decision tree classifying machine.
        return dtMachine;
    }

    /**
     * This helper method works out how the majority of the specified examples are classified by the named property. The
     * property should always be the goal property that the algorithm is learning and must always take on a finite
     * number of different values.
     *
     * @param  property The name of the property to find the majority classification for.
     * @param  examples A collection of state objects to count over in order to find the majority classification for the
     *                  named property.
     *
     * @return The value of the majority classification for the named property or null if the example collection was
     *         empty so no majority could be found.
     *
     * @throws LearningFailureException If the number of possible value that the specified property can take on is not
     *                                  finite.
     */
    private OrdinalAttribute getMajorityClassification(String property, Iterable<State> examples)
        throws LearningFailureException
    {
        /*log.fine("private OrdinalAttribute getMajorityClassification(String property, Collection<State> examples): called");*/
        /*log.fine("property = " + property);*/

        // Flag used to indicate that the map to hold the value counts in has been initialized.
        Map<OrdinalAttribute, Integer> countMap = null;

        // Used to hold the biggest count found so far.
        int biggestCount = 0;

        // Used to hold the value with the biggest count found so far.
        OrdinalAttribute biggestAttribute = null;

        // Loop over all the examples counting the number of occurences of each possible classification by the
        // named property.
        for (State example : examples)
        {
            OrdinalAttribute nextAttribute = (OrdinalAttribute) example.getProperty(property);
            /*log.fine("nextAttribute = " + nextAttribute);*/

            // If this is the first attribute then find out how many possible values it can take on.
            if (countMap == null)
            {
                // A check has already been performed at the start of the learning method to ensure that the output
                // property only takes on a finite number of values.
                countMap = new HashMap<OrdinalAttribute, Integer>();
            }

            int count;

            // Increment the count for the number of occurences of this classification.
            if (!countMap.containsKey(nextAttribute))
            {
                count = 1;
                countMap.put(nextAttribute, count);
            }
            else
            {
                count = countMap.get(nextAttribute);
                countMap.put(nextAttribute, count++);
            }

            // Compare it to the biggest score found so far to see if it is bigger.
            if (count > biggestCount)
            {
                // Update the biggest score.
                biggestCount = count;

                // Update the value of the majority classification.
                biggestAttribute = nextAttribute;
            }
        }

        // Return the majority classification found.
        return biggestAttribute;
    }

    /**
     * Tests if a property of a set of examples has the same value for all the examples. This algorithm works by
     * iterating through the examples until two different values are found or the end of the collection is reached
     * having found only one value.
     *
     * <p>As a side effect this method leaves the value of the classification in the member variable
     * {@link #allClassification} when all the examples do match.
     *
     * @param  property The name of the property to test.
     * @param  examples A collection of state objects to iterate over in order to test if they all have the same value
     *                  for the named property.
     *
     * @return true if they all have the same value; false otherwise.
     */
    private boolean allHaveSameClassification(String property, Iterable<State> examples)
    {
        // Used to hold the value of the first attribute seen.
        OrdinalAttribute firstAttribute = null;

        // Flag used to indicate that the test passed successfully.
        boolean success = true;

        // Loop over all the examples.
        for (State example : examples)
        {
            OrdinalAttribute nextAttribute = (OrdinalAttribute) example.getProperty(property);

            // If this is the first example just store its attribute value.
            if (firstAttribute == null)
            {
                firstAttribute = nextAttribute;
            }

            // Otherwise check if the attribute value does not match the first one in which case the test fails.
            else if (!nextAttribute.equals(firstAttribute))
            {
                success = false;

                break;
            }
        }

        // If the test passed then store the matching classification that all the examples have in a memeber variable
        // from where it can be accessed.
        if (success)
        {
            allClassification = firstAttribute;
        }

        return success;
    }

    /**
     * For a given set of examples, input properties and an output property this method chooses the input property that
     * provides the largest information gain on the value of the output property.
     *
     * @param  outputProperty  The name of the property to compare the information gain of the input properties against.
     * @param  examples        A collection of state objects to count over in order to find the highest information gain
     *                         input property.
     * @param  inputProperties A collection of input properties to find the best one amongst.
     *
     * @return The name of the input property with the highest information gain.
     */
    private String chooseBestPropertyToDecideOn(String outputProperty, Iterable<State> examples,
        Iterable<String> inputProperties)
    {
        /*log.fine("private String chooseBestPropertyToDecideOn(String outputProperty, Collection<State> examples, " +
         "Collection<String> inputProperties): called");*/

        // for (State e : examples) /*log.fine(e);*/

        // Determine how many possible values (symbols) the output property can have.
        int numOutputValues =
            examples.iterator().next().getComponentType().getPropertyType(outputProperty).getNumPossibleValues();

        // Used to hold the largest information gain found so far.
        double largestGain = 0.0d;

        // Used to hold the input property that gives the largest gain found so far.
        String largestGainProperty = null;

        // Loop over all the input properties.
        for (String inputProperty : inputProperties)
        {
            // let G = the set of goal property values.
            // let A = the set of property values that the input property can have.

            // Determine how many possible values (symbols) the input property can have.
            int numInputValues =
                examples.iterator().next().getComponentType().getPropertyType(inputProperty).getNumPossibleValues();

            // Create an array to hold the counts of the output symbols.
            int[] outputCounts = new int[numOutputValues];

            // Create arrays to hold the counts of the input symbols and the joint input/output counts.
            int[] inputCounts = new int[numInputValues];
            int[][] jointCounts = new int[numInputValues][numOutputValues];

            // Loop over all the examples.
            for (State example : examples)
            {
                // Extract the output property attribute value.
                OrdinalAttribute outputAttribute = (OrdinalAttribute) example.getProperty(outputProperty);

                // Extract the input property attribute value.
                OrdinalAttribute inputAttribute = (OrdinalAttribute) example.getProperty(inputProperty);

                // Increment the count for the occurence of this value of the output property.
                outputCounts[outputAttribute.ordinal()]++;

                // Increment the count for the occurence of this value of the input property.
                inputCounts[inputAttribute.ordinal()]++;

                // Increment the count for the joint occurrence of this input/output value pair.
                jointCounts[inputAttribute.ordinal()][outputAttribute.ordinal()]++;
            }

            // Calculate the estimated probability distribution of G from the occurrence counts over the examples.
            double[] pForG = InformationTheory.pForDistribution(outputCounts);

            // Calculate the estimated probability distribution of A from the occurrence counts over the examples.
            double[] pForA = InformationTheory.pForDistribution(inputCounts);

            // Calculate the estimated probability distribution p(g|a) from the joint occurrence counts over the
            // examples.
            double[][] pForGGivenA = InformationTheory.pForJointDistribution(jointCounts);

            // Calculate the information gain on G by knowing A.
            double gain = InformationTheory.gain(pForG, pForA, pForGGivenA);

            // Check if the gain is larger than the best found so far and update the best if so.
            if (gain > largestGain)
            {
                largestGain = gain;
                largestGainProperty = inputProperty;
            }
        }

        return largestGainProperty;
    }
}
