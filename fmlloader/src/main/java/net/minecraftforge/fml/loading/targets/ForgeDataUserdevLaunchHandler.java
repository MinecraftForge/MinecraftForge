/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import net.minecraftforge.api.distmarker.Dist;

public class ForgeDataUserdevLaunchHandler extends ForgeUserdevLaunchHandler {
    @Override
    public String name() { return "forgedatauserdev"; }

    @Override
    public Dist getDist() { return Dist.CLIENT; }

    @Override
    public boolean isData() { return true; }

    @Override
    public void devService(String[] arguments, ModuleLayer layer) throws Throwable {
        dataService(arguments, layer);
    }
}
