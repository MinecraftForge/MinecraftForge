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
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Mod(modid = RebuildChunkBlocksEventTest.MODID, name = "RebuildChunkBlocksEventTest", version = "1.0", acceptableRemoteVersions = "*", clientSideOnly = true)
public class RebuildChunkBlocksEventTest
{

    public static final String MODID = "rebuild_chunk_blocks_event_test";
    private static final boolean ENABLED = true;

    private static enum RebuildOptions
    {
        VANILLA, VANILLA_MODDED, FRAGMENTED, FACING, MARCHING_CUBES;
    }

    private static RebuildOptions[] rebuildOptions = new RebuildOptions[RebuildOptions.values().length];

    private static EnumFacing[] fragmentFacings = new EnumFacing[] {
            EnumFacing.DOWN,
            EnumFacing.UP
    };
    private static int fragmentRange;
    private static Random fragmentRandom = new Random();

    private static EnumFacing[] facingFacings = new EnumFacing[] {
            EnumFacing.NORTH,
            EnumFacing.SOUTH,
            EnumFacing.WEST,
            EnumFacing.EAST
    };

    /** this exists to allow easy hotswapping of values (requires IDE debug mode for code Hot Swap) */
    @SubscribeEvent
    public static void rebuildChunkSetup(final RebuildChunkBlocksEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

        rebuildOptions = new RebuildOptions[] { RebuildOptions.FACING };

        fragmentFacings = new EnumFacing[] {
                EnumFacing.DOWN,
                EnumFacing.UP,
                EnumFacing.NORTH,
                EnumFacing.SOUTH,
                EnumFacing.WEST,
                EnumFacing.EAST
        };
        fragmentRange = 15;
        // fragmentRandom = new Random();

        facingFacings = new EnumFacing[] {
                EnumFacing.DOWN,
                EnumFacing.UP,
                EnumFacing.NORTH,
                EnumFacing.SOUTH,
                EnumFacing.WEST,
                EnumFacing.EAST,
        };

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void cancelVanillaRebuildChunkIfNecessary(final RebuildChunkBlocksEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

        if (!Arrays.asList(rebuildOptions).contains(RebuildOptions.VANILLA))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void rebuildChunkVanillaModded(final RebuildChunkBlocksEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

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
        if (!ENABLED)
        {
            return;
        }

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
        if (!ENABLED)
        {
            return;
        }

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

                    for (final EnumFacing side : facingFacings)
                    {
                        final boolean used = renderBlockEnumFacing(iblockstate, blockpos$mutableblockpos, event.getWorldView(), bufferbuilder, event.getBlockRendererDispatcher(), side);

                        event.setBlockRenderLayerUsedWithOrOpperation(blockRenderLayer, used);
                    }

                }
            }
            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
        }
    }

    @SubscribeEvent
    public static void rebuildChunkMarchingCubes(final RebuildChunkBlocksEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

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

    private static boolean renderBlockEnumFacing(IBlockState state, final BlockPos pos, final IBlockAccess blockAccess, final BufferBuilder bufferBuilderIn, final BlockRendererDispatcher blockRendererDispatcher, final EnumFacing side)
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
                    return renderModel(blockAccess, model, state, pos, bufferBuilderIn, true, side);
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
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
            throw new ReportedException(crashreport);
        }
    }

    private static boolean renderModel(final IBlockAccess blockAccessIn, final IBakedModel modelIn, final IBlockState blockStateIn, final BlockPos blockPosIn, final BufferBuilder buffer, final boolean checkSides, final EnumFacing side)
    {
        return renderModel(blockAccessIn, modelIn, blockStateIn, blockPosIn, buffer, checkSides, side, MathHelper.getPositionRandom(blockPosIn));
    }

    private static boolean renderModel(final IBlockAccess worldIn, final IBakedModel modelIn, final IBlockState stateIn, final BlockPos posIn, final BufferBuilder buffer, final boolean checkSides, final EnumFacing side, final long rand)
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

        final int colorMultiplier = Minecraft.getMinecraft().getBlockColors().colorMultiplier(stateIn, worldIn, posIn, 0);
        final float redFloat = ((colorMultiplier >> 16) & 255) / 255.0F;
        final float greenFloat = ((colorMultiplier >> 8) & 255) / 255.0F;
        final float blueFloat = (colorMultiplier & 255) / 255.0F;

        final double x_size = 1 / 2d;
        final double y_size = 1 / 2d;
        final double z_size = 1 / 2d;
        final double x = posIn.getX() + x_size;
        final double y = posIn.getY() + y_size;
        final double z = posIn.getZ() + z_size;
        // final int red = 0xFF;
        // final int green = 0xFF;
        // final int blue = 0xFF;
        // final int red = new Random().nextInt(0xFF);
        // final int green = new Random().nextInt(0xFF);
        // final int blue = new Random().nextInt(0xFF);
        final int red = (int) (0xFF * redFloat);
        final int green = (int) (0xFF * greenFloat);
        final int blue = (int) (0xFF * blueFloat);
        final int alpha = 0xFF;
        final double minU = sprite.getMinU();
        final double maxU = sprite.getMaxU();
        final double minV = sprite.getMinV();
        final double maxV = sprite.getMaxV();
        final int lightmap1 = 240;
        final int lightmap2 = 0;

        switch (side)
        {
        case DOWN:
            buffer.pos(-x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
            break;
        case UP:
            buffer.pos(-x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            break;
        case NORTH:
            // LEFT
            buffer.pos(x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
            break;
        case SOUTH:
            // RIGHT
            buffer.pos(-x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            break;
        case WEST:
            // BACK
            buffer.pos(-x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();

            break;
        case EAST:// FRONT
            buffer.pos(x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();

            break;
        default:
            return false;
        }

        return true;
    }

}
