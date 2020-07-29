/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020/07/28
 *  Description: Fast O(N^2logN) solution of finding collinear points
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class FastCollinearPoints {
    private static final int INITIALSIZE = 8;
    private static final int THRESHOLD = 3;
    private int segmentsSize;
    private LineSegment[] segments;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        // check inputs
        if (points == null) {
            throw new IllegalArgumentException("Points can not be null.");
        }
        // copy the points
        Point[] pointsCopy = copyPointArray(points);
        // check repeat points
        Arrays.sort(pointsCopy);
        for (int i = 0; i < points.length - 1; i++) {
            if (pointsCopy[i].compareTo(pointsCopy[i + 1]) == 0) {
                throw new IllegalArgumentException("Points should be different to each other.");
            }
        }
        segments = new LineSegment[INITIALSIZE];
        segmentsSize = 0;
        // loop through the points
        for (Point p : pointsCopy) {
            Point[] localPoints = copyPointArray(pointsCopy);
            // sort based on slope to p, now collinear points are together
            Arrays.sort(localPoints, p.slopeOrder());
            double localSlope = p.slopeTo(localPoints[0]);
            int localCount = 1;
            for (int i = 1; i <= localPoints.length; i++) {
                if (i != localPoints.length && p.slopeTo(localPoints[i]) == localSlope) {
                    localCount += 1;
                }
                else {
                    if (localCount >= THRESHOLD) {
                        collectSegment(localPoints, p, i, localCount);
                    }
                    // reset the counters
                    if (i != localPoints.length) {
                        localSlope = p.slopeTo(localPoints[i]);
                        localCount = 1;
                    }
                }
            }
        }
    }

    // given points, current index, local count, return line segment to collect
    private void collectSegment(Point[] points, Point basePoint, int currentIndex, int localCount) {
        // collinear points are from points[currentIndex - localCount] to points[currentIndex - 1]
        Point[] collinearPoints = new Point[localCount + 1];
        for (int i = 0; i < localCount; i++) {
            collinearPoints[i] = points[currentIndex - localCount + i];
        }
        collinearPoints[localCount] = basePoint;
        Arrays.sort(collinearPoints);
        // if the point is the starting point, then we have not collected it before
        if (collinearPoints[0].compareTo(basePoint) == 0) {
            segments[segmentsSize++] = new LineSegment(basePoint, collinearPoints[localCount]);
            if (segmentsSize == segments.length) {
                resize(segmentsSize * 2);
            }
        }
    }

    private void resize(int newLength) {
        LineSegment[] newSegments = new LineSegment[newLength];
        System.arraycopy(segments, 0, newSegments, 0, segments.length);
        segments = newSegments;
    }

    // copy an point an array to another array
    private Point[] copyPointArray(Point[] points) {
        Point[] pointsCopy = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException("No point in the array can be null.");
            }
            pointsCopy[i] = points[i];
        }
        return pointsCopy;
    }

    // the number of line segments
    public int numberOfSegments() {
        return segmentsSize;
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] outputSegments = new LineSegment[segmentsSize];
        System.arraycopy(segments, 0, outputSegments, 0, segmentsSize);
        return outputSegments;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
