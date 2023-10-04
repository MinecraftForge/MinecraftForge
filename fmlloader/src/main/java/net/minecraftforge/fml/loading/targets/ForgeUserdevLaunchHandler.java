/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import net.minecraftforge.fml.loading.FMLLoader;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
abstract class ForgeUserdevLaunchHandler extends CommonDevLaunchHandler {
    private ForgeUserdevLaunchHandler(LaunchType type) {
        super(type, "forge_userdev_");
    }

    @Override
    public LocatedPaths getMinecraftPaths() {
        var legacyCP = Objects.requireNonNull(System.getProperty("legacyClassPath"), "Missing legacyClassPath, cannot find userdev jars").split(File.pathSeparator);
        var vers = FMLLoader.versionInfo();

        // Minecraft is extra jar {resources} + forge jar {patches}
        // The MC extra and forge jars are on the classpath, so try and pull them out
        var extra = findJarOnClasspath(legacyCP, "client-extra");
        var forge = findJarOnClasspath(legacyCP, "forge-" + vers.mcAndForgeVersion());
        // We need to filter the forge jar to just MC code
        var filter = getMcFilter(extra, List.of(forge));

        // Mods is forge classes + anything from MOD_CLASSES
        var modstream = Stream.<List<Path>>builder();
        // We want the forge as a seperate mod jar.
        var forgemod = this.getForgeMod(List.of(forge));
        modstream.add(List.of(forgemod));
        // Mod code is in exploded directories
        getModClasses().forEach((modid, paths) -> modstream.add(paths));

        return new LocatedPaths(
            List.of(forge, extra),
            filter,
            modstream.build().toList(),
            getLibraries(legacyCP)
        );
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
