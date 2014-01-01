package cpw.mods.fml.client;

import net.minecraft.client.renderer.Tessellator;

public class GuiModOptionList extends GuiScrollingList {

    private GuiIngameModOptions parent;

    public GuiModOptionList(GuiIngameModOptions parent)
    {
        super(parent.field_146297_k, 150, parent.field_146295_m, 32, parent.field_146295_m - 65 + 4, 10, 35);
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
        this.parent.getFontRenderer().func_78276_b(this.parent.getFontRenderer().func_78269_a("Test 1", listWidth - 10), this.left + 3 , var3 + 2, 0xFF2222);
        this.parent.getFontRenderer().func_78276_b(this.parent.getFontRenderer().func_78269_a("TEST 2", listWidth - 10), this.left + 3 , var3 + 12, 0xFF2222);
        this.parent.getFontRenderer().func_78276_b(this.parent.getFontRenderer().func_78269_a("DISABLED", listWidth - 10), this.left + 3 , var3 + 22, 0xFF2222);
    }

}
