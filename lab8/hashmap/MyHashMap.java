package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private static final int INIT_CAPACITY = 16;
    private Collection<Node>[] buckets;
    private double maxLoad = 0.75;
    private int n;                        //number of key-value pairs
    private int m;                        //hash table size
    private Set<K> keys;
    /** Constructors */
    public MyHashMap() {
        this(INIT_CAPACITY);
    }

    public MyHashMap(int initialSize) {
        this.m = initialSize;
        keys = new HashSet<>();
        buckets = (Collection<Node>[]) new Collection[initialSize];
        for (int i = 0; i < m; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.m = initialSize;
        this.keys = new HashSet<>();
        this.maxLoad = maxLoad;
        buckets = (Collection<Node>[]) new Collection[initialSize];
        for (int i = 0; i < m; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return null;
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return null;
    }

    private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) % m;
    }
    private void resize(int newSize){
        MyHashMap<K, V> temp = new MyHashMap<>(newSize);
        for (int i = 0; i < m; i++) {
            for (Node x : buckets[i]) {
                temp.put(x.key, x.value);
            }
        }
        this.m = temp.m;
        this.n = temp.n;
        this.buckets = temp.buckets;
    }

    @Override
    public void clear() {
        for (int i = 0; i < m; i++) {
            buckets[i].clear();
        }
        n = 0;
        keys = new HashSet<>();
    }

    @Override
    public boolean containsKey(K key) {
        if (keys.contains(key)) return true;
        return false;
    }


    @Override
    public V remove(K key) {
        if (!containsKey(key)) return null;
        int i = hash(key);
        for (Node x : buckets[i]) {
            if (key.equals(x.key)) {
                buckets[i].remove(x);
                n--;
                keys.remove(x.key);
                return x.value;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if (!containsKey(key)) return null;
        int i = hash(key);
        for (Node x : buckets[i]) {
            if (key.equals(x.key) && value.equals(x.value)) {
                buckets[i].remove(x);
                n--;
                keys.remove(x.key);
                return value;
            }
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        if (n > m * maxLoad) {
            resize(2 * m);
        }
        int index = hash(key);
        if (!containsKey(key)) {
            n++;
            buckets[index].add(new Node(key, value));
            keys.add(key);
        } else {
            Node x = getNode(key);
            x.value = value;
        }
    }

    private Node getNode(K key) {
        int index = hash(key);
        for (Node x : buckets[index]) {
            if (key.equals(x.key)) {
                return x;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public Set<K> keySet() {
        return keys;
    }

    @Override
    public V get(K key) {
        int index = hash(key);
        for (Node x : buckets[index]) {
            if (key.equals(x.key)) {
                return x.value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keys.iterator();
    }
}
