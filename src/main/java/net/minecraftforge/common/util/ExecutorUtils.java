/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

public class ExecutorUtils {

    private static final ThreadLocal<LocalEventLoop> localEventLoop = ThreadLocal.withInitial(LocalEventLoop::new);

    public static Executor getLocalThreadExecutor() {
        return localEventLoop.get();
    }

    public static void managedBlock(BooleanSupplier isDoneChecked) {
        while(!isDoneChecked.getAsBoolean()) {
            if (!localEventLoop.get().pollTask()) {
                localEventLoop.get().waitForTasks();
            }
        }
    }

    private static class LocalEventLoop extends ReentrantBlockableEventLoop<Runnable> {

        private final Thread localThread;

        public LocalEventLoop() {
            super(Thread.currentThread().getName());
            localThread = Thread.currentThread();
        }

        @Override
        protected @NotNull Runnable wrapRunnable(@NotNull Runnable runnable) {
            return runnable;
        }

        @Override
        protected boolean shouldRun(@NotNull Runnable runnable) {
            return true;
        }

        @Override
        protected @NotNull Thread getRunningThread() {
            return localThread;
        }

        @Override
        public void waitForTasks() {
            super.waitForTasks();
        }
    }
}
