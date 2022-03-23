/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.task;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

class ExecutorAsyncTaskConfigurator implements IAsyncTaskConfigurator, ICompletableFutureBuilder {

    protected final Executor asyncExecutor;
    protected final Executor syncExecutor;

    protected ExecutorTypedSyncTaskConfigurator<?> syncTaskConfigurator = null;

    ExecutorAsyncTaskConfigurator(Executor asyncExecutor, Executor syncExecutor) {
        this.asyncExecutor = asyncExecutor;
        this.syncExecutor = syncExecutor;
    }

    @Override
    public ISyncTaskConfigurator async(Runnable code) {
        return buildSyncExecutorConfigurator(CompletableFuture.runAsync(code, asyncExecutor));
    }

    @Override
    public <T> ITypedSyncTaskConfigurator<T> async(Supplier<T> code) {
        return buildSyncExecutorConfigurator(CompletableFuture.supplyAsync(code, asyncExecutor));
    }

    @Override
    public void sync(Runnable runnable) {
        buildSyncExecutorConfigurator(CompletableFuture.completedFuture(null)).sync(runnable);
    }

    @NotNull
    protected  <T> ExecutorTypedSyncTaskConfigurator<T> buildSyncExecutorConfigurator(CompletableFuture<T> asyncCode) {
        final ExecutorTypedSyncTaskConfigurator<T> result = new ExecutorTypedSyncTaskConfigurator<T>(
                this.syncExecutor,
                asyncCode
        );

        this.syncTaskConfigurator = result;
        return result;
    }

    public CompletableFuture<Void> build() {
        return syncTaskConfigurator.build();
    }
}
