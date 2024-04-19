package deque;

import java.util.Iterator;

public class ArrayDeque<Doom> implements Iterable<Doom>{
    private Doom[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private int length;

    public ArrayDeque() {
        items = (Doom[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
        length = 8;
    }

    public int size() {
        return size;
    }

    public void resize(int capacity) {
        Doom[] newitems = (Doom[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            newitems[(i + 1 + nextFirst) % capacity] = items[(i + 1 + nextFirst) % length];
        }
        nextFirst %= capacity;
        nextLast %= capacity;
        length = capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Doom get(int index) {
        if (index < 0 || index > size - 1) {
            return null;
        }
        return items[(nextFirst + 1 + index) % length];
    }

    public Doom removeFirst() {
        size -= 1;
        while (length > 16 && size < length / 4) {
            resize(length / 2);
        }
        Doom removedItem = items[(nextFirst + 1) % length];
        nextFirst = (nextFirst + 1) % length;
        return removedItem;
    }

    public Doom removeLast() {
        size -= 1;
        while (length > 16 && size < length / 4) {
            resize(length / 2);
        }
        Doom removedItem = items[(nextLast - 1) % length];
        nextLast = (nextLast - 1) % length;
        return removedItem;
    }

    public void addLast(Doom item) {
        if (size == length) {
            resize(length * 2);
        }
        size += 1;
        items[nextLast] = item;
        nextLast = (nextLast + 1) % length;
    }

    public  void addFirst(Doom item) {
        if (size == length) {
            resize(length * 2);
        }
        size += 1;
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1) % length;
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
            Doom returnItem = items[(nextFirst + wizpos + 1) % length];
            wizpos += 1;
            return returnItem;
        }
    }

    public boolean contains(Doom x) {
        for (Doom item : this) {
            if (item.equals(x)) {
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
        if (o instanceof ArrayDeque other) {
            if (other.size() != size) {
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
