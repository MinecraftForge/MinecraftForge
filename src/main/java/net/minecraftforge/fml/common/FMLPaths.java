/*
 * Minecraft Forge
 * Copyright (c) 2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common;

import cpw.mods.modlauncher.api.IEnvironment;
import net.minecraftforge.fml.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static net.minecraftforge.fml.Logging.CORE;

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

    private Path computePath(String... path)
    {
        return Paths.get(path[0], Arrays.copyOfRange(path, 1, path.length));
    }

    FMLPaths(boolean isDir, FMLPaths parent, String... path) {
        this.relativePath = parent.relativePath.resolve(computePath(path));
        this.isDirectory = isDir;
    }

    public static void setup(IEnvironment env) {
        final Path rootPath = env.getProperty(IEnvironment.Keys.GAMEDIR.get()).orElseThrow(() -> new RuntimeException("No game path found"));

        loadAbsolutePaths(rootPath);
    }

    public static void loadAbsolutePaths(Path rootPath)
    {
        for (FMLPaths path : FMLPaths.values())
        {
            path.absolutePath = rootPath.resolve(path.relativePath).toAbsolutePath();
            LOGGER.debug(CORE,"Path {} is {}", ()-> path, ()-> path.absolutePath);
            if (path.isDirectory)
            {
                FileUtils.getOrCreateDirectory(path.absolutePath, path.name());
            }
        }
    }

    public Path get() {
        return absolutePath;
    }
}
