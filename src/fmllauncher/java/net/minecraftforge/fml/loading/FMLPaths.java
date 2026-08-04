/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import cpw.mods.modlauncher.api.IEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public enum FMLPaths
{
    GAMEDIR(),
    MODSDIR("mods"),
    CONFIGDIR("config"),
    FMLCONFIG(false, CONFIGDIR, "fml.toml");

    private static final Logger LOGGER = LogManager.getLogger();
    private final Path relativePath;
    private final boolean isDirectory;
    private Path absolutePath;

    FMLPaths() {
        this("");
    }

    FMLPaths(String... path) {
        relativePath = computePath(path);
        this.isDirectory = true;
    }

    FMLPaths(boolean isDir, FMLPaths parent, String... path) {
        this.relativePath = parent.relativePath.resolve(computePath(path));
        this.isDirectory = isDir;
    }

    private Path computePath(String... path)
    {
        return Paths.get(path[0], Arrays.copyOfRange(path, 1, path.length));
    }

    public static void setup(IEnvironment env) {
        final Path rootPath = env.getProperty(IEnvironment.Keys.GAMEDIR.get()).orElseThrow(() -> new RuntimeException("No game path found"));

        loadAbsolutePaths(rootPath);
    }

    public static void loadAbsolutePaths(Path rootPath)
    {
        for (FMLPaths path : FMLPaths.values())
        {
            path.absolutePath = rootPath.resolve(path.relativePath).toAbsolutePath().normalize();
            if (path.isDirectory)
            {
                FileUtils.getOrCreateDirectory(path.absolutePath, path.name());
            }
            LOGGER.debug(CORE,"Path {} is {}", ()-> path, ()-> path.absolutePath);
        }
    }

    public static Path getOrCreateGameRelativePath(Path path, String name) {
        return FileUtils.getOrCreateDirectory(FMLPaths.GAMEDIR.get().resolve(path), name);
    }

    public Path relative() {
        return relativePath;
    }

    public Path get() {
        return absolutePath;
    }
}
