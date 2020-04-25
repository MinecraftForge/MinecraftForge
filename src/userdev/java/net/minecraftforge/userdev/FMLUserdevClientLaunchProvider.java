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

package net.minecraftforge.userdev;

import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.LibraryFinder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public class FMLUserdevClientLaunchProvider extends FMLUserdevLaunchProvider implements ILaunchHandlerService {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public Dist getDist() {
        return Dist.CLIENT;
    }

    @Override
    public String name() {
        return "fmluserdevclient";
    }

    @Override
    public Callable<Void> launchService(String[] arguments, ITransformingClassLoader launchClassLoader)
    {
        return () -> {
            LOGGER.debug(CORE, "Launching minecraft in {} with arguments {}", launchClassLoader, arguments);
            super.beforeStart(launchClassLoader);
            launchClassLoader.addTargetPackageFilter(getPackagePredicate());
            Class.forName("net.minecraft.client.main.Main", true, launchClassLoader.getInstance()).getMethod("main", String[].class).invoke(null, (Object)arguments);
            return null;
        };
    }
}
