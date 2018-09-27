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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
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

    @SubscribeEvent
    public static void renderChunkMarchingCubes(final RebuildChunkBlocksEvent event)
    {

        event.setCanceled(true);

        final ChunkCache cache = event.getWorldView();
        final BlockRendererDispatcher blockRendererDispatcher = event.getBlockRendererDispatcher();
        final ChunkCompileTaskGenerator generator = event.getGenerator();
        final CompiledChunk compiledChunk = event.getCompiledChunk();

        //

        //

        final boolean[] isBlockRenderLayerActive = new boolean[BlockRenderLayer.values().length];

        for (final BlockPos.MutableBlockPos blockpos$mutableblockpos : event.getChunkBlockPositions())
        {

            final IBlockState state = event.getWorldView().getBlockState(blockpos$mutableblockpos);
            final Block block = state.getBlock();
            for (final BlockRenderLayer blockrenderlayer1 : BlockRenderLayer.values())
            {
                if (!block.canRenderInLayer(state, blockrenderlayer1))
                {
                    continue;
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(blockrenderlayer1);
                final int j = blockrenderlayer1.ordinal();

                if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE)
                {
                    final BufferBuilder bufferbuilder = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(blockrenderlayer1);

                    if (!compiledChunk.isLayerStarted(blockrenderlayer1))
                    {
                        compiledChunk.setLayerStarted(blockrenderlayer1);

                        event.preRenderBlocks(bufferbuilder, event.getPosition());

                    }

                    isBlockRenderLayerActive[j] |= renderBlockEnumFacing(state, blockpos$mutableblockpos, event.getWorldView(), bufferbuilder, blockRendererDispatcher,
                            EnumFacing.UP);
                }
            }
            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
        }

        for (final BlockRenderLayer blockrenderlayer : BlockRenderLayer.values())
        {
            if (!event.isCanceled())
            {
                continue;
            }
            if (isBlockRenderLayerActive[blockrenderlayer.ordinal()])
            {
                compiledChunk.setLayerUsed(blockrenderlayer);
            }

            if (compiledChunk.isLayerStarted(blockrenderlayer))
            {
                event.postRenderBlocks(blockrenderlayer, event.getX(), event.getY(), event.getZ(), generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(blockrenderlayer), compiledChunk);
            }
        }

        if (true)
        {
            return;
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

                    renderCube(bufferBuilderIn, model, blockAccess, pos, state);

                    // DefaultVertexFormats.BLOCK;
                    //
                    // BLOCK.addElement(POSITION_3F);
                    // BLOCK.addElement(COLOR_4UB);
                    // BLOCK.addElement(TEX_2F);
                    // BLOCK.addElement(TEX_2S);

                    final int x_size = 0;
                    final int y_size = 0;
                    final int z_size = 0;

                    // bufferbuilder.pos(-x_size, y_size, -z_size).tex(maxU, maxV).endVertex();
                    // bufferbuilder.pos(-x_size, y_size, z_size).tex(maxU, minV).endVertex();
                    // bufferbuilder.pos(x_size, y_size, z_size).tex(minU, minV).endVertex();
                    // bufferbuilder.pos(x_size, y_size, -z_size).tex(minU, maxV).endVertex();

                    // bufferBuilderIn.pos(-x_size, y_size, -z_size).color(0xff, 0xff, 0xff, 0xff).tex(1, 1).endVertex();
                    // bufferBuilderIn.pos(-x_size, y_size, z_size).color(0xff, 0xff, 0xff, 0xff).tex(1, 1).endVertex();
                    // bufferBuilderIn.pos(x_size, y_size, z_size).color(0xff, 0xff, 0xff, 0xff).tex(1, 1).endVertex();
                    // bufferBuilderIn.pos(x_size, y_size, -z_size).color(0xff, 0xff, 0xff, 0xff).tex(1, 1).endVertex();

                    // state = state.getBlock().getExtendedState(state, blockAccess, pos);
                    //
                    // boolean flag = false;
                    //
                    // final List<BakedQuad> list1 = model.getQuads(state, facing, 0);
                    //
                    // if (!list1.isEmpty())
                    // {
                    //
                    // Method method = null;
                    // try
                    // {
                    // method = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderModelFlat", null, IBlockAccess.class, IBakedModel.class, IBlockState.class,
                    // BlockPos.class,
                    // BufferBuilder.class, boolean.class, long.class);
                    // }
                    // catch (final Throwable eh)
                    // {
                    // }
                    //
                    // if (method != null)
                    // {
                    // method.invoke(blockRendererDispatcher.getBlockModelRenderer(), blockAccess, model, state, pos, bufferBuilderIn, true, MathHelper.getPositionRandom(pos));
                    // }
                    // // this.renderQuadsFlat(blockAccess, state, pos, -1, true, bufferBuilderIn, list1, bitset);
                    // flag = true;
                    // }
                    //

                    blockRendererDispatcher.

                    //

                            getBlockModelRenderer().renderModel(blockAccess, model, state, pos, bufferBuilderIn, true);

                    return true;

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

    private static void renderCube(final BufferBuilder buffer, final IBakedModel model, final IBlockAccess blockAccessIn, final BlockPos posIn, final IBlockState stateIn)
    {

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

        // position, color, texf, texs;

        final Vec3d vec3d = stateIn.getOffset(blockAccessIn, posIn);
        final double d0 = (double) posIn.getX() + vec3d.x;
        final double d1 = (double) posIn.getY() + vec3d.y;
        final double d2 = (double) posIn.getZ() + vec3d.z;

        final double x_size = 1;
        final double y_size = 1;
        final double z_size = 1;

        final double x = d0;
        final double y = d1;
        final double z = d2;

        final double minU = sprite.getMinU();
        final double maxU = sprite.getMaxU();
        final double minV = sprite.getMinV();
        final double maxV = sprite.getMaxV();

        buffer.pos(-x_size + x, y_size + y, -z_size + z).color(0xff, 0xff, 0xff, 0xff).tex(maxU, maxV).endVertex();
        buffer.pos(-x_size + x, y_size + y, z_size + z).color(0xff, 0xff, 0xff, 0xff).tex(maxU, minV).endVertex();
        buffer.pos(x_size + x, y_size + y, z_size + z).color(0xff, 0xff, 0xff, 0xff).tex(minU, minV).endVertex();
        buffer.pos(x_size + x, y_size + y, -z_size + z).color(0xff, 0xff, 0xff, 0xff).tex(minU, maxV).endVertex();

        final List<BakedQuad> list = model.getQuads(stateIn, EnumFacing.DOWN, 0);
        for (int i = 0; i < 4; i++)
        {
            // buffer.pos(d0, d1, d2).color(0, 0, 0, 0).tex(0, 1).tex(1, 0).endVertex();
        }

        final float offset = -0.5f;
        final float spacing = (4.0f - (3.0f)) / 2.0f;

        final float minX = -0.5f;
        final float minY = -0.5f;
        final float minZ = -0.5f;

        final float maxZ = 0.5f;
        final float maxY = 0.5f;
        final float maxX = 0.5f;

        final float uA = (0 / 4f) * 16;
        final float uB = (1 / 4f) * 16;
        final float uC = (2 / 4f) * 16;
        final float uD = (3 / 4f) * 16;
        final float uE = (4 / 4f) * 16;

        final float vA = (3 / 4f) * 16;
        final float vB = (2 / 4f) * 16;
        final float vC = (1 / 4f) * 16;
        final float vD = (0 / 4f) * 16;

    }

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
        final float f = (float) ((colorMultiplier >> 16) & 255) / 255.0F;
        final float f1 = (float) ((colorMultiplier >> 8) & 255) / 255.0F;
        final float f2 = (float) (colorMultiplier & 255) / 255.0F;
        final boolean shouldRenderTop = blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.UP);
        final boolean shouldRenderBottom = blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.DOWN);
        final boolean[] aboolean = new boolean[] { blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.NORTH), blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.SOUTH), blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.WEST), blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.EAST) };

        if (!shouldRenderTop && !shouldRenderBottom && !aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3])
        {
            return false;
        }
        else
        {
            boolean flag3 = false;
            final float f3 = 0.5F;
            final float f4 = 1.0F;
            final float f5 = 0.8F;
            final float f6 = 0.6F;
            final Material material = blockStateIn.getMaterial();
            float f7 = this.getFluidHeight(blockAccess, blockPosIn, material);
            float f8 = this.getFluidHeight(blockAccess, blockPosIn.south(), material);
            float f9 = this.getFluidHeight(blockAccess, blockPosIn.east().south(), material);
            float f10 = this.getFluidHeight(blockAccess, blockPosIn.east(), material);
            final double d0 = (double) blockPosIn.getX();
            final double d1 = (double) blockPosIn.getY();
            final double d2 = (double) blockPosIn.getZ();
            final float f11 = 0.001F;

            if (shouldRenderTop)
            {
                flag3 = true;
                final float f12 = BlockLiquid.getSlopeAngle(blockAccess, blockPosIn, material, blockStateIn);
                final TextureAtlasSprite textureatlassprite = f12 > -999.0F ? atextureatlassprite[1] : atextureatlassprite[0];
                f7 -= 0.001F;
                f8 -= 0.001F;
                f9 -= 0.001F;
                f10 -= 0.001F;
                float f13;
                float f14;
                float f15;
                float f16;
                float f17;
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
                    final float f21 = MathHelper.sin(f12) * 0.25F;
                    final float f22 = MathHelper.cos(f12) * 0.25F;
                    final float f23 = 8.0F;
                    f13 = textureatlassprite.getInterpolatedU((double) (8.0F + ((-f22 - f21) * 16.0F)));
                    f17 = textureatlassprite.getInterpolatedV((double) (8.0F + ((-f22 + f21) * 16.0F)));
                    f14 = textureatlassprite.getInterpolatedU((double) (8.0F + ((-f22 + f21) * 16.0F)));
                    f18 = textureatlassprite.getInterpolatedV((double) (8.0F + ((f22 + f21) * 16.0F)));
                    f15 = textureatlassprite.getInterpolatedU((double) (8.0F + ((f22 + f21) * 16.0F)));
                    f19 = textureatlassprite.getInterpolatedV((double) (8.0F + ((f22 - f21) * 16.0F)));
                    f16 = textureatlassprite.getInterpolatedU((double) (8.0F + ((f22 - f21) * 16.0F)));
                    f20 = textureatlassprite.getInterpolatedV((double) (8.0F + ((-f22 - f21) * 16.0F)));
                }

                final int k2 = blockStateIn.getPackedLightmapCoords(blockAccess, blockPosIn);
                final int l2 = (k2 >> 16) & 65535;
                final int i3 = k2 & 65535;
                final float f24 = 1.0F * f;
                final float f25 = 1.0F * f1;
                final float f26 = 1.0F * f2;
                bufferBuilderIn.pos(d0 + 0.0D, d1 + (double) f7, d2 + 0.0D).color(f24, f25, f26, 1.0F).tex((double) f13, (double) f17).lightmap(l2, i3).endVertex();
                bufferBuilderIn.pos(d0 + 0.0D, d1 + (double) f8, d2 + 1.0D).color(f24, f25, f26, 1.0F).tex((double) f14, (double) f18).lightmap(l2, i3).endVertex();
                bufferBuilderIn.pos(d0 + 1.0D, d1 + (double) f9, d2 + 1.0D).color(f24, f25, f26, 1.0F).tex((double) f15, (double) f19).lightmap(l2, i3).endVertex();
                bufferBuilderIn.pos(d0 + 1.0D, d1 + (double) f10, d2 + 0.0D).color(f24, f25, f26, 1.0F).tex((double) f16, (double) f20).lightmap(l2, i3).endVertex();

                if (blockliquid.shouldRenderSides(blockAccess, blockPosIn.up()))
                {
                    bufferBuilderIn.pos(d0 + 0.0D, d1 + (double) f7, d2 + 0.0D).color(f24, f25, f26, 1.0F).tex((double) f13, (double) f17).lightmap(l2, i3).endVertex();
                    bufferBuilderIn.pos(d0 + 1.0D, d1 + (double) f10, d2 + 0.0D).color(f24, f25, f26, 1.0F).tex((double) f16, (double) f20).lightmap(l2, i3).endVertex();
                    bufferBuilderIn.pos(d0 + 1.0D, d1 + (double) f9, d2 + 1.0D).color(f24, f25, f26, 1.0F).tex((double) f15, (double) f19).lightmap(l2, i3).endVertex();
                    bufferBuilderIn.pos(d0 + 0.0D, d1 + (double) f8, d2 + 1.0D).color(f24, f25, f26, 1.0F).tex((double) f14, (double) f18).lightmap(l2, i3).endVertex();
                }
            }

            if (shouldRenderBottom)
            {
                final float minU = atextureatlassprite[0].getMinU();
                final float maxU = atextureatlassprite[0].getMaxU();
                final float minV = atextureatlassprite[0].getMinV();
                final float maxV = atextureatlassprite[0].getMaxV();
                final int lightMapCoordsDown = blockStateIn.getPackedLightmapCoords(blockAccess, blockPosIn.down());
                final int i2 = (lightMapCoordsDown >> 16) & 65535;
                final int j2 = lightMapCoordsDown & 65535;
                bufferBuilderIn.pos(d0, d1, d2 + 1.0D).color(0.5F, 0.5F, 0.5F, 1.0F).tex((double) minU, (double) maxV).lightmap(i2, j2).endVertex();
                bufferBuilderIn.pos(d0, d1, d2).color(0.5F, 0.5F, 0.5F, 1.0F).tex((double) minU, (double) minV).lightmap(i2, j2).endVertex();
                bufferBuilderIn.pos(d0 + 1.0D, d1, d2).color(0.5F, 0.5F, 0.5F, 1.0F).tex((double) maxU, (double) minV).lightmap(i2, j2).endVertex();
                bufferBuilderIn.pos(d0 + 1.0D, d1, d2 + 1.0D).color(0.5F, 0.5F, 0.5F, 1.0F).tex((double) maxU, (double) maxV).lightmap(i2, j2).endVertex();
                flag3 = true;
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
                        f39 = f7;
                        f40 = f10;
                        d3 = d0;
                        d5 = d0 + 1.0D;
                        d4 = d2 + 0.0010000000474974513D;
                        d6 = d2 + 0.0010000000474974513D;
                    }
                    else if (i1 == 1)
                    {
                        f39 = f9;
                        f40 = f8;
                        d3 = d0 + 1.0D;
                        d5 = d0;
                        d4 = (d2 + 1.0D) - 0.0010000000474974513D;
                        d6 = (d2 + 1.0D) - 0.0010000000474974513D;
                    }
                    else if (i1 == 2)
                    {
                        f39 = f8;
                        f40 = f7;
                        d3 = d0 + 0.0010000000474974513D;
                        d5 = d0 + 0.0010000000474974513D;
                        d4 = d2 + 1.0D;
                        d6 = d2;
                    }
                    else
                    {
                        f39 = f10;
                        f40 = f9;
                        d3 = (d0 + 1.0D) - 0.0010000000474974513D;
                        d5 = (d0 + 1.0D) - 0.0010000000474974513D;
                        d4 = d2;
                        d6 = d2 + 1.0D;
                    }

                    flag3 = true;
                    final float f41 = textureatlassprite1.getInterpolatedU(0.0D);
                    final float f27 = textureatlassprite1.getInterpolatedU(8.0D);
                    final float f28 = textureatlassprite1.getInterpolatedV((double) ((1.0F - f39) * 16.0F * 0.5F));
                    final float f29 = textureatlassprite1.getInterpolatedV((double) ((1.0F - f40) * 16.0F * 0.5F));
                    final float f30 = textureatlassprite1.getInterpolatedV(8.0D);
                    final int j = blockStateIn.getPackedLightmapCoords(blockAccess, blockpos);
                    final int k = (j >> 16) & 65535;
                    final int l = j & 65535;
                    final float f31 = i1 < 2 ? 0.8F : 0.6F;
                    final float f32 = 1.0F * f31 * f;
                    final float f33 = 1.0F * f31 * f1;
                    final float f34 = 1.0F * f31 * f2;
                    bufferBuilderIn.pos(d3, d1 + (double) f39, d4).color(f32, f33, f34, 1.0F).tex((double) f41, (double) f28).lightmap(k, l).endVertex();
                    bufferBuilderIn.pos(d5, d1 + (double) f40, d6).color(f32, f33, f34, 1.0F).tex((double) f27, (double) f29).lightmap(k, l).endVertex();
                    bufferBuilderIn.pos(d5, d1 + 0.0D, d6).color(f32, f33, f34, 1.0F).tex((double) f27, (double) f30).lightmap(k, l).endVertex();
                    bufferBuilderIn.pos(d3, d1 + 0.0D, d4).color(f32, f33, f34, 1.0F).tex((double) f41, (double) f30).lightmap(k, l).endVertex();

                    if (textureatlassprite1 != atlasSpriteWaterOverlay)
                    {
                        bufferBuilderIn.pos(d3, d1 + 0.0D, d4).color(f32, f33, f34, 1.0F).tex((double) f41, (double) f30).lightmap(k, l).endVertex();
                        bufferBuilderIn.pos(d5, d1 + 0.0D, d6).color(f32, f33, f34, 1.0F).tex((double) f27, (double) f30).lightmap(k, l).endVertex();
                        bufferBuilderIn.pos(d5, d1 + (double) f40, d6).color(f32, f33, f34, 1.0F).tex((double) f27, (double) f29).lightmap(k, l).endVertex();
                        bufferBuilderIn.pos(d3, d1 + (double) f39, d4).color(f32, f33, f34, 1.0F).tex((double) f41, (double) f28).lightmap(k, l).endVertex();
                    }
                }
            }

            return flag3;
        }
    }

    private float getFluidHeight(final IBlockAccess blockAccess, final BlockPos blockPosIn, final Material blockMaterial)
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
                final int k = ((Integer) iblockstate.getValue(BlockLiquid.LEVEL)).intValue();

                if ((k >= 8) || (k == 0))
                {
                    f += BlockLiquid.getLiquidHeightPercent(k) * 10.0F;
                    i += 10;
                }

                f += BlockLiquid.getLiquidHeightPercent(k);
                ++i;
            }
        }

        return 1.0F - (f / (float) i);
    }

}
