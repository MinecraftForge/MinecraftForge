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

package net.minecraftforge.client.event;

import java.util.HashSet;

import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Called when a {@link net.minecraft.client.renderer.chunk.RenderChunk#rebuildChunk RenderChunk.rebuildChunk} is called. This event is fired on
 * {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS} right after the CompiledChunk is generated and before any rebuilding is done. Canceling this event prevents all Blocks
 * and Tile Entities from being rebuilt to the chunk (and therefore rendered)
 */
@Cancelable
public class RebuildChunkEvent extends Event
{

    private final RenderGlobal context;
    private final ChunkCache worldView;
    private final ChunkCompileTaskGenerator generator;
    private final CompiledChunk compiledChunk;
    private final MutableBlockPos position;
    private final float x;
    private final float y;
    private final float z;

    public RebuildChunkEvent(final RenderGlobal renderGlobal, final ChunkCache worldView, final ChunkCompileTaskGenerator generator, final CompiledChunk compiledChunk, final MutableBlockPos position, final float x, final float y, final float z)
    {
        this.context = renderGlobal;
        this.worldView = worldView;
        this.generator = generator;
        this.compiledChunk = compiledChunk;
        this.position = position;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RenderGlobal getContext()
    {
        return this.context;
    }

    public ChunkCache getWorldView()
    {
        return this.worldView;
    }

    public ChunkCompileTaskGenerator getGenerator()
    {
        return this.generator;
    }

    public CompiledChunk getCompiledChunk()
    {
        return this.compiledChunk;
    }

    public MutableBlockPos getPosition()
    {
        return this.position;
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }

    public float getZ()
    {
        return this.z;
    }

    /**
     * Called when a {@link net.minecraft.client.renderer.chunk.RenderChunk#rebuildChunk RenderChunk.rebuildChunk} is called. This event is fired on
     * {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS} right before any rebuilding is done. Canceling this event prevents all Blocks from being rebuilt to the chunk (and
     * therefore rendered). TileEntities will still be rendered if this event is cancelled<br>
     *
     * To rebuild blocks using this event<br>
     * Itterate over all the positions in getChunkBlockPositions()<br>
     * for each block you want to render<br>
     * 1) get the Block's BlockRenderLayer<br>
     * 2) call {@link net.minecraftforge.client.ForgeHooksClient#setRenderLayer(BlockRenderLayer) net.minecraftforge.client.ForgeHooksClient#setRenderLayer(Block's
     * BlockRenderLayer)};<br>
     * 3) get a BufferBuilder instance with {@link RebuildChunkBlocksEvent#startOrContinueLayer(BlockRenderLayer) RebuildChunkBlocksEvent#startOrContinueLayer(Block's
     * BlockRenderLayer)};<br>
     * 4) put any data into the BufferBuilder<br>
     * 5) call {@link RebuildChunkBlocksEvent#setBlockRenderLayerUsed(BlockRenderLayer, boolean) RebuildChunkBlocksEvent#setBlockRenderLayerUsed(Block's BlockRenderLayer, boolean)}
     * with true if you want all the data in this BlockRenderLayer to be rendered or with false if you want none of the data in this BlockRenderLayer to be rendered
     *
     * @see net.minecraft.client.renderer.chunk.RenderChunk#rebuildChunk(float, float, float, ChunkCompileTaskGenerator)
     */
    @Cancelable
    public static class RebuildChunkBlocksEvent extends Event
    {

        private final RenderGlobal context;
        private final ChunkCache worldView;
        private final ChunkCompileTaskGenerator generator;
        private final CompiledChunk compiledChunk;
        private final Iterable<MutableBlockPos> chunkBlockPositions;
        private final BlockRendererDispatcher blockRendererDispatcher;
        private final MutableBlockPos position;
        private final float x;
        private final float y;
        private final float z;
        private final HashSet tileEntitiesWithGlobalRenderers;
        private final VisGraph visGraph;

        private final boolean[] usedBlockRenderLayers = new boolean[BlockRenderLayer.values().length];

        public RebuildChunkBlocksEvent(final RenderGlobal renderGlobal, final ChunkCache worldView, final ChunkCompileTaskGenerator generator, final CompiledChunk compiledChunk, final Iterable<MutableBlockPos> chunkBlockPositions, final BlockRendererDispatcher blockRendererDispatcher, final MutableBlockPos position, final float x, final float y, final float z, final HashSet tileEntitiesWithGlobalRenderers, final VisGraph visGraph)
        {
            this.context = renderGlobal;
            this.worldView = worldView;
            this.generator = generator;
            this.compiledChunk = compiledChunk;
            this.chunkBlockPositions = chunkBlockPositions;
            this.blockRendererDispatcher = blockRendererDispatcher;
            this.position = position;
            this.x = x;
            this.y = y;
            this.z = z;
            this.tileEntitiesWithGlobalRenderers = tileEntitiesWithGlobalRenderers;
            this.visGraph = visGraph;
        }

        public RenderGlobal getContext()
        {
            return this.context;
        }

        public ChunkCache getWorldView()
        {
            return this.worldView;
        }

        public ChunkCompileTaskGenerator getGenerator()
        {
            return this.generator;
        }

        public CompiledChunk getCompiledChunk()
        {
            return this.compiledChunk;
        }

        public Iterable<MutableBlockPos> getChunkBlockPositions()
        {
            return this.chunkBlockPositions;
        }

        public BlockRendererDispatcher getBlockRendererDispatcher()
        {
            return this.blockRendererDispatcher;
        }

        public MutableBlockPos getPosition()
        {
            return this.position;
        }

        public float getX()
        {
            return this.x;
        }

        public float getY()
        {
            return this.y;
        }

        public float getZ()
        {
            return this.z;
        }

        public HashSet getTileEntitiesWithGlobalRenderers()
        {
            return this.tileEntitiesWithGlobalRenderers;
        }

        public VisGraph getVisGraph()
        {
            return this.visGraph;
        }

        private BufferBuilder getBufferBuilder(final BlockRenderLayer blockRenderLayer)
        {
            return this.getGenerator().getRegionRenderCacheBuilder().getWorldRendererByLayer(blockRenderLayer);
        }

        /** only used BlockRenderLayers will be part of the rebuit chunk */
        public void setBlockRenderLayerUsed(final BlockRenderLayer blockRenderLayer, final boolean used)
        {
            this.usedBlockRenderLayers[blockRenderLayer.ordinal()] = used;
        }

        /** only used BlockRenderLayers will be part of the rebuit chunk */
        public void setBlockRenderLayerUsedWithOrOpperation(final BlockRenderLayer blockRenderLayer, final boolean used)
        {
            this.usedBlockRenderLayers[blockRenderLayer.ordinal()] |= used;
        }

        public BufferBuilder startOrContinueLayer(final BlockRenderLayer blockRenderLayer)
        {
            final BufferBuilder bufferbuilder = this.getBufferBuilder(blockRenderLayer);

            if (!this.getCompiledChunk().isLayerStarted(blockRenderLayer))
            {
                this.getCompiledChunk().setLayerStarted(blockRenderLayer);
                this.preRenderBlocks(bufferbuilder, this.getPosition());
            }

            return bufferbuilder;
        }

        private void preRenderBlocks(final BufferBuilder bufferBuilderIn, final BlockPos pos)
        {
            bufferBuilderIn.begin(7, DefaultVertexFormats.BLOCK);
            bufferBuilderIn.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
        }

        /**
         * if the boolean is true then the {@link BlockRenderLayer} will be rendered
         *
         * @return an array of booleans mapped to {@link BlockRenderLayer#ordinal()}
         */
        public boolean[] getUsedBlockRenderLayers()
        {
            return this.usedBlockRenderLayers;
        }

    }

}