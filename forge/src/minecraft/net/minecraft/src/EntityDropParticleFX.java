package net.minecraft.src;

public class EntityDropParticleFX extends EntityFX
{
    /** the material type for dropped items/blocks */
    private Material materialType;

    /** The height of the current bob */
    private int bobTimer;

    public EntityDropParticleFX(World par1World, double par2, double par4, double par6, Material par8Material)
    {
        super(par1World, par2, par4, par6, 0.0D, 0.0D, 0.0D);
        this.motionX = this.motionY = this.motionZ = 0.0D;

        if (par8Material == Material.water)
        {
            this.particleRed = 0.0F;
            this.particleGreen = 0.0F;
            this.particleBlue = 1.0F;
        }
        else
        {
            this.particleRed = 1.0F;
            this.particleGreen = 0.0F;
            this.particleBlue = 0.0F;
        }

        this.setParticleTextureIndex(113);
        this.setSize(0.01F, 0.01F);
        this.particleGravity = 0.06F;
        this.materialType = par8Material;
        this.bobTimer = 40;
        this.particleMaxAge = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
        this.motionX = this.motionY = this.motionZ = 0.0D;
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }

    public int getEntityBrightnessForRender(float par1)
    {
        return this.materialType == Material.water ? super.getEntityBrightnessForRender(par1) : 257;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getEntityBrightness(float par1)
    {
        return this.materialType == Material.water ? super.getEntityBrightness(par1) : 1.0F;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.materialType == Material.water)
        {
            this.particleRed = 0.2F;
            this.particleGreen = 0.3F;
            this.particleBlue = 1.0F;
        }
        else
        {
            this.particleRed = 1.0F;
            this.particleGreen = 16.0F / (float)(40 - this.bobTimer + 16);
            this.particleBlue = 4.0F / (float)(40 - this.bobTimer + 8);
        }

        this.motionY -= (double)this.particleGravity;

        if (this.bobTimer-- > 0)
        {
            this.motionX *= 0.02D;
            this.motionY *= 0.02D;
            this.motionZ *= 0.02D;
            this.setParticleTextureIndex(113);
        }
        else
        {
            this.setParticleTextureIndex(112);
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.particleMaxAge-- <= 0)
        {
            this.setEntityDead();
        }

        if (this.onGround)
        {
            if (this.materialType == Material.water)
            {
                this.setEntityDead();
                this.worldObj.spawnParticle("splash", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            }
            else
            {
                this.setParticleTextureIndex(114);
            }

            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }

        Material var1 = this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));

        if (var1.isLiquid() || var1.isSolid())
        {
            double var2 = (double)((float)(MathHelper.floor_double(this.posY) + 1) - BlockFluid.getFluidHeightPercent(this.worldObj.getBlockMetadata(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))));

            if (this.posY < var2)
            {
                this.setEntityDead();
            }
        }
    }
}
