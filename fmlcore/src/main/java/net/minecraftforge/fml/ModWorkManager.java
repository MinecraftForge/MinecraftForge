/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml;

import net.minecraftsingularity.fml.loading.FMLConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

import static net.minecraftsingularity.fml.Logging.LOADING;

public final class ModWorkManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final long PARK_TIME = TimeUnit.MILLISECONDS.toNanos(1);

    public sealed interface DrivenExecutor extends Executor {
        void drive(Runnable ticker);
    }

    private record SyncExecutor(ConcurrentLinkedDeque<Runnable> tasks) implements DrivenExecutor {
        public SyncExecutor() {
            this(new ConcurrentLinkedDeque<>());
        }

        private boolean driveOne() {
            final Runnable task = tasks.pollFirst();
            if (task != null) {
                task.run();
            }
            return task != null;
        }

        @Override
        public void execute(final Runnable command) {
            tasks.addLast(command);
        }

        @Override
        public void drive(Runnable ticker) {
            ticker.run();
            while (true) {
                if (!driveOne()) break;
            }
        }
    }

    private record WrappingExecutor(Executor wrapped) implements DrivenExecutor {
        @Override
        public void execute(final Runnable command) {
            wrapped.execute(command);
        }

        @Override
        public void drive(Runnable ticker) {
            // Park for a bit so other threads can schedule
            LockSupport.parkNanos(PARK_TIME);
        }
    }

    private static final SyncExecutor syncExecutor = new SyncExecutor();

    public static DrivenExecutor syncExecutor() {
        return syncExecutor;
    }

    public static DrivenExecutor wrappedExecutor(Executor executor) {
        return new WrappingExecutor(executor);
    }

    public static Executor parallelExecutor() {
        return LazyInit.PARALLEL_EXECUTOR;
    }

    private static ForkJoinWorkerThread newForkJoinWorkerThread(ForkJoinPool pool) {
        ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
        thread.setName("modloading-worker-" + thread.getPoolIndex());
        // The default sets it to the SystemClassloader, so copy the current one.
        thread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
        return thread;
    }

    private static final class LazyInit {
        private LazyInit() {}
        private static final ForkJoinPool PARALLEL_EXECUTOR;

        static {
            final int loadingThreadCount = FMLConfig.getIntConfigValue(FMLConfig.ConfigValue.MAX_THREADS);
            LOGGER.debug(LOADING, "Using {} threads for parallel mod-loading", loadingThreadCount);
            PARALLEL_EXECUTOR = new ForkJoinPool(loadingThreadCount, ModWorkManager::newForkJoinWorkerThread, null, false);
        }
    }
}
