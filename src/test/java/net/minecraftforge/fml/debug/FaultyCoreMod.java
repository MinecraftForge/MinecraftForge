package net.minecraftforge.fml.debug;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

public class FaultyCoreMod implements IFMLLoadingPlugin
{
    public static boolean enabled = false;

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{FaultyTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass()
    {
        return null;
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

    public static class FaultyTransformer implements IClassTransformer
    {

        @Override
        public byte[] transform(String name, String transformedName, byte[] basicClass)
        {
            if (enabled && name.equals("net.minecraft.client.gui.GuiMainMenu"))
            {
                throw new RuntimeException("Faulty transformer test exception");
            }
            return basicClass;
        }
    }
}
