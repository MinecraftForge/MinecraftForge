
package net.minecraftforge.fluids;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

/**
 * Default renderer for Forge fluid blocks.
 * 
 * @author King Lemming
 * 
 */
public class RenderBlockFluid implements ISimpleBlockRenderingHandler
{
    public static RenderBlockFluid instance = new RenderBlockFluid();

    static final float LIGHT_Y_NEG = 0.5F;
    static final float LIGHT_Y_POS = 1.0F;
    static final float LIGHT_XZ_NEG = 0.8F;
    static final float LIGHT_XZ_POS = 0.6F;
    static final double RENDER_OFFSET = 0.0010000000474974513D;

    public float getFluidHeightAverage(float[] flow)
    {
        float total = 0;
        int count = 0;

        for (int i = 0; i < flow.length; i++)
        {
            if (flow[i] >= 0.875F)
            {
                return flow[i];
            }

            if (flow[i] >= 0)
            {
                total += flow[i];
                count++;
            }
        }
        return total / count;
    }

    public float getFluidHeightForRender(IBlockAccess world, int x, int y, int z, BlockFluidBase block)
    {
        if (world.getBlockId(x, y, z) == block.blockID)
        {
            if (world.getBlockId(x, y - block.densityDir, z) == block.blockID)
            {
                return 1;
            }

            if (world.getBlockMetadata(x, y, z) == block.getMaxRenderHeightMeta())
            {
                return 0.875F;
            }
        }
        return !world.getBlockMaterial(x, y, z).isSolid() && world.getBlockId(x, y - block.densityDir, z) == block.blockID ? 1 : block.getQuantaPercentage(world, x, y, z) * 0.875F;
    }

    /* ISimpleBlockRenderingHandler */
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer){}

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        if (!(block instanceof BlockFluidBase))
        {
            return false;
        }

        Tessellator tessellator = Tessellator.instance;
        int color = block.colorMultiplier(world, x, y, z);
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        BlockFluidBase theFluid = (BlockFluidBase) block;
        int bMeta = world.getBlockMetadata(x, y, z);

        boolean renderTop = world.getBlockId(x, y - theFluid.densityDir, z) != theFluid.blockID;

        boolean renderBottom = block.shouldSideBeRendered(world, x, y + theFluid.densityDir, z, 0) && world.getBlockId(x, y + theFluid.densityDir, z) != theFluid.blockID;

        boolean[] renderSides = new boolean[]
        {
            block.shouldSideBeRendered(world, x, y, z - 1, 2), 
            block.shouldSideBeRendered(world, x, y, z + 1, 3),
            block.shouldSideBeRendered(world, x - 1, y, z, 4), 
            block.shouldSideBeRendered(world, x + 1, y, z, 5)
        };

        if (!renderTop && !renderBottom && !renderSides[0] && !renderSides[1] && !renderSides[2] && !renderSides[3])
        {
            return false;
        }
        else
        {
            boolean rendered = false;
            double heightNW, heightSW, heightSE, heightNE;
            float flow11 = getFluidHeightForRender(world, x, y, z, theFluid);

            if (flow11 != 1)
            {
                float flow00 = getFluidHeightForRender(world, x - 1, y, z - 1, theFluid);
                float flow01 = getFluidHeightForRender(world, x - 1, y, z,     theFluid);
                float flow02 = getFluidHeightForRender(world, x - 1, y, z + 1, theFluid);
                float flow10 = getFluidHeightForRender(world, x,     y, z - 1, theFluid);
                float flow12 = getFluidHeightForRender(world, x,     y, z + 1, theFluid);
                float flow20 = getFluidHeightForRender(world, x + 1, y, z - 1, theFluid);
                float flow21 = getFluidHeightForRender(world, x + 1, y, z,     theFluid);
                float flow22 = getFluidHeightForRender(world, x + 1, y, z + 1, theFluid);

                heightNW = getFluidHeightAverage(new float[]{ flow00, flow01, flow10, flow11 });
                heightSW = getFluidHeightAverage(new float[]{ flow01, flow02, flow12, flow11 });
                heightSE = getFluidHeightAverage(new float[]{ flow12, flow21, flow22, flow11 });
                heightNE = getFluidHeightAverage(new float[]{ flow10, flow20, flow21, flow11 });
            }
            else
            {
                heightNW = flow11;
                heightSW = flow11;
                heightSE = flow11;
                heightNE = flow11;
            }

            boolean rises = theFluid.densityDir == 1;
            if (renderer.renderAllFaces || renderTop)
            {
                rendered = true;
                Icon iconStill = block.getIcon(1, bMeta);
                float flowDir = (float) BlockFluidBase.getFlowDirection(world, x, y, z);

                if (flowDir > -999.0F)
                {
                    iconStill = block.getIcon(2, bMeta);
                }

                heightNW -= RENDER_OFFSET;
                heightSW -= RENDER_OFFSET;
                heightSE -= RENDER_OFFSET;
                heightNE -= RENDER_OFFSET;

                double u1, u2, u3, u4, v1, v2, v3, v4;

                if (flowDir < -999.0F)
                {
                    u2 = iconStill.getInterpolatedU(0.0D);
                    v2 = iconStill.getInterpolatedV(0.0D);
                    u1 = u2;
                    v1 = iconStill.getInterpolatedV(16.0D);
                    u4 = iconStill.getInterpolatedU(16.0D);
                    v4 = v1;
                    u3 = u4;
                    v3 = v2;
                }
                else
                {
                    float xFlow = MathHelper.sin(flowDir) * 0.25F;
                    float zFlow = MathHelper.cos(flowDir) * 0.25F;
                    u2 = iconStill.getInterpolatedU(8.0F + (-zFlow - xFlow) * 16.0F);
                    v2 = iconStill.getInterpolatedV(8.0F + (-zFlow + xFlow) * 16.0F);
                    u1 = iconStill.getInterpolatedU(8.0F + (-zFlow + xFlow) * 16.0F);
                    v1 = iconStill.getInterpolatedV(8.0F + (zFlow + xFlow) * 16.0F);
                    u4 = iconStill.getInterpolatedU(8.0F + (zFlow + xFlow) * 16.0F);
                    v4 = iconStill.getInterpolatedV(8.0F + (zFlow - xFlow) * 16.0F);
                    u3 = iconStill.getInterpolatedU(8.0F + (zFlow - xFlow) * 16.0F);
                    v3 = iconStill.getInterpolatedV(8.0F + (-zFlow - xFlow) * 16.0F);
                }

                tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
                tessellator.setColorOpaque_F(LIGHT_Y_POS * red, LIGHT_Y_POS * green, LIGHT_Y_POS * blue);

                if (!rises)
                {
                    tessellator.addVertexWithUV(x + 0, y + heightNW, z + 0, u2, v2);
                    tessellator.addVertexWithUV(x + 0, y + heightSW, z + 1, u1, v1);
                    tessellator.addVertexWithUV(x + 1, y + heightSE, z + 1, u4, v4);
                    tessellator.addVertexWithUV(x + 1, y + heightNE, z + 0, u3, v3);
                }
                else
                {
                    tessellator.addVertexWithUV(x + 1, y + 1 - heightNE, z + 0, u3, v3);
                    tessellator.addVertexWithUV(x + 1, y + 1 - heightSE, z + 1, u4, v4);
                    tessellator.addVertexWithUV(x + 0, y + 1 - heightSW, z + 1, u1, v1);
                    tessellator.addVertexWithUV(x + 0, y + 1 - heightNW, z + 0, u2, v2);
                }
            }

            if (renderer.renderAllFaces || renderBottom)
            {
                rendered = true;
                tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y - 1, z));
                if (!rises)
                {
                    tessellator.setColorOpaque_F(LIGHT_Y_NEG, LIGHT_Y_NEG, LIGHT_Y_NEG);
                    renderer.renderFaceYNeg(block, x, y + RENDER_OFFSET, z, block.getIcon(0, bMeta));
                }
                else
                {
                    tessellator.setColorOpaque_F(LIGHT_Y_POS, LIGHT_Y_POS, LIGHT_Y_POS);
                    renderer.renderFaceYPos(block, x, y + RENDER_OFFSET, z, block.getIcon(1, bMeta));
                }
            }

            for (int side = 0; side < 4; ++side)
            {
                int x2 = x;
                int z2 = z;

                switch (side)
                {
                    case 0: --z2; break;
                    case 1: ++z2; break;
                    case 2: --x2; break;
                    case 3: ++x2; break;
                }

                Icon iconFlow = block.getIcon(side + 2, bMeta);
                if (renderer.renderAllFaces || renderSides[side])
                {
                    rendered = true;

                    double ty1;
                    double tx1;
                    double ty2;
                    double tx2;
                    double tz1;
                    double tz2;

                    if (side == 0)
                    {
                        ty1 = heightNW;
                        ty2 = heightNE;
                        tx1 = x;
                        tx2 = x + 1;
                        tz1 = z + RENDER_OFFSET;
                        tz2 = z + RENDER_OFFSET;
                    }
                    else if (side == 1)
                    {
                        ty1 = heightSE;
                        ty2 = heightSW;
                        tx1 = x + 1;
                        tx2 = x;
                        tz1 = z + 1 - RENDER_OFFSET;
                        tz2 = z + 1 - RENDER_OFFSET;
                    }
                    else if (side == 2)
                    {
                        ty1 = heightSW;
                        ty2 = heightNW;
                        tx1 = x + RENDER_OFFSET;
                        tx2 = x + RENDER_OFFSET;
                        tz1 = z + 1;
                        tz2 = z;
                    }
                    else
                    {
                        ty1 = heightNE;
                        ty2 = heightSE;
                        tx1 = x + 1 - RENDER_OFFSET;
                        tx2 = x + 1 - RENDER_OFFSET;
                        tz1 = z;
                        tz2 = z + 1;
                    }

                    float u1Flow = iconFlow.getInterpolatedU(0.0D);
                    float u2Flow = iconFlow.getInterpolatedU(8.0D);
                    float v1Flow = iconFlow.getInterpolatedV((1.0D - ty1) * 16.0D * 0.5D);
                    float v2Flow = iconFlow.getInterpolatedV((1.0D - ty2) * 16.0D * 0.5D);
                    float v3Flow = iconFlow.getInterpolatedV(8.0D);
                    tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x2, y, z2));
                    float sideLighting = 1.0F;

                    if (side < 2)
                    {
                        sideLighting = LIGHT_XZ_NEG;
                    }
                    else
                    {
                        sideLighting = LIGHT_XZ_POS;
                    }

                    tessellator.setColorOpaque_F(LIGHT_Y_POS * sideLighting * red, LIGHT_Y_POS * sideLighting * green, LIGHT_Y_POS * sideLighting * blue);

                    if (!rises)
                    {
                        tessellator.addVertexWithUV(tx1, y + ty1, tz1, u1Flow, v1Flow);
                        tessellator.addVertexWithUV(tx2, y + ty2, tz2, u2Flow, v2Flow);
                        tessellator.addVertexWithUV(tx2, y + 0, tz2, u2Flow, v3Flow);
                        tessellator.addVertexWithUV(tx1, y + 0, tz1, u1Flow, v3Flow);
                    }
                    else
                    {
                        tessellator.addVertexWithUV(tx1, y + 1 - 0, tz1, u1Flow, v3Flow);
                        tessellator.addVertexWithUV(tx2, y + 1 - 0, tz2, u2Flow, v3Flow);
                        tessellator.addVertexWithUV(tx2, y + 1 - ty2, tz2, u2Flow, v2Flow);
                        tessellator.addVertexWithUV(tx1, y + 1 - ty1, tz1, u1Flow, v1Flow);
                    }
                }
            }
            renderer.renderMinY = 0;
            renderer.renderMaxY = 1;
            return rendered;
        }
    }

    @Override
    public boolean shouldRender3DInInventory(){ return false; }
    @Override
    public int getRenderId()
    {
        return FluidRegistry.renderIdFluid;
    }
}
