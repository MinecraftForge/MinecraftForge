package net.minecraftforge.fmp;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class FMLForgeMultipartPlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[0];
    }
    
    @Override
    public String getModContainerClass()
    {
        return "net.minecraftforge.fmp.ForgeMultipartModContainer";
    }
    
    @Override
    public String getSetupClass()
    {
        return null;
    }
    
    @Override
    public void injectData(Map<String, Object> data)
    {
    }
    
    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
