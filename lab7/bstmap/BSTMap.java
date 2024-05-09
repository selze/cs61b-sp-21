package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.*;

public class BSTMap<K extends Comparable, V> implements Map61B<K, V>{
    private Node root;                      // root of BST

    private class Node {
        private K key;
        private V val;                      //value
        private Node left, right;           //left and right subtrees
        private int size;                   //number of nodes in subtrees

        public Node(K key, V val, int size) {
            this.key = key;
            this.val = val;
            this.size = size;
        }
    }

    public BSTMap() {

    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) throw new IllegalArgumentException("argument to containsKey() is null");
        return containsKey(root, key);
    }

    private boolean containsKey(Node x, K key) {
        if (x == null) return false;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) return containsKey(x.left, key);
        else if (cmp > 0) return containsKey(x.right, key);
        else return true;
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(Node x, K key) {
        if (key == null) throw new IllegalArgumentException("calls get with a null key");
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) return get(x.left, key);
        else if (cmp > 0) return get(x.right, key);
        else return x.val;
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        else return x.size;
    }

    @Override
    public void put(K key, V val) {
        if (key == null) throw  new IllegalArgumentException("calls put() with a null key");
        root = put(root, key, val);
    }

    public Node put(Node x, K key, V val) {
        if (x == null) return new Node(key, val, 1);
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = put(x.left, key, val);
        } else if (cmp > 0) {
            x.right = put(x.right, key, val);
        } else {
            x.val = val;
        }
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (K key : this) {
            set.add(key);
        }
        return set;
    }


    @Override
    public V remove(K key) {
        if (key == null) throw new IllegalArgumentException("calls remove() with a null key");
        V val = get(key);
        root = remove(root, key);
        return val;
    }

    private Node min(Node x) {
        if (x.left == null) return x;
        else return min(x.left);
    }


    private Node remove(Node x, K key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = remove(x.left, key);
        else if (cmp > 0) x.right = remove(x.right, key);
        else {
            if (x.left == null) return x.right;
            if (x.right == null) return x.left;
            Node t = x;
            x = min(t.right);
            x.right = removeMin(t.right);
            x.left = t.left;
        }
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void removeMin() {
        if (isEmpty()) throw new NoSuchElementException("Symbol table underflow");
        root = removeMin(root);
    }

    private Node removeMin(Node x) {
        if (x.left == null) return x.right;
        x.left = removeMin(x.left);
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    @Override
    public V remove(K key, V val) {
        if (key == null) throw new IllegalArgumentException("calls remove() with a null key");
        if (containsKey(key) && get(key).equals(val)) {
            V val1 = get(key);
            root = remove(root, key);
            return val1;
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTIterator<K>(root);
    }

    private class BSTIterator<K> implements Iterator {
        private Stack<Node> stack = new Stack<>();

        public BSTIterator(Node node) {
            pushLeft(node);
        }

        private void pushLeft(Node node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public K next() {
            if (!hasNext()) throw new NoSuchElementException("No more elements");
            Node current = stack.pop();
            pushLeft(current.right);
            return (K) current.key;
        }
    }
}

