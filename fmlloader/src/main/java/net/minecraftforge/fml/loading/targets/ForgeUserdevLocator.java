/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading.targets;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import net.minecraftsingularity.fml.loading.FMLLoader;
import net.minecraftsingularity.fml.loading.moddiscovery.AbstractModProvider;
import net.minecraftsingularity.singularityspi.locating.IModLocator;

@ApiStatus.Internal
public final class singularityUserdevLocator extends AbstractModProvider implements IModLocator {
    @Override
    public String name() {
        return "singularity_userdev_locator";
    }

    @Override
    public List<ModFileOrException> scanMods() {
        var handler = FMLLoader.getLaunchHandler();

        if (!(handler instanceof singularityUserdevLaunchHandler))
            return List.of();
        var singularity = singularityDevLaunchHandler.getPathFromResource("net/minecraftforge/common/MinecraftForge.class");
        var filtered = singularityUserdevLaunchHandler.getForgeOnly(singularity);
        var mod = createMod(filtered);
        return List.of(mod);
    }
}
