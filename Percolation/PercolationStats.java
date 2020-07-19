/* *****************************************************************************
 *  Name:              Mingxuan Wu
 *  Coursera User ID:  Mingxuan Wu
 *  Last modified:     07/18/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private static final double NORMALSTD = 1.96;
    private final double[] x; // xt is the fraction of open sitesin computational experiment t
    private final double mean; // record mean after trials
    private final double stddev; // record standrad deviation after trials
    private final int trials;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("n and trials should be positive!");
        }

        x = new double[trials];
        this.trials = trials;
        for (int i = 0; i < trials; i++) {
            Percolation localPercolation = new Percolation(n);
            while (!localPercolation.percolates()) {
                int randomRow = StdRandom.uniform(1, n + 1);
                int randomCol = StdRandom.uniform(1, n + 1);
                localPercolation.open(randomRow, randomCol);
            }
            x[i] = (double) localPercolation.numberOfOpenSites() / (double) (n * n);
        }
        mean = StdStats.mean(x);
        stddev = StdStats.stddev(x);
    }

    // sample mean of percolation threshold
    public double mean() {
        return this.mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return this.stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        double confidenceLow = mean - NORMALSTD * stddev / Math.sqrt(trials);
        return confidenceLow;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        double confidenceHigh = mean + NORMALSTD * stddev / Math.sqrt(trials);
        return confidenceHigh;
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats localPercolationStats = new PercolationStats(n, trials);
        System.out.printf("mean=%f\n", localPercolationStats.mean);
        System.out.printf("stddev=%f\n", localPercolationStats.stddev);
        System.out.printf("95%% confidence interval = [%f,%f]\n",
                          localPercolationStats.confidenceLo(),
                          localPercolationStats.confidenceHi());
    }
}
