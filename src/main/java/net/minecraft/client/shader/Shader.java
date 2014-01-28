package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import javax.vecmath.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class Shader
{
    private final ShaderManager field_148051_c;
    public final Framebuffer field_148052_a;
    public final Framebuffer field_148050_b;
    private final List field_148048_d = Lists.newArrayList();
    private final List field_148049_e = Lists.newArrayList();
    private final List field_148046_f = Lists.newArrayList();
    private final List field_148047_g = Lists.newArrayList();
    private Matrix4f field_148053_h;
    private static final String __OBFID = "CL_00001042";

    public Shader(IResourceManager p_i45089_1_, String p_i45089_2_, Framebuffer p_i45089_3_, Framebuffer p_i45089_4_) throws JsonException
    {
        this.field_148051_c = new ShaderManager(p_i45089_1_, p_i45089_2_);
        this.field_148052_a = p_i45089_3_;
        this.field_148050_b = p_i45089_4_;
    }

    public void func_148044_b()
    {
        this.field_148051_c.func_147988_a();
    }

    public void func_148041_a(String p_148041_1_, Object p_148041_2_, int p_148041_3_, int p_148041_4_)
    {
        this.field_148049_e.add(this.field_148049_e.size(), p_148041_1_);
        this.field_148048_d.add(this.field_148048_d.size(), p_148041_2_);
        this.field_148046_f.add(this.field_148046_f.size(), Integer.valueOf(p_148041_3_));
        this.field_148047_g.add(this.field_148047_g.size(), Integer.valueOf(p_148041_4_));
    }

    private void func_148040_d()
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void func_148045_a(Matrix4f p_148045_1_)
    {
        this.field_148053_h = p_148045_1_;
    }

    public void func_148042_a(float p_148042_1_)
    {
        this.func_148040_d();
        this.field_148052_a.func_147609_e();
        float f1 = (float)this.field_148050_b.field_147622_a;
        float f2 = (float)this.field_148050_b.field_147620_b;
        GL11.glViewport(0, 0, (int)f1, (int)f2);
        this.field_148051_c.func_147992_a("DiffuseSampler", this.field_148052_a);

        for (int i = 0; i < this.field_148048_d.size(); ++i)
        {
            this.field_148051_c.func_147992_a((String)this.field_148049_e.get(i), this.field_148048_d.get(i));
            this.field_148051_c.func_147984_b("AuxSize" + i).func_148087_a((float)((Integer)this.field_148046_f.get(i)).intValue(), (float)((Integer)this.field_148047_g.get(i)).intValue());
        }

        this.field_148051_c.func_147984_b("ProjMat").func_148088_a(this.field_148053_h);
        this.field_148051_c.func_147984_b("InSize").func_148087_a((float)this.field_148052_a.field_147622_a, (float)this.field_148052_a.field_147620_b);
        this.field_148051_c.func_147984_b("OutSize").func_148087_a(f1, f2);
        this.field_148051_c.func_147984_b("Time").func_148090_a(p_148042_1_);
        Minecraft minecraft = Minecraft.getMinecraft();
        this.field_148051_c.func_147984_b("ScreenSize").func_148087_a((float)minecraft.displayWidth, (float)minecraft.displayHeight);
        this.field_148051_c.func_147995_c();
        this.field_148050_b.func_147614_f();
        this.field_148050_b.func_147610_a(false);
        GL11.glDepthMask(false);
        GL11.glColorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(-1);
        tessellator.addVertex(0.0D, (double)f2, 500.0D);
        tessellator.addVertex((double)f1, (double)f2, 500.0D);
        tessellator.addVertex((double)f1, 0.0D, 500.0D);
        tessellator.addVertex(0.0D, 0.0D, 500.0D);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glColorMask(true, true, true, true);
        this.field_148051_c.func_147993_b();
        this.field_148050_b.func_147609_e();
        this.field_148052_a.func_147606_d();
        Iterator iterator = this.field_148048_d.iterator();

        while (iterator.hasNext())
        {
            Object object = iterator.next();

            if (object instanceof Framebuffer)
            {
                ((Framebuffer)object).func_147606_d();
            }
        }
    }

    public ShaderManager func_148043_c()
    {
        return this.field_148051_c;
    }
}