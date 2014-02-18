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
package com.thesett.aima.math;

/**
 * InformationTheory is a math class that provides mathematical functions that relate to Shannon's information theory.
 *
 * <p>The {@link #expectedI}, {@link #remainder} and {@link #gain} functions all depend on knowing the probabilities of
 * the occurence of symbols. These may be derived analytically for some situations. In others they will be estimated by
 * gathering statistics. This class also provides some methods for estimating these probabilities from statistics.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Calculate expected information from a probability distribution.
 * <tr><td> Calculate remaining information to know one distribution from another.
 * <tr><td> Calaulate the gain in knowledge of one distribution from another.
 * <tr><td> Estimate a discrete probability distribution from a sample count.
 * <tr><td> Estimate a joint discrete probability distribution from a correlating sample count.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class InformationTheory
{
    /** Used to convert between nats and bits. This is equal to the natural logarithm of 2. */
    public static final double LN2 = Math.log(2.0d);

    /**
     * For a given set of probabilities of the occurences of symbols this function calculates the expected information
     * content of a set of symbols given its probability distribution. The answer is expressed as a positive number of
     * bits.
     *
     * <p>The probabilities should add up to one. This method does not provide any kind of check on this condition.
     *
     * @param  probabilities The probability distribution of the symbols. The probabilities should add to one.
     *
     * @return The expected information content, in bits, learned from being told a symbol drawn from the distribution.
     */
    public static double expectedI(double[] probabilities)
    {
        double result = 0.0d;

        // Loop over the probabilities for all the symbols calculating the contribution of each to the expected value.
        for (double p : probabilities)
        {
            // Calculate the information in each symbol. I(p) = - ln p and weight this by its probability of occurence
            // to get that symbols contribution to the expected value over all symbols.
            if (p > 0.0d)
            {
                result -= p * Math.log(p);
            }
        }

        // Convert the result from nats to bits by dividing by ln 2.
        return result / LN2;
    }

    /**
     * Supposing a stream generates pairs of symbols. Let the first be A and the second be G. A ranges over a set of
     * symbols, {h1, ... , hv} and G ranges over a set of symbols, {g1, ..., gn}.
     *
     * <p>G alone has a probability distribution which tells us what the expected information content of knowing what G
     * is, measured in bits.
     *
     * <p>This remainder function estimates how much more information is needed, if we know A, in order to know G.
     *
     * <p>If A and G are completely independent then this will be equal to the expected information content of G. In
     * other words A tells us nothing about G. If G is completely dependant on A then this will be equal to zero. In
     * other words as we know G already from knowing A, learning what G is will tell us nothing extra.
     *
     * <p>Remainder(A|G) = Sum i from 1 to v {P(hi) * expectedInformation(P(g1|hi), ..., P(gn|hi)}.
     *
     * <p>Where: p(g|h) = the probability of symbol g (of G) occuring given symbol h (of A).
     *
     * @param  pA       The probability distribution of the symbols of A. This will be an array of size v.
     * @param  pGgivenA The probability distribution of the symbols of G given a particular symbol of A. This will be an
     *                  array of size n by v (v arrays of size n, array is indexed as [v][n]).
     *
     * @return The expected remaining information needed to know G from knowing A.
     */
    public static double remainder(double[] pA, double[][] pGgivenA)
    {
        double result = 0.0d;

        // Loop over the probabilities for all the symbols of A calculating the contribution of each to
        // the expected value.
        for (int v = 0; v < pA.length; v++)
        {
            double phv = pA[v];

            // Calculate the expected information content of the distribution of the symbols of G given the symbol hv
            // and scale its contribution to the total by the probability of hv occuring.
            result += phv * expectedI(pGgivenA[v]);
        }

        // There is no need to convert the result from nats to bits and the expected information function is
        // already in bits.
        return result;
    }

    /**
     * Supposing a stream generates pairs of symbols. Let the first be A and the second be G. A ranges over a set of
     * symbols, {h1, ... , hv} and G ranges over a set of symbols, {g1, ..., gn}.
     *
     * <p>G alone has a probability distribution which tells us what the expected information content of knowing what G
     * is, measured in bits.
     *
     * <p>This gain function estimates how much information is gained, if we know A, about G.
     *
     * <p>Gain(A|G) = ExpectedI(G) - Remainder(A|G)
     *
     * @param  pG       The probability distribution of the symbols of G. This will be an array of size n.
     * @param  pA       The probability distribution of the symbols of A. This will be an array of size v.
     * @param  pGgivenA The probability distribution of the symbols of G given a particular symbol of A. This will be an
     *                  array of size n by v (v arrays of size n, array is indexed as [v][n]).
     *
     * @return the expected amount of information on G given by knowing A.
     */
    public static double gain(double[] pG, double[] pA, double[][] pGgivenA)
    {
        return expectedI(pG) - remainder(pA, pGgivenA);
    }

    /**
     * Estimates probabilities given a set of counts of occurrences of symbols.
     *
     * <p>P = number of times a symbol occurs/total number of symbols.
     *
     * @param  counts The counts of the occurrences of symbols over a data set of symbols.
     *
     * @return An array of the probability estimates for the occurences of the symbols. The index of the elements in
     *         this array corresponds with the index of the symbol in the input array.
     */
    public static double[] pForDistribution(int[] counts)
    {
        double[] probabilities = new double[counts.length];

        int total = 0;

        // Loop over the counts for all symbols adding up the total number.
        for (int c : counts)
        {
            total += c;
        }

        // Loop over the counts for all symbols dividing by the total number to provide a probability estimate.
        for (int i = 0; i < probabilities.length; i++)
        {
            if (total > 0)
            {
                probabilities[i] = ((double) counts[i]) / total;
            }
            else
            {
                probabilities[i] = 0.0d;
            }
        }

        return probabilities;
    }

    /**
     * Supposing a stream generates pairs of symbols. Let the first be A and the second be G. A ranges over a set of
     * symbols, {h1, ... , hv} and G ranges over a set of symbols, {g1, ..., gn}.
     *
     * <p>This function estimates p(g|h), the probability of a symbol g (of G) occuring given a symbol h (of A), over
     * all the symbols g and h from statistics gathered about the frequency of occurence of these symbols.
     *
     * @param  counts the counts of the occurence of the symbols of G given a particular symbol of A. This will be an
     *                array of size n by v (v arrays of size n, array is indexed as [v][n]).
     *
     * @return an estimate of the probability distribution of the symbols of G given a particular symbol of A. This will
     *         be an array of size n by v (v arrays of size n, array is indexed as [v][n]).
     */
    public static double[][] pForJointDistribution(int[][] counts)
    {
        double[][] results = new double[counts.length][];

        // Loop over all the symbols of A
        for (int i = 0; i < counts.length; i++)
        {
            // Extract the next distribution array of the symbols of G given A.
            int[] countsGgivenA = counts[i];

            // Convert the frequency distribution into a probability distribution and add it to the results.
            results[i] = pForDistribution(countsGgivenA);
        }

        return results;
    }
}
