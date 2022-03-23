/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.task;

import net.minecraft.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class ForgeTaskFactories {

    public static IAsyncTaskConfigurator simpleAsyncCapable(final Executor syncExecutor)
    {
          return simpleAsyncCapable(
                  Util.backgroundExecutor(),
                  syncExecutor
          );
    }

    public static IAsyncTaskConfigurator simpleAsyncCapable(final Executor asyncExecutor, final Executor syncExecutor)
    {
        return new ExecutorAsyncTaskConfigurator(
                asyncExecutor,
                syncExecutor
        );
    }

    public static IAsyncTaskConfigurator simpleBarrierAware(final BarrierContext barrierContext) {
        return simpleBarrierAware(barrierContext, Util.backgroundExecutor());
    }

    public static IAsyncTaskConfigurator simpleBarrierAware(final BarrierContext barrierContext, final Executor asyncExecutor) {
        return simpleBarrierAware(barrierContext, asyncExecutor, barrierContext.getBarrierExecutor());
    }

    public static IAsyncTaskConfigurator simpleBarrierAware(final BarrierContext barrierContext, final Executor asyncExecutor, final Executor syncExecutor) {
        return new BarrierAwareAsyncTaskConfigurator(
                asyncExecutor,
                syncExecutor,
                barrierContext.getBuilder()
        );
    }

    public static BarrierContext barrierFor(final Executor executor) {
        return new SynchronizedBarrierContext(executor);
    }

    public static CompletableFuture<Void> build(final ISyncTaskConfigurator configurator) {
        if (configurator instanceof ICompletableFutureBuilder builder)
            return builder.build();

        throw new IllegalArgumentException("The configurator is not a completable future builder.");
    }

    public interface BarrierContext {
        Executor getBarrierExecutor();

        BarrierAwareAsyncTaskConfigurator.BarrierBuilder getBuilder();

        void start();
    }

    private static class SynchronizedBarrierContext implements BarrierContext {

        private final Executor barrierOn;

        private final AtomicInteger nextContextId = new AtomicInteger(0);
        private final List<Integer> expectedContexts = Collections.synchronizedList(new ArrayList<>());

        private final CompletableFuture<Void> initialBarrierTask = new CompletableFuture<>();
        private final CompletableFuture<Void> primaryBarrierTask = new CompletableFuture<>();

        private SynchronizedBarrierContext(Executor barrierOn) {
            this.barrierOn = barrierOn;
        }

        @Override
        public Executor getBarrierExecutor() {
            return barrierOn;
        }

        @Override
        public BarrierAwareAsyncTaskConfigurator.BarrierBuilder getBuilder() {
            final int contextId = nextContextId.getAndIncrement();
            expectedContexts.add(contextId);
            return new BarrierAwareAsyncTaskConfigurator.BarrierBuilder() {
                @Override
                public <T> CompletableFuture<T> stage(T input) {
                    barrierOn.execute(() -> {
                        expectedContexts.removeIf(id -> id == contextId);
                        if (expectedContexts.isEmpty()) {
                            primaryBarrierTask.complete(null);
                        }

                    });

                    return primaryBarrierTask.thenApplyAsync((ignored) -> input, barrierOn);
                }

                @Override
                public CompletableFuture<Void> initial() {
                    return initialBarrierTask;
                }
            };
        }

        @Override
        public void start() {
            this.initialBarrierTask.completeAsync(() -> null, this.barrierOn);
        }
    }
}
