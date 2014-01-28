package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiOptionSlider extends GuiButton
{
    private float field_146134_p;
    public boolean field_146135_o;
    private GameSettings.Options field_146133_q;
    private final float field_146132_r;
    private final float field_146131_s;
    private static final String __OBFID = "CL_00000680";

    public GuiOptionSlider(int p_i45016_1_, int p_i45016_2_, int p_i45016_3_, GameSettings.Options p_i45016_4_)
    {
        this(p_i45016_1_, p_i45016_2_, p_i45016_3_, p_i45016_4_, 0.0F, 1.0F);
    }

    public GuiOptionSlider(int p_i45017_1_, int p_i45017_2_, int p_i45017_3_, GameSettings.Options p_i45017_4_, float p_i45017_5_, float p_i45017_6_)
    {
        super(p_i45017_1_, p_i45017_2_, p_i45017_3_, 150, 20, "");
        this.field_146134_p = 1.0F;
        this.field_146133_q = p_i45017_4_;
        this.field_146132_r = p_i45017_5_;
        this.field_146131_s = p_i45017_6_;
        Minecraft minecraft = Minecraft.getMinecraft();
        this.field_146134_p = p_i45017_4_.func_148266_c(minecraft.gameSettings.getOptionFloatValue(p_i45017_4_));
        this.field_146126_j = minecraft.gameSettings.getKeyBinding(p_i45017_4_);
    }

    protected int func_146114_a(boolean p_146114_1_)
    {
        return 0;
    }

    protected void func_146119_b(Minecraft p_146119_1_, int p_146119_2_, int p_146119_3_)
    {
        if (this.field_146125_m)
        {
            if (this.field_146135_o)
            {
                this.field_146134_p = (float)(p_146119_2_ - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8);

                if (this.field_146134_p < 0.0F)
                {
                    this.field_146134_p = 0.0F;
                }

                if (this.field_146134_p > 1.0F)
                {
                    this.field_146134_p = 1.0F;
                }

                float f = this.field_146133_q.func_148262_d(this.field_146134_p);
                p_146119_1_.gameSettings.setOptionFloatValue(this.field_146133_q, f);
                this.field_146134_p = this.field_146133_q.func_148266_c(f);
                this.field_146126_j = p_146119_1_.gameSettings.getKeyBinding(this.field_146133_q);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.field_146128_h + (int)(this.field_146134_p * (float)(this.field_146120_f - 8)), this.field_146129_i, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.field_146128_h + (int)(this.field_146134_p * (float)(this.field_146120_f - 8)) + 4, this.field_146129_i, 196, 66, 4, 20);
        }
    }

    public boolean func_146116_c(Minecraft p_146116_1_, int p_146116_2_, int p_146116_3_)
    {
        if (super.func_146116_c(p_146116_1_, p_146116_2_, p_146116_3_))
        {
            this.field_146134_p = (float)(p_146116_2_ - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8);

            if (this.field_146134_p < 0.0F)
            {
                this.field_146134_p = 0.0F;
            }

            if (this.field_146134_p > 1.0F)
            {
                this.field_146134_p = 1.0F;
            }

            p_146116_1_.gameSettings.setOptionFloatValue(this.field_146133_q, this.field_146133_q.func_148262_d(this.field_146134_p));
            this.field_146126_j = p_146116_1_.gameSettings.getKeyBinding(this.field_146133_q);
            this.field_146135_o = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    public void func_146118_a(int p_146118_1_, int p_146118_2_)
    {
        this.field_146135_o = false;
    }
}