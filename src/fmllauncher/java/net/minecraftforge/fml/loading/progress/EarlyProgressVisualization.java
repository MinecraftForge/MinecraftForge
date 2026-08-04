/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.progress;

import net.minecraftforge.api.distmarker.Dist;

import java.util.Locale;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public enum EarlyProgressVisualization {
    INSTANCE;

    private Visualization visualization;

    public Runnable accept(final Dist dist) {
        visualization = dist.isClient() && Boolean.parseBoolean(System.getProperty("fml.earlyprogresswindow", "true")) ? new ClientVisualization() : new NoVisualization();
        return visualization.start();
    }

    public long handOffWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, final LongSupplier monitor) {
        return visualization.handOffWindow(width, height, title, monitor);
    }

    public boolean replacedWindow() {
        return visualization.replacedWindow();
    }

    interface Visualization {
        Runnable start();

        default long handOffWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, LongSupplier monitorSupplier) {
            return new LongSupplier() {
                @Override
                public long getAsLong() {
                    return org.lwjgl.glfw.GLFW.glfwCreateWindow(width.getAsInt(), height.getAsInt(), title.get(), monitorSupplier.getAsLong(), 0L);
                }
            }.getAsLong();
        }

        default boolean replacedWindow() { return false; }
    }

    private static class NoVisualization implements Visualization {
        @Override
        public Runnable start() {
            return () -> {};
        }
    }

}

