/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import com.google.common.base.Strings;
import cpw.mods.jarhandling.SecureJar;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class CommonDevLaunchHandler extends CommonLaunchHandler {
    @Override public String getNaming() { return "mcp"; }
    @Override public boolean isProduction() { return false; }

    @Override
    public LocatedPaths getMinecraftPaths() {
        // Minecraft is extra jar {resources} + our exploded directories in dev
        final var mcstream = Stream.<Path>builder();

        // The extra jar is on the classpath, so try and pull it out of the legacy classpath
        var legacyCP = Objects.requireNonNull(System.getProperty("legacyClassPath"), "Missing legacyClassPath, cannot find client-extra").split(File.pathSeparator);
        var extra = Paths.get(Arrays.stream(legacyCP).filter(e -> e.contains("client-extra")).findFirst().orElseThrow(() -> new IllegalStateException("Could not find client-extra in legacy classpath")));
        mcstream.add(extra);

        // The MC code/Patcher edits are in exploded directories
        final var modstream = Stream.<List<Path>>builder();
        final var mods = getModClasses();
        final var minecraft = mods.remove("minecraft");
        if (minecraft == null)
            throw new IllegalStateException("Could not find 'minecraft' mod paths.");
        minecraft.stream().distinct().forEach(mcstream::add);
        mods.values().forEach(modstream::add);

        var mcFilter = getMcFilter(extra, minecraft, modstream);
        return new LocatedPaths(mcstream.build().toList(), mcFilter, modstream.build().toList(), getFmlStuff(legacyCP));
    }

    protected String[] preLaunch(String[] arguments, ModuleLayer layer) {
        if (getDist().isDedicatedServer())
            return arguments;

        fixNatives();

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

    protected List<Path> getFmlStuff(String[] classpath) {
        // We also want the FML things, fmlcore, javafmllanguage, mclanguage, I don't like hard coding these, but hey whatever works for now.
        return Arrays.stream(classpath)
            .filter(e -> e.contains("fmlcore") || e.contains("javafmllanguage") || e.contains("mclanguage"))
            .map(Paths::get)
            .toList();
    }

    protected BiPredicate<String, String> getMcFilter(Path extra, List<Path> minecraft, Stream.Builder<List<Path>> mods) {
        final var packages = getPackages();
        final var extraPath = extra.toString().replace('\\', '/');

        // We serve everything, except for things in the forge packages.
        BiPredicate<String, String> mcFilter = (path, base) -> {
            if (base.equals(extraPath) ||
                    path.endsWith("/")) return true;
            for (var pkg : packages)
                if (path.startsWith(pkg)) return false;
            return true;
        };

        // We need to separate out our resources/code so that we can show up as a different data pack.
        var modJar = SecureJar.from((path, base) -> {
            if (!path.endsWith(".class")) return true;
            for (var pkg : packages)
                if (path.startsWith(pkg)) return true;
            return false;
        }, minecraft.stream().distinct().toArray(Path[]::new));
        //modJar.getPackages().stream().sorted().forEach(System.out::println);
        mods.add(List.of(modJar.getRootPath()));

        return mcFilter;
    }

    protected String[] getPackages() {
        return new String[]{ "net/minecraftforge/", "META-INF/services/", "META-INF/coremods.json", "META-INF/mods.toml" };
    }

    private static String getRandomNumbers(int length) {
        // Generate a time-based random number, to mimic how n.m.client.Main works
        return Long.toString(System.nanoTime() % (int) Math.pow(10, length));
    }

    private static void fixNatives() {
        String paths = System.getProperty("java.library.path");
        String nativesDir = System.getProperty("nativesDirectory");
        if (nativesDir == null)
            return;

        if (Strings.isNullOrEmpty(paths))
            paths = nativesDir;
        else
            paths += File.pathSeparator + nativesDir;

        System.setProperty("java.library.path", paths);
    }
}
