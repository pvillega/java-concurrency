package com.perevillega;

import java.util.concurrent.ExecutionException;

public class HowToCreateThreads {

    public static final int SIZE = 10;

    /**
     * Creates two threads using different constructs
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Thread runnable = new Thread(new HelloRunnable());
        Thread thread = new HelloThread(runnable);

        runnable.start();
        thread.start();

        // we launch interrupt signals against our threads
        runnable.interrupt();
        thread.interrupt();
    }
}

class HelloRunnable implements Runnable {
    @Override
    public void run() {
        for(int i = 0; i < HowToCreateThreads.SIZE; i++) {
            System.out.println(i + "> Runnable");
            // do some heavy lifting in here,
            int j = 0;
            while(j < Math.pow(HowToCreateThreads.SIZE,8)) {
                j++;
            }
            // as we are doing heavy operations in this loop, and there is no 'sleep' that could raise an InterruptedException
            // we check at this point if we should stop processing due to an interrupt signal
            if (Thread.interrupted()) {
                // we react to interrupt messages into this thread
                System.out.println(i + "> Runnable was interrupted. We continue our processing but we could abort");
            }
        }
    }
}

class HelloThread extends Thread {

    private Thread other;

    public HelloThread(Thread other) {
        this.other = other;
    }

    @Override
    public void run() {
        for(int i = 0; i < HowToCreateThreads.SIZE; i++) {
            System.out.println(i + "> Thread");
            try {
                // we sleep thread to ensure alternating output
                Thread.sleep(30);
            } catch (InterruptedException e) {
                // we react to interrupt messages into this thread
                System.out.println(i + "> Thread was interrupted. We stop our processing until the other thread finishes");
                try {
                    other.join();
                } catch (InterruptedException e1) {
                    System.out.println(i + "> Thread join was interrupted. We continue our processing but we could abort");
                }

            }
        }
    }
}
