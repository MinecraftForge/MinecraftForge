package net.minecraftforge.classloading;

import java.io.File;
import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class FMLForgePlugin implements IFMLLoadingPlugin
{
    public static boolean RUNTIME_DEOBF = false;
    public static File forgeLocation;

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[0];
    }

    @Override
    public String getModContainerClass()
    {
        return "net.minecraftforge.common.ForgeModContainer";
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

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
