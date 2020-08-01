import edu.princeton.cs.algs4.StdRandom;

public class Kth {
    //2 way partition, return an int, so that a[i] < a[j] iff i < j; a[i] > a[j] iff i > j
    private static int twoWayPartition(int[] a, int lo, int high) {
        int i = lo;
        int j = high + 1;
        while (true) {
            //find element in the left to swap
            while (a[++i] < a[lo]) {
                if (i == high) break;
            }
            //find element in the right to swap
            while (a[--j] > a[lo]) {
                if (j == lo) break;
            }
            //check if the pointers cross
            if (i >= j) break;
            //exchange a[i] and a[j]
            exchange(a, i, j);
        }
        //exchange a[lo] and a[j]
        exchange(a, lo, j);
        return j;
    }

    //3way partition, return two int, lt and gt
    private static int[] threeWayPartition(int[] a, int lo, int hi) {
        int v = a[lo];
        int lt = lo, gt = hi;
        int i = lo;
        while (i <= gt) {
            if (a[i] < v) exchange(a, i++, lt++);
            else if (a[i] > v) exchange(a, i, gt--);
            else i++;
        }
        return new int[]{lt, gt};
    }

    private static void exchange(int[] a, int ind1, int ind2) {
        int temp = a[ind1];
        a[ind1] = a[ind2];
        a[ind2] = temp;
    }

    //quick select kth on one array using two way partition
    public static int kthByTwoWayPartition(int[] a, int k) {
        int[] aCopy = new int[a.length];
        System.arraycopy(a, 0, aCopy, 0, a.length);
        StdRandom.shuffle(aCopy);
        int lo = 0, hi = aCopy.length - 1;
        while (hi > lo) {
            int j = twoWayPartition(aCopy, lo, hi);
            if (j < k) lo = j + 1;
            else if (j > k) hi = j - 1;
            else return aCopy[j];
        }
        return aCopy[k];
    }

    //quick select kth on one array using three way partition
    public static int kthByThreeWayPartition(int[] a, int k) {
        int[] aCopy = new int[a.length];
        System.arraycopy(a, 0, aCopy, 0, a.length);
        StdRandom.shuffle(aCopy);
        int lo = 0, hi = aCopy.length - 1;
        while (hi > lo) {
            int[] ltGt = threeWayPartition(aCopy, lo, hi);
            int lt = ltGt[0];
            int gt = ltGt[1];
            if (gt < k) lo = gt + 1;
            else if (lt > k) hi = lt - 1;
            else return aCopy[k];
        }
        return aCopy[k];
    }

    //kth element for two sorted arrays, using median recursive method
    public static int kthForTwoSortedArraysUsingMedian(int[] a, int[] b, int k) {
        int[] aCopy = new int[a.length];
        System.arraycopy(a, 0, aCopy, 0, a.length);
        int[] bCopy = new int[b.length];
        System.arraycopy(b, 0, bCopy, 0, b.length);
        return kthForTwoSortedArrayUsingMedianHelper(aCopy, 0, aCopy.length - 1, bCopy, 0, bCopy.length - 1, k);
    }

    // helper function for recursive, note that loA, hiA, loB, hiB are all inclusive
    private static int kthForTwoSortedArrayUsingMedianHelper(int[] a, int loA, int hiA, int[] b, int loB, int hiB,
                                                             int k) {
        if (loA > hiA) return b[loB + k];
        if (loB > hiB) return a[loA + k];
        if (k == 0) return Math.min(a[loA], b[loB]);
        int midA = loA + Math.min(hiA - loA + 1, (k + 1) / 2) - 1;
        int midB = loB + Math.min(hiB - loB + 1, (k + 1) / 2) - 1;
        if (a[midA] > b[midB]) {
            //then b[loB] to b[midB] cannot be kth number
            return kthForTwoSortedArrayUsingMedianHelper(a, loA, hiA, b, midB + 1, hiB, k - midB + loB - 1);
        } else {
            //then a[loA] to a[midA] cannot be kth number
            return kthForTwoSortedArrayUsingMedianHelper(a, midA + 1, hiA, b, loB, hiB, k - midA + loA - 1);
        }
    }

    //unit test
    public static void main(String[] args) {
        int[] testArray = {3, 6, 7, 2, 1, 5};
        System.out.println("Testing kth element in one array using two way partition now.");
        System.out.printf("The minimum of this array is: %d.%n", Kth.kthByTwoWayPartition(testArray, 0));
        System.out.printf("The median of this array is: %d.%n", Kth.kthByTwoWayPartition(testArray, 3));
        System.out.printf("The maximum of this array is: %d.%n", Kth.kthByTwoWayPartition(testArray, 5));
        System.out.println("Testing kth element in one array using three way partition now.");
        System.out.printf("The minimum of this array is: %d.%n", Kth.kthByThreeWayPartition(testArray, 0));
        System.out.printf("The median of this array is: %d.%n", Kth.kthByThreeWayPartition(testArray, 3));
        System.out.printf("The maximum of this array is: %d.%n", Kth.kthByThreeWayPartition(testArray, 5));
        int[] testArray2 = {1, 2, 7, 9, 13, 15};
        int[] testArray3 = {3, 4, 6, 8, 10, 11};
        System.out.println("Testing kth element in two arrays using recursive on medians.");
        System.out.printf("The minimum of these arrays is: %d.%n", Kth.kthForTwoSortedArraysUsingMedian(testArray2,
                testArray3, 0));
        System.out.printf("The median of these arrays is: %d.%n", Kth.kthForTwoSortedArraysUsingMedian(testArray2,
                testArray3, 6));
        System.out.printf("The maximum of these arrays is is: %d.%n", Kth.kthForTwoSortedArraysUsingMedian(testArray2,
                testArray3, 11));
    }
}
