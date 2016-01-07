package net.minecraftforge.fml.common.launcher;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

public class FMLServerTweaker extends FMLTweaker {

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        super.acceptOptions(args, gameDir, assetsDir, profile);

        if (System.getProperty("log4j.configurationFile") == null)
        {
            System.setProperty("log4j.configurationFile", "log4j2_server.xml");
            ((LoggerContext) LogManager.getContext(false)).reconfigure();
        }
    }

    @Override
    public String getLaunchTarget()
    {
        return "net.minecraft.server.MinecraftServer";
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        // The log4j2 queue is excluded so it is correctly visible from the obfuscated
        // and deobfuscated parts of the code. Without, the UI won't show anything
        classLoader.addClassLoaderExclusion("com.mojang.util.QueueLogAppender");

        classLoader.addClassLoaderExclusion("jline.");
        classLoader.addClassLoaderExclusion("org.fusesource.");
        classLoader.addClassLoaderExclusion("net.minecraftforge.server.console.TerminalConsoleAppender");

        FMLLaunchHandler.configureForServerLaunch(classLoader, this);
        FMLLaunchHandler.appendCoreMods();
    }
}
