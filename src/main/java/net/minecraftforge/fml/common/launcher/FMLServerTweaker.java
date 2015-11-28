package net.minecraftforge.fml.common.launcher;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

public class FMLServerTweaker extends FMLTweaker {
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
        FMLLaunchHandler.configureForServerLaunch(classLoader, this);
        FMLLaunchHandler.appendCoreMods();
    }
}
