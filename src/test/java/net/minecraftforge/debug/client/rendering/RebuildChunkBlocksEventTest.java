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
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;
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

        rebuildOptions = new RebuildOptions[] { RebuildOptions.MARCHING_CUBES };

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

        final float redFloat;
        final float greenFloat;
        final float blueFloat;

        if (quad.hasTintIndex())
        {
            final int colorMultiplier = Minecraft.getMinecraft().getBlockColors().colorMultiplier(stateIn, worldIn, posIn, 0);
            redFloat = ((colorMultiplier >> 16) & 255) / 255.0F;
            greenFloat = ((colorMultiplier >> 8) & 255) / 255.0F;
            blueFloat = (colorMultiplier & 255) / 255.0F;
        }
        else
        {
            redFloat = 1;
            greenFloat = 1;
            blueFloat = 1;
        }

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
            buffer.pos(-x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            break;
        case SOUTH:
            buffer.pos(x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
            break;
        case WEST:
            buffer.pos(-x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(-x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            break;
        case EAST:
            buffer.pos(x_size + x, -y_size + y, z_size + z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, -y_size + y, -z_size + z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, y_size + y, -z_size + z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmap1, lightmap2).endVertex();
            buffer.pos(x_size + x, y_size + y, z_size + z).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmap1, lightmap2).endVertex();
            break;
        default:
            return false;
        }

        return true;
    }

    public static int[] cube_edges = new int[24];
    public static int[] edge_table = new int[256];

    static
    {
        int cube_edge_index = 0;

        for (int i = 0; i < 8; ++i)
        {
            for (int j = 1; j <= 4; j <<= 1)
            {
                final int p = i ^ j;
                if (i <= p)
                {
                    cube_edges[cube_edge_index++] = i;
                    cube_edges[cube_edge_index++] = p;
                }
            }
        }

        for (int edge_table_index = 0; edge_table_index < 256; ++edge_table_index)
        {
            int em = 0;

            for (int j = 0; j < 24; j += 2)
            {
                final boolean a = (edge_table_index & (1 << cube_edges[j])) != 0;
                final boolean b = (edge_table_index & (1 << cube_edges[j + 1])) != 0;
                em |= a != b ? 1 << (j >> 1) : 0;
            }

            edge_table[edge_table_index] = em;
        }

    }

    public static boolean shouldSmooth(final IBlockState state)
    {
        boolean smooth = false;

        smooth |= state.getBlock() instanceof BlockGrass;
        smooth |= state.getBlock() instanceof BlockDirt;
        smooth |= state.getBlock() instanceof BlockStone;
        smooth |= state.getBlock() instanceof BlockSand;
        smooth |= state.getBlock() instanceof BlockSandStone;
        smooth |= state.getBlock() instanceof BlockGravel;
        smooth |= state.getBlock() instanceof BlockOre;
        smooth |= state.getBlock() instanceof BlockSilverfish;

        return smooth;
    }

    public static float getBlockDensity(final BlockPos pos, final IBlockAccess cache)
    {
        float dens = 0.0F;

        for (int z = 0; z < 2; ++z)
        {
            for (int y = 0; y < 2; ++y)
            {
                for (int x = 0; x < 2; ++x)
                {
                    final IBlockState state = cache.getBlockState(pos.add(-x, -y, -z));
                    if (shouldSmooth(state))
                    {
                        ++dens;
                    }
                    else
                    {
                        --dens;
                    }
                }
            }
        }

        return dens;
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

        final ChunkCache cache = event.getWorldView();

        final int[] dims = new int[] { 16, 16, 16 };
        final BlockPos positioni = new BlockPos(event.getPosition().getX(), event.getPosition().getY(), event.getPosition().getZ());
        final int[] x = new int[3];
        final int[] r = new int[] { 1, dims[0] + 3, (dims[0] + 3) * (dims[1] + 3) };
        final float[] grid = new float[8];
        final float[][] buffer = new float[r[2] * 2][3];
        int bufno = 1;

        for (x[2] = 0; x[2] < (dims[2] + 1); r[2] = -r[2])
        {
            int m = 1 + ((dims[0] + 3) * (1 + (bufno * (dims[1] + 3))));

            for (x[1] = 0; x[1] < (dims[1] + 1); m += 2)
            {
                for (x[0] = 0; x[0] < (dims[0] + 1); ++m)
                {
                    int mask = 0;
                    int g = 0;

                    for (int k = 0; k < 2; ++k)
                    {
                        for (int j = 0; j < 2; ++j)
                        {
                            for (int i = 0; i < 2; ++g)
                            {
                                final float p = getBlockDensity(new BlockPos(positioni.getX() + x[0] + i, positioni.getY() + x[1] + j, positioni.getZ() + x[2] + k), cache);
                                grid[g] = p;
                                mask |= p > 0.0F ? 1 << g : 0;
                                ++i;
                            }
                        }
                    }

                    if ((mask != 0) && (mask != 255))
                    {
                        IBlockState state = Blocks.AIR.getDefaultState();
                        BlockPos statePos = BlockPos.ORIGIN;

                        label368: for (int k = -1; k < 2; ++k)
                        {
                            for (int j = -1; j < 2; ++j)
                            {
                                for (int i = -1; i < 2; ++i)
                                {
                                    final BlockPos pos = new BlockPos(positioni.getX() + x[0] + i, positioni.getY() + x[1] + k, positioni.getZ() + x[2] + j);
                                    final IBlockState tempState = cache.getBlockState(pos);
                                    if (shouldSmooth(tempState) && (state.getBlock() != Blocks.SNOW_LAYER) && (state.getBlock() != Blocks.GRASS))
                                    {
                                        state = tempState;
                                        statePos = pos;
                                        if ((tempState.getBlock() == Blocks.SNOW_LAYER) || (tempState.getBlock() == Blocks.GRASS_PATH))
                                        {
                                            break label368;
                                        }
                                    }
                                }
                            }
                        }

                        final int[] br = new int[] { positioni.getX() + x[0], positioni.getY() + x[1] + 1, positioni.getZ() + x[2] };

                        label594: for (int k = -1; k < 2; ++k)
                        {
                            for (int j = -2; j < 3; ++j)
                            {
                                for (int i = -1; i < 2; ++i)
                                {
                                    final IBlockState tempState = cache.getBlockState(new BlockPos(positioni.getX() + x[0] + i, positioni.getY() + x[1] + k, positioni.getZ() + x[2] + j));
                                    if (!tempState.isOpaqueCube())
                                    {
                                        br[0] = positioni.getX() + x[0] + i;
                                        br[1] = positioni.getY() + x[1] + k;
                                        br[2] = positioni.getZ() + x[2] + j;
                                        break label594;
                                    }
                                }
                            }
                        }

                        final IBakedModel model = event.getBlockRendererDispatcher().getModelForState(state);
                        if (model == null)
                        {
                            continue;
                        }
                        final List<BakedQuad> quads = model.getQuads(state, EnumFacing.UP, MathHelper.getPositionRandom(statePos));
                        if ((quads == null) || quads.isEmpty())
                        {
                            continue;
                        }
                        final BakedQuad quad = quads.get(0);
                        if (quad == null)
                        {
                            continue;
                        }

                        final TextureAtlasSprite sprite = quad.getSprite();
                        if (sprite == null)
                        {
                            continue;
                        }
                        final double tu0 = sprite.getMinU();
                        final double tu1 = sprite.getMaxU();
                        final double tv0 = sprite.getMinV();
                        final double tv1 = sprite.getMaxV();
                        final int edgemask = edge_table[mask];
                        int ecount = 0;
                        final Vector3d positiond = new Vector3d();
                        positiond.x = 0;
                        positiond.y = 0;
                        positiond.z = 0;

                        for (int i = 0; i < 12; ++i)
                        {
                            if ((edgemask & (1 << i)) != 0)
                            {
                                ++ecount;
                                final int e0 = cube_edges[i << 1];
                                final int e1 = cube_edges[(i << 1) + 1];
                                final float g0 = grid[e0];
                                final float g1 = grid[e1];
                                float t = g0 - g1;
                                if (Math.abs(t) > 0.0F)
                                {
                                    t = g0 / t;
                                    int j = 0;

                                    for (int k = 1; j < 3; k <<= 1)
                                    {
                                        final int a = e0 & k;
                                        final int b = e1 & k;
                                        if (a != b)
                                        {
                                            switch (j)
                                            {
                                            case 0:
                                                positiond.x += a != 0 ? 1.0F - t : t;
                                                break;
                                            case 1:
                                                positiond.y += a != 0 ? 1.0F - t : t;
                                                break;
                                            default:
                                            case 2:
                                                positiond.z += a != 0 ? 1.0F - t : t;
                                                break;
                                            }
                                        }
                                        else
                                        {
                                            switch (j)
                                            {
                                            case 0:
                                                positiond.x += a != 0 ? 1.0F : 0.0F;
                                                break;
                                            case 1:
                                                positiond.y += a != 0 ? 1.0F : 0.0F;
                                                break;
                                            default:
                                            case 2:
                                                positiond.z += a != 0 ? 1.0F : 0.0F;
                                                break;
                                            }
                                        }

                                        ++j;
                                    }
                                }
                            }
                        }

                        final float s = 1.0F / ecount;

                        positiond.x = (positioni.getX() + x[0]) + (s * positiond.x);
                        positiond.y = (positioni.getY() + x[1]) + (s * positiond.y);
                        positiond.z = (positioni.getZ() + x[2]) + (s * positiond.z);

                        final int tx = x[0] == 16 ? 0 : x[0];
                        final int ty = x[1] == 16 ? 0 : x[1];
                        final int tz = x[2] == 16 ? 0 : x[2];
                        long i1 = (tx * 3129871) ^ (tz * 116129781L) ^ ty;
                        i1 = (i1 * i1 * 42317861L) + (i1 * 11L);
                        positiond.x = (float) (positiond.x - (((((i1 >> 16) & 15L) / 15.0F) - 0.5D) * 0.2D));
                        positiond.y = (float) (positiond.y - (((((i1 >> 20) & 15L) / 15.0F) - 1.0D) * 0.2D));
                        positiond.z = (float) (positiond.z - (((((i1 >> 24) & 15L) / 15.0F) - 0.5D) * 0.2D));
                        buffer[m] = new float[] { (float) positiond.x, (float) positiond.y, (float) positiond.z };

                        final float redFloat;
                        final float greenFloat;
                        final float blueFloat;

                        if (quad.hasTintIndex())
                        {
                            final int colorMultiplier = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, cache, statePos, 0);
                            redFloat = ((colorMultiplier >> 16) & 255) / 255.0F;
                            greenFloat = ((colorMultiplier >> 8) & 255) / 255.0F;
                            blueFloat = (colorMultiplier & 255) / 255.0F;
                        }
                        else
                        {
                            redFloat = 1;
                            greenFloat = 1;
                            blueFloat = 1;
                        }

                        final BlockRenderLayer blockRenderLayer = state.getBlock().getBlockLayer();
                        final BufferBuilder bufferbuilder = event.startOrContinueLayer(blockRenderLayer);
                        event.setBlockRenderLayerUsedWithOrOpperation(blockRenderLayer, true);

                        final int red = (int) (0xFF * redFloat);
                        final int green = (int) (0xFF * greenFloat);
                        final int blue = (int) (0xFF * blueFloat);
                        final int alpha = 0xFF;
                        final double minU = sprite.getMinU();
                        final double maxU = sprite.getMaxU();
                        final double minV = sprite.getMinV();
                        final double maxV = sprite.getMaxV();

                        int lightmapSkyLight = 0;
                        int lightmapBlockLight = 0;

                        int lightmapSkyLightUsed = 0;
                        int lightmapBlockLightUsed = 0;

                        for (int offset = 0; offset <= 5; ++offset)
                        {
                            for (final EnumFacing facing : EnumFacing.VALUES)
                            {
                                final int skyLight = cache.getLightFor(EnumSkyBlock.SKY, statePos.offset(facing, offset));
                                if (skyLight > 0)
                                {
                                    lightmapSkyLight += skyLight;
                                    lightmapSkyLightUsed++;
                                }
                                final int blockLight = cache.getLightFor(EnumSkyBlock.BLOCK, statePos.offset(facing, offset));
                                if (blockLight > 0)
                                {
                                    lightmapBlockLight += blockLight;
                                    lightmapBlockLightUsed++;
                                }
                            }
                        }
                        if (lightmapSkyLightUsed > 0)
                        {
                            lightmapSkyLight = (lightmapSkyLight << 4) / lightmapSkyLightUsed;
                        }
                        if (lightmapBlockLightUsed > 0)
                        {
                            lightmapBlockLight = (lightmapBlockLight << 4) / lightmapBlockLightUsed;
                        }

                        for (int i = 0; i < 3; ++i)
                        {
                            if ((edgemask & (1 << i)) != 0)
                            {
                                final int iu = (i + 1) % 3;
                                final int iv = (i + 2) % 3;
                                if ((x[iu] != 0) && (x[iv] != 0))
                                {
                                    final int du = r[iu];
                                    final int dv = r[iv];

                                    final float[] v0 = buffer[m];
                                    final float[] v1 = buffer[m - du];
                                    final float[] v2 = buffer[m - du - dv];
                                    final float[] v3 = buffer[m - dv];

                                    if ((mask & 1) != 0)
                                    {
                                        bufferbuilder.pos(v0[0], v0[1], v0[2]).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmapSkyLight, lightmapBlockLight).endVertex();
                                        bufferbuilder.pos(v1[0], v1[1], v1[2]).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmapSkyLight, lightmapBlockLight).endVertex();
                                        bufferbuilder.pos(v2[0], v2[1], v2[2]).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmapSkyLight, lightmapBlockLight).endVertex();
                                        bufferbuilder.pos(v3[0], v3[1], v3[2]).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmapSkyLight, lightmapBlockLight).endVertex();
                                    }
                                    else
                                    {
                                        bufferbuilder.pos(v0[0], v0[1], v0[2]).color(red, green, blue, alpha).tex(minU, maxV).lightmap(lightmapSkyLight, lightmapBlockLight).endVertex();
                                        bufferbuilder.pos(v3[0], v3[1], v3[2]).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(lightmapSkyLight, lightmapBlockLight).endVertex();
                                        bufferbuilder.pos(v2[0], v2[1], v2[2]).color(red, green, blue, alpha).tex(maxU, minV).lightmap(lightmapSkyLight, lightmapBlockLight).endVertex();
                                        bufferbuilder.pos(v1[0], v1[1], v1[2]).color(red, green, blue, alpha).tex(minU, minV).lightmap(lightmapSkyLight, lightmapBlockLight).endVertex();
                                    }
                                }
                            }
                        }
                    }

                    ++x[0];
                }

                ++x[1];
            }

            ++x[2];
            bufno ^= 1;
        }

        for (final BlockPos.MutableBlockPos blockpos$mutableblockpos : event.getChunkBlockPositions())
        {
            final IBlockState iblockstate = event.getWorldView().getBlockState(blockpos$mutableblockpos);

            if (shouldSmooth(iblockstate))
            {
                continue;
            }

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

}
