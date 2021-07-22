/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
