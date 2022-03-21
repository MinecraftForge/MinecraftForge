/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import net.minecraftforge.fml.loading.VersionInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public abstract class FMLUserdevLaunchHandler extends CommonUserdevLaunchHandler {
    @Override
    protected void processStreams(String[] classpath, VersionInfo versionInfo, Stream.Builder<Path> mc, Stream.Builder<List<Path>> mods) {
        var fmlonly = findJarOnClasspath(classpath, "fmlonly-" + versionInfo.mcAndForgeVersion());
        mc.add(fmlonly);
    }
}
