package net.minecraft.src;

class GuiSlotStatsGeneral extends GuiSlot
{
    final GuiStats field_27276_a;

    public GuiSlotStatsGeneral(GuiStats par1GuiStats)
    {
        super(GuiStats.getMinecraft(par1GuiStats), par1GuiStats.width, par1GuiStats.height, 32, par1GuiStats.height - 64, 10);
        this.field_27276_a = par1GuiStats;
        this.func_27258_a(false);
    }

    /**
     * Gets the size of the current slot list.
     */
    protected int getSize()
    {
        return StatList.generalStats.size();
    }

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    protected void elementClicked(int par1, boolean par2) {}

    /**
     * returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int par1)
    {
        return false;
    }

    /**
     * return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.getSize() * 10;
    }

    protected void drawBackground()
    {
        this.field_27276_a.drawDefaultBackground();
    }

    protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5Tessellator)
    {
        StatBase var6 = (StatBase)StatList.generalStats.get(par1);
        this.field_27276_a.drawString(GuiStats.getFontRenderer1(this.field_27276_a), StatCollector.translateToLocal(var6.getName()), par2 + 2, par3 + 1, par1 % 2 == 0 ? 16777215 : 9474192);
        String var7 = var6.func_27084_a(GuiStats.getStatsFileWriter(this.field_27276_a).writeStat(var6));
        this.field_27276_a.drawString(GuiStats.getFontRenderer2(this.field_27276_a), var7, par2 + 2 + 213 - GuiStats.getFontRenderer3(this.field_27276_a).getStringWidth(var7), par3 + 1, par1 % 2 == 0 ? 16777215 : 9474192);
    }
}
