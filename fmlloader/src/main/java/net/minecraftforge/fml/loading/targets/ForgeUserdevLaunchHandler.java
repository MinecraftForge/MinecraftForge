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
        var minecraft = getMinecraftOnly(extra, forge);
        return List.of(minecraft);
    }

    @Deprecated // TODO: [Forge][Userdev] Make Forge and MC separate jars at dev time, so this filter isn't needed.
    private Path getMinecraftOnly(Path extra, Path forge) {
        var packages = getPackages(); // Pulled out so it is passed to the lambda as value
        var extraPath = extra.toString().replace('\\', '/');

        // We serve everything, except for things in the forge packages.
        BiPredicate<String, String> mcFilter = (path, base) -> {
            if (base.equals(extraPath) ||
                path.endsWith("/")) return true;
            for (var pkg : packages)
                if (path.startsWith(pkg)) return false;
            return true;
        };
        var fs = UnionHelper.newFileSystem(mcFilter, new Path[] { forge, extra });
        return fs.getRootDirectories().iterator().next();
    }


    @Deprecated // TODO: [Forge][Userdev] Make Forge and MC separate jars at dev time, so this filter isn't needed.
    static Path getForgeMod(Path forge) {
        var packages = getPackages(); // Pulled out so it is passed to the lambda as value
        // We need to separate out our resources/code so that we can show up as a different data pack.
        var modJar = SecureJar.from((path, base) -> {
            if (!path.endsWith(".class")) return true;
            for (var pkg : packages)
                if (path.startsWith(pkg)) return true;
            return false;
        }, new Path[] { forge });

        //modJar.getPackages().stream().sorted().forEach(System.out::println);
        return modJar.getRootPath();
    }

    private static String[] getPackages() {
        return new String[] {
            "net/minecraftforge/",
            "META-INF/services/",
            "META-INF/coremods.json",
            "META-INF/mods.toml"
        };
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
