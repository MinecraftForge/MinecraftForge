/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LibraryFinder;

@ApiStatus.Internal
abstract class ForgeProdLaunchHandler extends CommonLaunchHandler {
    protected ForgeProdLaunchHandler(LaunchType type) {
        super(type, "forge_");
    }

    @Override public String getNaming() { return "srg"; }
    @Override public boolean isProduction() { return true; }

    @Override
    public List<Path> getMinecraftPaths() {
        var vers = FMLLoader.versionInfo();
        var mc = LibraryFinder.findPathForMaven(vers.forgeGroup(), "forge", "", this.type.name(), vers.mcAndForgeVersion());
        return List.of(mc);
    }

    public static class Client extends ForgeProdLaunchHandler {
        public Client() {
            super(CLIENT);
        }
    }

    public static class Server extends ForgeProdLaunchHandler {
        public Server() {
            super(SERVER);
        }
    }
}
