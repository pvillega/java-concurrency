package com.perevillega;


import java.util.concurrent.ExecutionException;

public class Synchronisation {
    public static final int SIZE = 10000;

    /**
     * Creates two threads using different constructs
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        UnsafeCounter unsafe = new UnsafeCounter();
        VolatileCounter volCounter = new VolatileCounter();
        SyncSafeCounter syncSafe = new SyncSafeCounter();
        LockCounter lockCounter = new LockCounter();

        Thread op1 = new Thread(new Op1(unsafe, volCounter, syncSafe, lockCounter));
        Thread op2 = new Thread(new Op1(unsafe, volCounter, syncSafe, lockCounter));

        op1.start();
        op2.start();

        System.out.println("Unsafe value: " + unsafe.value());
        System.out.println("Volatile value: " + volCounter.value());
        System.out.println("SyncSafe value: " + syncSafe.value());
        System.out.println("LockCounter values: " + lockCounter.value1() + " - " + lockCounter.value2());
    }
}

class Op1 implements Runnable {

    private final UnsafeCounter unsafe;
    private VolatileCounter volCounter;
    private final SyncSafeCounter syncSafe;
    private LockCounter lockCounter;

    public Op1(UnsafeCounter unsafe, VolatileCounter volCounter, SyncSafeCounter syncSafe, LockCounter lockCounter) {
        this.unsafe = unsafe;
        this.volCounter = volCounter;
        this.syncSafe = syncSafe;
        this.lockCounter = lockCounter;
    }

    @Override
    public void run() {
        for(int i = 0; i < Synchronisation.SIZE; i++) {
            unsafe.inc();
            volCounter.inc();
            syncSafe.increment();
            lockCounter.inc1();
            lockCounter.inc2();
        }
    }
}

class UnsafeCounter {
    private int c = 0;

    // unsafe increment, not synchronised, may cause race conditions
    public void inc() {
        c++;
    }

    // unsafe decrement, not synchronised, may cause race conditions
    public void dec() {
        c--;
    }

    public int value() {
        return c;
    }
}

class VolatileCounter {
    private volatile int c = 0;

    // unsafe increment, not synchronised, may cause race conditions
    public void inc() {
        c++;
    }

    // unsafe decrement, not synchronised, may cause race conditions
    public void dec() {
        c--;
    }

    public int value() {
        return c;
    }
}

class SyncSafeCounter {
    private int c = 0;

    //safe increment at method level, only 1 thread at a time will access this method
    public synchronized void increment() {
        c++;
    }

    //safe decrement at method level, only 1 thread at a time will access this method
    public synchronized void decrement() {
        c--;
    }

    public synchronized int value() {
        return c;
    }
}

class LockCounter {
    private int c1 = 0;
    private int c2 = 0;
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void inc1() {
        synchronized(lock1) {
            c1++;
        }
    }

    public void inc2() {
        synchronized(lock2) {
            c2++;
        }
    }

    public int value1() {
        return c1;
    }

    public int value2() {
        return c2;
    }
}