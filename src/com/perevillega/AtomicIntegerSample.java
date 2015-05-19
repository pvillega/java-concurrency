package com.perevillega;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerSample {

    protected static Executor cachedPool = Executors.newCachedThreadPool();
    protected static final AtomicCounter counter = new AtomicCounter();

    public static void main(String[] args) throws InterruptedException {
        for(int i = 0; i < 10000; i++) {
            Runnable thread = new CounterThread(counter);
            cachedPool.execute(thread);
        }
        System.out.println("Counter value: " + counter.value());
    }
}

class CounterThread implements Runnable {

    private AtomicCounter counter;

    public CounterThread(AtomicCounter counter) {
        this.counter = counter;
    }

    public void run() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(5000));
        } catch (InterruptedException e) {
            System.out.println("Interrupted, ignore");
        }
        counter.increment();
    }
}

class AtomicCounter {
    private AtomicInteger c = new AtomicInteger(0);

    public void increment() {
        c.incrementAndGet();
    }

    public void decrement() {
        c.decrementAndGet();
    }

    public int value() {
        return c.get();
    }
}
