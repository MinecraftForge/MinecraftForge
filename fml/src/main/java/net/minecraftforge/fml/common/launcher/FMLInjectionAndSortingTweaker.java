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
