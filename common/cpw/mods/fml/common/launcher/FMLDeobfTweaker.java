package cpw.mods.fml.common.launcher;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.FMLRelaunchLog;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class FMLDeobfTweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        // Deobfuscation transformer, always last
        if (!(Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment"))
        {
            classLoader.registerTransformer("cpw.mods.fml.common.asm.transformers.DeobfuscationTransformer");
        }
        try
        {
            FMLRelaunchLog.fine("Validating minecraft");
            Class<?> loaderClazz = Class.forName("cpw.mods.fml.common.Loader", true, classLoader);
            Method m = loaderClazz.getMethod("injectData", Object[].class);
            m.invoke(null, (Object)FMLInjectionData.data());
            m = loaderClazz.getMethod("instance");
            m.invoke(null);
            FMLRelaunchLog.fine("Minecraft validated, launching...");
        }
        catch (Exception e)
        {
            // Load in the Loader, make sure he's ready to roll - this will initialize most of the rest of minecraft here
            System.out.println("A CRITICAL PROBLEM OCCURED INITIALIZING MINECRAFT - LIKELY YOU HAVE AN INCORRECT VERSION FOR THIS FML");
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
