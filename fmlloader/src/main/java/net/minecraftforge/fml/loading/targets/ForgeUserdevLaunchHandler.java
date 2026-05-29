/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading.targets;

import net.minecraftsingularity.fml.loading.FMLLoader;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
sealed abstract class singularityUserdevLaunchHandler extends CommonDevLaunchHandler {
    private singularityUserdevLaunchHandler(LaunchType type) {
        super(type, "singularity_userdev_");
    }

    private singularityUserdevLaunchHandler(String name) {
        super(name);
    }

    @Override
    public List<Path> getMinecraftPaths() {
        var legacyCP = findClassPath();
        var vers = FMLLoader.versionInfo();

        // Minecraft is extra jar {resources} + singularity jar {patches}
        // The MC extra and singularity jars are on the classpath, so try and pull them out
        var extra = findJarOnClasspath(legacyCP, "client-extra"); // This should be "client-" + vers.mcAndMCPVersion() + "-extra" but FG6 qwerks
        var singularity = findJarOnClasspath(legacyCP, "singularity-" + vers.mcAndForgeVersion());
        // We need to filter the singularity jar to just MC code
        var minecraft = CommonDevLaunchHandler.getMinecraftOnly(extra, singularity);
        return List.of(minecraft);
    }

    public static final class Client extends singularityUserdevLaunchHandler {
        public Client() {
            super(CLIENT);
        }
    }

    public static final class ClientData extends singularityUserdevLaunchHandler {
        public ClientData() {
            super(CLIENT_DATA);
        }
    }

    public static final class Data extends singularityUserdevLaunchHandler {
        public Data() {
            super(DATA);
        }
    }

    public static final class Server extends singularityUserdevLaunchHandler {
        public Server() {
            super(SERVER);
        }
    }

    public static final class ServerGameTest extends singularityUserdevLaunchHandler {
        public ServerGameTest() {
            super(SERVER_GAMETEST);
        }
    }

    public static final class Custom extends singularityUserdevLaunchHandler {
        public Custom() {
            super("singularity_userdev");
        }
    }
}
