/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020/08/04
 *  Description: Solver class to handle 8puzzle problem
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private final boolean isSolvableFinal;
    private final int moves;
    private final Stack<Board> solution;

    private class SearchNode implements Comparable<SearchNode> {
        private final Board currentBoard;
        private final int moves;
        private final int manhattan;
        private final SearchNode prevNode;
        private final int priority;

        public SearchNode(Board board, SearchNode prevNode, int moves) {
            this.moves = moves;
            this.currentBoard = board;
            this.prevNode = prevNode;
            this.manhattan = board.manhattan();
            this.priority = this.manhattan + this.moves;
        }

        public int getMoves() {
            return moves;
        }

        public Board getBoard() {
            return currentBoard;
        }

        public SearchNode getPrevNode() {
            return prevNode;
        }

        public int compareTo(SearchNode that) {
            if (this.priority < that.priority) return -1;
            else if (this.priority > that.priority) return 1;
            else return Integer.compare(this.manhattan, that.manhattan);
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException("Cannot initiate from a null board.");

        SearchNode initialSearchNode = new SearchNode(initial, null, 0);
        SearchNode twinInitialSearchNode = new SearchNode(initial.twin(), null, 0);
        MinPQ<SearchNode> pq = new MinPQ<>();
        pq.insert(initialSearchNode);
        MinPQ<SearchNode> twinPq = new MinPQ<>();
        twinPq.insert(twinInitialSearchNode);

        boolean isSolvable = false;
        SearchNode finalNode = null;

        while (!pq.isEmpty() && !twinPq.isEmpty()) {
            SearchNode tempSearchNode = pq.delMin();
            if (tempSearchNode.getBoard().isGoal()) {
                isSolvable = true;
                finalNode = tempSearchNode;
                break;
            }
            else {
                for (Board neighbor : tempSearchNode.getBoard().neighbors()) {
                    if (tempSearchNode.getPrevNode() == null || !neighbor
                            .equals(tempSearchNode.getPrevNode().getBoard()))
                        pq.insert(new SearchNode(neighbor, tempSearchNode,
                                                 tempSearchNode.getMoves() + 1));
                }
            }

            SearchNode twinTempSearchNode = twinPq.delMin();
            if (twinTempSearchNode.getBoard().isGoal()) {
                break;
            }
            else {
                for (Board neighbor : twinTempSearchNode.getBoard().neighbors()) {
                    if (twinTempSearchNode.getPrevNode() == null || !neighbor
                            .equals(twinTempSearchNode.getPrevNode().getBoard()))
                        twinPq.insert(new SearchNode(neighbor, twinTempSearchNode,
                                                     twinTempSearchNode.getMoves() + 1));
                }
            }
        }
        isSolvableFinal = isSolvable;
        if (isSolvableFinal) {
            moves = finalNode.getMoves();
            solution = new Stack<Board>();
            while (finalNode != null) {
                solution.push(finalNode.getBoard());
                finalNode = finalNode.getPrevNode();
            }
        }
        else {
            moves = -1;
            solution = null;
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvableFinal;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return solution;
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
