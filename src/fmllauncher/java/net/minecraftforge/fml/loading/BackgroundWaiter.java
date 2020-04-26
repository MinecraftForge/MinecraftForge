package net.minecraftforge.fml.loading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BackgroundWaiter {
    private static ExecutorService runner = Executors.newSingleThreadExecutor();

    public static void runAndTick(Runnable r, Runnable tick) {
        final Future<?> work = runner.submit(r);
        do {
            tick.run();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        } while (!work.isDone());
    }
}
