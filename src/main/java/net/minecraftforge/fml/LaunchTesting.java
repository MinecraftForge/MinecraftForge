/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml;

import com.google.common.base.Strings;
import com.google.common.collect.ObjectArrays;
import cpw.mods.modlauncher.Launcher;
import net.minecraftforge.fml.loading.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.filter.MarkerFilter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class LaunchTesting
{
    public static void main(String... args) throws InterruptedException
    {
        final String markerselection = System.getProperty("forge.logging.markers", "");
        Arrays.stream(markerselection.split(",")).forEach(marker-> System.setProperty("forge.logging.marker."+ marker.toLowerCase(Locale.ROOT), "ACCEPT"));

        String assets = System.getenv().getOrDefault("assetDirectory", "assets");
        String target = System.getenv().get("target");
        String[] launchArgs = new String[]{
                "--gameDir", ".",
                "--launchTarget", target,
                "--fml.forgeVersion", System.getProperty("forge.version"),
                "--fml.mcpVersion", System.getProperty("mcp.version"),
                "--fml.mcVersion", System.getProperty("mc.version"),
                "--fml.forgeGroup", System.getProperty("forge.group")
        };


        if (target == null) {
            throw new IllegalArgumentException("Environment variable target must be set.");
        }

        if (Objects.equals(target,"fmldevclient")) {
            hackNatives();
            launchArgs = ObjectArrays.concat(launchArgs, new String[] {
                            "--accessToken", "blah",
                            "--version", "FMLDev",
                            "--assetIndex", "1.13",
                            "--assetsDir", assets,
                            "--userProperties", "{}"
            }, String.class);
        }
        Launcher.main(launchArgs);
        Thread.sleep(10000);
    }

    private static void hackNatives()
    {
        String paths = System.getProperty("java.library.path");
        String nativesDir = System.getenv().get("nativesDirectory");

        if (Strings.isNullOrEmpty(paths))
            paths = nativesDir;
        else
            paths += File.pathSeparator + nativesDir;

        System.setProperty("java.library.path", paths);

        // hack the classloader now.
        try
        {
            final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);
        }
        catch(Throwable t) {}
    }

}
