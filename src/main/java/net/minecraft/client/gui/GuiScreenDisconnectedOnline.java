package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

@SideOnly(Side.CLIENT)
public class GuiScreenDisconnectedOnline extends GuiScreen
{
    private String field_146867_a;
    private IChatComponent field_146865_f;
    private List field_146866_g;
    private final GuiScreen field_146868_h;
    private static final String __OBFID = "CL_00000778";

    public GuiScreenDisconnectedOnline(GuiScreen p_i45037_1_, String p_i45037_2_, IChatComponent p_i45037_3_)
    {
        this.field_146868_h = p_i45037_1_;
        this.field_146867_a = I18n.getStringParams(p_i45037_2_, new Object[0]);
        this.field_146865_f = p_i45037_3_;
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2) {}

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146292_n.clear();
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.getStringParams("gui.back", new Object[0])));
        this.field_146866_g = this.field_146289_q.listFormattedStringToWidth(this.field_146865_f.func_150254_d(), this.field_146294_l - 50);
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146127_k == 0)
        {
            this.field_146297_k.func_147108_a(this.field_146868_h);
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, this.field_146867_a, this.field_146294_l / 2, this.field_146295_m / 2 - 50, 11184810);
        int k = this.field_146295_m / 2 - 30;

        if (this.field_146866_g != null)
        {
            for (Iterator iterator = this.field_146866_g.iterator(); iterator.hasNext(); k += this.field_146289_q.FONT_HEIGHT)
            {
                String s = (String)iterator.next();
                this.drawCenteredString(this.field_146289_q, s, this.field_146294_l / 2, k, 16777215);
            }
        }

        super.drawScreen(par1, par2, par3);
    }
}