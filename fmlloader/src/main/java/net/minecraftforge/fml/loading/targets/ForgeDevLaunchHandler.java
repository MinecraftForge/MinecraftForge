/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    static Path getPathFromResource(String resource) {
        var cl = ForgeDevLaunchHandler.class.getClassLoader();
        var url = cl.getResource(resource);
        if (url == null)
            throw new IllegalStateException("Could not find " + resource + " in classloader " + cl);

        var str = url.toString();
        int len = resource.length();
        if ("jar".equalsIgnoreCase(url.getProtocol())) {
            str = url.getFile();
            len += 2;
        }
        str = str.substring(0, str.length() - len);
        var path = Paths.get(URI.create(str));
        return path;
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
