/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.task;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

class BarrierAwareAsyncTaskConfigurator extends ExecutorAsyncTaskConfigurator {

    private final BarrierBuilder barrierBuilder;

    BarrierAwareAsyncTaskConfigurator(
            Executor asyncExecutor,
            Executor syncExecutor,
            BarrierBuilder barrierBuilder) {
        super(asyncExecutor, syncExecutor);
        this.barrierBuilder = barrierBuilder;
    }

    @Override
    protected @NotNull <T> ExecutorTypedSyncTaskConfigurator<T> buildSyncExecutorConfigurator(CompletableFuture<T> asyncCode) {
        return super.buildSyncExecutorConfigurator(
                this.barrierBuilder.initial().thenComposeAsync((ignored) -> asyncCode, this.asyncExecutor)
                        .thenComposeAsync(this.barrierBuilder::stage, this.syncExecutor)
        );
    }

    public interface BarrierBuilder {
        <T> CompletableFuture<T> stage(T input);

        CompletableFuture<Void> initial();
    }
}
