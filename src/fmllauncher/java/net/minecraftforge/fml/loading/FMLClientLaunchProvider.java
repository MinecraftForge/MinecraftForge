/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class FMLClientLaunchProvider extends FMLCommonLaunchHandler implements ILaunchHandlerService
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public String name()
    {
        return "fmlclient";
    }

    @Override
    public Callable<Void> launchService(String[] arguments, ITransformingClassLoader launchClassLoader)
    {
        return () -> {
            super.beforeStart(launchClassLoader);
            launchClassLoader.addTargetPackageFilter(getPackagePredicate());
            Class.forName("net.minecraft.client.main.Main", true, launchClassLoader.getInstance()).getMethod("main", String[].class).invoke(null, (Object)arguments);
            return null;
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setup(final IEnvironment environment, final Map<String, ?> arguments) {
        final List<String> mavenRoots = new ArrayList<>((List<String>) arguments.get("mavenRoots"));
        final List<String> mods = new ArrayList<>((List<String>) arguments.get("mods"));
        mavenRoots.add(LibraryFinder.findLibsPath().toString());
        final String forgeVersion = (String) arguments.get("forgeVersion");
        final String mcVersion = (String) arguments.get("mcVersion");
        final String forgeGroup = (String) arguments.get("forgeGroup");
        mods.add(forgeGroup+":forge:universal:"+mcVersion+"-"+forgeVersion);
        // generics are gross yea?
        ((Map)arguments).put("mavenRoots", mavenRoots);
        ((Map)arguments).put("mods", mods);
    }

    @Override
    public Dist getDist()
    {
        return Dist.CLIENT;
    }

    @Override
    protected String getNaming() {
        return "srg";
    }

    @Override
    public Path[] getPaths() {
        return FMLLoader.getMCPaths();
    }

    @Override
    public boolean isProduction() {
        return true;
    }
}
