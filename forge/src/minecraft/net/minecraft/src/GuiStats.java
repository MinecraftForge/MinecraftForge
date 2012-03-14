package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiStats extends GuiScreen
{
    private static RenderItem renderItem = new RenderItem();
    protected GuiScreen parentGui;

    /** The title of the stats screen. */
    protected String statsTitle = "Select world";

    /** The slot for general stats. */
    private GuiSlotStatsGeneral slotGeneral;

    /** The slot for item stats. */
    private GuiSlotStatsItem slotItem;

    /** The slot for block stats. */
    private GuiSlotStatsBlock slotBlock;
    private StatFileWriter statFileWriter;

    /** The currently-selected slot. */
    private GuiSlot selectedSlot = null;

    public GuiStats(GuiScreen par1GuiScreen, StatFileWriter par2StatFileWriter)
    {
        this.parentGui = par1GuiScreen;
        this.statFileWriter = par2StatFileWriter;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.statsTitle = StatCollector.translateToLocal("gui.stats");
        this.slotGeneral = new GuiSlotStatsGeneral(this);
        this.slotGeneral.registerScrollButtons(this.controlList, 1, 1);
        this.slotItem = new GuiSlotStatsItem(this);
        this.slotItem.registerScrollButtons(this.controlList, 1, 1);
        this.slotBlock = new GuiSlotStatsBlock(this);
        this.slotBlock.registerScrollButtons(this.controlList, 1, 1);
        this.selectedSlot = this.slotGeneral;
        this.addHeaderButtons();
    }

    /**
     * Creates the buttons that appear at the top of the Stats GUI.
     */
    public void addHeaderButtons()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        this.controlList.add(new GuiButton(0, this.width / 2 + 4, this.height - 28, 150, 20, var1.translateKey("gui.done")));
        this.controlList.add(new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, var1.translateKey("stat.generalButton")));
        GuiButton var2;
        this.controlList.add(var2 = new GuiButton(2, this.width / 2 - 46, this.height - 52, 100, 20, var1.translateKey("stat.blocksButton")));
        GuiButton var3;
        this.controlList.add(var3 = new GuiButton(3, this.width / 2 + 62, this.height - 52, 100, 20, var1.translateKey("stat.itemsButton")));

        if (this.slotBlock.getSize() == 0)
        {
            var2.enabled = false;
        }

        if (this.slotItem.getSize() == 0)
        {
            var3.enabled = false;
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 0)
            {
                this.mc.displayGuiScreen(this.parentGui);
            }
            else if (par1GuiButton.id == 1)
            {
                this.selectedSlot = this.slotGeneral;
            }
            else if (par1GuiButton.id == 3)
            {
                this.selectedSlot = this.slotItem;
            }
            else if (par1GuiButton.id == 2)
            {
                this.selectedSlot = this.slotBlock;
            }
            else
            {
                this.selectedSlot.actionPerformed(par1GuiButton);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.selectedSlot.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, this.statsTitle, this.width / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    /**
     * Draws the item sprite on top of the background sprite.
     */
    private void drawItemSprite(int par1, int par2, int par3)
    {
        this.drawButtonBackground(par1 + 1, par2 + 1);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.drawItemIntoGui(this.fontRenderer, this.mc.renderEngine, par3, 0, Item.itemsList[par3].getIconFromDamage(0), par1 + 2, par2 + 2);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    /**
     * Draws a gray box that serves as a button background.
     */
    private void drawButtonBackground(int par1, int par2)
    {
        this.drawSprite(par1, par2, 0, 0);
    }

    /**
     * Draws a sprite from /gui/slot.png.
     */
    private void drawSprite(int par1, int par2, int par3, int par4)
    {
        int var5 = this.mc.renderEngine.getTexture("/gui/slot.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var5);
        Tessellator var10 = Tessellator.instance;
        var10.startDrawingQuads();
        var10.addVertexWithUV((double)(par1 + 0), (double)(par2 + 18), (double)this.zLevel, (double)((float)(par3 + 0) * 0.0078125F), (double)((float)(par4 + 18) * 0.0078125F));
        var10.addVertexWithUV((double)(par1 + 18), (double)(par2 + 18), (double)this.zLevel, (double)((float)(par3 + 18) * 0.0078125F), (double)((float)(par4 + 18) * 0.0078125F));
        var10.addVertexWithUV((double)(par1 + 18), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + 18) * 0.0078125F), (double)((float)(par4 + 0) * 0.0078125F));
        var10.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + 0) * 0.0078125F), (double)((float)(par4 + 0) * 0.0078125F));
        var10.draw();
    }

    static Minecraft getMinecraft(GuiStats par0GuiStats)
    {
        return par0GuiStats.mc;
    }

    /**
     * there are 11 identical methods like this
     */
    static FontRenderer getFontRenderer1(GuiStats par0GuiStats)
    {
        return par0GuiStats.fontRenderer;
    }

    static StatFileWriter getStatsFileWriter(GuiStats par0GuiStats)
    {
        return par0GuiStats.statFileWriter;
    }

    /**
     * there are 11 identical methods like this
     */
    static FontRenderer getFontRenderer2(GuiStats par0GuiStats)
    {
        return par0GuiStats.fontRenderer;
    }

    /**
     * there are 11 identical methods like this
     */
    static FontRenderer getFontRenderer3(GuiStats par0GuiStats)
    {
        return par0GuiStats.fontRenderer;
    }

    /**
     * exactly the same as 27141
     */
    static Minecraft getMinecraft1(GuiStats par0GuiStats)
    {
        return par0GuiStats.mc;
    }

    /**
     * Draws a sprite from /gui/slot.png.
     */
    static void drawSprite(GuiStats par0GuiStats, int par1, int par2, int par3, int par4)
    {
        par0GuiStats.drawSprite(par1, par2, par3, par4);
    }

    /**
     * exactly the same as 27141 and 27143
     */
    static Minecraft getMinecraft2(GuiStats par0GuiStats)
    {
        return par0GuiStats.mc;
    }

    /**
     * there are 11 identical methods like this
     */
    static FontRenderer getFontRenderer4(GuiStats par0GuiStats)
    {
        return par0GuiStats.fontRenderer;
    }

    /**
     * there are 11 identical methods like this
     */
    static FontRenderer getFontRenderer5(GuiStats par0GuiStats)
    {
        return par0GuiStats.fontRenderer;
    }

    /**
     * there are 11 identical methods like this
     */
    static FontRenderer getFontRenderer6(GuiStats par0GuiStats)
    {
        return par0GuiStats.fontRenderer;
    }

    /**
     * there are 11 identical methods like this
     */
    static FontRenderer getFontRenderer7(GuiStats par0GuiStats)
    {
        return par0GuiStats.fontRenderer;
    }

    /**
     * there are 11 identical methods like this
     */
    static FontRenderer getFontRenderer8(GuiStats par0GuiStats)
    {
        return par0GuiStats.fontRenderer;
    }

    static void drawGradientRect(GuiStats par0GuiStats, int par1, int par2, int par3, int par4, int par5, int par6)
    {
        par0GuiStats.drawGradientRect(par1, par2, par3, par4, par5, par6);
    }

    /**
     * there are 11 identical methods like this
     */
    static FontRenderer getFontRenderer9(GuiStats par0GuiStats)
    {
        return par0GuiStats.fontRenderer;
    }

    /**
     * there are 11 identical methods like this
     */
    static FontRenderer getFontRenderer10(GuiStats par0GuiStats)
    {
        return par0GuiStats.fontRenderer;
    }

    /**
     * exactly the same as 27129
     */
    static void drawGradientRect1(GuiStats par0GuiStats, int par1, int par2, int par3, int par4, int par5, int par6)
    {
        par0GuiStats.drawGradientRect(par1, par2, par3, par4, par5, par6);
    }

    /**
     * there are 11 identical methods like this
     */
    static FontRenderer getFontRenderer11(GuiStats par0GuiStats)
    {
        return par0GuiStats.fontRenderer;
    }

    /**
     * Draws the item sprite on top of the background sprite.
     */
    static void drawItemSprite(GuiStats par0GuiStats, int par1, int par2, int par3)
    {
        par0GuiStats.drawItemSprite(par1, par2, par3);
    }
}
