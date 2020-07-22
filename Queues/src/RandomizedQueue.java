import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private static final int INITIALSIZE = 8;
    private Item[] myArray;
    private int size; // record the number of elements

    // construct an empty randomized queue
    public RandomizedQueue() {
        myArray = (Item[]) new Object[INITIALSIZE];
        size = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    private void resize(int newLength) {
        Item[] newArray = (Item[]) new Object[newLength];
        System.arraycopy(myArray, 0, newArray, 0, size);
        myArray = newArray;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot enqueue a null");
        }
        if (size == myArray.length) {
            resize(size * 2);
        }
        myArray[size++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot dequeue when the queue is empty");
        }
        int removeIndex = StdRandom.uniform(0, size);
        Item removeValue = myArray[removeIndex];
        myArray[removeIndex] = myArray[size - 1];
        myArray[--size] = null;
        if (myArray.length >= 16 && size < (myArray.length / 4)) {
            resize(myArray.length / 2);
        }
        return removeValue;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot dequeue when the queue is empty");
        }
        int randomIndex = StdRandom.uniform(0, size);
        return myArray[randomIndex];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedDequeIterator();
    }

    private class RandomizedDequeIterator implements Iterator<Item> {
        private int count; // record how many items we have visited
        private final int[] shuffledIndex; // record the shuffled index

        public RandomizedDequeIterator() {
            count = 0;
            shuffledIndex = StdRandom.permutation(size);
        }

        public boolean hasNext() {
            return count < size;
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException("no element to return next");
            }
            Item returnValue = myArray[shuffledIndex[count]];
            count += 1;
            return returnValue;
        }

        public void remove() {
            throw new UnsupportedOperationException("This iterator does not support remove operation");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Double> testRQ = new RandomizedQueue<>();
        System.out.printf("Is my RQ empty? %b%n", testRQ.isEmpty());
        for (int i = 0; i < 10; i++) {
            testRQ.enqueue(i + 0.5);
        }
        for (Double d : testRQ) {
            System.out.println(d);
        }
        System.out.printf("Is my RQ empty? %b%n", testRQ.isEmpty());
        System.out.printf("The size of my RQ is %d now.%n", testRQ.size());
        System.out.println(testRQ.sample());
        System.out.printf("The size of my RQ is %d now.%n", testRQ.size());
        System.out.println(testRQ.dequeue());
        for (Double d : testRQ) {
            System.out.println(d);
        }
        System.out.printf("The size of my RQ is %d now.%n", testRQ.size());
    }
}
