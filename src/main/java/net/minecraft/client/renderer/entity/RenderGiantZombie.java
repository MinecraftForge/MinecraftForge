package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderGiantZombie extends RenderLiving
{
    private static final ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
    // JAVADOC FIELD $$ field_77073_a
    private float scale;
    private static final String __OBFID = "CL_00000998";

    public RenderGiantZombie(ModelBase par1ModelBase, float par2, float par3)
    {
        super(par1ModelBase, par2 * par3);
        this.scale = par3;
    }

    // JAVADOC METHOD $$ func_77041_b
    protected void preRenderCallback(EntityGiantZombie par1EntityGiantZombie, float par2)
    {
        GL11.glScalef(this.scale, this.scale, this.scale);
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(EntityGiantZombie par1EntityGiantZombie)
    {
        return zombieTextures;
    }

    // JAVADOC METHOD $$ func_77041_b
    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2)
    {
        this.preRenderCallback((EntityGiantZombie)par1EntityLivingBase, par2);
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityGiantZombie)par1Entity);
    }
}