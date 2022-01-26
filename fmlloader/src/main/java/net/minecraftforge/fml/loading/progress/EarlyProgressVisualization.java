/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.util.ServiceLoaderUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.loading.IStartupMessageRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ServiceLoader;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public enum EarlyProgressVisualization implements Visualization
{
    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();

    private Visualization visualization;

    public Runnable accept(Dist dist, boolean isData, IModuleLayerManager moduleLayerManager)
    {
        return accept(dist, isData, null, moduleLayerManager);
    }

    public Runnable accept(final Dist dist, final boolean isData, @Nullable String mcVersion, IModuleLayerManager moduleLayerManager) {
        if (dist != Dist.CLIENT || isData) {
            visualization = new NoVisualization();
            return start(mcVersion);
        }

        var serviceLayer = moduleLayerManager.getLayer(IModuleLayerManager.Layer.SERVICE);
        visualization = serviceLayer.flatMap(moduleLayer -> ServiceLoaderUtils.streamServiceLoader(
            () -> ServiceLoader.load(moduleLayer, Visualization.class),
            sce -> LOGGER.fatal("Encountered serious error loading visualisation service, skipping.", sce)
          )
          .findFirst())
          .orElseGet(NoVisualization::new);

        return start(mcVersion);
    }

    @Override
    public int windowWidth()
    {
        return visualization.windowWidth();
    }

    @Override
    public int windowHeight()
    {
        return visualization.windowHeight();
    }

    @Override
    public Runnable start(@Nullable final String mcVersion)
    {
        return visualization.start(mcVersion);
    }

    public long handOffWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, final LongSupplier monitor) {
        return visualization.handOffWindow(width, height, title, monitor);
    }

    public void updateFBSize(IntConsumer width, IntConsumer height) {
        visualization.updateFBSize(width, height);
    }

    public boolean enabled()
    {
        return !(visualization instanceof NoVisualization);
    }
}

