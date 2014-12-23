package net.minecraftforge.client;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;

public interface IWorldRenderer {
    /*
     * Called from main thread right before corresponding vanilla block layer is rendered.
     */
    void onPreRenderLayer(RenderGlobal renderer, List<ContainerLocalRenderInformation> renderInfos, ChunkRenderContainer chunkContainer, EnumWorldBlockLayer layer, double partialTicks, int pass, Entity entity);

    /*
     * Called from main thread right after corresponding vanilla block layer is rendered.
     */
    void onPostRenderLayer(RenderGlobal renderer, List<ContainerLocalRenderInformation> renderInfos, ChunkRenderContainer chunkContainer, EnumWorldBlockLayer layer, double partialTicks, int pass, Entity entity);

    /*
     * Called before the blocks in the specified chunk (and layer) are re-rendered.
     * Might be called from render update thread.
     */
    void onPreRebuildChunk(RenderChunk renderChunk, EnumWorldBlockLayer layer, BlockPos pos);

    /*
     * Called when the specified block needs render update.
     * Might be called from render update thread.
     */
    boolean onBlockRender(RenderChunk renderChunk, IBlockState state, IBlockAccess world, BlockPos pos);

    /*
     * Called after the blocks in the specified chunk (and layer) are re-rendered.
     * Might be called from render update thread.
     */
    void onPostRebuildChunk(RenderChunk renderChunk, EnumWorldBlockLayer layer, BlockPos pos);
}
