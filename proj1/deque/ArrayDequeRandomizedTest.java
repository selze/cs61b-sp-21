package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class ArrayDequeRandomizedTest {
    @Test
    /** add integers 3 times and remove them while verifying they are equal. */
    public void testThreeAddThreeRemove() {
        ArrayDeque<Integer> buggy = new ArrayDeque<>();
        LinkedListDeque<Integer> correct = new LinkedListDeque<>();

        buggy.addLast(4);
        buggy.addLast(5);
        buggy.addLast(6);

        correct.addLast(4);
        correct.addLast(5);
        correct.addLast(6);

        assertEquals(correct.size(), buggy.size());

        assertEquals(correct.removeLast(), buggy.removeLast());
        assertEquals(correct.removeLast(), buggy.removeLast());
        assertEquals(correct.removeLast(), buggy.removeLast());
    }

    @Test
    /** randomly calls addLast and size on an AListNoResizing object
     * for a total number of these functions.
     */
    public void randomizedTest() {
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        ArrayDeque<Integer> B = new ArrayDeque<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                System.out.println("added");
            } else if (operationNumber == 1) {
                // size
                assertEquals(L.size(), B.size());
            } else if (L.isEmpty()) {
                continue;
            } else if (operationNumber == 2) {
                //removeLast
                int y = B.removeLast();
                int x = L.removeLast();
                System.out.println("removed " + x + " " + y);
                assertEquals(x, y);
            }
        }

    }
}
