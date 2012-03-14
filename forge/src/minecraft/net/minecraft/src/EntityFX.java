package net.minecraft.src;

public class EntityFX extends Entity
{
    private int particleTextureIndex;
    protected float particleTextureJitterX;
    protected float particleTextureJitterY;
    protected int particleAge = 0;
    protected int particleMaxAge = 0;
    protected float particleScale;
    protected float particleGravity;

    /** The red amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0. */
    protected float particleRed;

    /**
     * The green amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0.
     */
    protected float particleGreen;

    /**
     * The blue amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0.
     */
    protected float particleBlue;
    public static double interpPosX;
    public static double interpPosY;
    public static double interpPosZ;

    public EntityFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World);
        this.setSize(0.2F, 0.2F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(par2, par4, par6);
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.motionX = par8 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
        this.motionY = par10 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
        this.motionZ = par12 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
        float var14 = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
        float var15 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / (double)var15 * (double)var14 * 0.4000000059604645D;
        this.motionY = this.motionY / (double)var15 * (double)var14 * 0.4000000059604645D + 0.10000000149011612D;
        this.motionZ = this.motionZ / (double)var15 * (double)var14 * 0.4000000059604645D;
        this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
        this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
        this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.particleMaxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
        this.particleAge = 0;
    }

    public EntityFX multiplyVelocity(float par1)
    {
        this.motionX *= (double)par1;
        this.motionY = (this.motionY - 0.10000000149011612D) * (double)par1 + 0.10000000149011612D;
        this.motionZ *= (double)par1;
        return this;
    }

    public EntityFX func_405_d(float par1)
    {
        this.setSize(0.2F * par1, 0.2F * par1);
        this.particleScale *= par1;
        return this;
    }

    public void func_40097_b(float par1, float par2, float par3)
    {
        this.particleRed = par1;
        this.particleGreen = par2;
        this.particleBlue = par3;
    }

    public float func_40098_n()
    {
        return this.particleRed;
    }

    public float func_40101_o()
    {
        return this.particleGreen;
    }

    public float func_40102_p()
    {
        return this.particleBlue;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit() {}

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setEntityDead();
        }

        this.motionY -= 0.04D * (double)this.particleGravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float var8 = (float)(this.particleTextureIndex % 16) / 16.0F;
        float var9 = var8 + 0.0624375F;
        float var10 = (float)(this.particleTextureIndex / 16) / 16.0F;
        float var11 = var10 + 0.0624375F;
        float var12 = 0.1F * this.particleScale;
        float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)par2 - interpPosX);
        float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)par2 - interpPosY);
        float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par2 - interpPosZ);
        float var16 = 1.0F;
        par1Tessellator.setColorOpaque_F(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16);
        par1Tessellator.addVertexWithUV((double)(var13 - par3 * var12 - par6 * var12), (double)(var14 - par4 * var12), (double)(var15 - par5 * var12 - par7 * var12), (double)var9, (double)var11);
        par1Tessellator.addVertexWithUV((double)(var13 - par3 * var12 + par6 * var12), (double)(var14 + par4 * var12), (double)(var15 - par5 * var12 + par7 * var12), (double)var9, (double)var10);
        par1Tessellator.addVertexWithUV((double)(var13 + par3 * var12 + par6 * var12), (double)(var14 + par4 * var12), (double)(var15 + par5 * var12 + par7 * var12), (double)var8, (double)var10);
        par1Tessellator.addVertexWithUV((double)(var13 + par3 * var12 - par6 * var12), (double)(var14 - par4 * var12), (double)(var15 + par5 * var12 - par7 * var12), (double)var8, (double)var11);
    }

    public int getFXLayer()
    {
        return 0;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}

    /**
     * Public method to set private field particleTextureIndex.
     */
    public void setParticleTextureIndex(int par1)
    {
        this.particleTextureIndex = par1;
    }

    public int getParticleTextureIndex()
    {
        return this.particleTextureIndex;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }
}
