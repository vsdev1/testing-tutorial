package de.vsdev.sandbox.testing.tutorial.concurrent;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class LongAdderCounter implements Counter {

    private final LongAdder counter = new LongAdder();

    @Override
    public void increment() {
        counter.increment();
    }

    @Override
    public long getResult() {
        return counter.longValue();
    }
}
