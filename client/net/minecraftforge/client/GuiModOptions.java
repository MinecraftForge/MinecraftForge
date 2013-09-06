package net.minecraftforge.client;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

public class GuiModOptions extends GuiScreen {

    private GuiScreen parentScreen;
    private static GuiModOptions instance;
    private static GuiModOptionsScrollPanel scrollPanel;
    public static HashMap<String, GuiScreen> mods = new HashMap<String, GuiScreen>();
    
    private GuiModOptions() {}
    
    //When the user wants to get out of your mod settings page, use
    //Minecraft.getMinecraft().displayGuiScreen(GuiModOptions.getInstance());
    public static GuiModOptions getInstance()
    {
        if(instance == null)
        {
            instance = new GuiModOptions();
        }
        return instance;
    }
    
    //Name is the user friendly name, and your GuiScreen subclass will
    //be opened automatically
    public static void registerMod(String name, GuiScreen screen)
    {
        mods.put(name, screen);
    }
    
    public static void deregisterMod(String name)
    {
        mods.remove(name);
    }
    
    public void setParent(GuiScreen p)
    {
        parentScreen = p;
    }
    
    public void initGui()
    {
        scrollPanel = new GuiModOptionsScrollPanel(this, Minecraft.getMinecraft());
        scrollPanel.registerScrollButtons(7, 8);
        scrollPanel.mods = this.mods;
        
        this.buttonList.add(new GuiButton(1, this.width / 2 - 200, this.height - 24, I18n.func_135053_a("gui.back")));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height - 24, "OK"));
    }
    
    protected void actionPerformed(GuiButton par1GuiButton)
    {        
        if (par1GuiButton.id == 1)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        if (par1GuiButton.id == 2)
        {
            this.mc.displayGuiScreen(mods.get(scrollPanel.getSelected()));
        }
    }
    
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        scrollPanel.drawScreen(par1, par2, par3);
        drawCenteredString(fontRenderer, "Mod Options", width / 2, 4, 0xffffff);
        super.drawScreen(par1, par2, par3);
    }

}
