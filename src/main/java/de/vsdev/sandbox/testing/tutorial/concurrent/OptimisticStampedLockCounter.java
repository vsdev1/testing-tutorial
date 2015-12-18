package de.vsdev.sandbox.testing.tutorial.concurrent;

import java.util.concurrent.locks.StampedLock;

public class OptimisticStampedLockCounter implements Counter {

    private long counter;
    private final StampedLock stampedLock = new StampedLock();

    @Override
    public void increment() {
        long stamp = stampedLock.writeLock(); // blocking lock, returns a stamp
        try {
            counter++;
        } finally {
            stampedLock.unlockWrite(stamp); // release the lock in the same block
        }
    }

    @Override
    public long getResult() {
        long stamp = stampedLock.tryOptimisticRead(); // non blocking
        long result = counter;
        if (!stampedLock.validate(stamp)) {
            // if a write occurred, try again with a read lock
            stamp = stampedLock.readLock();
            try {
                return counter;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        return result;
    }
}
