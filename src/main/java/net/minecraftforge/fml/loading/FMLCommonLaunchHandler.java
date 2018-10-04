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
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import net.minecraftforge.api.distmarker.Dist;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public abstract class FMLCommonLaunchHandler
{
    private static final List<String> SKIPPACKAGES = Arrays.asList(
            // standard libs
            "joptsimple.", "org.lwjgl.", "com.mojang.", "com.google.", "org.apache.commons.", "io.netty.",
            "paulscode.sound.", "com.ibm.icu.", "sun.", "gnu.trove.", "com.electronwill.nightconfig.",
            "net.minecraftforge.fml.loading.", "net.minecraftforge.fml.language.", "net.minecraftforge.versions.",
            "net.minecraftforge.eventbus.", "net.minecraftforge.api."
    );

    protected Predicate<String> getPackagePredicate() {
        return cn -> SKIPPACKAGES.stream().noneMatch(cn::startsWith);
    }

    public void setup(final IEnvironment environment)
    {

    }

    public abstract Dist getDist();

    protected void beforeStart(ITransformingClassLoader launchClassLoader, Path forgePath)
    {
        FMLLoader.beforeStart(launchClassLoader, forgePath);
    }
}
