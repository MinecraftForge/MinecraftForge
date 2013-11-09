package net.minecraftforge.classloading;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class FMLForgePlugin implements IFMLLoadingPlugin
{
    public static boolean RUNTIME_DEOBF = false;
    public static File forgeLocation;

    @Override
    public String[] getLibraryRequestClass()
    {
        return null;
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{ 
                                "net.minecraftforge.transformers.ForgeAccessTransformer",
                                "net.minecraftforge.transformers.EventTransformer"
                           };
    }

    @Override
    public String getModContainerClass()
    {
        return "net.minecraftforge.common.ForgeDummyContainer";
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        RUNTIME_DEOBF = (Boolean)data.get("runtimeDeobfuscationEnabled");
        forgeLocation = (File)data.get("coremodLocation");
    }
}
