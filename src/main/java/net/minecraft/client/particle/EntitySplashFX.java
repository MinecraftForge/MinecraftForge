package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySplashFX extends EntityRainFX
{
    private static final String __OBFID = "CL_00000927";

    public EntitySplashFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World, par2, par4, par6);
        this.particleGravity = 0.04F;
        this.nextTextureIndexX();

        if (par10 == 0.0D && (par8 != 0.0D || par12 != 0.0D))
        {
            this.motionX = par8;
            this.motionY = par10 + 0.1D;
            this.motionZ = par12;
        }
    }
}