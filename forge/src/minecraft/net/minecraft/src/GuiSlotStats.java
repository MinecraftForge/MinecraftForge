package net.minecraft.src;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.lwjgl.input.Mouse;

abstract class GuiSlotStats extends GuiSlot
{
    protected int field_27268_b;
    protected List field_27273_c;
    protected Comparator field_27272_d;
    protected int field_27271_e;
    protected int field_27270_f;

    final GuiStats field_27269_g;

    protected GuiSlotStats(GuiStats par1GuiStats)
    {
        super(GuiStats.getMinecraft1(par1GuiStats), par1GuiStats.width, par1GuiStats.height, 32, par1GuiStats.height - 64, 20);
        this.field_27269_g = par1GuiStats;
        this.field_27268_b = -1;
        this.field_27271_e = -1;
        this.field_27270_f = 0;
        this.func_27258_a(false);
        this.func_27259_a(true, 20);
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

    protected void drawBackground()
    {
        this.field_27269_g.drawDefaultBackground();
    }

    protected void func_27260_a(int par1, int par2, Tessellator par3Tessellator)
    {
        if (!Mouse.isButtonDown(0))
        {
            this.field_27268_b = -1;
        }

        if (this.field_27268_b == 0)
        {
            GuiStats.drawSprite(this.field_27269_g, par1 + 115 - 18, par2 + 1, 0, 0);
        }
        else
        {
            GuiStats.drawSprite(this.field_27269_g, par1 + 115 - 18, par2 + 1, 0, 18);
        }

        if (this.field_27268_b == 1)
        {
            GuiStats.drawSprite(this.field_27269_g, par1 + 165 - 18, par2 + 1, 0, 0);
        }
        else
        {
            GuiStats.drawSprite(this.field_27269_g, par1 + 165 - 18, par2 + 1, 0, 18);
        }

        if (this.field_27268_b == 2)
        {
            GuiStats.drawSprite(this.field_27269_g, par1 + 215 - 18, par2 + 1, 0, 0);
        }
        else
        {
            GuiStats.drawSprite(this.field_27269_g, par1 + 215 - 18, par2 + 1, 0, 18);
        }

        if (this.field_27271_e != -1)
        {
            short var4 = 79;
            byte var5 = 18;

            if (this.field_27271_e == 1)
            {
                var4 = 129;
            }
            else if (this.field_27271_e == 2)
            {
                var4 = 179;
            }

            if (this.field_27270_f == 1)
            {
                var5 = 36;
            }

            GuiStats.drawSprite(this.field_27269_g, par1 + var4, par2 + 1, var5, 0);
        }
    }

    protected void func_27255_a(int par1, int par2)
    {
        this.field_27268_b = -1;

        if (par1 >= 79 && par1 < 115)
        {
            this.field_27268_b = 0;
        }
        else if (par1 >= 129 && par1 < 165)
        {
            this.field_27268_b = 1;
        }
        else if (par1 >= 179 && par1 < 215)
        {
            this.field_27268_b = 2;
        }

        if (this.field_27268_b >= 0)
        {
            this.func_27266_c(this.field_27268_b);
            GuiStats.getMinecraft2(this.field_27269_g).sndManager.playSoundFX("random.click", 1.0F, 1.0F);
        }
    }

    /**
     * Gets the size of the current slot list.
     */
    protected final int getSize()
    {
        return this.field_27273_c.size();
    }

    protected final StatCrafting func_27264_b(int par1)
    {
        return (StatCrafting)this.field_27273_c.get(par1);
    }

    protected abstract String func_27263_a(int var1);

    protected void func_27265_a(StatCrafting par1StatCrafting, int par2, int par3, boolean par4)
    {
        String var5;

        if (par1StatCrafting != null)
        {
            var5 = par1StatCrafting.func_27084_a(GuiStats.getStatsFileWriter(this.field_27269_g).writeStat(par1StatCrafting));
            this.field_27269_g.drawString(GuiStats.getFontRenderer4(this.field_27269_g), var5, par2 - GuiStats.getFontRenderer5(this.field_27269_g).getStringWidth(var5), par3 + 5, par4 ? 16777215 : 9474192);
        }
        else
        {
            var5 = "-";
            this.field_27269_g.drawString(GuiStats.getFontRenderer6(this.field_27269_g), var5, par2 - GuiStats.getFontRenderer7(this.field_27269_g).getStringWidth(var5), par3 + 5, par4 ? 16777215 : 9474192);
        }
    }

    protected void func_27257_b(int par1, int par2)
    {
        if (par2 >= this.top && par2 <= this.bottom)
        {
            int var3 = this.func_27256_c(par1, par2);
            int var4 = this.field_27269_g.width / 2 - 92 - 16;

            if (var3 >= 0)
            {
                if (par1 < var4 + 40 || par1 > var4 + 40 + 20)
                {
                    return;
                }

                StatCrafting var5 = this.func_27264_b(var3);
                this.func_27267_a(var5, par1, par2);
            }
            else
            {
                String var9 = "";

                if (par1 >= var4 + 115 - 18 && par1 <= var4 + 115)
                {
                    var9 = this.func_27263_a(0);
                }
                else if (par1 >= var4 + 165 - 18 && par1 <= var4 + 165)
                {
                    var9 = this.func_27263_a(1);
                }
                else
                {
                    if (par1 < var4 + 215 - 18 || par1 > var4 + 215)
                    {
                        return;
                    }

                    var9 = this.func_27263_a(2);
                }

                var9 = ("" + StringTranslate.getInstance().translateKey(var9)).trim();

                if (var9.length() > 0)
                {
                    int var6 = par1 + 12;
                    int var7 = par2 - 12;
                    int var8 = GuiStats.getFontRenderer8(this.field_27269_g).getStringWidth(var9);
                    GuiStats.drawGradientRect(this.field_27269_g, var6 - 3, var7 - 3, var6 + var8 + 3, var7 + 8 + 3, -1073741824, -1073741824);
                    GuiStats.getFontRenderer9(this.field_27269_g).drawStringWithShadow(var9, var6, var7, -1);
                }
            }
        }
    }

    protected void func_27267_a(StatCrafting par1StatCrafting, int par2, int par3)
    {
        if (par1StatCrafting != null)
        {
            Item var4 = Item.itemsList[par1StatCrafting.getItemID()];
            String var5 = ("" + StringTranslate.getInstance().translateNamedKey(var4.getItemName())).trim();

            if (var5.length() > 0)
            {
                int var6 = par2 + 12;
                int var7 = par3 - 12;
                int var8 = GuiStats.getFontRenderer10(this.field_27269_g).getStringWidth(var5);
                GuiStats.drawGradientRect1(this.field_27269_g, var6 - 3, var7 - 3, var6 + var8 + 3, var7 + 8 + 3, -1073741824, -1073741824);
                GuiStats.getFontRenderer11(this.field_27269_g).drawStringWithShadow(var5, var6, var7, -1);
            }
        }
    }

    protected void func_27266_c(int par1)
    {
        if (par1 != this.field_27271_e)
        {
            this.field_27271_e = par1;
            this.field_27270_f = -1;
        }
        else if (this.field_27270_f == -1)
        {
            this.field_27270_f = 1;
        }
        else
        {
            this.field_27271_e = -1;
            this.field_27270_f = 0;
        }

        Collections.sort(this.field_27273_c, this.field_27272_d);
    }
}
