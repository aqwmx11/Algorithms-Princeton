/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020-08-20
 *  Description: Implementation of SAP class
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.List;

public class SAP {
    private final Digraph G;
    private int[] lastPoints; // cache most recent input vertices, from smaller to larger
    private int[] lastRes; // cache most recent bfs result, length and ancestor
    private Stack<Integer> lastVisited; // vertices visited in last bfs
    private boolean[] markedV; // vertices visited by v
    private boolean[] markedW; // vertices visited by w
    private int[] distToV; // distance from V
    private int[] distToW; // distance from W

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException();
        this.G = new Digraph(G);
        lastPoints = new int[] { Integer.MIN_VALUE, Integer.MIN_VALUE };
        lastRes = new int[] { -1, -1 };
        lastVisited = new Stack<>();
        markedV = new boolean[G.V()];
        markedW = new boolean[G.V()];
        distToV = new int[G.V()];
        distToW = new int[G.V()];
        for (int v = 0; v < G.V(); v++) {
            distToV[v] = Integer.MAX_VALUE;
            distToW[v] = Integer.MAX_VALUE;
        }
    }

    // refresh auxiliary arrays from last BFS
    private void refreshAuxiliaryArrays() {
        while (!lastVisited.isEmpty()) {
            int visitedVertex = lastVisited.pop();
            markedV[visitedVertex] = false;
            markedW[visitedVertex] = false;
            distToV[visitedVertex] = Integer.MAX_VALUE;
            distToW[visitedVertex] = Integer.MAX_VALUE;
        }
    }

    // helper function to check if the input is valid for a single vertex
    private void validate(int v) {
        if (v < 0 || v >= G.V()) throw new IllegalArgumentException();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validate(v);
        validate(w);
        // try to read from cache
        if (lastPoints[0] == v && lastPoints[1] == w) return lastRes[0];
        if (lastPoints[0] == w && lastPoints[1] == v) return lastRes[0];
        // if we cannot find in cache, calculate using lockstep BFS
        // first refresh visited points in last BFS
        refreshAuxiliaryArrays();
        // then start BFS
        int length = bfs(v, w)[0];
        return length;
    }

    // update most recent result to cache
    private void updateCache(int v, int w, int bestDistance, int bestAncestor) {
        lastPoints[0] = v < w ? v : w;
        lastPoints[1] = v < w ? w : v;
        lastRes[0] = bestDistance == Integer.MAX_VALUE ? -1 : bestDistance;
        lastRes[1] = bestAncestor;
    }

    // implementation of lockstep bfs
    private int[] bfs(int v, int w) {
        int bestDistance = Integer.MAX_VALUE;
        int bestAncestor = -1;
        Queue<Integer> qV = new Queue<Integer>();
        Queue<Integer> qW = new Queue<Integer>();
        qV.enqueue(v);
        qW.enqueue(w);
        markedV[v] = true;
        distToV[v] = 0;
        lastVisited.push(v);
        markedW[w] = true;
        distToW[w] = 0;
        lastVisited.push(w);
        while (!qV.isEmpty() || !qW.isEmpty()) {
            if (!qV.isEmpty()) {
                int localV = qV.dequeue();
                // check if this point has been visited by w
                if (markedW[localV]) {
                    int localDistance = distToV[localV] + distToW[localV];
                    if (localDistance < bestDistance) {
                        bestAncestor = localV;
                        bestDistance = localDistance;
                    }
                }
                // check if it makes sense to continue searching
                // if distance of this point is already larger than bestDistance, then no need to search
                if (distToV[localV] < bestDistance) {
                    for (int localNeighbor : G.adj(localV)) {
                        if (!markedV[localNeighbor]) {
                            distToV[localNeighbor] = distToV[localV] + 1;
                            markedV[localNeighbor] = true;
                            qV.enqueue(localNeighbor);
                            lastVisited.push(localNeighbor);
                        }
                    }
                }
            }
            if (!qW.isEmpty()) {
                int localW = qW.dequeue();
                // check if this point has been visited by v
                if (markedV[localW]) {
                    int localDistance = distToV[localW] + distToW[localW];
                    if (localDistance < bestDistance) {
                        bestAncestor = localW;
                        bestDistance = localDistance;
                    }
                }
                // check if it makes sense to continue searching
                // if distance of this point is already larger than bestDistance, then no need to search
                if (distToW[localW] < bestDistance) {
                    for (int localNeighbor : G.adj(localW)) {
                        if (!markedW[localNeighbor]) {
                            distToW[localNeighbor] = distToW[localW] + 1;
                            markedW[localNeighbor] = true;
                            qW.enqueue(localNeighbor);
                            lastVisited.push(localNeighbor);
                        }
                    }
                }
            }
        }
        updateCache(v, w, bestDistance, bestAncestor);
        return lastRes;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validate(v);
        validate(w);
        // try to read from cache
        if (lastPoints[0] == v && lastPoints[1] == w) return lastRes[1];
        if (lastPoints[0] == w && lastPoints[1] == v) return lastRes[1];
        // if we cannot find in cache, calculate using lockstep BFS
        // first refresh visited points in last BFS
        refreshAuxiliaryArrays();
        // then start BFS
        int ancestor = bfs(v, w)[1];
        return ancestor;
    }

    // implementation of lockstep bfs
    private int[] bfs(Iterable<Integer> v, Iterable<Integer> w) {
        int bestDistance = Integer.MAX_VALUE;
        int bestAncestor = -1;
        Queue<Integer> qV = new Queue<Integer>();
        Queue<Integer> qW = new Queue<Integer>();
        for (int s : v) {
            qV.enqueue(s);
            markedV[s] = true;
            distToV[s] = 0;
            lastVisited.push(s);
        }
        for (int s : w) {
            qW.enqueue(s);
            markedW[s] = true;
            distToW[s] = 0;
            lastVisited.push(s);
        }
        while (!qV.isEmpty() || !qW.isEmpty()) {
            if (!qV.isEmpty()) {
                int localV = qV.dequeue();
                // check if this point has been visited by w
                if (markedW[localV]) {
                    int localDistance = distToV[localV] + distToW[localV];
                    if (localDistance < bestDistance) {
                        bestAncestor = localV;
                        bestDistance = localDistance;
                    }
                }
                // check if it makes sense to continue searching
                // if distance of this point is already larger than bestDistance, then no need to search
                if (distToV[localV] < bestDistance) {
                    for (int localNeighbor : G.adj(localV)) {
                        if (!markedV[localNeighbor]) {
                            distToV[localNeighbor] = distToV[localV] + 1;
                            markedV[localNeighbor] = true;
                            qV.enqueue(localNeighbor);
                            lastVisited.push(localNeighbor);
                        }
                    }
                }
            }
            if (!qW.isEmpty()) {
                int localW = qW.dequeue();
                // check if this point has been visited by v
                if (markedV[localW]) {
                    int localDistance = distToV[localW] + distToW[localW];
                    if (localDistance < bestDistance) {
                        bestAncestor = localW;
                        bestDistance = localDistance;
                    }
                }
                // check if it makes sense to continue searching
                // if distance of this point is already larger than bestDistance, then no need to search
                if (distToW[localW] < bestDistance) {
                    for (int localNeighbor : G.adj(localW)) {
                        if (!markedW[localNeighbor]) {
                            distToW[localNeighbor] = distToW[localW] + 1;
                            markedW[localNeighbor] = true;
                            qW.enqueue(localNeighbor);
                            lastVisited.push(localNeighbor);
                        }
                    }
                }
            }
        }
        // TODO: add cache here
        // updateCache(v, w, bestDistance, bestAncestor);
        bestDistance = bestDistance == Integer.MAX_VALUE ? -1 : bestDistance;
        int[] output = new int[] { bestDistance, bestAncestor };
        return output;
    }

    // helper function to check if the input is valid for vertices
    private void validate(Iterable<Integer> v) {
        for (Integer i : v) {
            if (i == null) throw new IllegalArgumentException();
            validate(i);
        }
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();
        validate(v);
        validate(w);
        // TODO: add cache here
        // first refresh visited points in last BFS
        refreshAuxiliaryArrays();
        // then start BFS
        int length = bfs(v, w)[0];
        return length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();
        validate(v);
        validate(w);
        // TODO: add cache here
        // first refresh visited points in last BFS
        refreshAuxiliaryArrays();
        // then start BFS
        int ancestor = bfs(v, w)[1];
        return ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        /*
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
         */

        In in = new In("diagraph_wmx.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        Integer[] a = new Integer[] { 13, 23, 24 };
        // Integer[] a = new Integer[] { };
        List<Integer> aList = Arrays.asList(a);
        Integer[] b = new Integer[] { 6, 16, 17 };
        // Integer[] b = new Integer[] { };
        List<Integer> bList = Arrays.asList(b);
        int length = sap.length(aList, bList);
        int ancestor = sap.ancestor(aList, bList);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);

    }
}
