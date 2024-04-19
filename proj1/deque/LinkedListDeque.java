package deque;

import java.util.Iterator;

public class LinkedListDeque<Doom> implements Iterable<Doom>{
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
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node p = sentinel.next;
        while (p != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
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
        if (index > size - 1 || index < 0) {
            return null;
        }
        Node p = sentinel.next;
        for (int i = 0 ; i < index; i++) {
            p = p.next;
        }
        return p.item;
    }

    public Doom getRecursive(int index) {
        if (index > size - 1 || index <0) {
            return null;
        }
        return getRecursiveHelper(sentinel.next, index);
    }

    private Doom getRecursiveHelper(Node node, int index) {
        if (index == 0) {
            return node.item;
        }

        return getRecursiveHelper(node.next, index - 1);
    }

    public Iterator<Doom> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<Doom> {
        private Node wizpos;
        public LinkedListDequeIterator() {
            wizpos = sentinel.next;
        }

        public boolean hasNext() {
            if (wizpos.next == sentinel) {
                return false;
            } else {
                return true;
            }
        }

        public Doom next() {
            Doom returnItem = wizpos.item;
            wizpos = wizpos.next;
            return  returnItem;
        }
    }

    public boolean contains(Doom item) {
        for (Doom t : this) {
            if (t.equals(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof LinkedListDeque other) {
            if (other.size() != this.size()) {
                return false;
            }
            for (Doom item : this) {
                if (!other.contains(item)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
