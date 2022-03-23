/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.task;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

class ExecutorTypedSyncTaskConfigurator<T> implements ITypedSyncTaskConfigurator<T>, ICompletableFutureBuilder  {

    private final Executor syncExecutor;
    private final CompletableFuture<T> asyncCode;

    private CompletableFuture<Void> syncCode;

    ExecutorTypedSyncTaskConfigurator(final Executor syncExecutor, CompletableFuture<T> asyncCode) {
        this.syncExecutor = syncExecutor;
        this.asyncCode = asyncCode;
        this.syncCode = asyncCode.thenAcceptAsync((ignored) -> {}, syncExecutor);
    }

    @Override
    public void sync(Consumer<T> consumer) {
        this.syncCode = asyncCode.thenAcceptAsync(consumer, this.syncExecutor);
    }

    @Override
    public void sync(Runnable runnable) {
        this.syncCode = asyncCode.thenRunAsync(runnable, this.syncExecutor);
    }

    public CompletableFuture<Void> build() {
        return syncCode;
    }
}
