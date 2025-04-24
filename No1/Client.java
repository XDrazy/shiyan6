// 1. 目标接口：定义了排序和查找方法
interface DataOperation {
    void sort(int[] data);
    int search(int[] data, int key);
}

// 2. 已有的快速排序类（不能改动）
class QuickSort {
    public void quickSort(int[] arr) {
        quickSortRecursive(arr, 0, arr.length - 1);
    }

    private void quickSortRecursive(int[] arr, int low, int high) {
        if (low < high) {
            int p = partition(arr, low, high);
            quickSortRecursive(arr, low, p - 1);
            quickSortRecursive(arr, p + 1, high);
        }
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private void swap(int[] arr, int i, int j) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }
}

// 3. 已有的二分查找类（不能改动）
class BinarySearch {
    // 返回 key 在 arr 中的索引，找不到返回 -1
    public int binarySearch(int[] arr, int key) {
        int low = 0, high = arr.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (arr[mid] == key) {
                return mid;
            } else if (arr[mid] < key) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }
}

// 4. 适配器类：将 QuickSort 和 BinarySearch 适配到 DataOperation
class DataOperationAdapter implements DataOperation {
    private QuickSort quickSort;
    private BinarySearch binarySearch;

    public DataOperationAdapter() {
        this.quickSort = new QuickSort();
        this.binarySearch = new BinarySearch();
    }

    @Override
    public void sort(int[] data) {
        // 适配 sort 调用 quickSort
        quickSort.quickSort(data);
    }

    @Override
    public int search(int[] data, int key) {
        // 适配 search 调用 binarySearch
        // 二分查找要求数组已排序
        return binarySearch.binarySearch(data, key);
    }
}

// 5. 客户端测试
public class Client {
    public static void main(String[] args) {
        int[] data = { 5, 3, 8, 4, 9, 1, 2 };
        DataOperation op = new DataOperationAdapter();

        System.out.println("排序前：");
        printArray(data);

        op.sort(data);
        System.out.println("排序后：");
        printArray(data);

        int key = 4;
        int idx = op.search(data, key);
        if (idx >= 0) {
            System.out.printf("元素 %d 在数组中的索引：%d%n", key, idx);
        } else {
            System.out.printf("数组中未找到元素 %d%n", key);
        }
    }

    private static void printArray(int[] arr) {
        for (int n : arr) {
            System.out.print(n + " ");
        }
        System.out.println();
    }
}
