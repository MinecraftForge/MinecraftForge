package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

class GuiSlotLanguage extends GuiSlot
{
    private ArrayList field_44013_b;
    private TreeMap field_44014_c;

    final GuiLanguage field_44015_a;

    public GuiSlotLanguage(GuiLanguage par1GuiLanguage)
    {
        super(par1GuiLanguage.mc, par1GuiLanguage.width, par1GuiLanguage.height, 32, par1GuiLanguage.height - 65 + 4, 18);
        this.field_44015_a = par1GuiLanguage;
        this.field_44014_c = StringTranslate.getInstance().getLanguageList();
        this.field_44013_b = new ArrayList();
        Iterator var2 = this.field_44014_c.keySet().iterator();

        while (var2.hasNext())
        {
            String var3 = (String)var2.next();
            this.field_44013_b.add(var3);
        }
    }

    /**
     * Gets the size of the current slot list.
     */
    protected int getSize()
    {
        return this.field_44013_b.size();
    }

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    protected void elementClicked(int par1, boolean par2)
    {
        StringTranslate.getInstance().setLanguage((String)this.field_44013_b.get(par1));
        this.field_44015_a.mc.fontRenderer.setUnicodeFlag(StringTranslate.getInstance().isUnicode());
        GuiLanguage.func_44005_a(this.field_44015_a).language = (String)this.field_44013_b.get(par1);
        this.field_44015_a.fontRenderer.setBidiFlag(StringTranslate.isBidrectional(GuiLanguage.func_44005_a(this.field_44015_a).language));
        GuiLanguage.func_46028_b(this.field_44015_a).displayString = StringTranslate.getInstance().translateKey("gui.done");
    }

    /**
     * returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int par1)
    {
        return ((String)this.field_44013_b.get(par1)).equals(StringTranslate.getInstance().getCurrentLanguage());
    }

    /**
     * return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.getSize() * 18;
    }

    protected void drawBackground()
    {
        this.field_44015_a.drawDefaultBackground();
    }

    protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5Tessellator)
    {
        this.field_44015_a.fontRenderer.setBidiFlag(true);
        this.field_44015_a.drawCenteredString(this.field_44015_a.fontRenderer, (String)this.field_44014_c.get(this.field_44013_b.get(par1)), this.field_44015_a.width / 2, par3 + 1, 16777215);
        this.field_44015_a.fontRenderer.setBidiFlag(StringTranslate.isBidrectional(GuiLanguage.func_44005_a(this.field_44015_a).language));
    }
}
