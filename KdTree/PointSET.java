/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020/08/10
 *  Description: Brute-force solution
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.TreeSet;

public class PointSET {
    private final TreeSet<Point2D> pointSets;

    // construct an empty set of points
    public PointSET() {
        pointSets = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return pointSets.isEmpty();
    }

    // number of points in the set
    public int size() {
        return pointSets.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Cannot add a null point!");
        pointSets.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Cannot add a null point!");
        return pointSets.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D p : pointSets) {
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("Cannot determine on a null rectangle");
        ArrayList<Point2D> subPointSets = new ArrayList<>();
        for (Point2D p : pointSets) {
            if (rect.contains(p)) subPointSets.add(p);
        }
        return subPointSets;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Cannot determine on a null point");
        if (isEmpty()) return null;
        Point2D targetPoint = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Point2D localP : pointSets) {
            if (localP.distanceSquaredTo(p) < minDistance) {
                minDistance = localP.distanceSquaredTo(p);
                targetPoint = localP;
            }
        }
        return targetPoint;
    }

    public static void main(String[] args) {
        PointSET testPointSet = new PointSET();
        testPointSet.insert(new Point2D(0.0, 0.0));
        testPointSet.insert(new Point2D(0.1, 0.4));
        testPointSet.insert(new Point2D(0.4, 0.3));
        testPointSet.insert(new Point2D(0.6, 0.5));
        System.out.println(testPointSet.isEmpty());
        System.out.println(testPointSet.size());
        Point2D testPoint1 = new Point2D(0.1, 0.4);
        Point2D testPoint2 = new Point2D(0.1, 0.5);
        System.out.println(testPointSet.contains(testPoint1));
        System.out.println(testPointSet.contains(testPoint2));
        testPointSet.draw();
        RectHV testRect = new RectHV(0.4, 0.3, 0.8, 0.6);
        for (Point2D p : testPointSet.range(testRect)) {
            System.out.println(p);
        }
        System.out.println(testPointSet.nearest(testPoint2));
    }
}
