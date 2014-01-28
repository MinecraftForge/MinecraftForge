package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderSilverfish extends RenderLiving
{
    private static final ResourceLocation silverfishTextures = new ResourceLocation("textures/entity/silverfish.png");
    private static final String __OBFID = "CL_00001022";

    public RenderSilverfish()
    {
        super(new ModelSilverfish(), 0.3F);
    }

    protected float getDeathMaxRotation(EntitySilverfish par1EntitySilverfish)
    {
        return 180.0F;
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(EntitySilverfish par1EntitySilverfish, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRender((EntityLiving)par1EntitySilverfish, par2, par4, par6, par8, par9);
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(EntitySilverfish par1EntitySilverfish)
    {
        return silverfishTextures;
    }

    // JAVADOC METHOD $$ func_77032_a
    protected int shouldRenderPass(EntitySilverfish par1EntitySilverfish, int par2, float par3)
    {
        return -1;
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntitySilverfish)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    protected float getDeathMaxRotation(EntityLivingBase par1EntityLivingBase)
    {
        return this.getDeathMaxRotation((EntitySilverfish)par1EntityLivingBase);
    }

    // JAVADOC METHOD $$ func_77032_a
    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3)
    {
        return this.shouldRenderPass((EntitySilverfish)par1EntityLivingBase, par2, par3);
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(EntityLivingBase par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntitySilverfish)par1Entity, par2, par4, par6, par8, par9);
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntitySilverfish)par1Entity);
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntitySilverfish)par1Entity, par2, par4, par6, par8, par9);
    }
}