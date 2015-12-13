package de.vsdev.sandbox.testing.tutorial.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronizedCounter implements Counter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizedCounter.class);

    private long counter;

    @Override
    public synchronized void increment() {
        counter++;
//        LOGGER.info("incremented to: " + counter);
//        System.out.println("incremented to: " + counter);
    }

    @Override
    public synchronized long getResult() {
//        System.out.println("result is: " + counter);
        return counter;
    }
}
