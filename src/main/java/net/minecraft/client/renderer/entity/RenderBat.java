package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderBat extends RenderLiving
{
    private static final ResourceLocation batTextures = new ResourceLocation("textures/entity/bat.png");
    // JAVADOC FIELD $$ field_82446_a
    private int renderedBatSize;
    private static final String __OBFID = "CL_00000979";

    public RenderBat()
    {
        super(new ModelBat(), 0.25F);
        this.renderedBatSize = ((ModelBat)this.mainModel).getBatSize();
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(EntityBat par1EntityBat, double par2, double par4, double par6, float par8, float par9)
    {
        int i = ((ModelBat)this.mainModel).getBatSize();

        if (i != this.renderedBatSize)
        {
            this.renderedBatSize = i;
            this.mainModel = new ModelBat();
        }

        super.doRender((EntityLiving)par1EntityBat, par2, par4, par6, par8, par9);
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(EntityBat par1EntityBat)
    {
        return batTextures;
    }

    // JAVADOC METHOD $$ func_77041_b
    protected void preRenderCallback(EntityBat par1EntityBat, float par2)
    {
        GL11.glScalef(0.35F, 0.35F, 0.35F);
    }

    // JAVADOC METHOD $$ func_77039_a
    protected void renderLivingAt(EntityBat par1EntityBat, double par2, double par4, double par6)
    {
        super.renderLivingAt(par1EntityBat, par2, par4, par6);
    }

    protected void rotateCorpse(EntityBat par1EntityBat, float par2, float par3, float par4)
    {
        if (!par1EntityBat.getIsBatHanging())
        {
            GL11.glTranslatef(0.0F, MathHelper.cos(par2 * 0.3F) * 0.1F, 0.0F);
        }
        else
        {
            GL11.glTranslatef(0.0F, -0.1F, 0.0F);
        }

        super.rotateCorpse(par1EntityBat, par2, par3, par4);
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityBat)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    // JAVADOC METHOD $$ func_77041_b
    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2)
    {
        this.preRenderCallback((EntityBat)par1EntityLivingBase, par2);
    }

    protected void rotateCorpse(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4)
    {
        this.rotateCorpse((EntityBat)par1EntityLivingBase, par2, par3, par4);
    }

    // JAVADOC METHOD $$ func_77039_a
    protected void renderLivingAt(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6)
    {
        this.renderLivingAt((EntityBat)par1EntityLivingBase, par2, par4, par6);
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityBat)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityBat)par1Entity);
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityBat)par1Entity, par2, par4, par6, par8, par9);
    }
}