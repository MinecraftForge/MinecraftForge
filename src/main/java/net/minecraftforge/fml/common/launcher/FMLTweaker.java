package net.minecraftforge.fml.common.launcher;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.FMLSecurityManager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FMLTweaker implements ITweaker {
    private File gameDir;
    private Map<String, String> launchArgs;
    private List<String> standaloneArgs;
    private static URI jarLocation;

    public FMLTweaker()
    {
        System.setProperty("java.net.preferIPv4Stack", "true"); //Lets do this as early as possible. Vanilla does it in Main.main
        try
        {
            System.setSecurityManager(new FMLSecurityManager());
        }
        catch (SecurityException se)
        {
            throw new RuntimeException("FML was unable to install the security manager. The game will not start", se);
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        this.gameDir = (gameDir == null ? new File(".") : gameDir);

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

        Yggdrasil.login(launchArgs);

        try
        {
            jarLocation = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
        }
        catch (URISyntaxException e)
        {
            LogManager.getLogger("FMLTWEAK").log(Level.ERROR, "Missing URI information for FML tweak");
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        FMLLaunchHandler.configureForClientLaunch(classLoader, this);
        FMLLaunchHandler.appendCoreMods();
    }

    @Override
    public String getLaunchTarget()
    {
        // Remove the extraneous mods and modListFile args
        @SuppressWarnings("unchecked")
        Map<String,String> args = (Map<String, String>) Launch.blackboard.get("launchArgs");
        args.remove("--modListFile");
        args.remove("--mods");
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
        @SuppressWarnings("unchecked")
        List<String> tweakClasses = (List<String>) Launch.blackboard.get("TweakClasses");
        tweakClasses.add(tweakClassName);
    }
}
