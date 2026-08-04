/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.progress;

import net.minecraftforge.api.distmarker.Dist;

import java.util.Locale;

public enum EarlyProgressVisualization {
    INSTANCE;

    private Visualization visualization;

    public void accept(final Dist dist) {
        if (visualization != null) return;
        // We don't show the window on Mac because mac is super mega triple shit and can't handle anything out of the ordinary
        final boolean ismac = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac");
        visualization = dist.isClient() && !ismac && Boolean.parseBoolean(System.getProperty("fml.earlyprogresswindow", "true")) ? new ClientVisualization() : new NoVisualization();
        visualization.start();
    }

    public void join() {
        visualization.join();
    }

    interface Visualization {
        void start();
        void join();
    }

    private static class NoVisualization implements Visualization {
        @Override
        public void start() {
        }

        @Override
        public void join() {
        }
    }

}

