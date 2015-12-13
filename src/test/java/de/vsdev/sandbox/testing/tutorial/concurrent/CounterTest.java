package de.vsdev.sandbox.testing.tutorial.concurrent;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CounterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CounterTest.class);

    private static final int THREAD_COUNT = 50;
    private static final long ITERATIONS_PER_THREAD = 1000000L;

    private Counter counter;

    @BeforeClass
    public static void setUp() {
        LOGGER.info("thread count: {}", THREAD_COUNT);
        LOGGER.info("iterations per thread: {}", ITERATIONS_PER_THREAD);
    }

    @Test
    public void longCounterIsBroken() throws Exception {
        verifyCounting(new LongCounter());
    }

    @Test
    public void volatileCounterIsBroken() throws Exception {
        verifyCounting(new VolatileCounter());
    }

    @Test
    public void synchronizedCounterWorks() throws Exception {
        verifyCounting(new SynchronizedCounter());
    }

    @Test
    public void readWriteLockCounterWorks() throws Exception {
        verifyCounting(new ReadWriteLockCounter());
    }

    @Test
    public void stampedLockCounterWorks() throws Exception {
        verifyCounting(new StampedLockCounter());
    }

    @Test
    public void atomicLongCounterWorks() throws Exception {
        verifyCounting(new AtomicLongCounter());
    }

    @Test
    public void longAdderCounterWorks() throws Exception {
        verifyCounting(new LongAdderCounter());
    }

    private void verifyCounting(final Counter counterToTest) throws Exception {
        counter = counterToTest;
        LOGGER.info("Tested counter is: {}", counter.getClass().getSimpleName());
        verifyCounting(THREAD_COUNT, ITERATIONS_PER_THREAD);
    }

    private void verifyCounting(final int threadCount, final long iterationsPerThread) throws Exception {
        long startTime = System.currentTimeMillis();

        List<Future<Void>> futures = countInThreads(threadCount);

        // read concurrently in order to measure the performance impact of locking
        readCounterResultInThreads(THREAD_COUNT);

        collectCountingResults(futures);
        assertThat(futures.size(), is(equalTo(threadCount)));

        long endTime = System.currentTimeMillis();
        LOGGER.info("Elapsed time in millis: {}", (endTime - startTime));

        assertThat(counter.getResult(), is(equalTo(threadCount * iterationsPerThread)));
    }

    private void collectCountingResults(List<Future<Void>> futures) throws InterruptedException, ExecutionException {
        for (Future<Void> future : futures) {
            future.get();
        }
    }

    private List<Future<Void>> countInThreads(int threadCount) throws InterruptedException {
        List<Callable<Void>> tasks = Collections.nCopies(threadCount, createCountWriterTask());
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        return executorService.invokeAll(tasks);
    }

    private List<Future<Void>> readCounterResultInThreads(int threadCount) throws InterruptedException {
        List<Callable<Void>> tasks = Collections.nCopies(threadCount, createResultReaderTask());
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        return executorService.invokeAll(tasks);
    }

    private Callable<Void> createCountWriterTask() {
        return () -> {
            for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                counter.increment();
            }

            return null;
        };
    }

    private Callable<Void> createResultReaderTask() {
        return () -> {
            for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                counter.getResult();
            }

            return null;
        };
    }
}