/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import cpw.mods.modlauncher.api.ServiceRunner;
import net.minecraftforge.api.distmarker.Dist;

public class ForgeClientUserdevLaunchHandler extends ForgeUserdevLaunchHandler {
    @Override
    public String name() { return "forgeclientuserdev"; }

    @Override
    public Dist getDist() { return Dist.CLIENT; }

    @Override
    public ServiceRunner launchService(String[] arguments, ModuleLayer layer) {
        return () -> {
            var args = preLaunch(arguments, layer);

            Class.forName(layer.findModule("minecraft").orElseThrow(), "net.minecraft.client.main.Main").getMethod("main", String[].class).invoke(null, (Object) args);
        };
    }
}
