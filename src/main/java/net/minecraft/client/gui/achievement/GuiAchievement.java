package net.minecraft.client.gui.achievement;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class GuiAchievement extends Gui
{
    private static final ResourceLocation field_146261_a = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    private Minecraft field_146259_f;
    private int field_146260_g;
    private int field_146267_h;
    private String field_146268_i;
    private String field_146265_j;
    private Achievement field_146266_k;
    private long field_146263_l;
    private RenderItem field_146264_m;
    private boolean field_146262_n;
    private static final String __OBFID = "CL_00000721";

    public GuiAchievement(Minecraft par1Minecraft)
    {
        this.field_146259_f = par1Minecraft;
        this.field_146264_m = new RenderItem();
    }

    public void func_146256_a(Achievement p_146256_1_)
    {
        this.field_146268_i = I18n.getStringParams("achievement.get", new Object[0]);
        this.field_146265_j = p_146256_1_.func_150951_e().func_150260_c();
        this.field_146263_l = Minecraft.getSystemTime();
        this.field_146266_k = p_146256_1_;
        this.field_146262_n = false;
    }

    public void func_146255_b(Achievement p_146255_1_)
    {
        this.field_146268_i = p_146255_1_.func_150951_e().func_150260_c();
        this.field_146265_j = p_146255_1_.getDescription();
        this.field_146263_l = Minecraft.getSystemTime() + 2500L;
        this.field_146266_k = p_146255_1_;
        this.field_146262_n = true;
    }

    private void func_146258_c()
    {
        GL11.glViewport(0, 0, this.field_146259_f.displayWidth, this.field_146259_f.displayHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        this.field_146260_g = this.field_146259_f.displayWidth;
        this.field_146267_h = this.field_146259_f.displayHeight;
        ScaledResolution scaledresolution = new ScaledResolution(this.field_146259_f.gameSettings, this.field_146259_f.displayWidth, this.field_146259_f.displayHeight);
        this.field_146260_g = scaledresolution.getScaledWidth();
        this.field_146267_h = scaledresolution.getScaledHeight();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double)this.field_146260_g, (double)this.field_146267_h, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    public void func_146254_a()
    {
        if (this.field_146266_k != null && this.field_146263_l != 0L && Minecraft.getMinecraft().thePlayer != null)
        {
            double d0 = (double)(Minecraft.getSystemTime() - this.field_146263_l) / 3000.0D;

            if (!this.field_146262_n)
            {
                if (d0 < 0.0D || d0 > 1.0D)
                {
                    this.field_146263_l = 0L;
                    return;
                }
            }
            else if (d0 > 0.5D)
            {
                d0 = 0.5D;
            }

            this.func_146258_c();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            double d1 = d0 * 2.0D;

            if (d1 > 1.0D)
            {
                d1 = 2.0D - d1;
            }

            d1 *= 4.0D;
            d1 = 1.0D - d1;

            if (d1 < 0.0D)
            {
                d1 = 0.0D;
            }

            d1 *= d1;
            d1 *= d1;
            int i = this.field_146260_g - 160;
            int j = 0 - (int)(d1 * 36.0D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            this.field_146259_f.getTextureManager().bindTexture(field_146261_a);
            GL11.glDisable(GL11.GL_LIGHTING);
            this.drawTexturedModalRect(i, j, 96, 202, 160, 32);

            if (this.field_146262_n)
            {
                this.field_146259_f.fontRenderer.drawSplitString(this.field_146265_j, i + 30, j + 7, 120, -1);
            }
            else
            {
                this.field_146259_f.fontRenderer.drawString(this.field_146268_i, i + 30, j + 7, -256);
                this.field_146259_f.fontRenderer.drawString(this.field_146265_j, i + 30, j + 18, -1);
            }

            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            this.field_146264_m.renderItemAndEffectIntoGUI(this.field_146259_f.fontRenderer, this.field_146259_f.getTextureManager(), this.field_146266_k.theItemStack, i + 8, j + 8);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
    }

    public void func_146257_b()
    {
        this.field_146266_k = null;
        this.field_146263_l = 0L;
    }
}