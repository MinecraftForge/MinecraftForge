package cpw.mods.fml.relauncher;

import java.util.Map;

public class FMLCorePlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getLibraryRequestClass()
    {
        return new String[] {"cpw.mods.fml.relauncher.CoreFMLLibraries"};
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {"cpw.mods.fml.common.asm.ASMTransformer"};
    }

    @Override
    public String getModContainerClass()
    {
        return "cpw.mods.fml.common.FMLDummyContainer";
    }

    @Override
    public String getSetupClass()
    {
        return "cpw.mods.fml.relauncher.FMLSanityChecker";
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        // don't care about this data
    }
}
