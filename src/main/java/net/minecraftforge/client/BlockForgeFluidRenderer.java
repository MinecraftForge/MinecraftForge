package net.minecraftforge.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.ICustomFluidTop;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockForgeFluidRenderer
{
    public boolean renderFluid(IBlockAccess blockAccess, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn)
    {
        BlockFluidBase blockliquid = (BlockFluidBase)blockStateIn.getBlock();
        blockliquid.setBlockBoundsBasedOnState(blockAccess, blockPosIn);
        TextureAtlasSprite[] atextureatlassprite = new TextureAtlasSprite[]{blockliquid.getFluid().getStillIcon(), blockliquid.getFluid().getFlowingIcon()};
        int i = blockliquid.colorMultiplier(blockAccess, blockPosIn);
        float f = (float)(i >> 16 & 255) / 255.0F;
        float f1 = (float)(i >> 8 & 255) / 255.0F;
        float f2 = (float)(i & 255) / 255.0F;
        boolean flag = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.up(), EnumFacing.UP);
        boolean flag1 = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.down(), EnumFacing.DOWN);
        boolean[] aboolean = new boolean[] {blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.north(), EnumFacing.NORTH), blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.south(), EnumFacing.SOUTH), blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.west(), EnumFacing.WEST), blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.east(), EnumFacing.EAST)};

        if (!flag && !flag1 && !aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3])
        {
            return false;
        }
        else
        {
            boolean flag2 = false;
            float f3 = 0.5F;
            float f4 = 1.0F;
            float f5 = 0.8F;
            float f6 = 0.6F;
            Material material = blockliquid.getMaterial();
            float f7 = this.getFluidHeight(blockAccess, blockPosIn, material);
            float f8 = this.getFluidHeight(blockAccess, blockPosIn.south(), material);
            float f9 = this.getFluidHeight(blockAccess, blockPosIn.east().south(), material);
            float f10 = this.getFluidHeight(blockAccess, blockPosIn.east(), material);
            double d0 = (double)blockPosIn.getX();
            double d1 = (double)blockPosIn.getY();
            double d2 = (double)blockPosIn.getZ();
            float f11 = 0.001F;
            TextureAtlasSprite textureatlassprite;
            float f12;
            float f13;
            float f14;
            float f15;
            float f16;
            float f17;

            if (flag)
            {
                flag2 = true;
                textureatlassprite = atextureatlassprite[0];
                f12 = (float)BlockLiquid.getFlowDirection(blockAccess, blockPosIn, material);

                if (f12 > -999.0F)
                {
                    textureatlassprite = atextureatlassprite[1];
                }

                f7 -= f11;
                f8 -= f11;
                f9 -= f11;
                f10 -= f11;
                float f18;
                float f19;
                float f20;

                if (f12 < -999.0F)
                {
                    f13 = textureatlassprite.getInterpolatedU(0.0D);
                    f17 = textureatlassprite.getInterpolatedV(0.0D);
                    f14 = f13;
                    f18 = textureatlassprite.getInterpolatedV(16.0D);
                    f15 = textureatlassprite.getInterpolatedU(16.0D);
                    f19 = f18;
                    f16 = f15;
                    f20 = f17;
                }
                else
                {
                    float f21 = MathHelper.sin(f12) * 0.25F;
                    float f22 = MathHelper.cos(f12) * 0.25F;
                    float f23 = 8.0F;
                    f13 = textureatlassprite.getInterpolatedU((double)(8.0F + (-f22 - f21) * 16.0F));
                    f17 = textureatlassprite.getInterpolatedV((double)(8.0F + (-f22 + f21) * 16.0F));
                    f14 = textureatlassprite.getInterpolatedU((double)(8.0F + (-f22 + f21) * 16.0F));
                    f18 = textureatlassprite.getInterpolatedV((double)(8.0F + (f22 + f21) * 16.0F));
                    f15 = textureatlassprite.getInterpolatedU((double)(8.0F + (f22 + f21) * 16.0F));
                    f19 = textureatlassprite.getInterpolatedV((double)(8.0F + (f22 - f21) * 16.0F));
                    f16 = textureatlassprite.getInterpolatedU((double)(8.0F + (f22 - f21) * 16.0F));
                    f20 = textureatlassprite.getInterpolatedV((double)(8.0F + (-f22 - f21) * 16.0F));
                }

                worldRendererIn.setBrightness(blockliquid.getMixedBrightnessForBlock(blockAccess, blockPosIn));
                worldRendererIn.setColorOpaque_F(f4 * f, f4 * f1, f4 * f2);
                worldRendererIn.addVertexWithUV(d0 + 0.0D, d1 + (double)f7, d2 + 0.0D, (double)f13, (double)f17);
                worldRendererIn.addVertexWithUV(d0 + 0.0D, d1 + (double)f8, d2 + 1.0D, (double)f14, (double)f18);
                worldRendererIn.addVertexWithUV(d0 + 1.0D, d1 + (double)f9, d2 + 1.0D, (double)f15, (double)f19);
                worldRendererIn.addVertexWithUV(d0 + 1.0D, d1 + (double)f10, d2 + 0.0D, (double)f16, (double)f20);

                if (this.shouldTopBerendered(blockliquid, blockAccess, blockPosIn.up()))
                {
                    worldRendererIn.addVertexWithUV(d0 + 0.0D, d1 + (double)f7, d2 + 0.0D, (double)f13, (double)f17);
                    worldRendererIn.addVertexWithUV(d0 + 1.0D, d1 + (double)f10, d2 + 0.0D, (double)f16, (double)f20);
                    worldRendererIn.addVertexWithUV(d0 + 1.0D, d1 + (double)f9, d2 + 1.0D, (double)f15, (double)f19);
                    worldRendererIn.addVertexWithUV(d0 + 0.0D, d1 + (double)f8, d2 + 1.0D, (double)f14, (double)f18);
                }
            }

            if (flag1)
            {
                worldRendererIn.setBrightness(blockliquid.getMixedBrightnessForBlock(blockAccess, blockPosIn.down()));
                worldRendererIn.setColorOpaque_F(f3, f3, f3);
                f12 = atextureatlassprite[0].getMinU();
                f13 = atextureatlassprite[0].getMaxU();
                f14 = atextureatlassprite[0].getMinV();
                f15 = atextureatlassprite[0].getMaxV();
                worldRendererIn.addVertexWithUV(d0, d1, d2 + 1.0D, (double)f12, (double)f15);
                worldRendererIn.addVertexWithUV(d0, d1, d2, (double)f12, (double)f14);
                worldRendererIn.addVertexWithUV(d0 + 1.0D, d1, d2, (double)f13, (double)f14);
                worldRendererIn.addVertexWithUV(d0 + 1.0D, d1, d2 + 1.0D, (double)f13, (double)f15);
                flag2 = true;
            }

            for (int j = 0; j < 4; ++j)
            {
                int k = 0;
                int l = 0;

                if (j == 0)
                {
                    --l;
                }

                if (j == 1)
                {
                    ++l;
                }

                if (j == 2)
                {
                    --k;
                }

                if (j == 3)
                {
                    ++k;
                }

                BlockPos blockpos1 = blockPosIn.add(k, 0, l);
                textureatlassprite = atextureatlassprite[1];

                if (aboolean[j])
                {
                    double d3;
                    double d4;
                    double d5;
                    double d6;

                    if (j == 0)
                    {
                        f16 = f7;
                        f17 = f10;
                        d4 = d0;
                        d6 = d0 + 1.0D;
                        d5 = d2 + (double)f11;
                        d3 = d2 + (double)f11;
                    }
                    else if (j == 1)
                    {
                        f16 = f9;
                        f17 = f8;
                        d4 = d0 + 1.0D;
                        d6 = d0;
                        d5 = d2 + 1.0D - (double)f11;
                        d3 = d2 + 1.0D - (double)f11;
                    }
                    else if (j == 2)
                    {
                        f16 = f8;
                        f17 = f7;
                        d4 = d0 + (double)f11;
                        d6 = d0 + (double)f11;
                        d5 = d2 + 1.0D;
                        d3 = d2;
                    }
                    else
                    {
                        f16 = f10;
                        f17 = f9;
                        d4 = d0 + 1.0D - (double)f11;
                        d6 = d0 + 1.0D - (double)f11;
                        d5 = d2;
                        d3 = d2 + 1.0D;
                    }

                    flag2 = true;
                    float f24 = textureatlassprite.getInterpolatedU(0.0D);
                    float f25 = textureatlassprite.getInterpolatedU(8.0D);
                    float f26 = textureatlassprite.getInterpolatedV((double)((1.0F - f16) * 16.0F * 0.5F));
                    float f27 = textureatlassprite.getInterpolatedV((double)((1.0F - f17) * 16.0F * 0.5F));
                    float f28 = textureatlassprite.getInterpolatedV(8.0D);
                    worldRendererIn.setBrightness(blockliquid.getMixedBrightnessForBlock(blockAccess, blockpos1));
                    float f29 = 1.0F;
                    f29 *= j < 2 ? f5 : f6;
                    worldRendererIn.setColorOpaque_F(f4 * f29 * f, f4 * f29 * f1, f4 * f29 * f2);
                    worldRendererIn.addVertexWithUV(d4, d1 + (double)f16, d5, (double)f24, (double)f26);
                    worldRendererIn.addVertexWithUV(d6, d1 + (double)f17, d3, (double)f25, (double)f27);
                    worldRendererIn.addVertexWithUV(d6, d1 + 0.0D, d3, (double)f25, (double)f28);
                    worldRendererIn.addVertexWithUV(d4, d1 + 0.0D, d5, (double)f24, (double)f28);
                    worldRendererIn.addVertexWithUV(d4, d1 + 0.0D, d5, (double)f24, (double)f28);
                    worldRendererIn.addVertexWithUV(d6, d1 + 0.0D, d3, (double)f25, (double)f28);
                    worldRendererIn.addVertexWithUV(d6, d1 + (double)f17, d3, (double)f25, (double)f27);
                    worldRendererIn.addVertexWithUV(d4, d1 + (double)f16, d5, (double)f24, (double)f26);
                }
            }

            return flag2;
        }
    }

    private boolean shouldTopBerendered(BlockFluidBase base, IBlockAccess world, BlockPos xyz)
    {
    	if(base instanceof ICustomFluidTop)
    	{
    		return ((ICustomFluidTop) base).canRenderTop(world, xyz);
    	}
        for (int i = -1; i <= 1; ++i)
        {
            for (int j = -1; j <= 1; ++j)
            {
                IBlockState iblockstate = world.getBlockState(xyz.add(i, 0, j));
                Block block = iblockstate.getBlock();

                if (block != base && !block.isFullBlock())
                {
                    return true;
                }
            }
        }

        return false;
    }
    
    private float getFluidHeight(IBlockAccess blockAccess, BlockPos blockPosIn, Material blockMaterial)
    {
        int i = 0;
        float f = 0.0F;

        for (int j = 0; j < 4; ++j)
        {
            BlockPos blockpos1 = blockPosIn.add(-(j & 1), 0, -(j >> 1 & 1));

            if (blockAccess.getBlockState(blockpos1.up()).getBlock().getMaterial() == blockMaterial)
            {
                return 1.0F;
            }

            IBlockState iblockstate = blockAccess.getBlockState(blockpos1);
            Material material1 = iblockstate.getBlock().getMaterial();

            if (material1 == blockMaterial)
            {
                int k = ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue();

                if (k >= 8 || k == 0)
                {
                    f += BlockLiquid.getLiquidHeightPercent(k) * 10.0F;
                    i += 10;
                }

                f += BlockLiquid.getLiquidHeightPercent(k);
                ++i;
            }
            else if (!material1.isSolid())
            {
                ++f;
                ++i;
            }
        }

        return 1.0F - f / (float)i;
    }
}
