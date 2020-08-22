/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020-08-21
 *  Description: Implementation of WordNet Class
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordNet {
    private final ArrayList<String> synsets; // record id to synsets
    private final HashMap<String, List<Integer>> synsetIdMap; // record synsets map to ids
    private final SAP sapCalculator; // BFS calculator to calculate SAP

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException();
        // first build the relationship between ids and synsets
        this.synsets = new ArrayList<>();
        this.synsetIdMap = new HashMap<>();
        In in = new In(synsets);
        while (in.hasNextLine()) {
            String[] localInput = in.readLine().split(",");
            int lineId = Integer.parseInt(localInput[0]);
            String words = localInput[1];
            this.synsets.add(words);
            for (String word : words.split(" ")) {
                if (!synsetIdMap.containsKey(word)) synsetIdMap.put(word, new ArrayList<Integer>());
                synsetIdMap.get(word).add(lineId);
            }
        }
        // second, build all hypernyms relationship
        // record hypernyms relationship
        int notRootNum = 0;
        Digraph hypernymGraph = new Digraph(this.synsets.size());
        in = new In(hypernyms);
        while (in.hasNextLine()) {
            String[] localInput = in.readLine().split(",");
            if (localInput.length > 1) notRootNum++;
            int lineId = Integer.parseInt(localInput[0]);
            for (int i = 1; i < localInput.length; i++) {
                hypernymGraph.addEdge(lineId, Integer.parseInt(localInput[i]));
            }
        }
        // third, check if the graph is a rooted acyclic graph
        boolean hasOneRoot = this.synsets.size() - notRootNum == 1;
        DirectedCycle g = new DirectedCycle(hypernymGraph);
        boolean hasCircle = g.hasCycle();
        if (!hasOneRoot || hasCircle) throw new IllegalArgumentException();
        // initialize BFS calculator
        sapCalculator = new SAP(hypernymGraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return new ArrayList<String>(synsetIdMap.keySet());
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        return synsetIdMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        List<Integer> aIds = synsetIdMap.get(nounA);
        List<Integer> bIds = synsetIdMap.get(nounB);
        return sapCalculator.length(aIds, bIds);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        List<Integer> aIds = synsetIdMap.get(nounA);
        List<Integer> bIds = synsetIdMap.get(nounB);
        return synsets.get(sapCalculator.ancestor(aIds, bIds));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        /*
        WordNet testWordNet = new WordNet("synsets6.txt", "hypernyms6InvalidTwoRoots.txt");
        for (String s : testWordNet.nouns()) System.out.println(s);
         */
        WordNet testWordNet = new WordNet("synsets.txt", "hypernyms.txt");
        System.out.println(testWordNet.sap("worm", "bird"));
        System.out.println(testWordNet.distance("worm", "bird"));
        System.out.println(testWordNet.distance("Black_Plague", "black_marlin"));
    }
}
