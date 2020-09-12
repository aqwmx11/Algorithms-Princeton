/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020-09-10
 *  Description: Followed TrieST, this class is a 26-way trie with cache
 **************************************************************************** */

public class TrieSTWithCache {
    private static final int R = 26;        // A to Z
    private static final int DIFF = 'A'; // map A - Z to 0 - 26

    private Node root;      // root of trie
    private String lastQueryPrefix; // for cache
    private Node lastQueryNode; // for cache

    // R-way trie node
    private static class Node {
        private Integer val;
        private Node[] next = new Node[R];
    }

    public TrieSTWithCache() {
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c - DIFF], key, d + 1);
    }

    public Integer get(String key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        Node x = get(root, key, 0);
        if (x == null) return null;
        return x.val;
    }

    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }

    private Node put(Node x, String key, int val, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            x.val = val;
            return x;
        }
        char c = key.charAt(d);
        x.next[c - DIFF] = put(x.next[c - DIFF], key, val, d + 1);
        return x;
    }

    public void put(String key, int val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        else root = put(root, key, val, 0);
    }

    private boolean collect(Node x) {
        if (x == null) return false;
        if (x.val != null) return true;
        for (char c = 0; c < R; c++) {
            if (collect(x.next[c])) return true;
        }
        return false;
    }

    // is String a prefix of string b?
    private boolean isPrefix(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() > b.length()) return false;
        return a.equals(b.substring(0, a.length()));
    }

    public boolean keysWithPrefix(String prefix) {
        Node x;
        if (isPrefix(lastQueryPrefix, prefix))
            x = get(lastQueryNode, prefix, lastQueryPrefix.length());
        else
            x = get(root, prefix, 0);
        boolean result = collect(x);
        lastQueryNode = x;
        lastQueryPrefix = prefix;
        return result;
    }

    public static void main(String[] args) {
        TrieSTWithCache testTST = new TrieSTWithCache();
        testTST.put("APPLE", 1);
        testTST.put("APP", 2);
        testTST.put("BOB", 3);
        System.out.println(testTST.get("APPLE"));
        System.out.println(testTST.contains("APPS"));
        System.out.println(testTST.keysWithPrefix("A"));
        System.out.println(testTST.keysWithPrefix("AP"));
        System.out.println(testTST.keysWithPrefix("B"));
        System.out.println(testTST.keysWithPrefix("BP"));
        System.out.println(testTST.isPrefix("AB", "ABC"));
        System.out.println(testTST.isPrefix("AB", "ACB"));
    }
}
