package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiLabel extends Gui
{
    protected int field_146167_a;
    protected int field_146161_f;
    public int field_146162_g;
    public int field_146174_h;
    private ArrayList field_146173_k;
    private boolean field_146170_l;
    public boolean field_146172_j;
    private boolean field_146171_m;
    private int field_146168_n;
    private int field_146169_o;
    private int field_146166_p;
    private int field_146165_q;
    private FontRenderer field_146164_r;
    private int field_146163_s;
    private static final String __OBFID = "CL_00000671";

    public void func_146159_a(Minecraft p_146159_1_, int p_146159_2_, int p_146159_3_)
    {
        if (this.field_146172_j)
        {
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.func_148821_a(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.func_146160_b(p_146159_1_, p_146159_2_, p_146159_3_);
            int k = this.field_146174_h + this.field_146161_f / 2 + this.field_146163_s / 2;
            int l = k - this.field_146173_k.size() * 10 / 2;

            for (int i1 = 0; i1 < this.field_146173_k.size(); ++i1)
            {
                if (this.field_146170_l)
                {
                    this.drawCenteredString(this.field_146164_r, (String)this.field_146173_k.get(i1), this.field_146162_g + this.field_146167_a / 2, l + i1 * 10, this.field_146168_n);
                }
                else
                {
                    this.drawString(this.field_146164_r, (String)this.field_146173_k.get(i1), this.field_146162_g, l + i1 * 10, this.field_146168_n);
                }
            }
        }
    }

    protected void func_146160_b(Minecraft p_146160_1_, int p_146160_2_, int p_146160_3_)
    {
        if (this.field_146171_m)
        {
            int k = this.field_146167_a + this.field_146163_s * 2;
            int l = this.field_146161_f + this.field_146163_s * 2;
            int i1 = this.field_146162_g - this.field_146163_s;
            int j1 = this.field_146174_h - this.field_146163_s;
            drawRect(i1, j1, i1 + k, j1 + l, this.field_146169_o);
            this.drawHorizontalLine(i1, i1 + k, j1, this.field_146166_p);
            this.drawHorizontalLine(i1, i1 + k, j1 + l, this.field_146165_q);
            this.drawVerticalLine(i1, j1, j1 + l, this.field_146166_p);
            this.drawVerticalLine(i1 + k, j1, j1 + l, this.field_146165_q);
        }
    }
}