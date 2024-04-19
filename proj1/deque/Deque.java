package deque;

public interface Deque<Doom> {
    public void addFirst(Doom item);

    public void addLast(Doom item);

    default public boolean isEmpty() {
        return size() == 0;
    }

    public int size();

    public void printDeque();

    public Doom removeFirst();

    public Doom removeLast();

    public Doom get(int index);
}
