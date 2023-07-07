import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static AtomicInteger maxIntA = new AtomicInteger();
    public static AtomicInteger maxIntB = new AtomicInteger();
    public static AtomicInteger maxIntC = new AtomicInteger();

    public static BlockingQueue<String> countQueueA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> countQueueB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> countQueueC = new ArrayBlockingQueue<>(100);

    public static final int SIZESTR = 10_000;
    public static final int LENGHTSTR = 100_000;

    public static final char A = 'a';
    public static final char B = 'b';
    public static final char C = 'c';

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        List<Thread> threads = new ArrayList<>();
        Thread generate = new Thread(() -> {
            for (int i = 0; i < SIZESTR; i++) {
                try {
                    String text = generateText("abc", LENGHTSTR);
                    countQueueA.put(text);
                    countQueueB.put(text);
                    countQueueC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }

        });

        threads.add(generate);
        threads.add(searchAorBorC(countQueueA, A, maxIntA));
        threads.add(searchAorBorC(countQueueB, B, maxIntB));
        threads.add(searchAorBorC(countQueueC, C, maxIntC));

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("MAX a " + maxIntA.get());
        System.out.println("MAX b " + maxIntB.get());
        System.out.println("MAX c " + maxIntC.get());
    }

    public static Thread searchAorBorC(BlockingQueue<String> queue, char c, AtomicInteger max) {
        Runnable run = () -> {
            for (int i = 0; i < SIZESTR; i++) {
                String s;
                try {
                    s = queue.take();
                } catch (InterruptedException e) {
                    return;
                }
                int count = 0;
                for (int j = 0; j < LENGHTSTR; j++) {
                    if (s.charAt(j) == c) {
                        count++;
                    }
                }
                if (count > max.get()) {
                    max.set(count);
                }
            }
        };
        return new Thread(run);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

}


