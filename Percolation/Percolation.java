/* *****************************************************************************
 *  Name:              Mingxuan Wu
 *  Coursera User ID:  Mingxuan Wu
 *  Last modified:     7/15/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final boolean[][] sites; // record status for each grid
    private final WeightedQuickUnionUF percolationSet; // record connect status
    private final WeightedQuickUnionUF fullHelperSet; // UF helper for isFull
    private final int n; // record the side length of the grid
    private final int startPointIndex; // record the index for the "helper" starter point
    private final int endPointIndex; // record the index for the "helper" end point
    // left, right, down, up
    private final int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    private int openGridSize;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n should be larger than 0");
        }
        this.n = n;
        sites = new boolean[n][n];
        percolationSet = new WeightedQuickUnionUF(n * n + 2);
        fullHelperSet = new WeightedQuickUnionUF(n * n + 2);
        startPointIndex = n * n;
        endPointIndex = n * n + 1;
        openGridSize = 0;
    }

    // Helper function to map (row, col) to the index in UF
    private int mapRowColToUFIndex(int row, int col) {
        return (row - 1) * n + col - 1;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (!isValid(row, col)) {
            throw new IllegalArgumentException("row or col has been out of boundary");
        }
        if (isOpen(row, col)) {
            return;
        }
        // Step 1: change the grid to open in sites
        sites[row - 1][col - 1] = true;

        // Step 2: connect the grid to its neighbors
        int localUFIndex = mapRowColToUFIndex(row, col);
        for (int[] localDirection : directions) {
            int newRow = row + localDirection[0];
            int newCol = col + localDirection[1];
            int newUFIndex = mapRowColToUFIndex(newRow, newCol);
            if (isValid(newRow, newCol) && isOpen(newRow, newCol)) {
                percolationSet.union(localUFIndex, newUFIndex);
                fullHelperSet.union(localUFIndex, newUFIndex);
            }
        }

        // Step 3: if the grid is in first row, connect to startPoint
        if (row == 1) {
            percolationSet.union(localUFIndex, startPointIndex);
            fullHelperSet.union(localUFIndex, startPointIndex);
        }

        // Step 4: if the grid is in last row, connect to endPoint
        // We should not connect in fullHelperSet to avoid backwash!
        if (row == n) {
            percolationSet.union(localUFIndex, endPointIndex);
        }

        // Step 5: add one to the open grid counter
        openGridSize += 1;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (!isValid(row, col)) {
            throw new IllegalArgumentException("row or col has been out of boundary");
        }
        return sites[row - 1][col - 1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!isValid(row, col)) {
            throw new IllegalArgumentException("row or col has been out of boundary");
        }
        int localUfIndex = mapRowColToUFIndex(row, col);
        int localRoot = fullHelperSet.find(localUfIndex);
        int startPointRoot = fullHelperSet.find(startPointIndex);
        return isOpen(row, col) && (localRoot == startPointRoot);
    }

    // is (row, col) a valid input?
    // note: row, col starts from 1!!!
    private boolean isValid(int row, int col) {
        return row >= 1 && row <= n && col >= 1 && col <= n;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openGridSize;
    }

    // does the system percolate?
    public boolean percolates() {
        int startPointRoot = percolationSet.find(startPointIndex);
        int endPointRoot = percolationSet.find(endPointIndex);
        return startPointRoot == endPointRoot;
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation testPercolation = new Percolation(2);
        testPercolation.open(1, 1);
        testPercolation.open(1, 2);
        testPercolation.open(2, 1);
        System.out.println(testPercolation.percolates());
    }
}
