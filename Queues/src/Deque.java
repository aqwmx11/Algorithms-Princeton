// Author: Mingxuan Wu
// Create Date: 2020/07/20
// Summary: doubly linked list implementation of deque

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private final dataNode Sentinel; // tricks to handle corner cases
    private int size; // record the number of nodes

    // nested class of a data node
    private class dataNode {
        Item value;
        dataNode prev;
        dataNode next;

        public dataNode(Item i, dataNode p, dataNode n) {
            value = i;
            prev = p;
            next = n;
        }
    }

    // construct an empty deque
    public Deque() {
        size = 0;
        Sentinel = new dataNode(null, null, null);
        Sentinel.prev = Sentinel;
        Sentinel.next = Sentinel;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("cannot add a node with value of null");
        }
        dataNode newNode = new dataNode(item, Sentinel, Sentinel.next);
        Sentinel.next.prev = newNode;
        Sentinel.next = newNode;
        size += 1;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("cannot add a node with value of null");
        }
        dataNode newNode = new dataNode(item, Sentinel.prev, Sentinel);
        Sentinel.prev.next = newNode;
        Sentinel.prev = newNode;
        size += 1;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot remove first from an empty deque");
        }
        Item removeValue = Sentinel.next.value;
        Sentinel.next.next.prev = Sentinel;
        Sentinel.next = Sentinel.next.next;
        size -= 1;
        return removeValue;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot remove last from an empty deque");
        }
        Item removeValue = Sentinel.prev.value;
        Sentinel.prev.prev.next = Sentinel;
        Sentinel.prev = Sentinel.prev.prev;
        size -= 1;
        return removeValue;
    }

    private class DequeIterator implements Iterator<Item> {
        private dataNode currentNode;

        public DequeIterator() {
            currentNode = Sentinel.next;
        }

        public boolean hasNext() {
            return currentNode != Sentinel;
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException("no more items to return!");
            }
            Item currentValue = currentNode.value;
            currentNode = currentNode.next;
            return currentValue;
        }

        public void remove() {
            throw new UnsupportedOperationException("this iterator does not support remove operation!");
        }
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> myDeque = new Deque<>();
        myDeque.addFirst(5);
        myDeque.addFirst(3);
        myDeque.addLast(6); // should be 3,5,6
        System.out.printf("Is my deque empty? %b%n", myDeque.isEmpty());
        System.out.printf("My deque now has %d elements.%n", myDeque.size());
        System.out.println("Print the elements of my deque now.");
        for (Integer i : myDeque) {
            System.out.println(i);
        }
        System.out.printf("Call remove first, expect 3, return %d.%n", myDeque.removeFirst());
        System.out.printf("Call remove last, expect 6, return %d.%n", myDeque.removeLast());
    }
}
