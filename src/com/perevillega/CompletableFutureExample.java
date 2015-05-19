package com.perevillega;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class CompletableFutureExample {

    public static void main(String[] args) throws InterruptedException {
        Holder result = new SlowCalculation().run();
        System.out.println("Holder value: " + result.getValue());
    }
}


class SlowCalculation  {

    public Holder run() {
        // we ensure we update the reference to best result atomically
        AtomicReference<Holder> best = new AtomicReference<>(new Holder(0));
        int size = 100;

        // latch to ensure we have at most 'size' threads ongoing
        CountDownLatch latch = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            CompletableFuture.supplyAsync(this::calculateNewHolder)
                    .thenAccept(result -> {
                        best.accumulateAndGet(result, (holder, holder2) -> holder.getValue() >= holder2.getValue() ? holder : holder2);
                        latch.countDown();
                    });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted during calculations", e);
        }

        return best.get();
    }

    private Holder calculateNewHolder () {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(600));
        } catch (InterruptedException e) {
            System.out.println("Interrupted ignored");
        }
        return new Holder(ThreadLocalRandom.current().nextInt());
    }

}

class Holder {
    private int value;

    public Holder(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
