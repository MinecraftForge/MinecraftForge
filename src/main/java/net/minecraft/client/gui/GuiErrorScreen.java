package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiErrorScreen extends GuiScreen
{
    private String field_146313_a;
    private String field_146312_f;
    private static final String __OBFID = "CL_00000696";

    public GuiErrorScreen(String par1Str, String par2Str)
    {
        this.field_146313_a = par1Str;
        this.field_146312_f = par2Str;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        super.initGui();
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, 140, I18n.getStringParams("gui.cancel", new Object[0])));
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawGradientRect(0, 0, this.field_146294_l, this.field_146295_m, -12574688, -11530224);
        this.drawCenteredString(this.field_146289_q, this.field_146313_a, this.field_146294_l / 2, 90, 16777215);
        this.drawCenteredString(this.field_146289_q, this.field_146312_f, this.field_146294_l / 2, 110, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73869_a
    protected void keyTyped(char par1, int par2) {}

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        this.field_146297_k.func_147108_a((GuiScreen)null);
    }
}