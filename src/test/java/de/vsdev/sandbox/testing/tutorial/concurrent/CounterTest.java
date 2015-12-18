package de.vsdev.sandbox.testing.tutorial.concurrent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CounterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CounterTest.class);

    private static final int WRITER_THREAD_COUNT = 2;
    private static final int READER_THREAD_COUNT = 1;
    private static final long ITERATIONS_PER_THREAD = 100000000L;

    private Counter counter;

    @BeforeClass
    public static void setUp() {
        LOGGER.info("writer thread count: {}", WRITER_THREAD_COUNT);
        LOGGER.info("reader thread count: {}", READER_THREAD_COUNT);
        LOGGER.info("iterations per thread: {}", ITERATIONS_PER_THREAD);
    }

    @Test
    public void longCounterIsBroken() throws Exception {
        verifyCounting(new LongCounter());
    }

    @Test
    public void volatileCounterIsBroken() throws Exception {
    }

    @Test
    public void synchronizedCounterWorks() throws Exception {
    }

    @Test
    public void readWriteLockCounterWorks() throws Exception {
    }

    @Test
    public void stampedLockCounterWorks() throws Exception {
    }

    @Test
    public void optimisticStampedLockCounterWorks() throws Exception {
    }

    @Test
    public void atomicLongCounterWorks() throws Exception {
    }

    @Test
    public void longAdderCounterWorks() throws Exception {
    }

    private void verifyCounting(final Counter counterToTest) throws Exception {
        counter = counterToTest;
        LOGGER.info("Tested counter is: {}", counter.getClass().getSimpleName());
        verifyCounting(WRITER_THREAD_COUNT, ITERATIONS_PER_THREAD);
    }

    private void verifyCounting(final int threadCount, final long iterationsPerThread) throws Exception {
        long startTime = System.currentTimeMillis();

        List<Future<Void>> futures = countInThreads(threadCount);

        // read concurrently in order to measure the performance impact of locking
        final List<Future<Void>> readingFutures = readCounterResultInThreads(READER_THREAD_COUNT);

        collectCountingResults(futures);
        assertThat(futures.size(), is(equalTo(threadCount)));

        readingFutures.parallelStream().forEach(readingFuture -> readingFuture.cancel(true));

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

        return  executorService.invokeAll(tasks);
    }

    private List<Future<Void>> readCounterResultInThreads(int threadCount) throws InterruptedException {
        List<Callable<Void>> tasks = Collections.nCopies(threadCount, createResultReaderTask());
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        final List<Future<Void>> futures = new ArrayList<>(tasks.size());
        for (Callable<Void> task : tasks) {
            futures.add(executorService.submit(task));
        }

        return futures;
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
            while (true) {
                counter.getResult();
            }
        };
    }
}