/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020/08/11
 *  Description: Use kd-tree to find nearest neighbor and points in a rectangle
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }

    private Node root;
    private int size;
    private Point2D nearestNeighbor;
    private double minDistance;

    public KdTree() {
        root = null;
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // point comparison based on x or y, assuming the plane is separated by vertical or horizontal
    private int pointComparisonWithDirection(Point2D p1, Point2D p2, boolean byVertical) {
        if (byVertical) return Double.compare(p1.x(), p2.x());
        else return Double.compare(p1.y(), p2.y());
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Cannot insert a null point");
        root = insert(root, p, 0, 0, 1, 1, true);
    }

    // private helper function in insert
    private Node insert(Node x, Point2D p, double xmin, double ymin, double xmax, double ymax,
                        boolean byVertical) {
        if (x == null) {
            size++;
            return new Node(p, new RectHV(xmin, ymin, xmax, ymax));
        }
        int cmp = pointComparisonWithDirection(p, x.p, byVertical);
        if (cmp < 0) {
            if (byVertical) x.lb = insert(x.lb, p, x.rect.xmin(), x.rect.ymin(), x.p.x(),
                                          x.rect.ymax(), false);
            else x.lb = insert(x.lb, p,
                               x.rect.xmin(), x.rect.ymin(), x.rect.xmax(), x.p.y(),
                               true);
        }
        // if they share one coordinate but not both, go to right subtree
        else if (cmp > 0 || !p.equals(x.p)) {
            if (byVertical) x.rt = insert(x.rt, p, x.p.x(), x.rect.ymin(), x.rect.xmax(),
                                          x.rect.ymax(), false);
            else x.rt = insert(x.rt, p,
                               x.rect.xmin(), x.p.y(), x.rect.xmax(), x.rect.ymax(),
                               true);
        }
        return x;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("cannot try to find a null point");
        return contains(root, p, true);
    }

    // private helper function in find
    private boolean contains(Node x, Point2D p, boolean byVertical) {
        if (x == null) return false;
        int cmp = pointComparisonWithDirection(p, x.p, byVertical);
        if (cmp < 0) return contains(x.lb, p, !byVertical);
        if (cmp > 0 || !p.equals(x.p)) return contains(x.rt, p, !byVertical);
        return true;
    }

    // draw all points to standard draw
    public void draw() {
        draw(root, true);
    }

    // private helper function for draw
    private void draw(Node x, boolean byVertical) {
        if (x == null) return;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        x.p.draw();
        StdDraw.setPenRadius();
        if (byVertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(x.p.x(), x.rect.ymin(), x.p.x(), x.rect.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(x.rect.xmin(), x.p.y(), x.rect.xmax(), x.p.y());
        }
        draw(x.lb, !byVertical);
        draw(x.rt, !byVertical);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("cannot implement range search on a null rectangle");
        Queue<Point2D> queue = new Queue<>();
        range(root, queue, rect);
        return queue;
    }

    // private helper function used in range search
    private void range(Node x, Queue<Point2D> queue, RectHV rect) {
        if (x == null) return;
        if (rect.contains(x.p)) queue.enqueue(x.p);
        if (x.lb != null && x.lb.rect.intersects(rect)) range(x.lb, queue, rect);
        if (x.rt != null && x.rt.rect.intersects(rect)) range(x.rt, queue, rect);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("cannot find nearest point for null point");
        if (isEmpty()) return null;
        minDistance = Double.POSITIVE_INFINITY;
        nearestNeighbor = root.p;
        nearest(root, p);
        return nearestNeighbor;
    }

    // helper function to find nearest neighbor
    private void nearest(Node x, Point2D p) {
        if (x == null) return;
        // check x.p
        double currentDistance = x.p.distanceTo(p);
        if (currentDistance < minDistance) {
            minDistance = currentDistance;
            nearestNeighbor = x.p;
        }
        int cmp = compareRectangle(x.lb, x.rt, p);
        if (cmp < 0) {
            nearest(x.lb, p);
            if (x.rt != null && minDistance > x.rt.rect.distanceTo(p)) nearest(x.rt, p);
        }
        else {
            nearest(x.rt, p);
            if (x.lb != null && minDistance > x.lb.rect.distanceTo(p)) nearest(x.lb, p);
        }
    }

    // helper function to determine which sub-tree to search first
    private int compareRectangle(Node x, Node y, Point2D p) {
        // if any node is empty, search that first
        if (x == null) return -1;
        if (y == null) return 1;
        // if p exists in any node's rectangle, search that first
        if (x.rect.contains(p)) return -1;
        if (y.rect.contains(p)) return 1;
        // otherwise, search in the subtree that is closer to the point
        if (x.rect.distanceTo(p) < y.rect.distanceTo(p)) return -1;
        return 1;
    }

    public static void main(String[] args) {
        KdTree testKdTree = new KdTree();
        testKdTree.insert(new Point2D(0.7, 0.2));
        testKdTree.insert(new Point2D(0.5, 0.4));
        testKdTree.insert(new Point2D(0.2, 0.3));
        testKdTree.insert(new Point2D(0.4, 0.7));
        testKdTree.insert(new Point2D(0.9, 0.6));
        testKdTree.insert(new Point2D(0.9, 0.6));
        System.out.println(testKdTree.contains(new Point2D(0.7, 0.2)));
        System.out.println(testKdTree.contains(new Point2D(0.7, 0.1)));
        testKdTree.draw();
    }
}
