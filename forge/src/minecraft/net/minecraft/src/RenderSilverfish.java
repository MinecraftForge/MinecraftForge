package net.minecraft.src;

public class RenderSilverfish extends RenderLiving
{
    public RenderSilverfish()
    {
        super(new ModelSilverfish(), 0.3F);
    }

    /**
     * Return the silverfish's maximum death rotation.
     */
    protected float getSilverfishDeathRotation(EntitySilverfish par1EntitySilverfish)
    {
        return 180.0F;
    }

    /**
     * Renders the silverfish.
     */
    public void renderSilverfish(EntitySilverfish par1EntitySilverfish, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRenderLiving(par1EntitySilverfish, par2, par4, par6, par8, par9);
    }

    /**
     * Disallows the silverfish to render the renderPassModel.
     */
    protected int shouldSilverfishRenderPass(EntitySilverfish par1EntitySilverfish, int par2, float par3)
    {
        return -1;
    }

    protected float getDeathMaxRotation(EntityLiving par1EntityLiving)
    {
        return this.getSilverfishDeathRotation((EntitySilverfish)par1EntityLiving);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
    {
        return this.shouldSilverfishRenderPass((EntitySilverfish)par1EntityLiving, par2, par3);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderSilverfish((EntitySilverfish)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderSilverfish((EntitySilverfish)par1Entity, par2, par4, par6, par8, par9);
    }
}
