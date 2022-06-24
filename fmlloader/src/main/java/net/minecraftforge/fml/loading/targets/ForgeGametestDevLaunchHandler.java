/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import net.minecraftforge.api.distmarker.Dist;

public class ForgeGametestDevLaunchHandler extends CommonDevLaunchHandler {
    @Override public String name() { return "forgegametestserverdev"; }
    @Override public Dist getDist() { return Dist.DEDICATED_SERVER; }

    @Override
    public void devService(String[] arguments, ModuleLayer layer) throws Throwable {
        Class.forName(layer.findModule("forge").orElseThrow(), "net.minecraftforge.gametest.GameTestMain").getMethod("main", String[].class).invoke(null, (Object)arguments);
    }
}