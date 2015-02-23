package net.minecraftforge.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class FluidRenderer extends BlockFluidRenderer {

    public boolean renderFluid(IBlockAccess blockAccess, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn)
    {
        if (blockStateIn.getBlock() instanceof BlockLiquid)
        {
            return super.renderFluid(blockAccess, blockStateIn, blockPosIn, worldRendererIn);
        }
        else if (blockStateIn.getBlock() instanceof BlockFluidBase)
        {
            BlockFluidBase theFluid = (BlockFluidBase) blockStateIn.getBlock();
            theFluid.setBlockBoundsBasedOnState(blockAccess, blockPosIn);
            int i = theFluid.colorMultiplier(blockAccess, blockPosIn);
            float f = (float) (i >> 16 & 255) / 255.0F;
            float f1 = (float) (i >> 8 & 255) / 255.0F;
            float f2 = (float) (i & 255) / 255.0F;
            boolean renderUp = theFluid.shouldSideBeRendered(blockAccess, blockPosIn.up(), EnumFacing.UP);
            boolean renderDown = theFluid.shouldSideBeRendered(blockAccess, blockPosIn.down(), EnumFacing.DOWN);
            boolean[] renderSides = new boolean[] { theFluid.shouldSideBeRendered(blockAccess, blockPosIn.north(), EnumFacing.NORTH),
                    theFluid.shouldSideBeRendered(blockAccess, blockPosIn.south(), EnumFacing.SOUTH),
                    theFluid.shouldSideBeRendered(blockAccess, blockPosIn.west(), EnumFacing.WEST),
                    theFluid.shouldSideBeRendered(blockAccess, blockPosIn.east(), EnumFacing.EAST) };

            if (!renderUp && !renderDown && !renderSides[0] && !renderSides[1] && !renderSides[2] && !renderSides[3])
            {
                return false;
            }
            else
            {
                TextureAtlasSprite stillSprite = theFluid.getFluid().getStillIcon();
                TextureAtlasSprite flowSprite = theFluid.getFluid().getFlowingIcon();
                boolean isRendered = false;
                float f3 = 0.5F;
                float f4 = 1.0F;
                float f5 = 0.8F;
                float f6 = 0.6F;
                float f7 = this.getFluidHeightForRender(blockAccess, blockPosIn, theFluid);
                float f8 = this.getFluidHeightForRender(blockAccess, blockPosIn.south(), theFluid);
                float f9 = this.getFluidHeightForRender(blockAccess, blockPosIn.east().south(), theFluid);
                float f10 = this.getFluidHeightForRender(blockAccess, blockPosIn.east(), theFluid);
                double d0 = (double) blockPosIn.getX();
                double d1 = (double) blockPosIn.getY();
                double d2 = (double) blockPosIn.getZ();
                float f11 = 0.001F;
                TextureAtlasSprite spriteToRender;
                float f12;
                float f13;
                float f14;
                float f15;
                float f16;
                float f17;

                if (renderUp)
                {
                    isRendered = true;
                    spriteToRender = stillSprite;
                    f12 = (float) BlockFluidBase.getFlowDirection(blockAccess, blockPosIn);

                    if (f12 > -999.0F)
                    {
                        spriteToRender = flowSprite;
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
                        f13 = spriteToRender.getInterpolatedU(0.0D);
                        f17 = spriteToRender.getInterpolatedV(0.0D);
                        f14 = f13;
                        f18 = spriteToRender.getInterpolatedV(16.0D);
                        f15 = spriteToRender.getInterpolatedU(16.0D);
                        f19 = f18;
                        f16 = f15;
                        f20 = f17;
                    }
                    else
                    {
                        float f21 = MathHelper.sin(f12) * 0.25F;
                        float f22 = MathHelper.cos(f12) * 0.25F;
                        float f23 = 8.0F;
                        f13 = spriteToRender.getInterpolatedU((double) (8.0F + (-f22 - f21) * 16.0F));
                        f17 = spriteToRender.getInterpolatedV((double) (8.0F + (-f22 + f21) * 16.0F));
                        f14 = spriteToRender.getInterpolatedU((double) (8.0F + (-f22 + f21) * 16.0F));
                        f18 = spriteToRender.getInterpolatedV((double) (8.0F + (f22 + f21) * 16.0F));
                        f15 = spriteToRender.getInterpolatedU((double) (8.0F + (f22 + f21) * 16.0F));
                        f19 = spriteToRender.getInterpolatedV((double) (8.0F + (f22 - f21) * 16.0F));
                        f16 = spriteToRender.getInterpolatedU((double) (8.0F + (f22 - f21) * 16.0F));
                        f20 = spriteToRender.getInterpolatedV((double) (8.0F + (-f22 - f21) * 16.0F));
                    }

                    worldRendererIn.setBrightness(theFluid.getMixedBrightnessForBlock(blockAccess, blockPosIn));
                    worldRendererIn.setColorOpaque_F(f4 * f, f4 * f1, f4 * f2);
                    worldRendererIn.addVertexWithUV(d0 + 0.0D, d1 + (double) f7, d2 + 0.0D, (double) f13, (double) f17);
                    worldRendererIn.addVertexWithUV(d0 + 0.0D, d1 + (double) f8, d2 + 1.0D, (double) f14, (double) f18);
                    worldRendererIn.addVertexWithUV(d0 + 1.0D, d1 + (double) f9, d2 + 1.0D, (double) f15, (double) f19);
                    worldRendererIn.addVertexWithUV(d0 + 1.0D, d1 + (double) f10, d2 + 0.0D, (double) f16, (double) f20);

                    if (theFluid.isTranslucent(blockAccess, blockPosIn.up()))
                    {
                        worldRendererIn.addVertexWithUV(d0 + 0.0D, d1 + (double) f7, d2 + 0.0D, (double) f13, (double) f17);
                        worldRendererIn.addVertexWithUV(d0 + 1.0D, d1 + (double) f10, d2 + 0.0D, (double) f16, (double) f20);
                        worldRendererIn.addVertexWithUV(d0 + 1.0D, d1 + (double) f9, d2 + 1.0D, (double) f15, (double) f19);
                        worldRendererIn.addVertexWithUV(d0 + 0.0D, d1 + (double) f8, d2 + 1.0D, (double) f14, (double) f18);
                    }
                }

                if (renderDown)
                {
                    worldRendererIn.setBrightness(theFluid.getMixedBrightnessForBlock(blockAccess, blockPosIn.down()));
                    worldRendererIn.setColorOpaque_F(f3, f3, f3);
                    f12 = stillSprite.getMinU();
                    f13 = stillSprite.getMaxU();
                    f14 = stillSprite.getMinV();
                    f15 = stillSprite.getMaxV();
                    worldRendererIn.addVertexWithUV(d0, d1, d2 + 1.0D, (double) f12, (double) f15);
                    worldRendererIn.addVertexWithUV(d0, d1, d2, (double) f12, (double) f14);
                    worldRendererIn.addVertexWithUV(d0 + 1.0D, d1, d2, (double) f13, (double) f14);
                    worldRendererIn.addVertexWithUV(d0 + 1.0D, d1, d2 + 1.0D, (double) f13, (double) f15);
                    isRendered = true;
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
                    spriteToRender = flowSprite;

                    if (renderSides[j])
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
                            d5 = d2 + (double) f11;
                            d3 = d2 + (double) f11;
                        }
                        else if (j == 1)
                        {
                            f16 = f9;
                            f17 = f8;
                            d4 = d0 + 1.0D;
                            d6 = d0;
                            d5 = d2 + 1.0D - (double) f11;
                            d3 = d2 + 1.0D - (double) f11;
                        }
                        else if (j == 2)
                        {
                            f16 = f8;
                            f17 = f7;
                            d4 = d0 + (double) f11;
                            d6 = d0 + (double) f11;
                            d5 = d2 + 1.0D;
                            d3 = d2;
                        }
                        else
                        {
                            f16 = f10;
                            f17 = f9;
                            d4 = d0 + 1.0D - (double) f11;
                            d6 = d0 + 1.0D - (double) f11;
                            d5 = d2;
                            d3 = d2 + 1.0D;
                        }

                        isRendered = true;
                        float f24 = spriteToRender.getInterpolatedU(0.0D);
                        float f25 = spriteToRender.getInterpolatedU(8.0D);
                        float f26 = spriteToRender.getInterpolatedV((double) ((1.0F - f16) * 16.0F * 0.5F));
                        float f27 = spriteToRender.getInterpolatedV((double) ((1.0F - f17) * 16.0F * 0.5F));
                        float f28 = spriteToRender.getInterpolatedV(8.0D);
                        worldRendererIn.setBrightness(theFluid.getMixedBrightnessForBlock(blockAccess, blockpos1));
                        float f29 = 1.0F;
                        f29 *= j < 2 ? f5 : f6;
                        worldRendererIn.setColorOpaque_F(f4 * f29 * f, f4 * f29 * f1, f4 * f29 * f2);
                        worldRendererIn.addVertexWithUV(d4, d1 + (double) f16, d5, (double) f24, (double) f26);
                        worldRendererIn.addVertexWithUV(d6, d1 + (double) f17, d3, (double) f25, (double) f27);
                        worldRendererIn.addVertexWithUV(d6, d1 + 0.0D, d3, (double) f25, (double) f28);
                        worldRendererIn.addVertexWithUV(d4, d1 + 0.0D, d5, (double) f24, (double) f28);
                        worldRendererIn.addVertexWithUV(d4, d1 + 0.0D, d5, (double) f24, (double) f28);
                        worldRendererIn.addVertexWithUV(d6, d1 + 0.0D, d3, (double) f25, (double) f28);
                        worldRendererIn.addVertexWithUV(d6, d1 + (double) f17, d3, (double) f25, (double) f27);
                        worldRendererIn.addVertexWithUV(d4, d1 + (double) f16, d5, (double) f24, (double) f26);
                    }
                }

                return isRendered;
            }
        }
        else
        {
            return false;
        }
    }

    private float getFluidHeightForRender(IBlockAccess world, BlockPos pos, BlockFluidBase theFluid)
    {
        int i = 0;
        float percent = 0.0F;

        for (int j = 0; j < 4; ++j)
        {
            BlockPos blockpos1 = pos.add(-(j & 1), 0, -(j >> 1 & 1));

            if (world.getBlockState(blockpos1.up()).getBlock().getMaterial() == theFluid.getMaterial())
            {
                return 1.0F;
            }

            IBlockState iblockstate = world.getBlockState(blockpos1);
            Material material1 = iblockstate.getBlock().getMaterial();

            if (material1.isLiquid())
            {
                int k = ((Integer) iblockstate.getValue(BlockLiquid.LEVEL)).intValue();

                if (k >= 8 || k == 0)
                {
                    percent += BlockLiquid.getLiquidHeightPercent(k) * 10.0F;
                    i += 10;
                }

                percent += BlockLiquid.getLiquidHeightPercent(k);
                ++i;
            }
            else if (!material1.isSolid())
            {
                ++percent;
                ++i;
            }
        }

        return 1.0F - percent / (float) i;
    }
}
