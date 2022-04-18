/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import net.minecraftforge.fml.loading.LibraryFinder;
import net.minecraftforge.fml.loading.VersionInfo;

public class FMLClientLaunchHandler extends CommonClientLaunchHandler {
    @Override public String name() { return "fmlclient"; }

    @Override
    protected void processMCStream(VersionInfo versionInfo, Stream.Builder<Path> mc, Stream.Builder<List<Path>> mods) {
        var fmlonly = LibraryFinder.findPathForMaven(versionInfo.forgeGroup(), "fmlonly", "", "universal", versionInfo.mcAndForgeVersion());
        mods.add(List.of(fmlonly));
    }
}
