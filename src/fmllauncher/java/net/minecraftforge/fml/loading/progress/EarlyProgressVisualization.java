/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

