/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020-07-27
 *  Description: implementation of brute-force check of collinear points
 **************************************************************************** */

import java.util.Arrays;

public class BruteCollinearPoints {
    private static final int INITIALSIZE = 8;
    private int segmentsSize;
    private LineSegment[] segments;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("Points can not be null.");
        }
        Point[] pointsCopy = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException("No point in the array can be null.");
            }
            pointsCopy[i] = points[i];
        }
        Arrays.sort(pointsCopy);
        for (int i = 0; i < pointsCopy.length - 1; i++) {
            if (pointsCopy[i].compareTo(pointsCopy[i + 1]) == 0) {
                throw new IllegalArgumentException("Points should be different to each other.");
            }
        }
        segments = new LineSegment[INITIALSIZE];
        segmentsSize = 0;
        for (int p = 0; p < pointsCopy.length - 3; p++) {
            for (int q = p + 1; q < pointsCopy.length - 2; q++) {
                for (int r = q + 1; r < pointsCopy.length - 1; r++) {
                    for (int s = r + 1; s < pointsCopy.length; s++) {
                        if (isFourPointsInOneLine(pointsCopy[p], pointsCopy[q], pointsCopy[r],
                                                  pointsCopy[s])) {
                            segments[segmentsSize++] = new LineSegment(pointsCopy[p],
                                                                       pointsCopy[s]);
                            if (segmentsSize == segments.length) {
                                resize(segmentsSize * 2);
                            }
                        }
                    }
                }
            }
        }
    }

    // helper function to determine whether four points are in one line
    private boolean isFourPointsInOneLine(Point p, Point q, Point r, Point s) {
        if (p.slopeTo(q) == p.slopeTo(r) && p.slopeTo(r) == p.slopeTo(s)) {
            return true;
        }
        return false;
    }

    // helper function to expand the segments array
    private void resize(int newLength) {
        LineSegment[] newSegments = new LineSegment[newLength];
        System.arraycopy(segments, 0, newSegments, 0, segments.length);
        segments = newSegments;
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
        Point[] testPointsArray = new Point[14];
        // should form segment y = x
        testPointsArray[0] = new Point(0, 0);
        testPointsArray[1] = new Point(1, 1);
        testPointsArray[2] = new Point(2, 2);
        testPointsArray[3] = new Point(3, 3);
        // should form segment y = x + 1
        testPointsArray[4] = new Point(0, 1);
        testPointsArray[5] = new Point(1, 2);
        testPointsArray[6] = new Point(2, 3);
        testPointsArray[7] = new Point(3, 4);
        // two separate points
        testPointsArray[8] = new Point(3, 5);
        testPointsArray[9] = new Point(2, 4);
        // should form segment y = 1
        testPointsArray[10] = new Point(2, 1);
        testPointsArray[11] = new Point(3, 1);
        // should form segment x = 1
        testPointsArray[12] = new Point(1, 3);
        testPointsArray[13] = new Point(1, 4);
        BruteCollinearPoints testBruteForce = new BruteCollinearPoints(testPointsArray);
        System.out.println(testBruteForce.numberOfSegments()); // expect 6
        LineSegment[] testRes = testBruteForce.segments();
        for (LineSegment ls : testRes) {
            System.out.println(ls);
        }
    }
}
