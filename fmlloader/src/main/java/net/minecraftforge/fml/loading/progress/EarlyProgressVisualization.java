/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.progress;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.api.distmarker.Dist;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public enum EarlyProgressVisualization {
    INSTANCE;

    private Visualization visualization;

    public Runnable accept(Dist dist, boolean isData)
    {
        return accept(dist, isData, null);
    }

    public Runnable accept(final Dist dist, final boolean isData, @Nullable String mcVersion) {
//        visualization = !isData && dist.isClient() && Boolean.parseBoolean(System.getProperty("fml.earlyprogresswindow", "true")) ? new ClientVisualization() : new NoVisualization();
        visualization = new NoVisualization();
        return visualization.start(mcVersion);
    }

    public long handOffWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, final LongSupplier monitor) {
        return visualization.handOffWindow(width, height, title, monitor);
    }

    public void updateFBSize(IntConsumer width, IntConsumer height) {
        visualization.updateFBSize(width, height);
    }

    interface Visualization {
        Runnable start(@Nullable String mcVersion);

        default long handOffWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, LongSupplier monitorSupplier) {
            return FMLLoader.getGameLayer().findModule("forge")
                    .map(l->Class.forName(l, "net.minecraftforge.client.loading.NoVizFallback"))
                    .map(LamdbaExceptionUtils.rethrowFunction(c->c.getMethod("fallback", IntSupplier.class, IntSupplier.class, Supplier.class, LongSupplier.class)))
                    .map(LamdbaExceptionUtils.rethrowFunction(m->(LongSupplier)m.invoke(null, width, height, title, monitorSupplier)))
                    .map(LongSupplier::getAsLong)
                    .orElseThrow(()->new IllegalStateException("Why are you here?"));
        }

        default void updateFBSize(IntConsumer width, IntConsumer height) {
        }
    }

    private static class NoVisualization implements Visualization {
        @Override
        public Runnable start(@Nullable String mcVersion) {
            return () -> {};
        }
    }

}

