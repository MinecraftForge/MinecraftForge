/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.AbstractModProvider;
import net.minecraftforge.forgespi.locating.IModLocator;

@ApiStatus.Internal
public final class ForgeDevLocator extends AbstractModProvider implements IModLocator {
    @Override
    public String name() {
        return "forge_dev_locator";
    }

    @Override
    public List<ModFileOrException> scanMods() {
        var handler = FMLLoader.getLaunchHandler();

        if (!(handler instanceof ForgeDevLaunchHandler))
            return List.of();

        var mods = getMods();
        var ret = new ArrayList<ModFileOrException>();
        for (var path : mods) {
            var mod = createMod(path);
            if (mod != null)
                ret.add(mod);
        }
        return ret;
    }

    private static List<Path> getMods() {
        // Forge is an exploded directory as well
        var minecraft = ForgeDevLaunchHandler.getPathFromResource("net/minecraft/client/Minecraft.class");
        var forge = ForgeDevLaunchHandler.getPathFromResource("net/minecraftforge/common/MinecraftForge.class");
        if (minecraft.equals(forge)) {
            // If both Forge and MC are in the same folder, then we are in intellij or gradle
            // So we have to create a filtered jar
            forge = CommonDevLaunchHandler.getForgeOnly(forge);
        }
        var ret = new ArrayList<Path>();
        ret.add(forge);
        return ret;
    }
}
