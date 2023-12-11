/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
abstract class ForgeDevLaunchHandler extends CommonDevLaunchHandler {
    private ForgeDevLaunchHandler(LaunchType type) {
        super(type, "forge_dev_");
    }

    @Override
    public List<Path> getMinecraftPaths() {

        // The extra jar is on the classpath, so try and pull it out of the legacy classpath
        var legacyCP = findClassPath();
        var extra = findJarOnClasspath(legacyCP, "client-extra");
        // Minecraft is an exploded directory, so find it.
        var minecraft = getPathFromResource("net/minecraft/client/Minecraft.class");
        var forge = getPathFromResource("net/minecraftforge/common/MinecraftForge.class");

        // If both Forge and MC are in the same folder, then we are in intellij or gradle
        // So we have to create a filtered jar
        if (!forge.equals(minecraft))
            return List.of(minecraft, extra);

        var filtered = CommonDevLaunchHandler.getMinecraftOnly(extra, minecraft);
        return List.of(filtered);
    }

    public static class Client extends ForgeDevLaunchHandler {
        public Client() {
            super(CLIENT);
        }
    }

    public static class Data extends ForgeDevLaunchHandler {
        public Data() {
            super(DATA);
        }
    }

    public static class Server extends ForgeDevLaunchHandler {
        public Server() {
            super(SERVER);
        }
    }

    public static class ServerGameTest extends ForgeDevLaunchHandler {
        public ServerGameTest() {
            super(SERVER_GAMETEST);
        }
    }
}
