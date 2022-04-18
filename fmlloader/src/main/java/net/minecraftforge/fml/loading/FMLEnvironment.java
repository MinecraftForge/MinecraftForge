/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.TypesafeMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.forgespi.Environment;

import java.util.function.Supplier;

public class FMLEnvironment
{
    public static final Dist dist = FMLLoader.getDist();
    public static final String naming = FMLLoader.getNaming();
    public static final boolean production = FMLLoader.isProduction() || System.getProperties().containsKey("production");
    public static final boolean secureJarsEnabled = FMLLoader.isSecureJarEnabled();

    static void setupInteropEnvironment(IEnvironment environment) {
        environment.computePropertyIfAbsent(IEnvironment.Keys.NAMING.get(), v->naming);
        environment.computePropertyIfAbsent(Environment.Keys.DIST.get(), v->dist);
    }

    public static class Keys {
        public static final Supplier<TypesafeMap.Key<ClassLoader>> LOCATORCLASSLOADER = IEnvironment.buildKey("LOCATORCLASSLOADER",ClassLoader.class);
    }
}
