/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import net.minecraftforge.fml.loading.FMLLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiPredicate;
import org.jetbrains.annotations.ApiStatus;

import cpw.mods.jarhandling.SecureJar;

@ApiStatus.Internal
abstract class ForgeUserdevLaunchHandler extends CommonDevLaunchHandler {
    private ForgeUserdevLaunchHandler(LaunchType type) {
        super(type, "forge_userdev_");
    }

    @Override
    public List<Path> getMinecraftPaths() {
        var legacyCP = findClassPath();
        var vers = FMLLoader.versionInfo();

        // Minecraft is extra jar {resources} + forge jar {patches}
        // The MC extra and forge jars are on the classpath, so try and pull them out
        var extra = findJarOnClasspath(legacyCP, "client-extra");
        var forge = findJarOnClasspath(legacyCP, "forge-" + vers.mcAndForgeVersion());
        // We need to filter the forge jar to just MC code
        var minecraft = CommonDevLaunchHandler.getMinecraftOnly(extra, forge);
        return List.of(minecraft);
    }

    public static class Client extends ForgeUserdevLaunchHandler {
        public Client() {
            super(CLIENT);
        }
    }

    public static class Data extends ForgeUserdevLaunchHandler {
        public Data() {
            super(DATA);
        }
    }

    public static class Server extends ForgeUserdevLaunchHandler {
        public Server() {
            super(SERVER);
        }
    }

    public static class ServerGameTest extends ForgeUserdevLaunchHandler {
        public ServerGameTest() {
            super(SERVER_GAMETEST);
        }
    }
}
