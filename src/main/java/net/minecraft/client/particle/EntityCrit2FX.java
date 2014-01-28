package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityCrit2FX extends EntityFX
{
    // JAVADOC FIELD $$ field_70557_a
    private Entity theEntity;
    private int currentLife;
    private int maximumLife;
    private String particleName;
    private static final String __OBFID = "CL_00000899";

    public EntityCrit2FX(World par1World, Entity par2Entity)
    {
        this(par1World, par2Entity, "crit");
    }

    public EntityCrit2FX(World par1World, Entity par2Entity, String par3Str)
    {
        super(par1World, par2Entity.posX, par2Entity.boundingBox.minY + (double)(par2Entity.height / 2.0F), par2Entity.posZ, par2Entity.motionX, par2Entity.motionY, par2Entity.motionZ);
        this.theEntity = par2Entity;
        this.maximumLife = 3;
        this.particleName = par3Str;
        this.onUpdate();
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {}

    // JAVADOC METHOD $$ func_70071_h_
    public void onUpdate()
    {
        for (int i = 0; i < 16; ++i)
        {
            double d0 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
            double d1 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
            double d2 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);

            if (d0 * d0 + d1 * d1 + d2 * d2 <= 1.0D)
            {
                double d3 = this.theEntity.posX + d0 * (double)this.theEntity.width / 4.0D;
                double d4 = this.theEntity.boundingBox.minY + (double)(this.theEntity.height / 2.0F) + d1 * (double)this.theEntity.height / 4.0D;
                double d5 = this.theEntity.posZ + d2 * (double)this.theEntity.width / 4.0D;
                this.worldObj.spawnParticle(this.particleName, d3, d4, d5, d0, d1 + 0.2D, d2);
            }
        }

        ++this.currentLife;

        if (this.currentLife >= this.maximumLife)
        {
            this.setDead();
        }
    }

    public int getFXLayer()
    {
        return 3;
    }
}