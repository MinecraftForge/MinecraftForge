package net.minecraft.src;

public class EntityDiggingFX extends EntityFX
{
    private Block blockInstance;

    public EntityDiggingFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, Block par14Block, int par15, int par16)
    {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.blockInstance = par14Block;
        this.setParticleTextureIndex(par14Block.getBlockTextureFromSideAndMetadata(0, par16));
        this.particleGravity = par14Block.blockParticleGravity;
        this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
        this.particleScale /= 2.0F;
    }

    public EntityDiggingFX func_4041_a(int par1, int par2, int par3)
    {
        if (this.blockInstance == Block.grass)
        {
            return this;
        }
        else
        {
            int var4 = this.blockInstance.colorMultiplier(this.worldObj, par1, par2, par3);
            this.particleRed *= (float)(var4 >> 16 & 255) / 255.0F;
            this.particleGreen *= (float)(var4 >> 8 & 255) / 255.0F;
            this.particleBlue *= (float)(var4 & 255) / 255.0F;
            return this;
        }
    }

    public int getFXLayer()
    {
        return 1;
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float var8 = ((float)(this.getParticleTextureIndex() % 16) + this.particleTextureJitterX / 4.0F) / 16.0F;
        float var9 = var8 + 0.015609375F;
        float var10 = ((float)(this.getParticleTextureIndex() / 16) + this.particleTextureJitterY / 4.0F) / 16.0F;
        float var11 = var10 + 0.015609375F;
        float var12 = 0.1F * this.particleScale;
        float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)par2 - interpPosX);
        float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)par2 - interpPosY);
        float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par2 - interpPosZ);
        float var16 = 1.0F;
        par1Tessellator.setColorOpaque_F(var16 * this.particleRed, var16 * this.particleGreen, var16 * this.particleBlue);
        par1Tessellator.addVertexWithUV((double)(var13 - par3 * var12 - par6 * var12), (double)(var14 - par4 * var12), (double)(var15 - par5 * var12 - par7 * var12), (double)var8, (double)var11);
        par1Tessellator.addVertexWithUV((double)(var13 - par3 * var12 + par6 * var12), (double)(var14 + par4 * var12), (double)(var15 - par5 * var12 + par7 * var12), (double)var8, (double)var10);
        par1Tessellator.addVertexWithUV((double)(var13 + par3 * var12 + par6 * var12), (double)(var14 + par4 * var12), (double)(var15 + par5 * var12 + par7 * var12), (double)var9, (double)var10);
        par1Tessellator.addVertexWithUV((double)(var13 + par3 * var12 - par6 * var12), (double)(var14 - par4 * var12), (double)(var15 + par5 * var12 - par7 * var12), (double)var9, (double)var11);
    }
}
