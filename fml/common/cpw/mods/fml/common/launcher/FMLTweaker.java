package cpw.mods.fml.common.launcher;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class FMLTweaker implements ITweaker {
    private List<String> args;
    private File gameDir;
    private File assetsDir;
    private String profile;
    private static URI jarLocation;
    private String[] array;
    private List<ITweaker> cascadedTweaks;
    private String profileName;
    private OptionSet parsedOptions;
    private ArgumentAcceptingOptionSpec<String> cascadedTweaksOption;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        this.gameDir = (gameDir == null ? new File(".") : gameDir);
        this.assetsDir = assetsDir;
        this.profile = profile;
        try
        {
            jarLocation = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
        }
        catch (URISyntaxException e)
        {
            Logger.getLogger("FMLTWEAK").log(Level.SEVERE, "Missing URI information for FML tweak");
            throw Throwables.propagate(e);
        }

        OptionParser optionParser = new OptionParser();
        cascadedTweaksOption = optionParser.accepts("cascadedTweaks", "Additional tweaks to be called by FML, implementing ITweaker").withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
        ArgumentAcceptingOptionSpec<String> profileNameOption = optionParser.accepts("profileName", "A profile name, parsed by FML to control mod loading and such").withRequiredArg().ofType(String.class);
        optionParser.allowsUnrecognizedOptions();
        NonOptionArgumentSpec<String> nonOptions = optionParser.nonOptions();

        parsedOptions = optionParser.parse(args.toArray(new String[args.size()]));
        if (parsedOptions.has(profileNameOption))
        {
            profileName = profileNameOption.value(parsedOptions);
        }
        this.args = parsedOptions.valuesOf(nonOptions);
        this.cascadedTweaks = Lists.newArrayList();
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        computeCascadedTweaks(classLoader);
        classLoader.addTransformerExclusion("cpw.mods.fml.repackage.");
        classLoader.addTransformerExclusion("cpw.mods.fml.relauncher.");
        classLoader.addTransformerExclusion("cpw.mods.fml.common.asm.transformers.");
        classLoader.addClassLoaderExclusion("LZMA.");
        FMLLaunchHandler.configureForClientLaunch(classLoader, this);
        runAdditionalTweaks(classLoader);
        FMLLaunchHandler.appendCoreMods();
    }

    void computeCascadedTweaks(LaunchClassLoader classLoader)
    {
        if (parsedOptions.has(cascadedTweaksOption))
        {
            for (String tweaker : cascadedTweaksOption.values(parsedOptions))
            {
                try
                {
                    classLoader.addClassLoaderExclusion(tweaker.substring(0,tweaker.lastIndexOf('.')));
                    Class<? extends ITweaker> tweakClass = (Class<? extends ITweaker>) Class.forName(tweaker,true,classLoader);
                    ITweaker additionalTweak = tweakClass.newInstance();
                    cascadedTweaks.add(additionalTweak);
                }
                catch (Exception e)
                {
                    Logger.getLogger("FMLTWEAK").log(Level.INFO, "Missing additional tweak class "+tweaker);
                }
            }
        }
    }

    void runAdditionalTweaks(LaunchClassLoader classLoader)
    {
        List<String> fmlArgs = Lists.newArrayList(args);
        fmlArgs.add("--fmlIsPresent");
        for (ITweaker tweak : cascadedTweaks)
        {
            tweak.acceptOptions(fmlArgs, gameDir, assetsDir, profile);
            tweak.injectIntoClassLoader(classLoader);
        }
    }

    @Override
    public String getLaunchTarget()
    {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments()
    {
        String[] array = args.toArray(new String[args.size()]);

        if (gameDir != null)
        {
            array = ObjectArrays.concat(gameDir.getAbsolutePath(),array);
            array = ObjectArrays.concat("--gameDir",array);
        }

        if (assetsDir != null)
        {
            array = ObjectArrays.concat(assetsDir.getAbsolutePath(),array);
            array = ObjectArrays.concat("--assetsDir",array);
        }
        if (profile != null)
        {
            array = ObjectArrays.concat(profile,array);
            array = ObjectArrays.concat("--version",array);
        }
        else
        {
            array = ObjectArrays.concat("UnknownFMLProfile",array);
            array = ObjectArrays.concat("--version",array);
        }
        for (ITweaker tweak: cascadedTweaks)
        {
            array = ObjectArrays.concat(tweak.getLaunchArguments(), array, String.class);
        }
        return array;
    }

    public File getGameDir()
    {
        return gameDir;
    }

    public static URI getJarLocation()
    {
        return jarLocation;
    }

    public void injectCascadingTweak(ITweaker tweaker)
    {
        cascadedTweaks.add(tweaker);
    }

}
