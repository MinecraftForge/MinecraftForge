package cpw.mods.fml.common.launcher;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class FMLServerTweaker extends FMLTweaker {
    @Override
    public String getLaunchTarget()
    {
        return "net.minecraft.server.MinecraftServer";
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        classLoader.addTransformerExclusion("cpw.mods.fml.repackage.");
        classLoader.addTransformerExclusion("cpw.mods.fml.relauncher.");
        classLoader.addTransformerExclusion("cpw.mods.fml.common.asm.transformers.");
        classLoader.addClassLoaderExclusion("LZMA.");
        FMLLaunchHandler.configureForServerLaunch(classLoader, this);
        FMLLaunchHandler.appendCoreMods();
    }
}
