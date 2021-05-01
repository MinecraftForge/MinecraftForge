/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml;

import net.minecraftforge.fml.loading.FMLConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

import static net.minecraftforge.fml.Logging.LOADING;

public class ModWorkManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final long PARK_TIME = TimeUnit.MILLISECONDS.toNanos(1);
    public interface DrivenExecutor extends Executor {
        boolean selfDriven();
        boolean driveOne();

        default void drive(Runnable ticker) {
            if (!selfDriven()) {
                while (driveOne()) {
                    ticker.run();
                }
            } else {
                // park for a bit so other threads can schedule
                LockSupport.parkNanos(PARK_TIME);
            }
        }
    }
    private static class SyncExecutor implements DrivenExecutor {
        private ConcurrentLinkedDeque<Runnable> tasks = new ConcurrentLinkedDeque<>();

        @Override
        public boolean driveOne() {
            final Runnable task = tasks.pollFirst();
            if (task != null) {
                task.run();
            }
            return task != null;
        }

        @Override
        public boolean selfDriven() {
            return false;
        }

        @Override
        public void execute(final Runnable command) {
            tasks.addLast(command);
        }
    }

    private static class WrappingExecutor implements DrivenExecutor {
        private final Executor wrapped;

        public WrappingExecutor(final Executor executor) {
            this.wrapped = executor;
        }

        @Override
        public boolean selfDriven() {
            return true;
        }

        @Override
        public boolean driveOne() {
            return false;
        }

        @Override
        public void execute(final Runnable command) {
            wrapped.execute(command);
        }
    }

    private static SyncExecutor syncExecutor;

    public static DrivenExecutor syncExecutor() {
        if (syncExecutor == null)
            syncExecutor = new SyncExecutor();
        return syncExecutor;
    }

    public static DrivenExecutor wrappedExecutor(Executor executor) {
        return new WrappingExecutor(executor);
    }

    private static ForkJoinPool parallelThreadPool;
    public static Executor parallelExecutor() {
        if (parallelThreadPool == null) {
            final int loadingThreadCount = FMLConfig.loadingThreadCount();
            LOGGER.debug(LOADING, "Using {} threads for parallel mod-loading", loadingThreadCount);
            parallelThreadPool = new ForkJoinPool(loadingThreadCount, ModWorkManager::newForkJoinWorkerThread, null, false);
        }
        return parallelThreadPool;
    }

    private static ForkJoinWorkerThread newForkJoinWorkerThread(ForkJoinPool pool) {
        ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
        thread.setName("modloading-worker-" + thread.getPoolIndex());
        // The default sets it to the SystemClassloader, so copy the current one.
        thread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
        return thread;
    }

}
