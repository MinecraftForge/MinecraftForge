package net.minecraft.src;

public class EntitySplashFX extends EntityRainFX
{
    public EntitySplashFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World, par2, par4, par6);
        this.particleGravity = 0.04F;
        this.setParticleTextureIndex(this.getParticleTextureIndex() + 1);

        if (par10 == 0.0D && (par8 != 0.0D || par12 != 0.0D))
        {
            this.motionX = par8;
            this.motionY = par10 + 0.1D;
            this.motionZ = par12;
        }
    }
}
