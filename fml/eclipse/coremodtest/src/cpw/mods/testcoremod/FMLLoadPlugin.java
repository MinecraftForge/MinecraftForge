package cpw.mods.testcoremod;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class FMLLoadPlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getLibraryRequestClass()
    {
        return null;
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return null;
    }

    @Override
    public String getModContainerClass()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSetupClass()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        // TODO Auto-generated method stub

    }

}
