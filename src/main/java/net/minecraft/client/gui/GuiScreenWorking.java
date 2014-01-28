package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IProgressUpdate;

@SideOnly(Side.CLIENT)
public class GuiScreenWorking extends GuiScreen implements IProgressUpdate
{
    private String field_146591_a = "";
    private String field_146589_f = "";
    private int field_146590_g;
    private boolean field_146592_h;
    private static final String __OBFID = "CL_00000707";

    // JAVADOC METHOD $$ func_73720_a
    public void displayProgressMessage(String par1Str)
    {
        this.resetProgressAndMessage(par1Str);
    }

    // JAVADOC METHOD $$ func_73721_b
    public void resetProgressAndMessage(String par1Str)
    {
        this.field_146591_a = par1Str;
        this.resetProgresAndWorkingMessage("Working...");
    }

    // JAVADOC METHOD $$ func_73719_c
    public void resetProgresAndWorkingMessage(String par1Str)
    {
        this.field_146589_f = par1Str;
        this.setLoadingProgress(0);
    }

    // JAVADOC METHOD $$ func_73718_a
    public void setLoadingProgress(int par1)
    {
        this.field_146590_g = par1;
    }

    public void func_146586_a()
    {
        this.field_146592_h = true;
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        if (this.field_146592_h)
        {
            this.field_146297_k.func_147108_a((GuiScreen)null);
        }
        else
        {
            this.func_146276_q_();
            this.drawCenteredString(this.field_146289_q, this.field_146591_a, this.field_146294_l / 2, 70, 16777215);
            this.drawCenteredString(this.field_146289_q, this.field_146589_f + " " + this.field_146590_g + "%", this.field_146294_l / 2, 90, 16777215);
            super.drawScreen(par1, par2, par3);
        }
    }
}