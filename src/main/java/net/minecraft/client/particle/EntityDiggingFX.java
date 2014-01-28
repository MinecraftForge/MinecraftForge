package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityDiggingFX extends EntityFX
{
    private Block field_145784_a;
    private static final String __OBFID = "CL_00000932";
    private int side;

    public EntityDiggingFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, Block par14Block, int par15)
    {
        this(par1World, par2, par4, par6, par8, par10, par12, par14Block, par15, par1World.rand.nextInt(6));
    }

    public EntityDiggingFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, Block par14Block, int par15, int side)
    {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.field_145784_a = par14Block;
        this.setParticleIcon(par14Block.func_149691_a(side, par15));
        this.particleGravity = par14Block.field_149763_I;
        this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
        this.particleScale /= 2.0F;
        this.side = side;
    }

    // JAVADOC METHOD $$ func_70596_a
    public EntityDiggingFX applyColourMultiplier(int par1, int par2, int par3)
    {
        if (this.field_145784_a == Blocks.grass && this.side != 1)
        {
            return this;
        }
        else
        {
            int l = this.field_145784_a.func_149720_d(this.worldObj, par1, par2, par3);
            this.particleRed *= (float)(l >> 16 & 255) / 255.0F;
            this.particleGreen *= (float)(l >> 8 & 255) / 255.0F;
            this.particleBlue *= (float)(l & 255) / 255.0F;
            return this;
        }
    }

    // JAVADOC METHOD $$ func_90019_g
    public EntityDiggingFX applyRenderColor(int par1)
    {
        if (this.field_145784_a == Blocks.grass)
        {
            return this;
        }
        else
        {
            int j = this.field_145784_a.func_149741_i(par1);
            this.particleRed *= (float)(j >> 16 & 255) / 255.0F;
            this.particleGreen *= (float)(j >> 8 & 255) / 255.0F;
            this.particleBlue *= (float)(j & 255) / 255.0F;
            return this;
        }
    }

    public int getFXLayer()
    {
        return 1;
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float f6 = ((float)this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
        float f7 = f6 + 0.015609375F;
        float f8 = ((float)this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
        float f9 = f8 + 0.015609375F;
        float f10 = 0.1F * this.particleScale;

        if (this.particleIcon != null)
        {
            f6 = this.particleIcon.getInterpolatedU((double)(this.particleTextureJitterX / 4.0F * 16.0F));
            f7 = this.particleIcon.getInterpolatedU((double)((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
            f8 = this.particleIcon.getInterpolatedV((double)(this.particleTextureJitterY / 4.0F * 16.0F));
            f9 = this.particleIcon.getInterpolatedV((double)((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
        }

        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)par2 - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)par2 - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par2 - interpPosZ);
        par1Tessellator.setColorOpaque_F(this.particleRed, this.particleGreen, this.particleBlue);
        par1Tessellator.addVertexWithUV((double)(f11 - par3 * f10 - par6 * f10), (double)(f12 - par4 * f10), (double)(f13 - par5 * f10 - par7 * f10), (double)f6, (double)f9);
        par1Tessellator.addVertexWithUV((double)(f11 - par3 * f10 + par6 * f10), (double)(f12 + par4 * f10), (double)(f13 - par5 * f10 + par7 * f10), (double)f6, (double)f8);
        par1Tessellator.addVertexWithUV((double)(f11 + par3 * f10 + par6 * f10), (double)(f12 + par4 * f10), (double)(f13 + par5 * f10 + par7 * f10), (double)f7, (double)f8);
        par1Tessellator.addVertexWithUV((double)(f11 + par3 * f10 - par6 * f10), (double)(f12 - par4 * f10), (double)(f13 + par5 * f10 - par7 * f10), (double)f7, (double)f9);
    }
}