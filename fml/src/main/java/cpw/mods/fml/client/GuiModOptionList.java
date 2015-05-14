package cpw.mods.fml.client;

import net.minecraft.client.renderer.Tessellator;

public class GuiModOptionList extends GuiScrollingList {

    private GuiIngameModOptions parent;

    public GuiModOptionList(GuiIngameModOptions parent)
    {
        super(parent.mc, 150, parent.height, 32, parent.height - 65 + 4, 10, 35);
        this.parent = parent;
    }

    @Override
    protected int getSize()
    {
        return 1;
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick)
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean isSelected(int index)
    {
        return false;
    }

    @Override
    protected void drawBackground()
    {
    }

    @Override
    protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5)
    {
        this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth("Test 1", listWidth - 10), this.left + 3 , var3 + 2, 0xFF2222);
        this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth("TEST 2", listWidth - 10), this.left + 3 , var3 + 12, 0xFF2222);
        this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth("DISABLED", listWidth - 10), this.left + 3 , var3 + 22, 0xFF2222);
    }

}
