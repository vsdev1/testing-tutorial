package de.vsdev.sandbox.testing.tutorial.concurrent;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongCounter implements Counter {

    private volatile AtomicLong counter = new AtomicLong();

    @Override
    public void increment() {
        counter.incrementAndGet();
    }

    @Override
    public long getResult() {
        return counter.get();
    }
}
