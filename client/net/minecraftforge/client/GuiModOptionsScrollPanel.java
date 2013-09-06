package net.minecraftforge.client;
import java.util.HashMap;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.gui.GuiScreen;


public class GuiModOptionsScrollPanel extends GuiSlot {
    
    public HashMap<String, GuiScreen> mods = new HashMap<String, GuiScreen>();
    private int selected;
    private Minecraft mc;
    private GuiModOptions parent;

    public GuiModOptionsScrollPanel(GuiModOptions modOptions, Minecraft mc)
    {
        super(mc, modOptions.width, modOptions.height, 16, (modOptions.height - 32) + 4, 25);
        this.mc = mc;
        parent = modOptions;
    }

    private String getKeyFromIndex(int index)
    {
        int count = 0;
        String key = "";
        for(String k : mods.keySet())
        {
            if(count == index)
            {
                key = k;
                break;
            }
            count++;
        }
        return key;
    }
    
    public GuiScreen getSelected()
    {
        return mods.get(getKeyFromIndex(selected));
    }
    
    @Override
    protected int getSize()
    {
        if(mods==null) return 0;
        return mods.size();
    }

    @Override
    protected void elementClicked(int i, boolean flag)
    {
        if(!flag)
        {
            selected = i;
        } else {
            selected = -1;
            System.out.println(getKeyFromIndex(i));
            this.mc.displayGuiScreen(mods.get(getKeyFromIndex(i)));
        }
    }

    @Override
    protected boolean isSelected(int i)
    {
        return selected==i;
    }

    @Override
    protected void drawBackground(){}

    @Override
    protected void drawSlot(int i, int xPosition, int yPosition, int l, Tessellator tessellator)
    {
        int width = 70;
        int height = 20;
        int count = 0;
        String label = "ERROR";
        for(String key : mods.keySet())
        {
            if(count == i)
            {
                label = key;
            }
            count++;
        }
        parent.drawCenteredString(mc.fontRenderer, label, parent.width / 2, yPosition + (height - 8) / 2, 0xFFFFFFFF);
    }

}
