import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {

    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        int count = 1; //record how many elements we have visited
        RandomizedQueue<String> myRQ = new RandomizedQueue<>();
        while (!StdIn.isEmpty()) {
            String inputString = StdIn.readString();
            if (count <= k) {
                myRQ.enqueue(inputString);
            } else {
                //resovire sampling
                double persistProbability = StdRandom.uniform();
                if (persistProbability < (double) k / (double) count) {
                    myRQ.dequeue();
                    myRQ.enqueue(inputString);
                }
            }
            count += 1;
        }
        for (String s : myRQ) {
            System.out.println(s);
        }
    }
}
