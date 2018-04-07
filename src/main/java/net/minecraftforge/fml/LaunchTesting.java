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

package net.minecraftforge.fml;

import com.google.common.base.Strings;
import cpw.mods.modlauncher.Launcher;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.lang.reflect.Field;

public class LaunchTesting
{
    public static void main(String... args) throws InterruptedException
    {
        Configurator.setRootLevel(Level.DEBUG);
        hackNatives();
        Launcher.main("--launchTarget", "devfml","--gameDir", "projects/run", "--accessToken", "blah", "--version", "FMLDev");
        Thread.sleep(10000);
    }

    private static void hackNatives()
    {
        String paths = System.getProperty("java.library.path");
        String nativesDir = "/home/cpw/.gradle/caches/minecraft/net/minecraft/natives/1.12.2";

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
