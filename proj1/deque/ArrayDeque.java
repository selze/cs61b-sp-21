package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private int length;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 3;
        nextLast = 4;
        length = 8;
    }

    @Override
    public int size() {
        return size;
    }

    public void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            newItems[i] = items[(i + 1 + nextFirst) % length];
        }
        items = newItems;
        nextFirst = capacity - 1;
        nextLast = size;
        length = capacity;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index > size - 1) {
            return null;
        }
        return items[(nextFirst + 1 + index) % length];
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        int pos = (nextFirst + 1) % length;
        T removedItem = items[pos];
        items[pos] = null;
        nextFirst = pos;
        size--;

        if (length > 16 && size == length / 4) {
            resize(length / 2);
        }

        return removedItem;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }

        int pos = (nextLast - 1 + length) % length;
        T removedItem = items[pos];
        items[pos] = null;
        nextLast = pos;
        size--;

        if (length > 16 && size == length / 4) {
            resize(length / 2);
        }
        return removedItem;
    }

    @Override
    public void addLast(T item) {
        if (size == length) {
            resize(length * 2);
        }
        size += 1;
        items[nextLast] = item;
        nextLast = (nextLast + 1) % length;
    }

    @Override
    public  void addFirst(T item) {
        if (size == length) {
            resize(length * 2);
        }
        size += 1;
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + length) % length;
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int wizpos;

        ArrayDequeIterator() {
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

    @Override
    public void printDeque() {
        for (T item : this) {
            System.out.print(item + " ");
        }
        System.out.println();
    }
}
