package cpw.mods.fml.common.launcher;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Throwables;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

import net.minecraft.launchwrapper.ITweaker;
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
        this.args = args;
        this.gameDir = gameDir;
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
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        classLoader.addTransformerExclusion("cpw.mods.fml.repackage.");
        classLoader.addTransformerExclusion("cpw.mods.fml.relauncher.");
        classLoader.addTransformerExclusion("cpw.mods.fml.common.asm.transformers.");
        FMLLaunchHandler.configureForClientLaunch(classLoader, this);
    }

    @Override
    public String getLaunchTarget()
    {
        return "net.minecraft.client.Minecraft";
    }

    @Override
    public String[] getLaunchArguments()
    {
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

}
