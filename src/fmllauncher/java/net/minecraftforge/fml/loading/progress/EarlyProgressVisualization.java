/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public enum EarlyProgressVisualization {
    INSTANCE;

    private Visualization visualization;

    public Runnable accept(Dist dist, boolean isData)
    {
        return accept(dist, isData, null);
    }

    public Runnable accept(final Dist dist, final boolean isData, @Nullable String mcVersion) {
        visualization = !isData && dist.isClient() && Boolean.parseBoolean(System.getProperty("fml.earlyprogresswindow", "true")) ? new ClientVisualization() : new NoVisualization();
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
            return new LongSupplier() {
                @Override
                public long getAsLong() {
                    return org.lwjgl.glfw.GLFW.glfwCreateWindow(width.getAsInt(), height.getAsInt(), title.get(), monitorSupplier.getAsLong(), 0L);
                }
            }.getAsLong();
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

