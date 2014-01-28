package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiScreenOptionsSounds extends GuiScreen
{
    private final GuiScreen field_146505_f;
    private final GameSettings field_146506_g;
    protected String field_146507_a = "Options";
    private String field_146508_h;
    private static final String __OBFID = "CL_00000716";

    public GuiScreenOptionsSounds(GuiScreen p_i45025_1_, GameSettings p_i45025_2_)
    {
        this.field_146505_f = p_i45025_1_;
        this.field_146506_g = p_i45025_2_;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        byte b0 = 0;
        this.field_146507_a = I18n.getStringParams("options.sounds.title", new Object[0]);
        this.field_146508_h = I18n.getStringParams("options.off", new Object[0]);
        this.field_146292_n.add(new GuiScreenOptionsSounds.Button(SoundCategory.MASTER.func_147156_b(), this.field_146294_l / 2 - 155 + b0 % 2 * 160, this.field_146295_m / 6 - 12 + 24 * (b0 >> 1), SoundCategory.MASTER, true));
        int k = b0 + 2;
        SoundCategory[] asoundcategory = SoundCategory.values();
        int i = asoundcategory.length;

        for (int j = 0; j < i; ++j)
        {
            SoundCategory soundcategory = asoundcategory[j];

            if (soundcategory != SoundCategory.MASTER)
            {
                this.field_146292_n.add(new GuiScreenOptionsSounds.Button(soundcategory.func_147156_b(), this.field_146294_l / 2 - 155 + k % 2 * 160, this.field_146295_m / 6 - 12 + 24 * (k >> 1), soundcategory, false));
                ++k;
            }
        }

        this.field_146292_n.add(new GuiButton(200, this.field_146294_l / 2 - 100, this.field_146295_m / 6 + 168, I18n.getStringParams("gui.done", new Object[0])));
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k == 200)
            {
                this.field_146297_k.gameSettings.saveOptions();
                this.field_146297_k.func_147108_a(this.field_146505_f);
            }
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, this.field_146507_a, this.field_146294_l / 2, 15, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    protected String func_146504_a(SoundCategory p_146504_1_)
    {
        float f = this.field_146506_g.func_151438_a(p_146504_1_);
        return f == 0.0F ? this.field_146508_h : (int)(f * 100.0F) + "%";
    }

    @SideOnly(Side.CLIENT)
    class Button extends GuiButton
    {
        private final SoundCategory field_146153_r;
        private final String field_146152_s;
        public float field_146156_o = 1.0F;
        public boolean field_146155_p;
        private static final String __OBFID = "CL_00000717";

        public Button(int p_i45024_2_, int p_i45024_3_, int p_i45024_4_, SoundCategory p_i45024_5_, boolean p_i45024_6_)
        {
            super(p_i45024_2_, p_i45024_3_, p_i45024_4_, p_i45024_6_ ? 310 : 150, 20, "");
            this.field_146153_r = p_i45024_5_;
            this.field_146152_s = I18n.getStringParams("soundCategory." + p_i45024_5_.func_147155_a(), new Object[0]);
            this.field_146126_j = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.func_146504_a(p_i45024_5_);
            this.field_146156_o = GuiScreenOptionsSounds.this.field_146506_g.func_151438_a(p_i45024_5_);
        }

        protected int func_146114_a(boolean p_146114_1_)
        {
            return 0;
        }

        protected void func_146119_b(Minecraft p_146119_1_, int p_146119_2_, int p_146119_3_)
        {
            if (this.field_146125_m)
            {
                if (this.field_146155_p)
                {
                    this.field_146156_o = (float)(p_146119_2_ - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8);

                    if (this.field_146156_o < 0.0F)
                    {
                        this.field_146156_o = 0.0F;
                    }

                    if (this.field_146156_o > 1.0F)
                    {
                        this.field_146156_o = 1.0F;
                    }

                    p_146119_1_.gameSettings.func_151439_a(this.field_146153_r, this.field_146156_o);
                    p_146119_1_.gameSettings.saveOptions();
                    this.field_146126_j = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.func_146504_a(this.field_146153_r);
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.drawTexturedModalRect(this.field_146128_h + (int)(this.field_146156_o * (float)(this.field_146120_f - 8)), this.field_146129_i, 0, 66, 4, 20);
                this.drawTexturedModalRect(this.field_146128_h + (int)(this.field_146156_o * (float)(this.field_146120_f - 8)) + 4, this.field_146129_i, 196, 66, 4, 20);
            }
        }

        public boolean func_146116_c(Minecraft p_146116_1_, int p_146116_2_, int p_146116_3_)
        {
            if (super.func_146116_c(p_146116_1_, p_146116_2_, p_146116_3_))
            {
                this.field_146156_o = (float)(p_146116_2_ - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8);

                if (this.field_146156_o < 0.0F)
                {
                    this.field_146156_o = 0.0F;
                }

                if (this.field_146156_o > 1.0F)
                {
                    this.field_146156_o = 1.0F;
                }

                p_146116_1_.gameSettings.func_151439_a(this.field_146153_r, this.field_146156_o);
                p_146116_1_.gameSettings.saveOptions();
                this.field_146126_j = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.func_146504_a(this.field_146153_r);
                this.field_146155_p = true;
                return true;
            }
            else
            {
                return false;
            }
        }

        public void func_146113_a(SoundHandler p_146113_1_) {}

        public void func_146118_a(int p_146118_1_, int p_146118_2_)
        {
            if (this.field_146155_p)
            {
                if (this.field_146153_r == SoundCategory.MASTER)
                {
                    float f = 1.0F;
                }
                else
                {
                    GuiScreenOptionsSounds.this.field_146506_g.func_151438_a(this.field_146153_r);
                }

                GuiScreenOptionsSounds.this.field_146297_k.func_147118_V().func_147682_a(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
            }

            this.field_146155_p = false;
        }
    }
}