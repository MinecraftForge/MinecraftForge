package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiButtonLanguage extends GuiButton
{
    private static final String __OBFID = "CL_00000672";

    public GuiButtonLanguage(int par1, int par2, int par3)
    {
        super(par1, par2, par3, 20, 20, "");
    }

    public void func_146112_a(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
    {
        if (this.field_146125_m)
        {
            p_146112_1_.getTextureManager().bindTexture(GuiButton.field_146122_a);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = p_146112_2_ >= this.field_146128_h && p_146112_3_ >= this.field_146129_i && p_146112_2_ < this.field_146128_h + this.field_146120_f && p_146112_3_ < this.field_146129_i + this.field_146121_g;
            int k = 106;

            if (flag)
            {
                k += this.field_146121_g;
            }

            this.drawTexturedModalRect(this.field_146128_h, this.field_146129_i, 0, k, this.field_146120_f, this.field_146121_g);
        }
    }
}