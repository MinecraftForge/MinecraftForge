/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.userdev;

import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import net.minecraftforge.api.distmarker.Dist;
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
