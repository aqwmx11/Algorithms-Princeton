/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020-08-26
 *  Description: Implementation of seam carving technique
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

import java.util.ArrayList;

public class SeamCarver {
    private static final int BOARDER_ENERGY = 1000;
    private int[][] pictureRGB;
    private final double[][] energy;
    private int width;
    private int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
        width = picture.width();
        height = picture.height();
        pictureRGB = new int[picture.height()][picture.width()];
        for (int i = 0; i < picture.height(); i++) {
            for (int j = 0; j < picture.width(); j++)
                pictureRGB[i][j] = picture.getRGB(j, i);
        }
        energy = new double[picture.height()][picture.width()];
    }

    // current picture
    public Picture picture() {
        Picture thisPicture = new Picture(width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++)
                thisPicture.setRGB(j, i, pictureRGB[i][j]);
        }
        return thisPicture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // helper function to get r, g, b value for (y, x)
    private int[] getRGB(int x, int y) {
        int rgb = pictureRGB[y][x];
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb) & 0xFF;
        int[] RGB = { r, g, b };
        return RGB;
    }

    // helper function used in calculating energy
    private int dx(int x, int y) {
        int[] leftRGB = getRGB(x - 1, y);
        int[] rightRGB = getRGB(x + 1, y);
        int redDiff = leftRGB[0] - rightRGB[0];
        int blueDiff = leftRGB[1] - rightRGB[1];
        int greenDiff = leftRGB[2] - rightRGB[2];
        return redDiff * redDiff + blueDiff * blueDiff + greenDiff * greenDiff;
    }

    // helper function used in calculating energy
    private int dy(int x, int y) {
        int[] downRGB = getRGB(x, y + 1);
        int[] upRGB = getRGB(x, y - 1);
        int redDiff = downRGB[0] - upRGB[0];
        int blueDiff = downRGB[1] - upRGB[1];
        int greenDiff = downRGB[2] - upRGB[2];
        return redDiff * redDiff + blueDiff * blueDiff + greenDiff * greenDiff;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            throw new IllegalArgumentException();
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1)
            return BOARDER_ENERGY;
        return Math.sqrt(dx(x, y) + dy(x, y));
    }

    // helper function to update a part of the energy matrix
    private void updateEnergy() {
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++)
                energy[i][j] = energy(j, i);
        }
    }

    // helper function used to relax vertex in vertical direction
    private void relaxVertical(int row, int col, double[][] distTo, int[][] edgeTo) {
        ArrayList<int[]> directions = new ArrayList<>();
        directions.add(new int[] { -1, 0 }); // we can always look up
        if (col > 0) directions.add(new int[] { -1, -1 });
        if (col < width() - 1) directions.add(new int[] { -1, 1 });
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            if (distTo[newRow][newCol] + energy[row][col] < distTo[row][col]) {
                distTo[row][col] = distTo[newRow][newCol] + energy[row][col];
                edgeTo[row][col] = newCol;
            }
        }
    }

    // helper function used to relax vertex in horizontal direction
    private void relaxHorizontal(int row, int col, double[][] distTo, int[][] edgeTo) {
        ArrayList<int[]> directions = new ArrayList<>();
        directions.add(new int[] { 0, -1 }); // we can always look left
        if (row > 0) directions.add(new int[] { -1, -1 });
        if (row < height() - 1) directions.add(new int[] { 1, -1 });
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            if (distTo[newRow][newCol] + energy[row][col] < distTo[row][col]) {
                distTo[row][col] = distTo[newRow][newCol] + energy[row][col];
                edgeTo[row][col] = newRow;
            }
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        updateEnergy();
        double[][] distTo = new double[height()][width()];
        int[][] edgeTo = new int[height()][width()];
        // initialize dist to
        for (int i = 0; i < height(); i++) distTo[i][0] = energy[i][0];
        for (int i = 0; i < height(); i++) {
            for (int j = 1; j < width(); j++)
                distTo[i][j] = Double.POSITIVE_INFINITY;
        }
        // start relaxing
        for (int i = 1; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                relaxHorizontal(j, i, distTo, edgeTo);
            }
        }
        // output the seam
        int[] seam = new int[width()];
        double minEnergy = Double.POSITIVE_INFINITY;
        for (int i = 0; i < height(); i++) {
            if (distTo[i][width() - 1] < minEnergy) {
                minEnergy = distTo[i][width() - 1];
                seam[width() - 1] = i;
            }
        }
        for (int i = width() - 2; i >= 0; i--) seam[i] = edgeTo[seam[i + 1]][i + 1];
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        updateEnergy();
        double[][] distTo = new double[height()][width()];
        int[][] edgeTo = new int[height()][width()];
        // initialize dist to
        for (int i = 0; i < width(); i++) distTo[0][i] = energy[0][i];
        for (int i = 1; i < height(); i++) {
            for (int j = 0; j < width(); j++)
                distTo[i][j] = Double.POSITIVE_INFINITY;
        }
        // start relaxing
        for (int i = 1; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                relaxVertical(i, j, distTo, edgeTo);
            }
        }
        // output the seam
        int[] seam = new int[height()];
        double minEnergy = Double.POSITIVE_INFINITY;
        for (int i = 0; i < width(); i++) {
            if (distTo[height() - 1][i] < minEnergy) {
                minEnergy = distTo[height() - 1][i];
                seam[height() - 1] = i;
            }
        }
        for (int i = height() - 2; i >= 0; i--) seam[i] = edgeTo[i + 1][seam[i + 1]];
        return seam;
    }

    // helper function to help check input
    private void validateSeam(int[] seam, boolean isVertical) {
        if (seam == null) throw new IllegalArgumentException();
        if (isVertical) {
            if (width <= 1) throw new IllegalArgumentException();
            if (seam.length != height) throw new IllegalArgumentException();
            for (int i : seam) {
                if (i < 0 || i >= width) throw new IllegalArgumentException();
            }
        }
        else {
            if (height <= 1) throw new IllegalArgumentException();
            if (seam.length != width) throw new IllegalArgumentException();
            for (int i : seam) {
                if (i < 0 || i >= height) throw new IllegalArgumentException();
            }
        }
        for (int i = 0; i < seam.length - 1; i++) {
            if (seam[i] - seam[i + 1] < -1 || seam[i] - seam[i + 1] > 1)
                throw new IllegalArgumentException();
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam, false);
        // unfortunately, there is no easy way to extract columns in 2d array
        int[][] newRGB = new int[height - 1][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < seam[i]; j++) newRGB[j][i] = pictureRGB[j][i];
            for (int j = seam[i]; j < height - 1; j++) newRGB[j][i] = pictureRGB[j + 1][i];
        }
        pictureRGB = newRGB;
        height--;
    }

    // helper function to remove seam in vertical side
    private int[][] updateRGBVertical(int[] seam) {
        int[][] newRGB = new int[height][width - 1];
        for (int i = 0; i < height; i++) {
            System.arraycopy(pictureRGB[i], 0, newRGB[i], 0, seam[i]);
            System.arraycopy(pictureRGB[i], seam[i] + 1, newRGB[i], seam[i], width - seam[i] - 1);
        }
        return newRGB;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, true);
        pictureRGB = updateRGBVertical(seam);
        width--;
    }

    //  unit testing (optional)
    public static void main(String[] args) {

    }
}
