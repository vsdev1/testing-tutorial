package de.vsdev.sandbox.testing.tutorial.concurrent;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockCounter implements Counter {

    private long counter;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Override
    public void increment() {
        readWriteLock.writeLock().lock();
        try {
            counter++;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public long getResult() {
        readWriteLock.readLock().lock();
        try {
            return counter;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
