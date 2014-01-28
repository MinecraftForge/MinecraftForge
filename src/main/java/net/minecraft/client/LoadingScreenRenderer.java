package net.minecraft.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MinecraftError;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class LoadingScreenRenderer implements IProgressUpdate
{
    private String field_73727_a = "";
    // JAVADOC FIELD $$ field_73725_b
    private Minecraft mc;
    // JAVADOC FIELD $$ field_73726_c
    private String currentlyDisplayedText = "";
    private long field_73723_d = Minecraft.getSystemTime();
    private boolean field_73724_e;
    private ScaledResolution field_146587_f;
    private Framebuffer field_146588_g;
    private static final String __OBFID = "CL_00000655";

    public LoadingScreenRenderer(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
        this.field_146587_f = new ScaledResolution(par1Minecraft.gameSettings, par1Minecraft.displayWidth, par1Minecraft.displayHeight);
        this.field_146588_g = new Framebuffer(this.field_146587_f.getScaledWidth(), this.field_146587_f.getScaledHeight(), false);
        this.field_146588_g.func_147607_a(9728);
    }

    // JAVADOC METHOD $$ func_73721_b
    public void resetProgressAndMessage(String par1Str)
    {
        this.field_73724_e = false;
        this.func_73722_d(par1Str);
    }

    // JAVADOC METHOD $$ func_73720_a
    public void displayProgressMessage(String par1Str)
    {
        this.field_73724_e = true;
        this.func_73722_d(par1Str);
    }

    public void func_73722_d(String par1Str)
    {
        this.currentlyDisplayedText = par1Str;

        if (!this.mc.running)
        {
            if (!this.field_73724_e)
            {
                throw new MinecraftError();
            }
        }
        else
        {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();

            if (OpenGlHelper.func_148822_b())
            {
                int i = this.field_146587_f.getScaleFactor();
                GL11.glOrtho(0.0D, (double)(this.field_146587_f.getScaledWidth() * i), (double)(this.field_146587_f.getScaledHeight() * i), 0.0D, 100.0D, 300.0D);
            }
            else
            {
                ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
                GL11.glOrtho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
            }

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        }
    }

    // JAVADOC METHOD $$ func_73719_c
    public void resetProgresAndWorkingMessage(String par1Str)
    {
        if (!this.mc.running)
        {
            if (!this.field_73724_e)
            {
                throw new MinecraftError();
            }
        }
        else
        {
            this.field_73723_d = 0L;
            this.field_73727_a = par1Str;
            this.setLoadingProgress(-1);
            this.field_73723_d = 0L;
        }
    }

    // JAVADOC METHOD $$ func_73718_a
    public void setLoadingProgress(int par1)
    {
        if (!this.mc.running)
        {
            if (!this.field_73724_e)
            {
                throw new MinecraftError();
            }
        }
        else
        {
            long j = Minecraft.getSystemTime();

            if (j - this.field_73723_d >= 100L)
            {
                this.field_73723_d = j;
                ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
                int k = scaledresolution.getScaleFactor();
                int l = scaledresolution.getScaledWidth();
                int i1 = scaledresolution.getScaledHeight();

                if (OpenGlHelper.func_148822_b())
                {
                    this.field_146588_g.func_147614_f();
                }
                else
                {
                    GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
                }

                this.field_146588_g.func_147610_a(true);
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();

                if (OpenGlHelper.func_148822_b())
                {
                    GL11.glOrtho(0.0D, (double)l, (double)i1, 0.0D, 100.0D, 300.0D);
                }
                else
                {
                    GL11.glOrtho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
                }

                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GL11.glTranslatef(0.0F, 0.0F, -200.0F);

                if (!OpenGlHelper.func_148822_b())
                {
                    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                }

                Tessellator tessellator = Tessellator.instance;
                this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
                float f = 32.0F;
                tessellator.startDrawingQuads();
                tessellator.setColorOpaque_I(4210752);
                tessellator.addVertexWithUV(0.0D, (double)i1, 0.0D, 0.0D, (double)((float)i1 / f));
                tessellator.addVertexWithUV((double)l, (double)i1, 0.0D, (double)((float)l / f), (double)((float)i1 / f));
                tessellator.addVertexWithUV((double)l, 0.0D, 0.0D, (double)((float)l / f), 0.0D);
                tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
                tessellator.draw();

                if (par1 >= 0)
                {
                    byte b0 = 100;
                    byte b1 = 2;
                    int j1 = l / 2 - b0 / 2;
                    int k1 = i1 / 2 + 16;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    tessellator.setColorOpaque_I(8421504);
                    tessellator.addVertex((double)j1, (double)k1, 0.0D);
                    tessellator.addVertex((double)j1, (double)(k1 + b1), 0.0D);
                    tessellator.addVertex((double)(j1 + b0), (double)(k1 + b1), 0.0D);
                    tessellator.addVertex((double)(j1 + b0), (double)k1, 0.0D);
                    tessellator.setColorOpaque_I(8454016);
                    tessellator.addVertex((double)j1, (double)k1, 0.0D);
                    tessellator.addVertex((double)j1, (double)(k1 + b1), 0.0D);
                    tessellator.addVertex((double)(j1 + par1), (double)(k1 + b1), 0.0D);
                    tessellator.addVertex((double)(j1 + par1), (double)k1, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.func_148821_a(770, 771, 1, 0);
                this.mc.fontRenderer.drawStringWithShadow(this.currentlyDisplayedText, (l - this.mc.fontRenderer.getStringWidth(this.currentlyDisplayedText)) / 2, i1 / 2 - 4 - 16, 16777215);
                this.mc.fontRenderer.drawStringWithShadow(this.field_73727_a, (l - this.mc.fontRenderer.getStringWidth(this.field_73727_a)) / 2, i1 / 2 - 4 + 8, 16777215);
                this.field_146588_g.func_147609_e();

                if (OpenGlHelper.func_148822_b())
                {
                    this.field_146588_g.func_147615_c(l * k, i1 * k);
                }

                this.mc.func_147120_f();

                try
                {
                    Thread.yield();
                }
                catch (Exception exception)
                {
                    ;
                }
            }
        }
    }

    public void func_146586_a() {}
}