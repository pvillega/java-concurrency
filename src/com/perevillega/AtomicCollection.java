package com.perevillega;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AtomicCollection {

    public static final Random random = new Random();
    public static final ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

    protected static Executor cachedPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        for(int i = 0; i < 10000; i++) {
            cachedPool.execute(new ConcurrentMapUser());
        }
    }
}


class ConcurrentMapUser implements Runnable {

    public void run() {
        try {
            Thread.sleep(AtomicCollection.random.nextInt(5000));
        } catch (InterruptedException e) {
            System.out.println("Interrupted, ignore");
        }
        AtomicCollection.map.putIfAbsent(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        AtomicCollection.map.remove(AtomicCollection.map.keySet().iterator().next());
        System.out.println("ConcurrentMap size: " + AtomicCollection.map.size());
    }
}