package cpw.mods.fml.common.launcher;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class FMLTweaker implements ITweaker {
    private List<String> args;
    private File gameDir;
    private File assetsDir;
    private String profile;
    private Map<String, String> launchArgs;
    private List<String> standaloneArgs;
    private static URI jarLocation;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        this.gameDir = (gameDir == null ? new File(".") : gameDir);
        this.assetsDir = assetsDir;
        this.profile = profile;
        this.args = args;

        this.launchArgs = (Map<String, String>)Launch.blackboard.get("launchArgs");

        this.standaloneArgs = Lists.newArrayList();
        if (this.launchArgs == null)
        {
            this.launchArgs = Maps.newHashMap();
            Launch.blackboard.put("launchArgs", this.launchArgs);
        }

        String classifier = null;

        for (String arg : args)
        {
            if (arg.startsWith("-"))
            {
                if (classifier != null)
                {
                    classifier = launchArgs.put(classifier, "");
                }
                else if (arg.contains("="))
                {
                    classifier = launchArgs.put(arg.substring(0, arg.indexOf('=')), arg.substring(arg.indexOf('=') + 1));
                }
                else
                {
                    classifier = arg;
                }
            }
            else
            {
                if (classifier != null)
                {
                    classifier = launchArgs.put(classifier, arg);
                }
                else
                {
                    this.standaloneArgs.add(arg);
                }
            }
        }

        if (!this.launchArgs.containsKey("--version"))
        {
            launchArgs.put("--version", profile != null ? profile : "UnknownFMLProfile");
        }

        if (!this.launchArgs.containsKey("--gameDir") && gameDir != null)
        {
            launchArgs.put("--gameDir", gameDir.getAbsolutePath());
        }

        if (!this.launchArgs.containsKey("--assetsDir") && assetsDir != null)
        {
            launchArgs.put("--assetsDir", assetsDir.getAbsolutePath());
        }

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
        List<String> args = Lists.newArrayList();
        args.addAll(standaloneArgs);

        for (Entry<String, String> arg : launchArgs.entrySet())
        {
            args.add(arg.getKey());
            args.add(arg.getValue());
        }
        launchArgs.clear();

        return args.toArray(new String[args.size()]);
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
