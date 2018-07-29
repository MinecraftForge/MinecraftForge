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

package net.minecraftforge.fml.common.launcher;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

public class FMLDeobfTweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        // Deobfuscation transformer, always last, and the access transformer tweaker as well
        classLoader.registerTransformer("net.minecraftforge.fml.common.asm.transformers.DeobfuscationTransformer");
        // Add all the access transformers now as well
        for (String transformer : CoreModManager.getAccessTransformers())
        {
            classLoader.registerTransformer(transformer);
        }
        classLoader.registerTransformer("net.minecraftforge.fml.common.asm.transformers.ModAccessTransformer");
        classLoader.registerTransformer("net.minecraftforge.fml.common.asm.transformers.ItemStackTransformer");
        classLoader.registerTransformer("net.minecraftforge.fml.common.asm.transformers.ItemBlockTransformer");
        classLoader.registerTransformer("net.minecraftforge.fml.common.asm.transformers.ItemBlockSpecialTransformer");
        try
        {
            FMLLog.log.debug("Validating minecraft");
            Class<?> loaderClazz = Class.forName("net.minecraftforge.fml.common.Loader", true, classLoader);
            Method m = loaderClazz.getMethod("injectData", Object[].class);
            m.invoke(null, (Object)FMLInjectionData.data());
            m = loaderClazz.getMethod("instance");
            m.invoke(null);
            FMLLog.log.debug("Minecraft validated, launching...");
        }
        catch (Exception e)
        {
            // Load in the Loader, make sure he's ready to roll - this will initialize most of the rest of minecraft here
            FMLLog.log.fatal("A CRITICAL PROBLEM OCCURRED INITIALIZING MINECRAFT - LIKELY YOU HAVE AN INCORRECT VERSION FOR THIS FML");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getLaunchTarget()
    {
        throw new RuntimeException("Invalid for use as a primary tweaker");
    }

    @Override
    public String[] getLaunchArguments()
    {
        return new String[0];
    }

}
