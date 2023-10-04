/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.api.ServiceRunner;
import net.minecraftforge.fml.loading.LogMarkers;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
abstract class CommonDevLaunchHandler extends CommonLaunchHandler {
    protected CommonDevLaunchHandler(LaunchType type, String prefix) {
        super(type, prefix);
    }

    @Override public String getNaming() { return "mcp"; }
    @Override public boolean isProduction() { return false; }

    @Override
    protected String[] preLaunch(String[] arguments, ModuleLayer layer) {
        super.preLaunch(arguments, layer);

        if (getDist().isDedicatedServer())
            return arguments;

        if (isData())
            return arguments;

        var args = ArgumentList.from(arguments);

        String username = args.get("username");
        if (username != null) { // Replace '#' placeholders with random numbers
            Matcher m = Pattern.compile("#+").matcher(username);
            StringBuffer replaced = new StringBuffer();
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

    protected List<Path> getLibraries(String[] classpath) {
        // TODO: These should all be FMLModType: LIBRARY, find them that way?
        // Or at the very least include version info
        var fmlcore = findJarOnClasspath(classpath, "fmlcore");
        var javafmllang = findJarOnClasspath(classpath, "javafmllanguage");
        var lowcodelang = findJarOnClasspath(classpath, "lowcodelanguage");
        var mclang = findJarOnClasspath(classpath, "mclanguage");
        return List.of(fmlcore, javafmllang, lowcodelang, mclang);
    }

    protected BiPredicate<String, String> getMcFilter(Path extra, List<Path> minecraft) {
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

        return mcFilter;
    }

    protected Path getForgeMod(List<Path> minecraft) {
        var packages = getPackages(); // Pulled out so it is passed to the lambda as value
        // We need to separate out our resources/code so that we can show up as a different data pack.
        var modJar = SecureJar.from((path, base) -> {
            if (!path.endsWith(".class")) return true;
            for (var pkg : packages)
                if (path.startsWith(pkg)) return true;
            return false;
        }, minecraft.stream().distinct().toArray(Path[]::new));

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

    private static String getRandomNumbers(int length) {
        // Generate a time-based random number, to mimic how n.m.client.Main works
        return Long.toString(System.nanoTime() % (int) Math.pow(10, length));
    }

    @Override
    protected ServiceRunner makeService(final String[] arguments, final ModuleLayer gameLayer) {
        return super.makeService(preLaunch(arguments, gameLayer), gameLayer);
    }

    // TODO: Get rid of this string based classpath crap when I re-write mod loader
    protected static Map<String, List<Path>> getModClasses() {
        var modClasses = Optional.ofNullable(System.getenv("MOD_CLASSES")).orElse("");
        LOGGER.debug(LogMarkers.CORE, "Got mod coordinates {} from env", modClasses);

        /*
         * So MOD_CLASSES at the end of the day is a Map<ModId, List<Path>> but because we want to shove that into a single string.
         * And pass it in as an environment variable.. instead od using json, or a file path, or anything sane, cpw did hacky string stuff
         * Basically what we want to do is split the string by File.pathSeperator to get each key-value pair.
         * Then we find the magic string "%%" and split on that. Using the first half as the mod id and the second as the path.
         * BUT the modID is optional, and if not specified {why wouldn't it be?} it uses "defaultmodid"
         * Anyways, it was stream hell, but now it's simpler to debug/understand.
         */
        Map<String, List<Path>> ret = new HashMap<>();
        var entries = modClasses.split(File.pathSeparator);
        for (var entry : entries) {
            var modid = "defaultmodid";
            var path = entry;

            int idx = entry.indexOf("%%");
            if (idx != -1) {
                modid = entry.substring(0, idx);
                path = entry.substring(idx + 2);
            }

            var p = Paths.get(path);
            var lst = ret.computeIfAbsent(modid, k -> new ArrayList<>());
            if (!lst.contains(p))
                lst.add(p);
        }

        LOGGER.debug(LogMarkers.CORE, "Found supplied mod coordinates:");
        ret.forEach((k, v) -> {
            if (v.size() == 1)
                LOGGER.debug(LogMarkers.CORE, "\t{}: {}", k, v.get(0));
            else {
                LOGGER.debug(LogMarkers.CORE, "\t{}:", k);
                v.forEach(e -> LOGGER.debug("\t\t{}", e));
            }
        });

        return ret;
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
            new IllegalStateException("Could not find " + match + " in classpath");

        return Paths.get(ret);
    }
}
