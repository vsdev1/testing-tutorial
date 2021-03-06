package de.vsdev.sandbox.testing.tutorial.concurrent;

public class LongCounter implements Counter {

    private long counter;

    @Override
    public void increment() {
        counter++;
    }

    @Override
    public long getResult() {
        return counter;
    }
}
