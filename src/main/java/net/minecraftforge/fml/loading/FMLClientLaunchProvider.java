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

package net.minecraftforge.fml.loading;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeVersion;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class FMLClientLaunchProvider extends FMLCommonLaunchHandler implements ILaunchHandlerService
{
    private static final Path forgePath;
    private static final Path mcPath;
    private static final List<String> SKIPPACKAGES = Arrays.asList(
            "joptsimple.", "org.lwjgl.", "com.mojang.", "com.google.",
            "org.apache.commons.", "io.netty.", "net.minecraftforge.fml.loading.", "net.minecraftforge.fml.language.",
            "net.minecraftforge.eventbus.", "it.unimi.dsi.fastutil.", "net.minecraftforge.api.",
            "paulscode.sound.", "com.ibm.icu.", "sun.", "gnu.trove.", "com.electronwill.nightconfig.",
            "net.minecraftforge.fml.common.versioning."
    );
    static {
        try {
            forgePath = Paths.get(FMLClientLaunchProvider.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            mcPath = forgePath.resolveSibling("forge-"+ForgeVersion.getVersion()+"-srg.jar");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unable to locate myself!");
        }
    }
    @Override
    public String name()
    {
        return "fmlclient";
    }

    @Override
    public Path[] identifyTransformationTargets()
    {
        return new Path[] {mcPath, forgePath};
    }

    @Override
    public Callable<Void> launchService(String[] arguments, ITransformingClassLoader launchClassLoader)
    {
        return () -> {
            super.beforeStart(launchClassLoader, forgePath);
            launchClassLoader.addTargetPackageFilter(cn -> SKIPPACKAGES.stream().noneMatch(cn::startsWith));
            Class.forName("net.minecraft.client.main.Main", true, launchClassLoader.getInstance()).getMethod("main", String[].class).invoke(null, (Object)arguments);
            return null;
        };
    }

    @Override
    public void setup(final IEnvironment environment) {
    }

    @Override
    public Dist getDist()
    {
        return Dist.CLIENT;
    }
}
