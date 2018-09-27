/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.client.rendering;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.RebuildChunkEvent.RebuildChunkBlocksEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod.EventBusSubscriber
@Mod(modid = RenderChunkEventTest.MODID, name = "RenderChunkEventTest", version = "1.0", acceptableRemoteVersions = "*", clientSideOnly = true)
public class RenderChunkEventTest
{

    static final String MODID = "render_chunk_event_test";

    static enum RebuildOptions
    {
        VANILLA, VANILLA_MODDED, FRAGMENTED, FACING, MARCHING_CUBES;
    }

    static RebuildOptions[] rebuildOptions = new RebuildOptions[RebuildOptions.values().length];

    private static EnumFacing[] fragmentFacings = new EnumFacing[] {
            EnumFacing.DOWN,
            EnumFacing.UP
    };
    private static int fragmentRange;
    private static Random fragmentRandom = new Random();

    private static EnumFacing facingFacing = EnumFacing.UP;

    @SubscribeEvent
    public static void rebuildChunkSetup(final RebuildChunkBlocksEvent event)
    {
        rebuildOptions = new RebuildOptions[] { RebuildOptions.VANILLA };

        fragmentFacings = new EnumFacing[] {
                EnumFacing.DOWN,
                EnumFacing.UP,
                EnumFacing.NORTH,
                EnumFacing.SOUTH,
                EnumFacing.EAST,
                EnumFacing.WEST
        };
        fragmentRange = 15;
        // fragmentRandom = new Random();

        facingFacing = EnumFacing.UP;

    }

    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public static void cancelVanillaRebuildChunkIfNecessary(final RebuildChunkBlocksEvent event)
    // {
    // if (!Arrays.asList(rebuildOptions).contains(RebuildOptions.VANILLA))
    // {
    // event.setCanceled(true);
    // }
    // }

    @SubscribeEvent
    public static void rebuildChunkVanillaModded(final RebuildChunkBlocksEvent event)
    {
        if (!Arrays.asList(rebuildOptions).contains(RebuildOptions.VANILLA_MODDED))
        {
            return;
        }

        for (final BlockPos.MutableBlockPos blockpos$mutableblockpos : event.getChunkBlockPositions())
        {
            final IBlockState iblockstate = event.getWorldView().getBlockState(blockpos$mutableblockpos);
            final Block block = iblockstate.getBlock();

            for (final BlockRenderLayer blockRenderLayer : BlockRenderLayer.values())
            {
                if (!block.canRenderInLayer(iblockstate, blockRenderLayer))
                {
                    continue;
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(blockRenderLayer);

                if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE)
                {
                    final BufferBuilder bufferbuilder = event.startOrContinueLayer(blockRenderLayer);

                    final boolean used = event.getBlockRendererDispatcher().renderBlock(iblockstate, blockpos$mutableblockpos, event.getWorldView(), bufferbuilder);

                    event.setBlockRenderLayerUsedWithOrOpperation(blockRenderLayer, used);

                }
            }
            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
        }
    }

    @SubscribeEvent
    public static void rebuildChunkFragmented(final RebuildChunkBlocksEvent event)
    {
        if (!Arrays.asList(rebuildOptions).contains(RebuildOptions.FRAGMENTED))
        {
            return;
        }

        for (final BlockPos.MutableBlockPos blockpos$mutableblockpos : event.getChunkBlockPositions())
        {
            final IBlockState iblockstate = event.getWorldView().getBlockState(blockpos$mutableblockpos);
            final Block block = iblockstate.getBlock();

            for (final BlockRenderLayer blockRenderLayer : BlockRenderLayer.values())
            {
                if (!block.canRenderInLayer(iblockstate, blockRenderLayer))
                {
                    continue;
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(blockRenderLayer);

                if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE)
                {
                    final BufferBuilder bufferbuilder = event.startOrContinueLayer(blockRenderLayer);

                    BlockPos offsetPos = blockpos$mutableblockpos;

                    for (final EnumFacing facing : fragmentFacings)
                    {
                        offsetPos = offsetPos.offset(facing, fragmentRandom.nextInt(fragmentRange));
                    }

                    final boolean used = event.getBlockRendererDispatcher().renderBlock(iblockstate, offsetPos, event.getWorldView(), bufferbuilder);

                    event.setBlockRenderLayerUsedWithOrOpperation(blockRenderLayer, used);

                }
            }
            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
        }

    }

    @SubscribeEvent
    public static void rebuildChunkFacing(final RebuildChunkBlocksEvent event)
    {
        if (!Arrays.asList(rebuildOptions).contains(RebuildOptions.FACING))
        {
            return;
        }

        for (final BlockPos.MutableBlockPos blockpos$mutableblockpos : event.getChunkBlockPositions())
        {
            final IBlockState iblockstate = event.getWorldView().getBlockState(blockpos$mutableblockpos);
            final Block block = iblockstate.getBlock();

            for (final BlockRenderLayer blockRenderLayer : BlockRenderLayer.values())
            {
                if (!block.canRenderInLayer(iblockstate, blockRenderLayer))
                {
                    continue;
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(blockRenderLayer);

                if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE)
                {
                    final BufferBuilder bufferbuilder = event.startOrContinueLayer(blockRenderLayer);

                    final boolean used = renderBlockEnumFacing(iblockstate, blockpos$mutableblockpos, event.getWorldView(), bufferbuilder, event.getBlockRendererDispatcher(), facingFacing);

                    event.setBlockRenderLayerUsedWithOrOpperation(blockRenderLayer, used);

                }
            }
            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
        }
    }

    @SubscribeEvent
    public static void rebuildChunkMarchingCubes(final RebuildChunkBlocksEvent event)
    {
        if (!Arrays.asList(rebuildOptions).contains(RebuildOptions.MARCHING_CUBES))
        {
            return;
        }

        for (final BlockPos.MutableBlockPos blockpos$mutableblockpos : event.getChunkBlockPositions())
        {
            final IBlockState iblockstate = event.getWorldView().getBlockState(blockpos$mutableblockpos);
            final Block block = iblockstate.getBlock();

            for (final BlockRenderLayer blockRenderLayer : BlockRenderLayer.values())
            {
                if (!block.canRenderInLayer(iblockstate, blockRenderLayer))
                {
                    continue;
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(blockRenderLayer);

                if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE)
                {
                    final BufferBuilder bufferbuilder = event.startOrContinueLayer(blockRenderLayer);

                    final boolean used = event.getBlockRendererDispatcher().renderBlock(iblockstate, blockpos$mutableblockpos, event.getWorldView(), bufferbuilder);

                    event.setBlockRenderLayerUsedWithOrOpperation(blockRenderLayer, used);

                }
            }
            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
        }
    }

    public static boolean renderBlockEnumFacing(IBlockState state, final BlockPos pos, final IBlockAccess blockAccess, final BufferBuilder bufferBuilderIn, final BlockRendererDispatcher blockRendererDispatcher, final EnumFacing facing)
    {

        try
        {
            final EnumBlockRenderType enumblockrendertype = state.getRenderType();

            if (enumblockrendertype == EnumBlockRenderType.INVISIBLE)
            {
                return false;
            }
            else
            {
                if (blockAccess.getWorldType() != WorldType.DEBUG_ALL_BLOCK_STATES)
                {
                    try
                    {
                        state = state.getActualState(blockAccess, pos);
                    }
                    catch (final Exception var8)
                    {
                        ;
                    }
                }

                switch (enumblockrendertype)
                {
                case MODEL:

                    final IBakedModel model = blockRendererDispatcher.getModelForState(state);

                    return renderCube(bufferBuilderIn, model, blockAccess, pos, state);

                // return blockRendererDispatcher.getBlockModelRenderer().renderModel(blockAccess, model, state, pos, bufferBuilderIn, true);

                case ENTITYBLOCK_ANIMATED:
                    return false;
                case LIQUID:
                    return blockRendererDispatcher.renderBlock(state, pos, blockAccess, bufferBuilderIn);
                default:
                    return false;
                }
            }
        }
        catch (final Throwable throwable)
        {
            if (false)
            {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
                CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
                throw new ReportedException(crashreport);
            }
            return false;
        }
    }

    public static boolean renderModel(final IBlockAccess blockAccessIn, final IBakedModel modelIn, final IBlockState blockStateIn, final BlockPos blockPosIn, final BufferBuilder buffer, final boolean checkSides, final EnumFacing side)
    {
        return renderModel(blockAccessIn, modelIn, blockStateIn, blockPosIn, buffer, checkSides, side, MathHelper.getPositionRandom(blockPosIn));
    }

    public static boolean renderModel(final IBlockAccess worldIn, final IBakedModel modelIn, final IBlockState stateIn, final BlockPos posIn, final BufferBuilder buffer, final boolean checkSides, final EnumFacing side, final long rand)
    {
        if (checkSides && !stateIn.shouldSideBeRendered(worldIn, posIn, side))
        {
            return false;
        }

        final List<BakedQuad> quads = modelIn.getQuads(stateIn, side, MathHelper.getPositionRandom(posIn));
        if (quads.size() <= 0)
        {
            return false;
        }
        final BakedQuad quad = quads.get(0);
        if (quad == null)
        {
            return false;
        }
        final TextureAtlasSprite sprite = quad.getSprite();
        if (sprite == null)
        {
            return false;
        }

    }

    private static boolean renderCube(final BufferBuilder buffer, final IBakedModel model, final IBlockAccess blockAccessIn, final BlockPos posIn, final IBlockState stateIn)
    {

        state.shouldSideBeRendered(world, pos, side))

         final List<BakedQuad> quads = model.getQuads(stateIn, EnumFacing.UP, MathHelper.getPositionRandom(posIn));
         if (quads.size() <= 0)
         {
         return;
         }
         final BakedQuad quad = quads.get(0);
         if (quad == null)
         {
         return;
         }
         final TextureAtlasSprite sprite = quad.getSprite();
         if (sprite == null)
         {
         return;
         }

        final TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/lava_flow");

        final double posX = posIn.getX();
        final double posY = posIn.getY();
        final double posZ = posIn.getZ();

        // final Material material = stateIn.getMaterial();

        final Material material = Material.LAVA;

        float fluidHeight_________ = getFluidHeight(blockAccessIn, posIn, material);
        float fluidHeightSouth____ = getFluidHeight(blockAccessIn, posIn.south(), material);
        float fluidHeightEastSouth = getFluidHeight(blockAccessIn, posIn.east().south(), material);
        float fluidHeightEast_____ = getFluidHeight(blockAccessIn, posIn.east(), material);

        // final float slopeAngle = BlockLiquid.getSlopeAngle(blockAccessIn, posIn, material, stateIn);

        final float slopeAngle = -1000;

        // final TextureAtlasSprite textureatlassprite = slopeAngle > -999.0F ? atextureatlassprite[1] : atextureatlassprite[0];

        final TextureAtlasSprite textureatlassprite = sprite;

        fluidHeight_________ -= 0.001F;
        fluidHeightSouth____ -= 0.001F;
        fluidHeightEastSouth -= 0.001F;
        fluidHeightEast_____ -= 0.001F;

        final float minInterpolatedU = textureatlassprite.getInterpolatedU(0.0D);
        final float minInterpolatedV = textureatlassprite.getInterpolatedV(0.0D);

        final float maxInterpolatedV = textureatlassprite.getInterpolatedV(16.0D);
        final float maxInterpolatedU = textureatlassprite.getInterpolatedU(16.0D);

        // final int packedLightmapCoords = stateIn.getPackedLightmapCoords(blockAccessIn, posIn);
        // final int packedLightmapCoordsShifted16And65535 = (packedLightmapCoords >> 16) & 65535;
        // final int packedLightmapCoordsAnd65535_________ = packedLightmapCoords & 65535;

        final int packedLightmapCoordsShifted16And65535 = 240;
        final int packedLightmapCoordsAnd65535_________ = 0;

        final int red = 0xFF;
        final int green = 0xFF;
        final int blue = 0xFF;
        final int alpha = 0xFF;

        buffer.pos(posX + 0, posY + 1, posZ + 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 0).lightmap(240, 0).endVertex();
        buffer.pos(posX + 0, posY + 1, posZ + 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 1).lightmap(240, 0).endVertex();
        buffer.pos(posX + 1, posY + 1, posZ + 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 1).lightmap(240, 0).endVertex();
        buffer.pos(posX + 1, posY + 1, posZ + 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 0).lightmap(240, 0).endVertex();

        final BufferBuilder bufferbuilder = buffer;
        final double x_size = 1 / 2d;
        final double y_size = 1 / 2d;
        final double z_size = 1 / 2d;
        final double x = posX + x_size;
        final double y = posY + y_size;
        final double z = posZ + z_size;
        final double minU = textureatlassprite.getMinU();
        final double maxU = textureatlassprite.getMaxU();
        final double minV = textureatlassprite.getMinV();
        final double maxV = textureatlassprite.getMaxV();
        final int lightmap1 = 240;
        final int lightmap2 = 0;

        // UP
        bufferbuilder.pos(-x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(-x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();

        // DOWN
        bufferbuilder.pos(-x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(-x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();

        // LEFT
        bufferbuilder.pos(x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();

        // RIGHT
        bufferbuilder.pos(-x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(-x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(-x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(-x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();

        // BACK
        bufferbuilder.pos(-x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(-x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();

        // FRONT
        bufferbuilder.pos(x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(-x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
        bufferbuilder.pos(-x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();

        // buffer.pos(posX + 0.0D, posY + fluidHeight_________, posZ + 0.0D).color(red, green, blue, alpha).tex(minInterpolatedU,
        // minInterpolatedV).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
        // buffer.pos(posX + 0.0D, posY + fluidHeightSouth____, posZ + 1.0D).color(red, green, blue, alpha).tex(minInterpolatedU,
        // maxInterpolatedV).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
        // buffer.pos(posX + 1.0D, posY + fluidHeightEastSouth, posZ + 1.0D).color(red, green, blue, alpha).tex(maxInterpolatedU,
        // maxInterpolatedV).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
        // buffer.pos(posX + 1.0D, posY + fluidHeightEast_____, posZ + 0.0D).color(red, green, blue, alpha).tex(maxInterpolatedU,
        // minInterpolatedV).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
        //
        // buffer.pos(posX + 0.0D, posY + fluidHeight_________, posZ + 0.0D).color(red, green, blue, alpha).tex(minInterpolatedU,
        // minInterpolatedV).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
        // buffer.pos(posX + 1.0D, posY + fluidHeightEast_____, posZ + 0.0D).color(red, green, blue, alpha).tex(maxInterpolatedU,
        // minInterpolatedV).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
        // buffer.pos(posX + 1.0D, posY + fluidHeightEastSouth, posZ + 1.0D).color(red, green, blue, alpha).tex(maxInterpolatedU,
        // maxInterpolatedV).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
        // buffer.pos(posX + 0.0D, posY + fluidHeightSouth____, posZ + 1.0D).color(red, green, blue, alpha).tex(minInterpolatedU,
        // maxInterpolatedV).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();

    }

    // debug quad
    // VertexBuffer.pos(0, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 0).lightmap(240, 0).endVertex();
    // VertexBuffer.pos(0, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 1).lightmap(240, 0).endVertex();
    // VertexBuffer.pos(1, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 1).lightmap(240, 0).endVertex();
    // VertexBuffer.pos(1, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 0).lightmap(240, 0).endVertex();

    public boolean renderFluid(final IBlockAccess blockAccess, final IBlockState blockStateIn, final BlockPos blockPosIn, final BufferBuilder bufferBuilderIn)
    {

        final BlockFluidRenderer fluidRenderer;
        final BlockColors blockColors;

        try
        {

            fluidRenderer = ReflectionHelper.getPrivateValue(BlockRendererDispatcher.class, Minecraft.getMinecraft().getBlockRendererDispatcher(), "fluidRenderer", null);

            blockColors = ReflectionHelper.getPrivateValue(BlockFluidRenderer.class, fluidRenderer, "blockColors", null);

        }
        catch (final Throwable t)
        {
            return false;
        }

        final TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        final TextureAtlasSprite[] atlasSpritesLava = new TextureAtlasSprite[2];
        atlasSpritesLava[0] = texturemap.getAtlasSprite("minecraft:blocks/lava_still");
        atlasSpritesLava[1] = texturemap.getAtlasSprite("minecraft:blocks/lava_flow");
        final TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];
        atlasSpritesWater[0] = texturemap.getAtlasSprite("minecraft:blocks/water_still");
        atlasSpritesWater[1] = texturemap.getAtlasSprite("minecraft:blocks/water_flow");
        final TextureAtlasSprite atlasSpriteWaterOverlay = texturemap.getAtlasSprite("minecraft:blocks/water_overlay");

        final BlockLiquid blockliquid = (BlockLiquid) blockStateIn.getBlock();
        final boolean isLava = blockStateIn.getMaterial() == Material.LAVA;
        final TextureAtlasSprite[] atextureatlassprite = isLava ? atlasSpritesLava : atlasSpritesWater;
        final int colorMultiplier = blockColors.colorMultiplier(blockStateIn, blockAccess, blockPosIn, 0);
        final float colorMultiplierRed = ((colorMultiplier >> 16) & 255) / 255.0F;
        final float colorMultiplierGreen = ((colorMultiplier >> 8) & 255) / 255.0F;
        final float colorMultiplierBlue = (colorMultiplier & 255) / 255.0F;
        final boolean shouldRenderTop = blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.UP);
        final boolean shouldRenderBottom = blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.DOWN);
        final boolean[] aboolean = new boolean[] { blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.NORTH), blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.SOUTH), blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.WEST), blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.EAST) };

        if (!shouldRenderTop && !shouldRenderBottom && !aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3])
        {
            return false;
        }
        else
        {
            boolean didRender = false;
            final Material material = blockStateIn.getMaterial();
            float fluidHeight_________ = this.getFluidHeight(blockAccess, blockPosIn, material);
            float fluidHeightSouth____ = this.getFluidHeight(blockAccess, blockPosIn.south(), material);
            float fluidHeightEastSouth = this.getFluidHeight(blockAccess, blockPosIn.east().south(), material);
            float fluidHeightEast_____ = this.getFluidHeight(blockAccess, blockPosIn.east(), material);
            final double posX = blockPosIn.getX();
            final double posY = blockPosIn.getY();
            final double posZ = blockPosIn.getZ();

            if (shouldRenderTop)
            {
                didRender = true;
                final float slopeAngle = BlockLiquid.getSlopeAngle(blockAccess, blockPosIn, material, blockStateIn);
                final TextureAtlasSprite textureatlassprite = slopeAngle > -999.0F ? atextureatlassprite[1] : atextureatlassprite[0];
                fluidHeight_________ -= 0.001F;
                fluidHeightSouth____ -= 0.001F;
                fluidHeightEastSouth -= 0.001F;
                fluidHeightEast_____ -= 0.001F;
                float minInterpolatedU;
                float f14_____________;
                float maxInterpolatedU;
                float f16_____________;
                float minInterpolatedV;
                float maxInterpolatedV;
                float f19_____________;
                float f20_____________;

                if (slopeAngle < -999.0F)
                {
                    minInterpolatedU = textureatlassprite.getInterpolatedU(0.0D);
                    minInterpolatedV = textureatlassprite.getInterpolatedV(0.0D);
                    f14_____________ = minInterpolatedU;
                    maxInterpolatedV = textureatlassprite.getInterpolatedV(16.0D);
                    maxInterpolatedU = textureatlassprite.getInterpolatedU(16.0D);
                    f19_____________ = maxInterpolatedV;
                    f16_____________ = maxInterpolatedU;
                    f20_____________ = minInterpolatedV;
                }
                else
                {
                    final float f21 = MathHelper.sin(slopeAngle) * 0.25F;
                    final float f22 = MathHelper.cos(slopeAngle) * 0.25F;
                    final float f23 = 8.0F;
                    minInterpolatedU = textureatlassprite.getInterpolatedU(8.0F + ((-f22 - f21) * 16.0F));
                    minInterpolatedV = textureatlassprite.getInterpolatedV(8.0F + ((-f22 + f21) * 16.0F));
                    f14_____________ = textureatlassprite.getInterpolatedU(8.0F + ((-f22 + f21) * 16.0F));
                    maxInterpolatedV = textureatlassprite.getInterpolatedV(8.0F + ((f22 + f21) * 16.0F));
                    maxInterpolatedU = textureatlassprite.getInterpolatedU(8.0F + ((f22 + f21) * 16.0F));
                    f19_____________ = textureatlassprite.getInterpolatedV(8.0F + ((f22 - f21) * 16.0F));
                    f16_____________ = textureatlassprite.getInterpolatedU(8.0F + ((f22 - f21) * 16.0F));
                    f20_____________ = textureatlassprite.getInterpolatedV(8.0F + ((-f22 - f21) * 16.0F));
                }

                final int packedLightmapCoords = blockStateIn.getPackedLightmapCoords(blockAccess, blockPosIn);
                final int packedLightmapCoordsShifted16And65535 = (packedLightmapCoords >> 16) & 65535;
                final int packedLightmapCoordsAnd65535_________ = packedLightmapCoords & 65535;
                final float red = 1.0F * colorMultiplierRed;
                final float green = 1.0F * colorMultiplierGreen;
                final float blue = 1.0F * colorMultiplierBlue;
                bufferBuilderIn.pos(posX + 0.0D, posY + fluidHeight_________, posZ + 0.0D).color(red, green, blue, 1.0F).tex(minInterpolatedU, minInterpolatedV).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
                bufferBuilderIn.pos(posX + 0.0D, posY + fluidHeightSouth____, posZ + 1.0D).color(red, green, blue, 1.0F).tex(f14_____________, maxInterpolatedV).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
                bufferBuilderIn.pos(posX + 1.0D, posY + fluidHeightEastSouth, posZ + 1.0D).color(red, green, blue, 1.0F).tex(maxInterpolatedU, f19_____________).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
                bufferBuilderIn.pos(posX + 1.0D, posY + fluidHeightEast_____, posZ + 0.0D).color(red, green, blue, 1.0F).tex(f16_____________, f20_____________).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();

                if (blockliquid.shouldRenderSides(blockAccess, blockPosIn.up()))
                {
                    bufferBuilderIn.pos(posX + 0.0D, posY + fluidHeight_________, posZ + 0.0D).color(red, green, blue, 1.0F).tex(minInterpolatedU, minInterpolatedV).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
                    bufferBuilderIn.pos(posX + 1.0D, posY + fluidHeightEast_____, posZ + 0.0D).color(red, green, blue, 1.0F).tex(f16_____________, f20_____________).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
                    bufferBuilderIn.pos(posX + 1.0D, posY + fluidHeightEastSouth, posZ + 1.0D).color(red, green, blue, 1.0F).tex(maxInterpolatedU, f19_____________).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
                    bufferBuilderIn.pos(posX + 0.0D, posY + fluidHeightSouth____, posZ + 1.0D).color(red, green, blue, 1.0F).tex(f14_____________, maxInterpolatedV).lightmap(packedLightmapCoordsShifted16And65535, packedLightmapCoordsAnd65535_________).endVertex();
                }
            }

            if (shouldRenderBottom)
            {
                final float minU = atextureatlassprite[0].getMinU();
                final float maxU = atextureatlassprite[0].getMaxU();
                final float minV = atextureatlassprite[0].getMinV();
                final float maxV = atextureatlassprite[0].getMaxV();
                final int packedLightmapCoordsDown = blockStateIn.getPackedLightmapCoords(blockAccess, blockPosIn.down());
                final int packedLightmapCoordsDownShifted16And65535 = (packedLightmapCoordsDown >> 16) & 65535;
                final int packedLightmapCoordsDownAnd65535_________ = packedLightmapCoordsDown & 65535;
                bufferBuilderIn.pos(posX, posY, posZ + 1.0D).color(0.5F, 0.5F, 0.5F, 1.0F).tex(minU, maxV).lightmap(packedLightmapCoordsDownShifted16And65535, packedLightmapCoordsDownAnd65535_________).endVertex();
                bufferBuilderIn.pos(posX, posY, posZ).color(0.5F, 0.5F, 0.5F, 1.0F).tex(minU, minV).lightmap(packedLightmapCoordsDownShifted16And65535, packedLightmapCoordsDownAnd65535_________).endVertex();
                bufferBuilderIn.pos(posX + 1.0D, posY, posZ).color(0.5F, 0.5F, 0.5F, 1.0F).tex(maxU, minV).lightmap(packedLightmapCoordsDownShifted16And65535, packedLightmapCoordsDownAnd65535_________).endVertex();
                bufferBuilderIn.pos(posX + 1.0D, posY, posZ + 1.0D).color(0.5F, 0.5F, 0.5F, 1.0F).tex(maxU, maxV).lightmap(packedLightmapCoordsDownShifted16And65535, packedLightmapCoordsDownAnd65535_________).endVertex();
                didRender = true;
            }

            for (int i1 = 0; i1 < 4; ++i1)
            {
                int offsetX = 0;
                int offsetY = 0;

                if (i1 == 0)
                {
                    --offsetY;
                }

                if (i1 == 1)
                {
                    ++offsetY;
                }

                if (i1 == 2)
                {
                    --offsetX;
                }

                if (i1 == 3)
                {
                    ++offsetX;
                }

                final BlockPos blockpos = blockPosIn.add(offsetX, 0, offsetY);
                TextureAtlasSprite textureatlassprite1 = atextureatlassprite[1];

                if (!isLava)
                {
                    final IBlockState state = blockAccess.getBlockState(blockpos);

                    if (state.getBlockFaceShape(blockAccess, blockpos, EnumFacing.VALUES[i1 + 2].getOpposite()) == net.minecraft.block.state.BlockFaceShape.SOLID)
                    {
                        textureatlassprite1 = atlasSpriteWaterOverlay;
                    }
                }

                if (aboolean[i1])
                {
                    float f39;
                    float f40;
                    double d3;
                    double d4;
                    double d5;
                    double d6;

                    if (i1 == 0)
                    {
                        f39 = fluidHeight_________;
                        f40 = fluidHeightEast_____;
                        d3 = posX;
                        d5 = posX + 1.0D;
                        d4 = posZ + 0.0010000000474974513D;
                        d6 = posZ + 0.0010000000474974513D;
                    }
                    else if (i1 == 1)
                    {
                        f39 = fluidHeightEastSouth;
                        f40 = fluidHeightSouth____;
                        d3 = posX + 1.0D;
                        d5 = posX;
                        d4 = (posZ + 1.0D) - 0.0010000000474974513D;
                        d6 = (posZ + 1.0D) - 0.0010000000474974513D;
                    }
                    else if (i1 == 2)
                    {
                        f39 = fluidHeightSouth____;
                        f40 = fluidHeight_________;
                        d3 = posX + 0.0010000000474974513D;
                        d5 = posX + 0.0010000000474974513D;
                        d4 = posZ + 1.0D;
                        d6 = posZ;
                    }
                    else
                    {
                        f39 = fluidHeightEast_____;
                        f40 = fluidHeightEastSouth;
                        d3 = (posX + 1.0D) - 0.0010000000474974513D;
                        d5 = (posX + 1.0D) - 0.0010000000474974513D;
                        d4 = posZ;
                        d6 = posZ + 1.0D;
                    }

                    didRender = true;
                    final float f41 = textureatlassprite1.getInterpolatedU(0.0D);
                    final float f27 = textureatlassprite1.getInterpolatedU(8.0D);
                    final float f28 = textureatlassprite1.getInterpolatedV((1.0F - f39) * 16.0F * 0.5F);
                    final float f29 = textureatlassprite1.getInterpolatedV((1.0F - f40) * 16.0F * 0.5F);
                    final float f30 = textureatlassprite1.getInterpolatedV(8.0D);
                    final int j = blockStateIn.getPackedLightmapCoords(blockAccess, blockpos);
                    final int k = (j >> 16) & 65535;
                    final int l = j & 65535;
                    final float f31 = i1 < 2 ? 0.8F : 0.6F;
                    final float f32 = 1.0F * f31 * colorMultiplierRed;
                    final float f33 = 1.0F * f31 * colorMultiplierGreen;
                    final float f34 = 1.0F * f31 * colorMultiplierBlue;
                    bufferBuilderIn.pos(d3, posY + f39, d4).color(f32, f33, f34, 1.0F).tex(f41, f28).lightmap(k, l).endVertex();
                    bufferBuilderIn.pos(d5, posY + f40, d6).color(f32, f33, f34, 1.0F).tex(f27, f29).lightmap(k, l).endVertex();
                    bufferBuilderIn.pos(d5, posY + 0.0D, d6).color(f32, f33, f34, 1.0F).tex(f27, f30).lightmap(k, l).endVertex();
                    bufferBuilderIn.pos(d3, posY + 0.0D, d4).color(f32, f33, f34, 1.0F).tex(f41, f30).lightmap(k, l).endVertex();

                    if (textureatlassprite1 != atlasSpriteWaterOverlay)
                    {
                        bufferBuilderIn.pos(d3, posY + 0.0D, d4).color(f32, f33, f34, 1.0F).tex(f41, f30).lightmap(k, l).endVertex();
                        bufferBuilderIn.pos(d5, posY + 0.0D, d6).color(f32, f33, f34, 1.0F).tex(f27, f30).lightmap(k, l).endVertex();
                        bufferBuilderIn.pos(d5, posY + f40, d6).color(f32, f33, f34, 1.0F).tex(f27, f29).lightmap(k, l).endVertex();
                        bufferBuilderIn.pos(d3, posY + f39, d4).color(f32, f33, f34, 1.0F).tex(f41, f28).lightmap(k, l).endVertex();
                    }
                }
            }

            return didRender;
        }
    }

    private static float getFluidHeight(final IBlockAccess blockAccess, final BlockPos blockPosIn, final Material blockMaterial)
    {
        int i = 0;
        float f = 0.0F;

        for (int j = 0; j < 4; ++j)
        {
            final BlockPos blockpos = blockPosIn.add(-(j & 1), 0, -((j >> 1) & 1));

            if (blockAccess.getBlockState(blockpos.up()).getMaterial() == blockMaterial)
            {
                return 1.0F;
            }

            final IBlockState iblockstate = blockAccess.getBlockState(blockpos);
            final Material material = iblockstate.getMaterial();

            if (material != blockMaterial)
            {
                if (!material.isSolid())
                {
                    ++f;
                    ++i;
                }
            }
            else
            {
                final int k = iblockstate.getValue(BlockLiquid.LEVEL).intValue();

                if ((k >= 8) || (k == 0))
                {
                    f += BlockLiquid.getLiquidHeightPercent(k) * 10.0F;
                    i += 10;
                }

                f += BlockLiquid.getLiquidHeightPercent(k);
                ++i;
            }
        }

        return 1.0F - (f / i);
    }

}
