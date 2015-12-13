package de.vsdev.sandbox.testing.tutorial.concurrent;

public class VolatileCounter implements Counter {

    private volatile long counter;

    @Override
    public void increment() {
        counter++;
    }

    @Override
    public long getResult() {
        return counter;
    }
}
