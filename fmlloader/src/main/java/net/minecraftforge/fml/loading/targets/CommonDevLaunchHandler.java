/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.api.ServiceRunner;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
abstract class CommonDevLaunchHandler extends CommonLaunchHandler {
    protected CommonDevLaunchHandler(LaunchType type, String prefix) {
        super(type, prefix);
    }

    protected CommonDevLaunchHandler(String name) {
        super(name);
    }

    @Override public String getNaming() { return "mcp"; }
    @Override public boolean isProduction() { return false; }

    @Override
    protected String[] preLaunch(String[] arguments, ModuleLayer layer) {
        super.preLaunch(arguments, layer);

        var args = ArgumentList.from(arguments);

        if (this.module == null) {
            var entry = args.get("launchEntry");
            if (entry == null)
                throw new IllegalArgumentException("When using " + this.name() + " launch target you must specify a --launchEntry argument");

            args.remove("launchEntry");

            int idx = entry.indexOf('/');
            if (idx == -1) {
                this.module = "minecraft";
                this.main = entry;
            } else {
                this.module = entry.substring(0, idx);
                this.main = entry.substring(idx + 1);
            }

            if (args.has("launchData")) {
                args.remove("launchData");
                this.isData = true;
            }
        }

        if (getDist().isDedicatedServer())
            return args.getArguments();

        if (isData())
            return args.getArguments();

        String username = args.get("username");
        if (username != null) { // Replace '#' placeholders with random numbers
            Matcher m = Pattern.compile("#+").matcher(username);
            StringBuilder replaced = new StringBuilder();
            while (m.find()) {
                m.appendReplacement(replaced, getRandomNumbers(m.group().length()));
            }
            m.appendTail(replaced);
            args.put("username", replaced.toString());
        } else {
            args.putLazy("username", "Dev");
        }

        if (!args.hasValue("accessToken")) {
            args.put("accessToken", "0");
        }

        return args.getArguments();
    }

    private static String getRandomNumbers(int length) {
        // Generate a time-based random number, to mimic how n.m.client.Main works
        return Long.toString(System.nanoTime() % (int) Math.pow(10, length));
    }

    @Override
    protected ServiceRunner makeService(final String[] arguments, final ModuleLayer gameLayer) {
        return super.makeService(preLaunch(arguments, gameLayer), gameLayer);
    }

    protected static String[] findClassPath() {
        var classpath = System.getProperty("legacyClassPath");
        if (classpath == null)
            classpath = System.getProperty("java.class.path");
        return classpath.split(File.pathSeparator);
    }

    protected static Path findJarOnClasspath(String[] classpath, String match) {
        String ret = null;

        for (var entry : classpath) {
            int idx = entry.lastIndexOf(File.separatorChar);
            if (idx == -1) continue;

            var name = entry.substring(idx + 1);
            if (name.startsWith(match)) {
                ret = entry;
                break;
            }
        }

        if (ret == null)
            throw new IllegalStateException("Could not find " + match + " in classpath");

        return Paths.get(ret);
    }

    // TODO: [Forge][UFS][DEV] Make Forge and MC seperate sources at dev time so we don't have to filter
    protected static Path getMinecraftOnly(Path extra, Path forge) {
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

    // TODO: [Forge][UFS][DEV] Make Forge and MC seperate sources at dev time so we don't have to filter
    protected static Path getForgeOnly(Path forge) {
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
}
