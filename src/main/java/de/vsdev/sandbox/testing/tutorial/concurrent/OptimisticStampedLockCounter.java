package de.vsdev.sandbox.testing.tutorial.concurrent;

import java.util.concurrent.locks.StampedLock;

public class OptimisticStampedLockCounter implements Counter {

    private long counter;
    private final StampedLock stampedLock = new StampedLock();

    @Override
    public void increment() {
        long stamp = stampedLock.writeLock();
        try {
            counter++;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    @Override
    public long getResult() {
        long stamp = stampedLock.tryOptimisticRead();
        if (!stampedLock.validate(stamp)) {
            stamp = stampedLock.readLock();
            try {
                return counter;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        return counter;
    }
}
