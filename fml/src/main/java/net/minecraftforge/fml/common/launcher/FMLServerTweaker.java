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
        classLoader.addClassLoaderExclusion("org.objectweb.asm.");
        classLoader.addTransformerExclusion("net.minecraftforge.fml.repackage.");
        classLoader.addTransformerExclusion("net.minecraftforge.fml.relauncher.");
        classLoader.addTransformerExclusion("net.minecraftforge.fml.common.asm.transformers.");
        classLoader.addClassLoaderExclusion("LZMA.");
        FMLLaunchHandler.configureForServerLaunch(classLoader, this);
        FMLLaunchHandler.appendCoreMods();
    }
}
