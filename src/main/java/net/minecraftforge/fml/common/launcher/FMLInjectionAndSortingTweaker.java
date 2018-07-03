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
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.CoreModManager;

/**
 * This class is to manage the injection of coremods as tweakers into the tweak framework.
 * It has to inject the coremod tweaks during construction, because that is the only time
 * the tweak list is writeable.
 * @author cpw
 *
 */
public class FMLInjectionAndSortingTweaker implements ITweaker {
    private boolean run;
    public FMLInjectionAndSortingTweaker()
    {
        CoreModManager.injectCoreModTweaks(this);
        run = false;
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        if (!run)
        {
            // We sort the tweak list here so that it obeys the tweakordering
            CoreModManager.sortTweakList();
            @SuppressWarnings("unchecked")
            List<String> newTweaks = (List<String>) Launch.blackboard.get("TweakClasses");
            newTweaks.add("net.minecraftforge.fml.common.launcher.TerminalTweaker");
        }
        run = true;
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
    }

    @Override
    public String getLaunchTarget()
    {
        return "";
    }

    @Override
    public String[] getLaunchArguments()
    {
        return new String[0];
    }

}
