/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import cpw.mods.modlauncher.api.ServiceRunner;
import net.minecraftforge.api.distmarker.Dist;
import java.util.concurrent.Callable;

public class FMLServerDevLaunchHandler extends CommonDevLaunchHandler {
    @Override public String name() { return "fmlserverdev"; }
    @Override public Dist getDist() { return Dist.DEDICATED_SERVER; }

    @Override
    public ServiceRunner launchService(String[] arguments, ModuleLayer layer) {
        return () -> {
            var args = preLaunch(arguments, layer);

            Class.forName(layer.findModule("minecraft").orElseThrow(),"net.minecraft.server.Main").getMethod("main", String[].class).invoke(null, (Object)args);
        };
    }
}