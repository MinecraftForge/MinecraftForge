package cpw.mods.fml.common.launcher;

import java.io.File;
import java.util.List;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class FMLTweaker implements ITweaker {
    private List<String> args;
    private File gameDir;
    private File assetsDir;
    private String profile;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        this.args = args;
        this.gameDir = gameDir;
        this.assetsDir = assetsDir;
        this.profile = profile;
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
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

}
