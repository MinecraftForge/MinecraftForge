/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BackgroundWaiter {

    public static void runAndTick(Runnable r, Runnable tick) {
        var runner = Executors.newSingleThreadExecutor();
        ImmediateWindowHandler.updateProgress("Loading bootstrap resources");
        final Future<?> work = runner.submit(r);
        do {
            tick.run();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        } while (!work.isDone());
        try {
            runner.shutdown();
            work.get();
        } catch (ExecutionException ee) {
            throw new RuntimeException(ee.getCause());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
