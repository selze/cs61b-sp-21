package deque;

import java.util.Iterator;

public class LinkedListDeque<Doom> {
    private int size;
    private  Node sentinel;

    private class Node {
        public Node next;
        public Node prev;
        public Doom item;

        /** constructor, add i to the first of n. */
        public Node(Doom i, Node n) {
            this.item = i;
            this.next = n;
            this.prev = n.prev;
            n.prev.next = this;
            n.prev = this;
        }

        public  Node() {
            this.next = this;
            this.prev = this;
            this.item = null;
        }
    }

    /** creates an empty list */
    public LinkedListDeque() {
        sentinel = new Node();
        size = 0;

    }

    public void addFirst(Doom item) {
        sentinel = new Node(item, sentinel.next).prev;
        size = size +1;
    }

    public void  addLast(Doom item) {
        sentinel = new Node(item, sentinel).next;
        size = size + 1;
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        } else {
            return false;
        }
    }

    public int size() {
        return size;
    }

    public void printDeque() {

    }

    public Doom removeFirst() {
        if (size == 0) {
            return null;
        }
        Doom x = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return x;
    }

    public Doom removeLast() {
        if (size == 0) {
            return null;
        }
        Doom x = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return x;
    }

    public Doom get(int index) {
        return null;
    }

    public Iterator<LinkedListDeque> iterator() {
        return null;
    }

    public boolean equals(Object o) {
        return true;
    }
}
