package deque;

import java.util.Iterator;

public class ArrayDeque<Doom> implements Iterable<Doom>, Deque<Doom>{
    private Doom[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private int length;

    public ArrayDeque() {
        items = (Doom[]) new Object[8];
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
        Doom[] newItems = (Doom[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            newItems[i] = items[(i + 1 + nextFirst) % length];
        }
        items = newItems;
        nextFirst = capacity - 1;
        nextLast = size;
        length = capacity;
    }

    @Override
    public Doom get(int index) {
        if (index < 0 || index > size - 1) {
            return null;
        }
        return items[(nextFirst + 1 + index) % length];
    }

    @Override
    public Doom removeFirst() {
        if (size == 0) {
            return null;
        }

        int pos = (nextFirst + 1) % length;
        Doom removedItem = items[pos];
        items[pos] = null;
        nextFirst = pos;
        size --;

        if (length > 16 && size == length / 4) {
            resize(length / 2);
        }

        return removedItem;
    }

    @Override
    public Doom removeLast() {
        if (size == 0) {
            return null;
        }

        int pos = (nextLast - 1 + length) % length;
        Doom removedItem = items[pos];
        items[pos] = null;
        nextLast = pos;
        size --;

        if (length > 16 && size == length / 4) {
            resize(length / 2);
        }
        return removedItem;
    }

    @Override
    public void addLast(Doom item) {
        if (size == length) {
            resize(length * 2);
        }
        size += 1;
        items[nextLast] = item;
        nextLast = (nextLast + 1) % length;
    }

    @Override
    public  void addFirst(Doom item) {
        if (size == length) {
            resize(length * 2);
        }
        size += 1;
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + length) % length;
    }

    public Iterator<Doom> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<Doom>{
        public int wizpos;

        public ArrayDequeIterator() {
            wizpos = 0;
        }

        public boolean hasNext() {
            return wizpos < size;
        }

        public Doom next() {
            Doom returnItem = get(wizpos);
            wizpos += 1;
            return returnItem;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof ArrayDeque)) {
            return false;
        }

        ArrayDeque<?> other = (ArrayDeque<?>) o;

        if (other.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (other.get(i) != get(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void printDeque() {
        for (Doom item : this) {
            System.out.print(item + " ");
        }
        System.out.println();
    }
}
