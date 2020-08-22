/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020-08-22
 *  Description: Implementation of OutCast: find least related words
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null) throw new IllegalArgumentException();
        int maxDistance = -1;
        String leastRelatedWord = null;
        for (String word : nouns) {
            int localSum = 0;
            for (String s : nouns) localSum += wordNet.distance(word, s);
            if (localSum > maxDistance) {
                maxDistance = localSum;
                leastRelatedWord = word;
            }
        }
        return leastRelatedWord;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
