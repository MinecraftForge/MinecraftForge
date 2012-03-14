package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class GuiBrewingStand extends GuiContainer
{
    private TileEntityBrewingStand field_40217_h;

    public GuiBrewingStand(InventoryPlayer par1InventoryPlayer, TileEntityBrewingStand par2TileEntityBrewingStand)
    {
        super(new ContainerBrewingStand(par1InventoryPlayer, par2TileEntityBrewingStand));
        this.field_40217_h = par2TileEntityBrewingStand;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everythin in front of the items)
     */
    protected void drawGuiContainerForegroundLayer()
    {
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.brewing"), 56, 6, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int var4 = this.mc.renderEngine.getTexture("/gui/alchemy.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        int var7 = this.field_40217_h.getBrewTime();

        if (var7 > 0)
        {
            int var8 = (int)(28.0F * (1.0F - (float)var7 / 400.0F));

            if (var8 > 0)
            {
                this.drawTexturedModalRect(var5 + 97, var6 + 16, 176, 0, 9, var8);
            }

            int var9 = var7 / 2 % 7;

            switch (var9)
            {
                case 0:
                    var8 = 29;
                    break;

                case 1:
                    var8 = 24;
                    break;

                case 2:
                    var8 = 20;
                    break;

                case 3:
                    var8 = 16;
                    break;

                case 4:
                    var8 = 11;
                    break;

                case 5:
                    var8 = 6;
                    break;

                case 6:
                    var8 = 0;
            }

            if (var8 > 0)
            {
                this.drawTexturedModalRect(var5 + 65, var6 + 14 + 29 - var8, 185, 29 - var8, 12, var8);
            }
        }
    }
}
