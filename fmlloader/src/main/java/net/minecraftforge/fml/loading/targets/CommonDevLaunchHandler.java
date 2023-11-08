/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import cpw.mods.modlauncher.api.ServiceRunner;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            new IllegalStateException("Could not find " + match + " in classpath");

        return Paths.get(ret);
    }
}
