package net.minecraft.src.blocks;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class FurnaceGui extends GuiContainer
{
    public FurnaceLogic furnacelogic;

    public FurnaceGui(InventoryPlayer inventoryplayer, FurnaceLogic infifurnacelogic)
    {
        super(new FurnaceContainer(inventoryplayer, infifurnacelogic));
        furnacelogic = infifurnacelogic;
    }

    protected void drawGuiContainerForegroundLayer()
    {
        fontRenderer.drawString("Furnace", 60, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        int k = mc.renderEngine.getTexture("/gui/furnace.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(k);
        int l = (width - xSize) / 2;
        int i1 = (height - ySize) / 2;
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        if (furnacelogic.fuel > 0)
        {
            int j1 = furnacelogic.gaugeFuelScaled(12);
            drawTexturedModalRect(l + 56, (i1 + 36 + 12) - j1, 176, 12 - j1, 14, j1 + 2);
        }
        int k1 = furnacelogic.gaugeProgressScaled(15);
        drawTexturedModalRect(l + 79, i1 + 34, 176, 14, k1, 16);
    }
}
