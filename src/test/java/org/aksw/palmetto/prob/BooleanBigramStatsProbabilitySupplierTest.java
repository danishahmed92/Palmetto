package org.aksw.palmetto.prob;

import java.util.Arrays;
import java.util.Collection;

import org.aksw.palmetto.corpus.BooleanBigramStatsSupportingAdapter;
import org.aksw.palmetto.subsets.SubsetDefinition;
import org.aksw.palmetto.subsets.SubsetProbabilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.carrotsearch.hppc.BitSet;

@RunWith(Parameterized.class)
public class BooleanBigramStatsProbabilitySupplierTest implements BooleanBigramStatsSupportingAdapter {

    private static final double DOUBLE_PRECISION_DELTA = 0.00000001;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays
                .asList(new Object[][] {
                        /*
                         * cooccurence matrix
                         * 
                         * ----- 0 1 2
                         * 
                         * word0 5 2 3
                         * 
                         * word1 2 2 1
                         * 
                         * word2 3 1 6
                         */

                        {
                                new int[][] { { 5, 2, 3 }, { 2, 2, 1 }, { 3, 1, 6 } },
                                1, 6.0, 13.0,
                                new double[] { 0, 5.0 / 13.0, 2.0 / 13.0, 2.0 / 6.0, 6.0 / 13.0, 3.0 / 6.0, 1.0 / 6.0,
                                        0 } },
                        /*
                         * with minFreq = 2
                         * 
                         * ----- 0 1 2
                         * 
                         * word0 5 2 3
                         * 
                         * word1 2 2 1
                         * 
                         * word2 3 1 6
                         */

                        { new int[][] { { 5, 2, 3 }, { 2, 2, 1 }, { 3, 1, 6 } }, 2, 6.0, 13.0,
                                new double[] { 0, 5.0 / 13.0, 2.0 / 13.0, 2.0 / 6.0, 6.0 / 13.0, 3.0 / 6.0, 0, 0 } },
                        /*
                         * with minFreq = 3
                         * 
                         * ----- 0 1 2
                         * 
                         * word0 5 2 3
                         * 
                         * word1 2 2 1
                         * 
                         * word2 3 1 6
                         */

                        { new int[][] { { 5, 2, 3 }, { 2, 2, 1 }, { 3, 1, 6 } }, 3, 6.0, 13.0,
                                new double[] { 0, 5.0 / 13.0, 0, 0, 6.0 / 13.0, 3.0 / 6.0, 0, 0 } },

                        /*
                         * 
                         * ----- 0 1 2
                         * 
                         * word0 1 0 0
                         * 
                         * word1 0 1 1
                         * 
                         * word2 0 1 1
                         */

                        { new int[][] { { 1, 0, 0 }, { 0, 1, 1 }, { 0, 1, 1 } }, 1, 1.0, 3.0,
                                new double[] { 0, 1.0 / 3.0, 1.0 / 3.0, 0, 1.0 / 3.0, 0, 1.0, 0 } },
                });
    }

    private int wordCooccurences[][];
    private int minFrequency;
    private double expectedProbabilities[];
    private double numberOfCooccurenceCounts;
    private double numberOfWordCounts;

    public BooleanBigramStatsProbabilitySupplierTest(int[][] wordCooccurences, int minFrequency,
            double numberOfCooccurenceCounts, double numberOfWordCounts, double[] expectedProbabilities) {
        this.wordCooccurences = wordCooccurences;
        this.minFrequency = minFrequency;
        this.expectedProbabilities = expectedProbabilities;
        this.numberOfCooccurenceCounts = numberOfCooccurenceCounts;
        this.numberOfWordCounts = numberOfWordCounts;
    }

    @Test
    public void test() {
        String words[] = new String[wordCooccurences.length];
        for (int i = 0; i < words.length; i++) {
            words[i] = Integer.toString(i);
        }

        BooleanBigramStatsProbabilitySupplier probSupplier = BooleanBigramStatsProbabilitySupplier.create(this);
        probSupplier.setMinFrequency(minFrequency);
        SubsetProbabilities subsetProbs[] = probSupplier
                .getProbabilities(new String[][] { words }, new SubsetDefinition[] { new SubsetDefinition(null, null,
                        new BitSet(expectedProbabilities.length - 1)) });

        double probabilities[] = subsetProbs[0].probabilities;
        Assert.assertArrayEquals(expectedProbabilities, probabilities, DOUBLE_PRECISION_DELTA);
    }

    @Override
    public int getCount(String word1) {
        int id = Integer.parseInt(word1);
        return wordCooccurences[id][id];
    }

    @Override
    public double getNumberOfWords() {
        return numberOfWordCounts;
    }

    @Override
    public int getCooccurenceCount(String word1, String word2) {
        int id1 = Integer.parseInt(word1);
        int id2 = Integer.parseInt(word2);
        return wordCooccurences[id1][id2];
    }

    @Override
    public double getNumberOfCooccurences() {
        return numberOfCooccurenceCounts;
    }
}
