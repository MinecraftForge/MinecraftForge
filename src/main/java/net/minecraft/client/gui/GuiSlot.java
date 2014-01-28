package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class GuiSlot
{
    private final Minecraft field_148161_k;
    protected int field_148155_a;
    private int field_148158_l;
    protected int field_148153_b;
    protected int field_148154_c;
    protected int field_148151_d;
    protected int field_148152_e;
    protected final int field_148149_f;
    private int field_148159_m;
    private int field_148156_n;
    protected int field_148150_g;
    protected int field_148162_h;
    protected boolean field_148163_i = true;
    private float field_148157_o = -2.0F;
    private float field_148170_p;
    private float field_148169_q;
    private int field_148168_r = -1;
    private long field_148167_s;
    private boolean field_148166_t = true;
    private boolean field_148165_u;
    protected int field_148160_j;
    private boolean field_148164_v = true;
    private static final String __OBFID = "CL_00000679";

    public GuiSlot(Minecraft par1Minecraft, int par2, int par3, int par4, int par5, int par6)
    {
        this.field_148161_k = par1Minecraft;
        this.field_148155_a = par2;
        this.field_148158_l = par3;
        this.field_148153_b = par4;
        this.field_148154_c = par5;
        this.field_148149_f = par6;
        this.field_148152_e = 0;
        this.field_148151_d = par2;
    }

    public void func_148122_a(int p_148122_1_, int p_148122_2_, int p_148122_3_, int p_148122_4_)
    {
        this.field_148155_a = p_148122_1_;
        this.field_148158_l = p_148122_2_;
        this.field_148153_b = p_148122_3_;
        this.field_148154_c = p_148122_4_;
        this.field_148152_e = 0;
        this.field_148151_d = p_148122_1_;
    }

    public void func_148130_a(boolean p_148130_1_)
    {
        this.field_148166_t = p_148130_1_;
    }

    protected void func_148133_a(boolean p_148133_1_, int p_148133_2_)
    {
        this.field_148165_u = p_148133_1_;
        this.field_148160_j = p_148133_2_;

        if (!p_148133_1_)
        {
            this.field_148160_j = 0;
        }
    }

    protected abstract int func_148127_b();

    protected abstract void func_148144_a(int var1, boolean var2, int var3, int var4);

    protected abstract boolean func_148131_a(int var1);

    protected int func_148138_e()
    {
        return this.func_148127_b() * this.field_148149_f + this.field_148160_j;
    }

    protected abstract void func_148123_a();

    protected abstract void func_148126_a(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7);

    protected void func_148129_a(int p_148129_1_, int p_148129_2_, Tessellator p_148129_3_) {}

    protected void func_148132_a(int p_148132_1_, int p_148132_2_) {}

    protected void func_148142_b(int p_148142_1_, int p_148142_2_) {}

    public int func_148124_c(int p_148124_1_, int p_148124_2_)
    {
        int k = this.field_148152_e + this.field_148155_a / 2 - this.func_148139_c() / 2;
        int l = this.field_148152_e + this.field_148155_a / 2 + this.func_148139_c() / 2;
        int i1 = p_148124_2_ - this.field_148153_b - this.field_148160_j + (int)this.field_148169_q - 4;
        int j1 = i1 / this.field_148149_f;
        return p_148124_1_ < this.func_148137_d() && p_148124_1_ >= k && p_148124_1_ <= l && j1 >= 0 && i1 >= 0 && j1 < this.func_148127_b() ? j1 : -1;
    }

    public void func_148134_d(int p_148134_1_, int p_148134_2_)
    {
        this.field_148159_m = p_148134_1_;
        this.field_148156_n = p_148134_2_;
    }

    private void func_148121_k()
    {
        int i = this.func_148135_f();

        if (i < 0)
        {
            i /= 2;
        }

        if (!this.field_148163_i && i < 0)
        {
            i = 0;
        }

        if (this.field_148169_q < 0.0F)
        {
            this.field_148169_q = 0.0F;
        }

        if (this.field_148169_q > (float)i)
        {
            this.field_148169_q = (float)i;
        }
    }

    public int func_148135_f()
    {
        return this.func_148138_e() - (this.field_148154_c - this.field_148153_b - 4);
    }

    public int func_148148_g()
    {
        return (int)this.field_148169_q;
    }

    public boolean func_148141_e(int p_148141_1_)
    {
        return p_148141_1_ >= this.field_148153_b && p_148141_1_ <= this.field_148154_c;
    }

    public void func_148145_f(int p_148145_1_)
    {
        this.field_148169_q += (float)p_148145_1_;
        this.func_148121_k();
        this.field_148157_o = -2.0F;
    }

    public void func_148147_a(GuiButton p_148147_1_)
    {
        if (p_148147_1_.field_146124_l)
        {
            if (p_148147_1_.field_146127_k == this.field_148159_m)
            {
                this.field_148169_q -= (float)(this.field_148149_f * 2 / 3);
                this.field_148157_o = -2.0F;
                this.func_148121_k();
            }
            else if (p_148147_1_.field_146127_k == this.field_148156_n)
            {
                this.field_148169_q += (float)(this.field_148149_f * 2 / 3);
                this.field_148157_o = -2.0F;
                this.func_148121_k();
            }
        }
    }

    public void func_148128_a(int p_148128_1_, int p_148128_2_, float p_148128_3_)
    {
        this.field_148150_g = p_148128_1_;
        this.field_148162_h = p_148128_2_;
        this.func_148123_a();
        int k = this.func_148127_b();
        int l = this.func_148137_d();
        int i1 = l + 6;
        int l1;
        int i2;
        int k2;
        int i3;

        if (p_148128_1_ > this.field_148152_e && p_148128_1_ < this.field_148151_d && p_148128_2_ > this.field_148153_b && p_148128_2_ < this.field_148154_c)
        {
            if (Mouse.isButtonDown(0) && this.func_148125_i())
            {
                if (this.field_148157_o == -1.0F)
                {
                    boolean flag1 = true;

                    if (p_148128_2_ >= this.field_148153_b && p_148128_2_ <= this.field_148154_c)
                    {
                        int k1 = this.field_148155_a / 2 - this.func_148139_c() / 2;
                        l1 = this.field_148155_a / 2 + this.func_148139_c() / 2;
                        i2 = p_148128_2_ - this.field_148153_b - this.field_148160_j + (int)this.field_148169_q - 4;
                        int j2 = i2 / this.field_148149_f;

                        if (p_148128_1_ >= k1 && p_148128_1_ <= l1 && j2 >= 0 && i2 >= 0 && j2 < k)
                        {
                            boolean flag = j2 == this.field_148168_r && Minecraft.getSystemTime() - this.field_148167_s < 250L;
                            this.func_148144_a(j2, flag, p_148128_1_, p_148128_2_);
                            this.field_148168_r = j2;
                            this.field_148167_s = Minecraft.getSystemTime();
                        }
                        else if (p_148128_1_ >= k1 && p_148128_1_ <= l1 && i2 < 0)
                        {
                            this.func_148132_a(p_148128_1_ - k1, p_148128_2_ - this.field_148153_b + (int)this.field_148169_q - 4);
                            flag1 = false;
                        }

                        if (p_148128_1_ >= l && p_148128_1_ <= i1)
                        {
                            this.field_148170_p = -1.0F;
                            i3 = this.func_148135_f();

                            if (i3 < 1)
                            {
                                i3 = 1;
                            }

                            k2 = (int)((float)((this.field_148154_c - this.field_148153_b) * (this.field_148154_c - this.field_148153_b)) / (float)this.func_148138_e());

                            if (k2 < 32)
                            {
                                k2 = 32;
                            }

                            if (k2 > this.field_148154_c - this.field_148153_b - 8)
                            {
                                k2 = this.field_148154_c - this.field_148153_b - 8;
                            }

                            this.field_148170_p /= (float)(this.field_148154_c - this.field_148153_b - k2) / (float)i3;
                        }
                        else
                        {
                            this.field_148170_p = 1.0F;
                        }

                        if (flag1)
                        {
                            this.field_148157_o = (float)p_148128_2_;
                        }
                        else
                        {
                            this.field_148157_o = -2.0F;
                        }
                    }
                    else
                    {
                        this.field_148157_o = -2.0F;
                    }
                }
                else if (this.field_148157_o >= 0.0F)
                {
                    this.field_148169_q -= ((float)p_148128_2_ - this.field_148157_o) * this.field_148170_p;
                    this.field_148157_o = (float)p_148128_2_;
                }
            }
            else
            {
                for (; !this.field_148161_k.gameSettings.touchscreen && Mouse.next(); this.field_148161_k.currentScreen.func_146274_d())
                {
                    int j1 = Mouse.getEventDWheel();

                    if (j1 != 0)
                    {
                        if (j1 > 0)
                        {
                            j1 = -1;
                        }
                        else if (j1 < 0)
                        {
                            j1 = 1;
                        }

                        this.field_148169_q += (float)(j1 * this.field_148149_f / 2);
                    }
                }

                this.field_148157_o = -1.0F;
            }
        }

        this.func_148121_k();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator tessellator = Tessellator.instance;
        drawContainerBackground(tessellator);
        l1 = this.field_148152_e + this.field_148155_a / 2 - this.func_148139_c() / 2 + 2;
        i2 = this.field_148153_b + 4 - (int)this.field_148169_q;

        if (this.field_148165_u)
        {
            this.func_148129_a(l1, i2, tessellator);
        }

        this.func_148120_b(l1, i2, p_148128_1_, p_148128_2_);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        byte b0 = 4;
        this.func_148136_c(0, this.field_148153_b, 255, 255);
        this.func_148136_c(this.field_148154_c, this.field_148158_l, 255, 255);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.func_148821_a(770, 771, 0, 1);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0, 0);
        tessellator.addVertexWithUV((double)this.field_148152_e, (double)(this.field_148153_b + b0), 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV((double)this.field_148151_d, (double)(this.field_148153_b + b0), 0.0D, 1.0D, 1.0D);
        tessellator.setColorRGBA_I(0, 255);
        tessellator.addVertexWithUV((double)this.field_148151_d, (double)this.field_148153_b, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV((double)this.field_148152_e, (double)this.field_148153_b, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0, 255);
        tessellator.addVertexWithUV((double)this.field_148152_e, (double)this.field_148154_c, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV((double)this.field_148151_d, (double)this.field_148154_c, 0.0D, 1.0D, 1.0D);
        tessellator.setColorRGBA_I(0, 0);
        tessellator.addVertexWithUV((double)this.field_148151_d, (double)(this.field_148154_c - b0), 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV((double)this.field_148152_e, (double)(this.field_148154_c - b0), 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        i3 = this.func_148135_f();

        if (i3 > 0)
        {
            k2 = (this.field_148154_c - this.field_148153_b) * (this.field_148154_c - this.field_148153_b) / this.func_148138_e();

            if (k2 < 32)
            {
                k2 = 32;
            }

            if (k2 > this.field_148154_c - this.field_148153_b - 8)
            {
                k2 = this.field_148154_c - this.field_148153_b - 8;
            }

            int l2 = (int)this.field_148169_q * (this.field_148154_c - this.field_148153_b - k2) / i3 + this.field_148153_b;

            if (l2 < this.field_148153_b)
            {
                l2 = this.field_148153_b;
            }

            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(0, 255);
            tessellator.addVertexWithUV((double)l, (double)this.field_148154_c, 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV((double)i1, (double)this.field_148154_c, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV((double)i1, (double)this.field_148153_b, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV((double)l, (double)this.field_148153_b, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(8421504, 255);
            tessellator.addVertexWithUV((double)l, (double)(l2 + k2), 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV((double)i1, (double)(l2 + k2), 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV((double)i1, (double)l2, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV((double)l, (double)l2, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(12632256, 255);
            tessellator.addVertexWithUV((double)l, (double)(l2 + k2 - 1), 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV((double)(i1 - 1), (double)(l2 + k2 - 1), 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV((double)(i1 - 1), (double)l2, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV((double)l, (double)l2, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
        }

        this.func_148142_b(p_148128_1_, p_148128_2_);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void func_148143_b(boolean p_148143_1_)
    {
        this.field_148164_v = p_148143_1_;
    }

    public boolean func_148125_i()
    {
        return this.field_148164_v;
    }

    public int func_148139_c()
    {
        return 220;
    }

    protected void func_148120_b(int p_148120_1_, int p_148120_2_, int p_148120_3_, int p_148120_4_)
    {
        int i1 = this.func_148127_b();
        Tessellator tessellator = Tessellator.instance;

        for (int j1 = 0; j1 < i1; ++j1)
        {
            int k1 = p_148120_2_ + j1 * this.field_148149_f + this.field_148160_j;
            int l1 = this.field_148149_f - 4;

            if (k1 <= this.field_148154_c && k1 + l1 >= this.field_148153_b)
            {
                if (this.field_148166_t && this.func_148131_a(j1))
                {
                    int i2 = this.field_148152_e + (this.field_148155_a / 2 - this.func_148139_c() / 2);
                    int j2 = this.field_148152_e + this.field_148155_a / 2 + this.func_148139_c() / 2;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    tessellator.setColorOpaque_I(8421504);
                    tessellator.addVertexWithUV((double)i2, (double)(k1 + l1 + 2), 0.0D, 0.0D, 1.0D);
                    tessellator.addVertexWithUV((double)j2, (double)(k1 + l1 + 2), 0.0D, 1.0D, 1.0D);
                    tessellator.addVertexWithUV((double)j2, (double)(k1 - 2), 0.0D, 1.0D, 0.0D);
                    tessellator.addVertexWithUV((double)i2, (double)(k1 - 2), 0.0D, 0.0D, 0.0D);
                    tessellator.setColorOpaque_I(0);
                    tessellator.addVertexWithUV((double)(i2 + 1), (double)(k1 + l1 + 1), 0.0D, 0.0D, 1.0D);
                    tessellator.addVertexWithUV((double)(j2 - 1), (double)(k1 + l1 + 1), 0.0D, 1.0D, 1.0D);
                    tessellator.addVertexWithUV((double)(j2 - 1), (double)(k1 - 1), 0.0D, 1.0D, 0.0D);
                    tessellator.addVertexWithUV((double)(i2 + 1), (double)(k1 - 1), 0.0D, 0.0D, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                this.func_148126_a(j1, p_148120_1_, k1, l1, tessellator, p_148120_3_, p_148120_4_);
            }
        }
    }

    protected int func_148137_d()
    {
        return this.field_148155_a / 2 + 124;
    }

    private void func_148136_c(int p_148136_1_, int p_148136_2_, int p_148136_3_, int p_148136_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        this.field_148161_k.getTextureManager().bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(4210752, p_148136_4_);
        tessellator.addVertexWithUV((double)this.field_148152_e, (double)p_148136_2_, 0.0D, 0.0D, (double)((float)p_148136_2_ / f));
        tessellator.addVertexWithUV((double)(this.field_148152_e + this.field_148155_a), (double)p_148136_2_, 0.0D, (double)((float)this.field_148155_a / f), (double)((float)p_148136_2_ / f));
        tessellator.setColorRGBA_I(4210752, p_148136_3_);
        tessellator.addVertexWithUV((double)(this.field_148152_e + this.field_148155_a), (double)p_148136_1_, 0.0D, (double)((float)this.field_148155_a / f), (double)((float)p_148136_1_ / f));
        tessellator.addVertexWithUV((double)this.field_148152_e, (double)p_148136_1_, 0.0D, 0.0D, (double)((float)p_148136_1_ / f));
        tessellator.draw();
    }

    public void func_148140_g(int p_148140_1_)
    {
        this.field_148152_e = p_148140_1_;
        this.field_148151_d = p_148140_1_ + this.field_148155_a;
    }

    public int func_148146_j()
    {
        return this.field_148149_f;
    }

    protected void drawContainerBackground(Tessellator tessellator)
    {
        this.field_148161_k.getTextureManager().bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f1 = 32.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(2105376);
        tessellator.addVertexWithUV((double)this.field_148152_e, (double)this.field_148154_c, 0.0D, (double)((float)this.field_148152_e / f1), (double)((float)(this.field_148154_c + (int)this.field_148169_q) / f1));
        tessellator.addVertexWithUV((double)this.field_148151_d, (double)this.field_148154_c, 0.0D, (double)((float)this.field_148151_d / f1), (double)((float)(this.field_148154_c + (int)this.field_148169_q) / f1));
        tessellator.addVertexWithUV((double)this.field_148151_d, (double)this.field_148153_b, 0.0D, (double)((float)this.field_148151_d / f1), (double)((float)(this.field_148153_b + (int)this.field_148169_q) / f1));
        tessellator.addVertexWithUV((double)this.field_148152_e, (double)this.field_148153_b, 0.0D, (double)((float)this.field_148152_e / f1), (double)((float)(this.field_148153_b + (int)this.field_148169_q) / f1));
        tessellator.draw();
    }
}