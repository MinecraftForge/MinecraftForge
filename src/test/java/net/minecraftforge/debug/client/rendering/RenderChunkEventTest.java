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

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkCache;
import net.minecraftforge.client.event.RebuildChunkEvent.RebuildChunkBlocksEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Mod(modid = RenderChunkEventTest.MODID, name = "RenderChunkEventTest", version = "1.0", acceptableRemoteVersions = "*", clientSideOnly = true)
public class RenderChunkEventTest {

    static final String MODID = "render_chunk_event_test";
    
    static enum RenderType {
        VANILLA, MODDED_VANILLA_BEHAVIOUR, UPSIDE_DOWN, 
    }

    @SubscribeEvent
    public static void onRenderChunkEvent(final RebuildChunkBlocksEvent event) {

        final RenderType renderType = RenderType.VANILLA;
        
        final boolean cancel = renderType!=RenderType.VANILLA;

        event.setCanceled(cancel);

        if (!cancel) {
            return;
        }

        final ChunkCache cache = event.getWorldView();
        final BlockRendererDispatcher blockRendererDispatcher = event.getBlockRendererDispatcher();
        final ChunkCompileTaskGenerator generator = event.getGenerator();
        final CompiledChunk compiledChunk = event.getCompiledChunk();

        final int x_size = 1;
        final int x = 0;
        final int y_size = 1;
        final int y = 0;
        final int z_size = 1;
        final int z = 0;

        final boolean[] aboolean = new boolean[BlockRenderLayer.values().length];
        for (final BlockPos.MutableBlockPos blockpos$mutableblockpos : event.getChunkBlockPositions()) {

            final IBlockState state = event.getWorldView().getBlockState(blockpos$mutableblockpos);
            final Block block = state.getBlock();
            for (final BlockRenderLayer blockrenderlayer1 : BlockRenderLayer.values()) {
                if (!block.canRenderInLayer(state, blockrenderlayer1)) {
                    continue;
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(blockrenderlayer1);
                final int j = blockrenderlayer1.ordinal();

                if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE) {
                    final BufferBuilder bufferbuilder = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(j);

                    if (!compiledChunk.isLayerStarted(blockrenderlayer1)) {
                        compiledChunk.setLayerStarted(blockrenderlayer1);
                        event.preRenderBlocks(bufferbuilder, event.getChunkBlockPositions().iterator().next());
                    }

                    aboolean[j] |= blockRendererDispatcher.renderBlock(state, blockpos$mutableblockpos, event.getWorldView(), bufferbuilder);
                }
            }
            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
        }

        for (final BlockRenderLayer blockrenderlayer : BlockRenderLayer.values()) {
            if (aboolean[blockrenderlayer.ordinal()]) {
                compiledChunk.setLayerUsed(blockrenderlayer);
            }

            if (compiledChunk.isLayerStarted(blockrenderlayer)) {
                event.postRenderBlocks(blockrenderlayer, event.getX(), event.getY(), event.getZ(), generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(blockrenderlayer), compiledChunk);
            }
        }
        if (true) {
            return;
        }
        final IBlockState blahblah = cache.getBlockState(event.getPosition());

        for (final MutableBlockPos pos : event.getChunkBlockPositions()) {

            final IBlockState state = cache.getBlockState(pos);

            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder bufferbuilder = tessellator.getBuffer();

//            final BufferBuilder bufferbuilder = event.getBufferBuilder(state.getBlock().getBlockLayer());

            final IBakedModel model = blockRendererDispatcher.getModelForState(state);

            final List<BakedQuad> quads = model.getQuads(state, EnumFacing.UP, MathHelper.getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ()));

            if (quads.size() <= 0) {
                continue;
            }

            final BakedQuad quad = quads.get(0);

            if (quad == null) {
                continue;
            }

            final TextureAtlasSprite sprite = quad.getSprite();

            if (sprite == null) {
                continue;
            }

            final double minU = sprite.getMinU();
            final double maxU = sprite.getMaxU();
            final double minV = sprite.getMinV();
            final double maxV = sprite.getMaxV();

            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            // UP
            bufferbuilder.pos(-x_size + x, y_size + y, -z_size + z).tex(maxU, maxV).endVertex();
            bufferbuilder.pos(-x_size + x, y_size + y, z_size + z).tex(maxU, minV).endVertex();
            bufferbuilder.pos(x_size + x, y_size + y, z_size + z).tex(minU, minV).endVertex();
            bufferbuilder.pos(x_size + x, y_size + y, -z_size + z).tex(minU, maxV).endVertex();

            // DOWN
            bufferbuilder.pos(-x_size + x, -y_size + y, z_size + z).tex(minU, minV).endVertex();
            bufferbuilder.pos(-x_size + x, -y_size + y, -z_size + z).tex(minU, maxV).endVertex();
            bufferbuilder.pos(x_size + x, -y_size + y, -z_size + z).tex(maxU, maxV).endVertex();
            bufferbuilder.pos(x_size + x, -y_size + y, z_size + z).tex(maxU, minV).endVertex();

            // LEFT
            bufferbuilder.pos(x_size + x, -y_size + y, z_size + z).tex(maxU, minV).endVertex();
            bufferbuilder.pos(x_size + x, -y_size + y, -z_size + z).tex(maxU, maxV).endVertex();
            bufferbuilder.pos(x_size + x, y_size + y, -z_size + z).tex(minU, maxV).endVertex();
            bufferbuilder.pos(x_size + x, y_size + y, z_size + z).tex(minU, minV).endVertex();

            // RIGHT
            bufferbuilder.pos(-x_size + x, -y_size + y, -z_size + z).tex(minU, maxV).endVertex();
            bufferbuilder.pos(-x_size + x, -y_size + y, z_size + z).tex(minU, minV).endVertex();
            bufferbuilder.pos(-x_size + x, y_size + y, z_size + z).tex(maxU, minV).endVertex();
            bufferbuilder.pos(-x_size + x, y_size + y, -z_size + z).tex(maxU, maxV).endVertex();

            // BACK
            bufferbuilder.pos(-x_size + x, -y_size + y, -z_size + z).tex(minU, maxV).endVertex();
            bufferbuilder.pos(-x_size + x, y_size + y, -z_size + z).tex(minU, minV).endVertex();
            bufferbuilder.pos(x_size + x, y_size + y, -z_size + z).tex(maxU, minV).endVertex();
            bufferbuilder.pos(x_size + x, -y_size + y, -z_size + z).tex(maxU, maxV).endVertex();

            // FRONT
            bufferbuilder.pos(x_size + x, -y_size + y, z_size + z).tex(maxU, minV).endVertex();
            bufferbuilder.pos(x_size + x, y_size + y, z_size + z).tex(maxU, maxV).endVertex();
            bufferbuilder.pos(-x_size + x, y_size + y, z_size + z).tex(minU, maxV).endVertex();
            bufferbuilder.pos(-x_size + x, -y_size + y, z_size + z).tex(minU, minV).endVertex();

            Tessellator.getInstance().draw();

//            bufferbuilder.finishDrawing();

        }

//        final int[] dims = new int[]{16, 16, 16};
////        int[] c = new int[]{cx, cy, cz};
//        final int[] x = new int[3];
//        final int[] r = new int[]{1, dims[0] + 3, (dims[0] + 3) * (dims[1] + 3)};
//        final float[] grid = new float[8];
//        final float[][] buffer = new float[r[2] * 2][3];
//        int bufno = 1;
//
//        for(x[2] = 0; x[2] < (dims[2] + 1); r[2] = -r[2]) {
//           int m = 1 + ((dims[0] + 3) * (1 + (bufno * (dims[1] + 3))));
//
//           for(x[1] = 0; x[1] < (dims[1] + 1); m += 2) {
//              for(x[0] = 0; x[0] < (dims[0] + 1); ++m) {
//                 int mask = 0;
//                 int g = 0;
//
//                 for(int k = 0; k < 2; ++k) {
//                    for(int j = 0; j < 2; ++j) {
//                       for(int i = 0; i < 2; ++g) {
//                          final float p = 0.1f;// getBlockDensity(c[0] + x[0] + i, c[1] + x[1] + j, c[2] + x[2] + k, cache);
//                          grid[g] = p;
//                          mask |= p > 0.0F?1 << g:0;
//                          ++i;
//                       }
//                    }
//                 }
//
//                 if((mask != 0) && (mask != 255)) {
//
//
//                    label368:
//                    for(int k = -1; k < 2; ++k) {
//                       for(int j = -1; j < 2; ++j) {
//                          for(int i = -1; i < 2; ++i) {
////                             Block b = cache.getBlockState(new BlockPos(c[0] + x[0] + i, c[1] + x[1] + k, c[2] + x[2] + j)).getBlock();
////                             if(true && block != Blocks.SNOW_LAYER && block != Blocks.GRASS) {
////                                block = b;
////                                meta = cache.getBlockState(new BlockPos(c[0] + x[0] + i, c[1] + x[1] + k, c[2] + x[2] + j));
////                                if(b == Blocks.SNOW_LAYER || b == Blocks.GRASS) {
////                                   break label368;
////                                }
////                             }
//                          }
//                       }
//                    }
//
//                    final int[] br = new int[]{chunkPos.x + x[0], c[1] + x[1] + 1, c[2] + x[2]};
//
//                    label594:
//                    for(int k = -1; k < 2; ++k) {
//                       for(int j = -2; j < 3; ++j) {
//                          for(int i = -1; i < 2; ++i) {
//                             final Block b = cache.getBlock(c[0] + x[0] + i, c[1] + x[1] + k, c[2] + x[2] + j);
//                             if(!b.isOpaqueCube()) {
//                                br[0] = c[0] + x[0] + i;
//                                br[1] = c[1] + x[1] + k;
//                                br[2] = c[2] + x[2] + j;
//                                break label594;
//                             }
//                          }
//                       }
//                    }
//
//                    final IIcon icon = renderer.getBlockIconFromSideAndMetadata(block, 1, meta);
//                    final double tu0 = (double)icon.getMinU();
//                    final double tu1 = (double)icon.getMaxU();
//                    final double tv0 = (double)icon.getMinV();
//                    final double tv1 = (double)icon.getMaxV();
//                    final int edgemask = edge_table[mask];
//                    int ecount = 0;
//                    final float[] v = new float[]{0.0F, 0.0F, 0.0F};
//
//                    for(int i = 0; i < 12; ++i) {
//                       if((edgemask & (1 << i)) != 0) {
//                          ++ecount;
//                          final int e0 = cube_edges[i << 1];
//                          final int e1 = cube_edges[(i << 1) + 1];
//                          final float g0 = grid[e0];
//                          final float g1 = grid[e1];
//                          float t = g0 - g1;
//                          if(Math.abs(t) > 0.0F) {
//                             t = g0 / t;
//                             int j = 0;
//
//                             for(int k = 1; j < 3; k <<= 1) {
//                                final int a = e0 & k;
//                                final int b = e1 & k;
//                                if(a != b) {
//                                   v[j] += a != 0?1.0F - t:t;
//                                } else {
//                                   v[j] += a != 0?1.0F:0.0F;
//                                }
//
//                                ++j;
//                             }
//                          }
//                       }
//                    }
//
//                    final float s = 1.0F / (float)ecount;
//
//                    for(int i = 0; i < 3; ++i) {
//                       v[i] = (float)(c[i] + x[i]) + (s * v[i]);
//                    }
//
//                    final int tx = x[0] == 16?0:x[0];
//                    final int ty = x[1] == 16?0:x[1];
//                    final int tz = x[2] == 16?0:x[2];
//                    long i1 = (long)(tx * 3129871) ^ ((long)tz * 116129781L) ^ (long)ty;
//                    i1 = (i1 * i1 * 42317861L) + (i1 * 11L);
//                    v[0] = (float)((double)v[0] - (((double)((float)((i1 >> 16) & 15L) / 15.0F) - 0.5D) * 0.2D));
//                    v[1] = (float)((double)v[1] - (((double)((float)((i1 >> 20) & 15L) / 15.0F) - 1.0D) * 0.2D));
//                    v[2] = (float)((double)v[2] - (((double)((float)((i1 >> 24) & 15L) / 15.0F) - 0.5D) * 0.2D));
//                    buffer[m] = v;
//
//                    for(int i = 0; i < 3; ++i) {
//                       if((edgemask & (1 << i)) != 0) {
//                          final int iu = (i + 1) % 3;
//                          final int iv = (i + 2) % 3;
//                          if((x[iu] != 0) && (x[iv] != 0)) {
//                             final int du = r[iu];
//                             final int dv = r[iv];
//                             tess.setBrightness(block.getMixedBrightnessForBlock(Minecraft.getMinecraft().theWorld, br[0], br[1], br[2]));
//                             tess.setColorOpaque_I(block.colorMultiplier(cache, c[0] + x[0], c[1] + x[1], c[2] + x[2]));
//                             final float[] v0 = buffer[m];
//                             final float[] v1 = buffer[m - du];
//                             final float[] v2 = buffer[m - du - dv];
//                             final float[] v3 = buffer[m - dv];
//                             if((mask & 1) != 0) {
//                                tess.addVertexWithUV((double)v0[0], (double)v0[1], (double)v0[2], tu0, tv1);
//                                tess.addVertexWithUV((double)v1[0], (double)v1[1], (double)v1[2], tu1, tv1);
//                                tess.addVertexWithUV((double)v2[0], (double)v2[1], (double)v2[2], tu1, tv0);
//                                tess.addVertexWithUV((double)v3[0], (double)v3[1], (double)v3[2], tu0, tv0);
//                             } else {
//                                tess.addVertexWithUV((double)v0[0], (double)v0[1], (double)v0[2], tu0, tv1);
//                                tess.addVertexWithUV((double)v3[0], (double)v3[1], (double)v3[2], tu1, tv1);
//                                tess.addVertexWithUV((double)v2[0], (double)v2[1], (double)v2[2], tu1, tv0);
//                                tess.addVertexWithUV((double)v1[0], (double)v1[1], (double)v1[2], tu0, tv0);
//                             }
//                          }
//                       }
//                    }
//                 }
//
//                 ++x[0];
//              }
//
//              ++x[1];
//           }
//
//           ++x[2];
//           bufno ^= 1;
//        }
//
//        return true;
//     }
//  }

    }

}
