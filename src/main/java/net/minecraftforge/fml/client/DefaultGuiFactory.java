package net.minecraftforge.fml.client;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.common.ModContainer;

public class DefaultGuiFactory implements IModGuiFactory
{
    
    protected String modid, title;
    protected Minecraft minecraft;
    
    protected DefaultGuiFactory(String modid, String title)
    {
        this.modid = modid;
        this.title = title;
    }

    @Override
    public void initialize(Minecraft minecraftInstance)
    {
        this.minecraft = minecraftInstance;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen)
    {  
        return new GuiConfig(parentScreen, modid, title);
    }

    @Deprecated
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return null;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return null;
    }
    
    public static IModGuiFactory forMod(ModContainer mod)
    {
        return new DefaultGuiFactory(mod.getModId(), mod.getName());
    }

}
