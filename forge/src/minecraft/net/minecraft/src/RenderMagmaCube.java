package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderMagmaCube extends RenderLiving
{
    private int field_40276_c;

    public RenderMagmaCube()
    {
        super(new ModelMagmaCube(), 0.25F);
        this.field_40276_c = ((ModelMagmaCube)this.mainModel).func_40343_a();
    }

    public void renderMagmaCube(EntityMagmaCube par1EntityMagmaCube, double par2, double par4, double par6, float par8, float par9)
    {
        int var10 = ((ModelMagmaCube)this.mainModel).func_40343_a();

        if (var10 != this.field_40276_c)
        {
            this.field_40276_c = var10;
            this.mainModel = new ModelMagmaCube();
            System.out.println("new lava slime model");
        }

        super.doRenderLiving(par1EntityMagmaCube, par2, par4, par6, par8, par9);
    }

    protected void scaleMagmaCube(EntityMagmaCube par1EntityMagmaCube, float par2)
    {
        int var3 = par1EntityMagmaCube.getSlimeSize();
        float var4 = (par1EntityMagmaCube.field_767_b + (par1EntityMagmaCube.field_768_a - par1EntityMagmaCube.field_767_b) * par2) / ((float)var3 * 0.5F + 1.0F);
        float var5 = 1.0F / (var4 + 1.0F);
        float var6 = (float)var3;
        GL11.glScalef(var5 * var6, 1.0F / var5 * var6, var5 * var6);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLiving par1EntityLiving, float par2)
    {
        this.scaleMagmaCube((EntityMagmaCube)par1EntityLiving, par2);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderMagmaCube((EntityMagmaCube)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderMagmaCube((EntityMagmaCube)par1Entity, par2, par4, par6, par8, par9);
    }
}
