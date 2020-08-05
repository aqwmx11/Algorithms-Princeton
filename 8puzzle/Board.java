/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020/08/03
 *  Description: Board class
 **************************************************************************** */

import java.util.ArrayList;
import java.util.Arrays;

public class Board {
    private final int[][] tiles;
    private final int n;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.tiles = new int[tiles.length][tiles.length];
        this.n = tiles.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.tiles[i][j] = tiles[i][j];
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int hamming = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != i * n + j + 1) hamming++;
            }
        }
        // note that tiles[n-1][n-1] will always be wrong, but should not be included
        hamming--;
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manhattan = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) continue;
                int correctRow = (tiles[i][j] - 1) / n;
                int correctCol = (tiles[i][j] - 1) % n;
                manhattan += Math.abs(correctRow - i) + Math.abs(correctCol - j);
            }
        }
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == n - 1 && j == n - 1) continue;
                if (i * n + j + 1 != tiles[i][j]) return false;
            }
        }
        return true;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        return Arrays.deepEquals(tiles, that.tiles);
    }

    // exchange (i1, j1) with (i2, j2)
    private void exchange(int i1, int j1, int i2, int j2) {
        int temp = tiles[i1][j1];
        tiles[i1][j1] = tiles[i2][j2];
        tiles[i2][j2] = temp;
    }

    // is (i,j) in the tile?
    private boolean isValid(int i, int j) {
        return i >= 0 && i < n && j >= 0 && j < n;
    }

    private int[] findZero() {
        int[] zeroPos = new int[2];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    zeroPos[0] = i;
                    zeroPos[1] = j;
                    return zeroPos;
                }
            }
        }
        return zeroPos;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        ArrayList<Board> neighbors = new ArrayList<>();
        // find (row, col) for 0
        int[] zeroPos = findZero();
        for (int[] direction : directions) {
            int newRow = zeroPos[0] + direction[0];
            int newCol = zeroPos[1] + direction[1];
            if (isValid(newRow, newCol)) {
                Board tempBoard = new Board(tiles);
                tempBoard.exchange(zeroPos[0], zeroPos[1], newRow, newCol);
                neighbors.add(tempBoard);
            }
        }
        return neighbors;
    }

    private int[] findTwoTiles() {
        int[] twoPairs = new int[4];
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != 0) {
                    twoPairs[count * 2] = i;
                    twoPairs[count * 2 + 1] = j;
                    count++;
                }
                if (count == 2) return twoPairs;
            }
        }
        return twoPairs;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[] twoPairs = findTwoTiles();
        Board twin = new Board(tiles);
        twin.exchange(twoPairs[0], twoPairs[1], twoPairs[2], twoPairs[3]);
        return twin;
    }

    public static void main(String[] args) {
        int[][] testTiles = { { 8, 1, 3 }, { 4, 0, 2 }, { 7, 6, 5 } };
        Board testBoard = new Board(testTiles);
        System.out.println(testBoard);
        System.out.printf("The dimension of the board is: %d.%n", testBoard.dimension());
        System.out.printf("The hamming of the board is: %d.%n", testBoard.hamming());
        System.out.printf("The manhattan distance of the board is: %d.%n", testBoard.manhattan());
        System.out.printf("Is this board goal board? %b.%n", testBoard.isGoal());
        int[][] testTiles2 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 } };
        Board testBoard2 = new Board(testTiles2);
        System.out.printf("Is this board goal board? %b.%n", testBoard2.isGoal());
        System.out.printf("Does the two boards equal? %b.%n", testBoard.equals(testBoard2));
        System.out.println("The neighbors of the board is:");
        for (Board neighbor : testBoard.neighbors()) {
            System.out.println(neighbor);
        }
        System.out.println("The twin of the board is:");
        System.out.println(testBoard.twin());
    }
}
