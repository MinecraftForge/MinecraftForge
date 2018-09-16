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

package net.minecraftforge.fml.loading;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static net.minecraftforge.fml.Logging.CORE;

public class FMLDevClientLaunchProvider extends FMLCommonLaunchHandler implements ILaunchHandlerService
{

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public String name()
    {
        return "fmldevclient";
    }

    private static final List<String> SKIPPACKAGES = Arrays.asList(
            "joptsimple.", "org.lwjgl.", "com.mojang.", "com.google.",
            "org.apache.commons.", "io.netty.", "net.minecraftforge.fml.loading.", "net.minecraftforge.fml.language.",
            "net.minecraftforge.eventbus.", "it.unimi.dsi.fastutil.", "net.minecraftforge.api.",
            "paulscode.sound.", "com.ibm.icu.", "sun.", "gnu.trove.", "com.electronwill.nightconfig.",
            "net.minecraftforge.fml.common.versioning."
    );

    private static final Path myPath;

    static
    {
        try
        {
            myPath = Paths.get(FMLDevClientLaunchProvider.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException("HUH?");
        }
    }

    @Override
    public Path[] identifyTransformationTargets()
    {
            return new Path[] { myPath };
    }

    @Override
    public Callable<Void> launchService(String[] arguments, ITransformingClassLoader launchClassLoader)
    {
        return () -> {
            LOGGER.debug(CORE, "Launching minecraft in {} with arguments {}", launchClassLoader, arguments);
            super.beforeStart(launchClassLoader, myPath);
            launchClassLoader.addTargetPackageFilter(cn -> SKIPPACKAGES.stream().noneMatch(cn::startsWith));
            Field scl = ClassLoader.class.getDeclaredField("scl"); // Get system class loader
            scl.setAccessible(true); // Set accessible
            scl.set(null, launchClassLoader.getInstance()); // Update it to your class loader
            Thread.currentThread().setContextClassLoader(launchClassLoader.getInstance());
            Class.forName("net.minecraft.client.main.Main", true, launchClassLoader.getInstance()).getMethod("main", String[].class).invoke(null, (Object)arguments);
            return null;
        };
    }

    @Override
    public void setup(IEnvironment environment)
    {
        LOGGER.debug(CORE, "No jar creation necessary. Launch is dev environment");
    }

    @Override
    public Dist getDist()
    {
        return Dist.CLIENT;
    }
}
