package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    private int size;
    private  Node sentinel;

    private class Node {
        private Node next;
        private Node prev;
        private T item;

        /** constructor, add i to the first of n. */
        Node(T i, Node n) {
            this.item = i;
            this.next = n;
            this.prev = n.prev;
            n.prev.next = this;
            n.prev = this;
        }

        Node() {
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

    @Override
    public void addFirst(T item) {
        sentinel = new Node(item, sentinel.next).prev;
        size = size + 1;
    }

    public void  addLast(T item) {
        sentinel = new Node(item, sentinel).next;
        size = size + 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = sentinel.next;
        while (p != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T x = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return x;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T x = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return x;
    }

    @Override
    public T get(int index) {
        if (index > size - 1 || index < 0) {
            return null;
        }
        Node p = sentinel.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if (index > size - 1 || index < 0) {
            return null;
        }
        return getRecursiveHelper(sentinel.next, index);
    }

    private T getRecursiveHelper(Node node, int index) {
        if (index == 0) {
            return node.item;
        }

        return getRecursiveHelper(node.next, index - 1);
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int wizpos;
        LinkedListDequeIterator() {
            wizpos = 0;
        }

        public boolean hasNext() {
            return wizpos < size;
        }

        public T next() {
            T returnItem = get(wizpos);
            wizpos += 1;
            return returnItem;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (other.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!(other.get(i).equals(get(i)))) {
                return false;
            }
        }
        return true;
    }

}
