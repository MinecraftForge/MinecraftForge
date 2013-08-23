package cpw.mods.fml.common.launcher;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class FMLTweaker implements ITweaker {
    private List<String> args;
    private File gameDir;
    private File assetsDir;
    private String profile;
    private static URI jarLocation;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        this.gameDir = (gameDir == null ? new File(".") : gameDir);
        this.assetsDir = assetsDir;
        this.profile = profile;
        this.args = args;
        try
        {
            jarLocation = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
        }
        catch (URISyntaxException e)
        {
            Logger.getLogger("FMLTWEAK").log(Level.SEVERE, "Missing URI information for FML tweak");
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        classLoader.addTransformerExclusion("cpw.mods.fml.repackage.");
        classLoader.addTransformerExclusion("cpw.mods.fml.relauncher.");
        classLoader.addTransformerExclusion("cpw.mods.fml.common.asm.transformers.");
        classLoader.addClassLoaderExclusion("LZMA.");
        FMLLaunchHandler.configureForClientLaunch(classLoader, this);
        FMLLaunchHandler.appendCoreMods();
    }

    @Override
    public String getLaunchTarget()
    {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments()
    {
        List<String> blackboardArgs = (List<String>) Launch.blackboard.get("ArgumentList");

        String[] launchArgsArray = args.toArray(new String[args.size()]);
        
        if (gameDir != null && !blackboardArgs.contains("--gameDir"))
        {
            launchArgsArray = ObjectArrays.concat(gameDir.getAbsolutePath(),launchArgsArray);
            launchArgsArray = ObjectArrays.concat("--gameDir",launchArgsArray);
        }

        if (assetsDir != null && !blackboardArgs.contains("--assetsDir"))
        {
            launchArgsArray = ObjectArrays.concat(assetsDir.getAbsolutePath(),launchArgsArray);
            launchArgsArray = ObjectArrays.concat("--assetsDir",launchArgsArray);
        }
        if (!blackboardArgs.contains("--version"))
        {
            launchArgsArray = ObjectArrays.concat(profile != null ? profile : "UnknownFMLProfile",launchArgsArray);
            launchArgsArray = ObjectArrays.concat("--version",launchArgsArray);
        }
        return launchArgsArray;
    }

    public File getGameDir()
    {
        return gameDir;
    }

    public static URI getJarLocation()
    {
        return jarLocation;
    }

    public void injectCascadingTweak(String tweakClassName)
    {
        List<String> tweakClasses = (List<String>) Launch.blackboard.get("TweakClasses");
        tweakClasses.add(tweakClassName);
    }

}
