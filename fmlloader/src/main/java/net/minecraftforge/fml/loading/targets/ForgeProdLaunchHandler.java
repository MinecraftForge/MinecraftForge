/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
abstract class ForgeProdLaunchHandler extends CommonLaunchHandler {
    protected ForgeProdLaunchHandler(LaunchType type) {
        super(type, "forge_");
    }

    @Override public String getNaming() { return "mcp"; }
    @Override public boolean isProduction() { return true; }

    public static class Client extends ForgeProdLaunchHandler {
        public Client() {
            super(CLIENT);
        }

        @Override
        public List<Path> getMinecraftPaths() {
            return List.of(getPathFromResource("net/minecraft/client/Minecraft.class"));
        }
    }

    public static class Server extends ForgeProdLaunchHandler {
        public Server() {
            super(SERVER);
        }

        @Override
        public List<Path> getMinecraftPaths() {
            return List.of(getPathFromResource("net/minecraft/server/MinecraftServer.class"));
        }
    }
}
