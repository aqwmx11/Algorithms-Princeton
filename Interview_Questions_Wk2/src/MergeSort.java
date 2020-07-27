public class MergeSort {
    private static Comparable[] aux;

    private static void sort(Comparable[] a, int low, int high) {
        if (low >= high) {
            return;
        }
        int mid = low + (high - low) / 2;
        sort(a, low, mid);
        sort(a, mid + 1, high);
        merge(a, low, mid, high);
    }

    private static int sortAndCount(Comparable[] a, int low, int high) {
        if (low >= high) {
            return 0;
        }
        int count = 0;
        int mid = low + (high - low) / 2;
        count += sortAndCount(a, low, mid);
        count += sortAndCount(a, mid + 1, high);
        count += mergeAndCound(a, low, mid, high);
        return count;
    }

    private static void merge(Comparable[] a, int low, int mid, int high) {
        int i = low;
        int j = mid + 1;
        System.arraycopy(a, low, aux, low, high - low + 1);
        for (int k = low; k <= high; k++) {
            if (i > mid) a[k] = aux[j++];
            else if (j > high) a[k] = aux[i++];
                //stability
            else if (less(aux[j], aux[i])) a[k] = aux[j++];
            else a[k] = aux[i++];
        }
    }

    private static int mergeAndCound(Comparable[] a, int low, int mid, int high) {
        int i = low;
        int j = mid + 1;
        int count = 0;
        System.arraycopy(a, low, aux, low, high - low + 1);
        for (int k = low; k <= high; k++) {
            if (i > mid) a[k] = aux[j++];
            else if (j > high) a[k] = aux[i++];
                //stability
            else if (less(aux[j], aux[i])) {
                a[k] = aux[j++];
                count += mid - i + 1;
            } else a[k] = aux[i++];
        }
        return count;
    }

    private static boolean less(Comparable a, Comparable b) {
        return a.compareTo(b) < 0;
    }

    public static void sort(Comparable[] a) {
        aux = new Comparable[a.length];
        sort(a, 0, a.length - 1);
    }

    public static int getInversion(Comparable[] a) {
        aux = new Comparable[a.length];
        return sortAndCount(a, 0, a.length - 1);
    }

    public static void main(String[] args) {
        Integer[] testArray = new Integer[]{1, 6, 3, 7, 2, 8};
        System.out.printf("The inversion of this array is: %d.%n", MergeSort.getInversion(testArray));
        for (Integer i : testArray) {
            System.out.println(i);
        }
    }
}
