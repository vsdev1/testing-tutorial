package de.vsdev.sandbox.testing.tutorial.concurrent;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VisibilityTest {

    private boolean stopped = false;

    @Test
    public void testStopping() throws Exception {
        stopInDifferentThread();

        while (!stopped) {
            Thread.sleep(10L);
        }

        System.out.println("finished");
    }

    private void stopInDifferentThread() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            stopped = true;
        });
    }
}
