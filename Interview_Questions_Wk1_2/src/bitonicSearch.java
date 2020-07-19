import java.util.Arrays;
import java.util.Comparator;

public class bitonicSearch {
    private final Integer[] bitonicArrayToSearch; // store the array
    private final int size; // record the size of the array
    private final int target; // store the target we want to find

    public bitonicSearch(Integer[] arr, int i) {
        size = arr.length;
        bitonicArrayToSearch = new Integer[size];
        System.arraycopy(arr, 0, bitonicArrayToSearch, 0, size);
        target = i;
    }

    // Helper function to decide whether an index is in sub-array1,
    // return -1 for left, 0 for turning point and 1 for right
    private int findPosition(int index) {
        if (index == 0) {
            return -1;
        }
        if (index == size - 1) {
            return 1;
        }
        if (bitonicArrayToSearch[index] > bitonicArrayToSearch[index - 1] &&
                bitonicArrayToSearch[index] > bitonicArrayToSearch[index + 1]) {
           return 0;
        }
        if (bitonicArrayToSearch[index] > bitonicArrayToSearch[index - 1]) {
            return -1;
        }
        return 1;
    }

    //Helper function to combine index from left and right binary search result
    private int combineLeftRightIndex(int leftIndex, int rightIndex) {
        if (leftIndex < 0 && rightIndex < 0) {
            return -1;
        }
        if (leftIndex >= 0) {
            return leftIndex;
        }
        return rightIndex;
    }

    //helper function for recursion
    //Note: low and high are both inclusive, but in Arrays.binarySearch, toIndex is exclusive!!!
    private int bitonicSearchHelper(int low, int high) {
        if (low > high) { //meaning, we failed to find target
            return -1;
        }
        int mid = low + (high - low) / 2;
        if (bitonicArrayToSearch[mid] == target) {
            return mid;
        }
        int position = findPosition(mid);
        if (position == 0) {
            int leftInd = Arrays.binarySearch(bitonicArrayToSearch, low, mid, target);
            int rightInd = Arrays.binarySearch(bitonicArrayToSearch, mid + 1, high + 1, target,
                    Comparator.reverseOrder());
            return combineLeftRightIndex(leftInd, rightInd);
        }
        else if (position == -1) {
            if (target < bitonicArrayToSearch[mid]) {
                int leftInd = Arrays.binarySearch(bitonicArrayToSearch, low, mid, target);
                int rightInd = bitonicSearchHelper(mid + 1, high);
                return combineLeftRightIndex(leftInd, rightInd);
            }
            else {
                return bitonicSearchHelper(mid + 1, high);
            }
        }
        else {
            if (target < bitonicArrayToSearch[mid]) {
                int leftInd = bitonicSearchHelper(low, mid - 1);
                int rightInd = Arrays.binarySearch(bitonicArrayToSearch, mid + 1, high + 1, target,
                        Comparator.reverseOrder());
                return combineLeftRightIndex(leftInd, rightInd);
            }
            else {
                return bitonicSearchHelper(low, mid - 1);
            }
        }
    }

    //public interface
    public int bitonicSearchBinary() {
        return bitonicSearchHelper(0, size - 1);
    }

    public static void main(String[] args) {
        //Test Case 1:
        bitonicSearch testCaseOne = new bitonicSearch(new Integer[]{1, 2, 3, 5, 7, 9, 6, 4}, 3);
        System.out.println(testCaseOne.bitonicSearchBinary());

        //Test Case 2:
        bitonicSearch testCaseTwo = new bitonicSearch(new Integer[]{1, 2, 3, 5, 7, 9, 6, 4}, 8);
        System.out.println(testCaseTwo.bitonicSearchBinary());

        //Test Case 3:
        bitonicSearch testCaseThree = new bitonicSearch(new Integer[]{1, 2, 3, 5, 7, 9, 6, 4}, 6);
        System.out.println(testCaseThree.bitonicSearchBinary());
    }
}
