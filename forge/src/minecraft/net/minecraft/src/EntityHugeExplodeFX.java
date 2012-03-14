package net.minecraft.src;

public class EntityHugeExplodeFX extends EntityFX
{
    private int timeSinceStart = 0;

    /** the maximum time for the explosion */
    private int maximumTime = 0;

    public EntityHugeExplodeFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World, par2, par4, par6, 0.0D, 0.0D, 0.0D);
        this.maximumTime = 8;
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {}

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        for (int var1 = 0; var1 < 6; ++var1)
        {
            double var2 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
            double var4 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
            double var6 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
            this.worldObj.spawnParticle("largeexplode", var2, var4, var6, (double)((float)this.timeSinceStart / (float)this.maximumTime), 0.0D, 0.0D);
        }

        ++this.timeSinceStart;

        if (this.timeSinceStart == this.maximumTime)
        {
            this.setEntityDead();
        }
    }

    public int getFXLayer()
    {
        return 1;
    }
}
