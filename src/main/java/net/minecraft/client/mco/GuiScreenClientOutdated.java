package net.minecraft.client.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiScreenClientOutdated extends GuiScreen
{
    private final GuiScreen field_146901_a;
    private static final String __OBFID = "CL_00000772";

    public GuiScreenClientOutdated(GuiScreen par1GuiScreen)
    {
        this.field_146901_a = par1GuiScreen;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        this.field_146292_n.clear();
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, "Back"));
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        String s = I18n.getStringParams("mco.client.outdated.title", new Object[0]);
        String s1 = I18n.getStringParams("mco.client.outdated.msg", new Object[0]);
        this.drawCenteredString(this.field_146289_q, s, this.field_146294_l / 2, this.field_146295_m / 2 - 50, 16711680);
        this.drawCenteredString(this.field_146289_q, s1, this.field_146294_l / 2, this.field_146295_m / 2 - 30, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146127_k == 0)
        {
            this.field_146297_k.func_147108_a(this.field_146901_a);
        }
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 28 || par2 == 156)
        {
            this.field_146297_k.func_147108_a(this.field_146901_a);
        }
    }
}