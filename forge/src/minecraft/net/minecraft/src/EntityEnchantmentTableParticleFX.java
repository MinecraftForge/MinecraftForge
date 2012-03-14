package net.minecraft.src;

public class EntityEnchantmentTableParticleFX extends EntityFX
{
    private float field_40107_a;
    private double field_40109_aw;
    private double field_40108_ax;
    private double field_40106_ay;

    public EntityEnchantmentTableParticleFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.motionX = par8;
        this.motionY = par10;
        this.motionZ = par12;
        this.field_40109_aw = this.posX = par2;
        this.field_40108_ax = this.posY = par4;
        this.field_40106_ay = this.posZ = par6;
        float var14 = this.rand.nextFloat() * 0.6F + 0.4F;
        this.field_40107_a = this.particleScale = this.rand.nextFloat() * 0.5F + 0.2F;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F * var14;
        this.particleGreen *= 0.9F;
        this.particleRed *= 0.9F;
        this.particleMaxAge = (int)(Math.random() * 10.0D) + 30;
        this.noClip = true;
        this.setParticleTextureIndex((int)(Math.random() * 26.0D + 1.0D + 224.0D));
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }

    public int getEntityBrightnessForRender(float par1)
    {
        int var2 = super.getEntityBrightnessForRender(par1);
        float var3 = (float)this.particleAge / (float)this.particleMaxAge;
        var3 *= var3;
        var3 *= var3;
        int var4 = var2 & 255;
        int var5 = var2 >> 16 & 255;
        var5 += (int)(var3 * 15.0F * 16.0F);

        if (var5 > 240)
        {
            var5 = 240;
        }

        return var4 | var5 << 16;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getEntityBrightness(float par1)
    {
        float var2 = super.getEntityBrightness(par1);
        float var3 = (float)this.particleAge / (float)this.particleMaxAge;
        var3 *= var3;
        var3 *= var3;
        return var2 * (1.0F - var3) + var3;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        float var1 = (float)this.particleAge / (float)this.particleMaxAge;
        var1 = 1.0F - var1;
        float var2 = 1.0F - var1;
        var2 *= var2;
        var2 *= var2;
        this.posX = this.field_40109_aw + this.motionX * (double)var1;
        this.posY = this.field_40108_ax + this.motionY * (double)var1 - (double)(var2 * 1.2F);
        this.posZ = this.field_40106_ay + this.motionZ * (double)var1;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setEntityDead();
        }
    }
}
