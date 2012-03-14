package net.minecraft.src;

public class EntitySuspendFX extends EntityFX
{
    public EntitySuspendFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World, par2, par4 - 0.125D, par6, par8, par10, par12);
        this.particleRed = 0.4F;
        this.particleGreen = 0.4F;
        this.particleBlue = 0.7F;
        this.setParticleTextureIndex(0);
        this.setSize(0.01F, 0.01F);
        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
        this.motionX = par8 * 0.0D;
        this.motionY = par10 * 0.0D;
        this.motionZ = par12 * 0.0D;
        this.particleMaxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) != Material.water)
        {
            this.setEntityDead();
        }

        if (this.particleMaxAge-- <= 0)
        {
            this.setEntityDead();
        }
    }
}
