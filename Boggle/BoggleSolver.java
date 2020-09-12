/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020/09/10
 *  Description: Implementation of boggle solver - find max scores in a board
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private static final int[][] DIRECTIONS = {
            { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 },
            { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 }
    };

    private final TrieSTWithCache cachedTST;
    private BoggleBoard board;
    private int rows, cols;
    private boolean[][] marked;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        cachedTST = new TrieSTWithCache();
        for (String s : dictionary) cachedTST.put(s, 1);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) throw new IllegalArgumentException();
        this.board = board;
        rows = board.rows();
        cols = board.cols();
        marked = new boolean[rows][cols];
        SET<String> words = new SET<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                refresh();
                collect(i, j, new StringBuilder(), words);
            }
        }
        return words;
    }

    private boolean isValidRow(int row) {
        return row >= 0 && row < rows;
    }

    private boolean isValidCol(int col) {
        return col >= 0 && col < cols;
    }

    private void refresh() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++)
                marked[i][j] = false;
        }
    }

    private void collect(int row, int col, StringBuilder currentWord, SET<String> words) {
        marked[row][col] = true;
        char newChar = board.getLetter(row, col);
        currentWord.append(newChar == 'Q' ? "QU" : newChar);
        // determine if we can add currentWord to words
        String currentWordStr = currentWord.toString();
        if (currentWordStr.length() >= 3 && cachedTST.contains(currentWordStr))
            words.add(currentWordStr);
        // determine if we need to continue searching
        if (cachedTST.keysWithPrefix(currentWordStr)) {
            for (int[] direction : DIRECTIONS) {
                int newRow = row + direction[0];
                int newCol = col + direction[1];
                if (isValidRow(newRow) && isValidCol(newCol) && !marked[newRow][newCol])
                    collect(newRow, newCol, new StringBuilder(currentWord), words);
            }
        }
        marked[row][col] = false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) throw new IllegalArgumentException();
        if (!cachedTST.contains(word)) return 0;
        int wordLength = word.length();
        if (wordLength < 3) return 0;
        if (wordLength <= 4) return 1;
        if (wordLength == 5) return 2;
        if (wordLength == 6) return 3;
        if (wordLength == 7) return 5;
        return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
