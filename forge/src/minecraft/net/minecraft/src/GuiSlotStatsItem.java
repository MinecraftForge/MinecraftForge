package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;

class GuiSlotStatsItem extends GuiSlotStats
{
    final GuiStats field_27275_a;

    public GuiSlotStatsItem(GuiStats par1GuiStats)
    {
        super(par1GuiStats);
        this.field_27275_a = par1GuiStats;
        this.field_27273_c = new ArrayList();
        Iterator var2 = StatList.itemStats.iterator();

        while (var2.hasNext())
        {
            StatCrafting var3 = (StatCrafting)var2.next();
            boolean var4 = false;
            int var5 = var3.getItemID();

            if (GuiStats.getStatsFileWriter(par1GuiStats).writeStat(var3) > 0)
            {
                var4 = true;
            }
            else if (StatList.objectBreakStats[var5] != null && GuiStats.getStatsFileWriter(par1GuiStats).writeStat(StatList.objectBreakStats[var5]) > 0)
            {
                var4 = true;
            }
            else if (StatList.objectCraftStats[var5] != null && GuiStats.getStatsFileWriter(par1GuiStats).writeStat(StatList.objectCraftStats[var5]) > 0)
            {
                var4 = true;
            }

            if (var4)
            {
                this.field_27273_c.add(var3);
            }
        }

        this.field_27272_d = new SorterStatsItem(this, par1GuiStats);
    }

    protected void func_27260_a(int par1, int par2, Tessellator par3Tessellator)
    {
        super.func_27260_a(par1, par2, par3Tessellator);

        if (this.field_27268_b == 0)
        {
            GuiStats.drawSprite(this.field_27275_a, par1 + 115 - 18 + 1, par2 + 1 + 1, 72, 18);
        }
        else
        {
            GuiStats.drawSprite(this.field_27275_a, par1 + 115 - 18, par2 + 1, 72, 18);
        }

        if (this.field_27268_b == 1)
        {
            GuiStats.drawSprite(this.field_27275_a, par1 + 165 - 18 + 1, par2 + 1 + 1, 18, 18);
        }
        else
        {
            GuiStats.drawSprite(this.field_27275_a, par1 + 165 - 18, par2 + 1, 18, 18);
        }

        if (this.field_27268_b == 2)
        {
            GuiStats.drawSprite(this.field_27275_a, par1 + 215 - 18 + 1, par2 + 1 + 1, 36, 18);
        }
        else
        {
            GuiStats.drawSprite(this.field_27275_a, par1 + 215 - 18, par2 + 1, 36, 18);
        }
    }

    protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5Tessellator)
    {
        StatCrafting var6 = this.func_27264_b(par1);
        int var7 = var6.getItemID();
        GuiStats.drawItemSprite(this.field_27275_a, par2 + 40, par3, var7);
        this.func_27265_a((StatCrafting)StatList.objectBreakStats[var7], par2 + 115, par3, par1 % 2 == 0);
        this.func_27265_a((StatCrafting)StatList.objectCraftStats[var7], par2 + 165, par3, par1 % 2 == 0);
        this.func_27265_a(var6, par2 + 215, par3, par1 % 2 == 0);
    }

    protected String func_27263_a(int par1)
    {
        return par1 == 1 ? "stat.crafted" : (par1 == 2 ? "stat.used" : "stat.depleted");
    }
}
