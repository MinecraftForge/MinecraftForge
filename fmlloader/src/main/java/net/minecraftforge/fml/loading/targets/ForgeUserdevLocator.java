/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.AbstractModProvider;
import net.minecraftforge.forgespi.locating.IModLocator;

@ApiStatus.Internal
public class ForgeUserdevLocator extends AbstractModProvider implements IModLocator {
    @Override
    public String name() {
        return "forge_userdev_locator";
    }

    @Override
    public List<ModFileOrException> scanMods() {
        var handler = FMLLoader.getLaunchHandler();

        if (!(handler instanceof ForgeUserdevLaunchHandler))
            return List.of();
        var forge = ForgeDevLaunchHandler.getPathFromResource("net/minecraftforge/common/MinecraftForge.class");
        var filtered = ForgeUserdevLaunchHandler.getForgeOnly(forge);
        var mod = createMod(filtered);
        return List.of(mod);
    }
}
