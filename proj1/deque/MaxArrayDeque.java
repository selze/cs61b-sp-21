package deque;

import java.util.Comparator;

public class MaxArrayDeque<Doom> extends ArrayDeque<Doom>{
    private Comparator<Doom> com;

    public MaxArrayDeque(Comparator<Doom> c) {
        super();
        com = c;
    }

    public Doom max() {
        if (isEmpty()) {
            return null;
        }
        Doom maxItem = get(0);
        for (Doom item : this) {
            if (com.compare(item, maxItem) > 0) {
                maxItem = item;
            }
        }
        return maxItem;
    }

    public Doom max(Comparator<Doom> c) {
        if (isEmpty()) {
            return null;
        }
        Doom maxItem = get(0);
        for (Doom item : this) {
            if (c.compare(item, maxItem) > 0) {
                maxItem = item;
            }
        }
        return maxItem;
    }


}
