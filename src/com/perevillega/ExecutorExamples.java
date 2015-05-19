package com.perevillega;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ExecutorExamples {

    // pool of threads with fixed size
    protected static Executor fixedPool = Executors.newFixedThreadPool(3);
    // executor that runs tasks one by one, linearly (1 thread)
    protected static Executor singleThread = Executors.newSingleThreadExecutor();
    // executor that can grow dynamically
    protected static Executor cachedPool = Executors.newCachedThreadPool();

    // executor service allows use of Callable to return results
    protected static ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static void main(String[] args) {
        new BeeperControl().beepForAnHour();
        cachedPool.execute(new SimpleExecutor());
        cachedPool.execute(new ExecutorServiceCallable());
    }
}

class SimpleExecutor implements Runnable {
    private Executor pool = Executors.newFixedThreadPool(3);

    public void run() {
        for (; ; ) {
            pool.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("SimpleExecutor interrupted, ignore");
                }
                System.out.println("SimpleExecutor");
            });
        }
    }
}

class ExecutorServiceCallable implements Runnable {
    private final ExecutorService pool = Executors.newFixedThreadPool(3);
    private Set<Future<String>> futures = new HashSet<>();

    public void run() { // run the service
        try {
            for (; ; ) {
                Future<String> future = pool.submit(new CallableClass());
                futures.add(future);
                testCompletedFutures();
            }
        } catch (Exception ex) {
            System.out.println("Callable exception: " + ex.getMessage());
            shutdownAndAwaitTermination(pool);
        }
    }

    void testCompletedFutures() throws ExecutionException, InterruptedException {
        Set<Future<String>> remainder = new HashSet<>();
        for(Future<String> f : futures) {
            if(f.isDone()) {
                System.out.println("Callable says: " + f.get());
            } else {
                remainder.add(f);
            }
        }
        futures = remainder;
    }

    /**
     * The following method shuts down an {@code ExecutorService} in two phases,
     * first by calling {@code shutdown} to reject incoming tasks, and then
     * calling {@code shutdownNow}, if necessary, to cancel any lingering tasks:
     */
    void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}

class CallableClass implements Callable<String> {
    public static Random random = new Random();

    @Override
    public String call() throws Exception {
        Thread.sleep(random.nextInt(5000));
        return UUID.randomUUID().toString();
    }
}

// uses scheduled executor to run code every 10 seconds
class BeeperControl {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void beepForAnHour() {
        final Runnable beeper = () -> System.out.println("beep");

        // run 'beep' runnable every 10 seconds
        final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);

        // kill scheduled task after 1 hour
        scheduler.schedule(() -> beeperHandle.cancel(true), 60 * 60, SECONDS);
    }
}