/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.electronwill.nightconfig.core.Config;
import cpw.mods.jarhandling.SecureJar;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import net.minecraftforge.forgespi.locating.ModFileFactory;
import java.nio.file.Path;
import java.util.List;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class MinecraftLocator extends AbstractModProvider implements IModLocator {
    @Override
    public List<IModLocator.ModFileOrException> scanMods() {
        var minecraft = FMLLoader.getLaunchHandler().getMinecraftPaths();
        var paths = minecraft.toArray(Path[]::new);

        // Minecraft itself.
        var meta = new ModJarMetadata();
        var mcjar = SecureJar.from(jar -> meta, paths);
        var mc = ModFileFactory.FACTORY.build(mcjar, this, MinecraftLocator::buildMinecraftTOML);
        meta.setModFile(mc);

        return List.of(new ModFileOrException(mc, null));
    }

    private static IModFileInfo buildMinecraftTOML(final IModFile iModFile) {
        // We haven't changed this in years, and I can't be asked right now to special case this one file in the path.
        final var conf = Config.inMemory();
        conf.set("modLoader", "minecraft");
        conf.set("loaderVersion", "1");
        conf.set("license", "Mojang Studios, All Rights Reserved");
        final var mods = Config.inMemory();
        mods.set("modId", "minecraft");
        mods.set("version", FMLLoader.versionInfo().mcVersion());
        mods.set("displayName", "Minecraft");
        mods.set("logoFile", "mcplogo.png");
        mods.set("credits", "Mojang, deobfuscated by MCP");
        mods.set("authors", "MCP: Searge,ProfMobius,IngisKahn,Fesh0r,ZeuX,R4wk,LexManos,Bspkrs");
        mods.set("description", "Minecraft, decompiled and deobfuscated with MCP technology");
        conf.set("mods", List.of(mods));

        var configWrapper = new NightConfigWrapper(conf);
        return new ModFileInfo((ModFile) iModFile, configWrapper, configWrapper::setFile);
    }

    @Override
    public String name() {
        return "minecraft";
    }
}
