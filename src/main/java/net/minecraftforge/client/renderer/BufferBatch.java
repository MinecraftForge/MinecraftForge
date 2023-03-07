package net.minecraftforge.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

/**
 * Manages batching buffers during rendering dynamic geometry.
 */
public final class BufferBatch implements MultiBufferSource
{
    private final DynamicBufferManager bufferManager;
    private final Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> startedTypes;

    public BufferBatch(DynamicBufferManager manager)
    {
        this.bufferManager = manager;
        this.startedTypes = new Object2ObjectLinkedOpenHashMap<>();
    }
    
    private RenderType lastRenderType()
    {
        return startedTypes.size() == 0
            ? null
            : startedTypes.lastKey();
    }

    /**
     * This method exists only to satisfy the interface of {@link MultiBufferSource}.
     * Use {@link #beginBatch(RenderType)} instead.
     * 
     * @see #beginBatch(RenderType)
     */
    @Override
    @Deprecated
    public VertexConsumer getBuffer(RenderType renderType)
    {
        return beginBatch(renderType);
    }

    /**
     * Begin a rendering batch for the given {@link RenderType render type}.
     * 
     * @param renderType The render type to begin rendering using.
     * @return A {@link BufferVertexConsumer} that can be used to build geometry. 
     */
    public BufferVertexConsumer beginBatch(RenderType renderType)
    {
        final var lastBatch = lastRenderType();
        if (lastBatch != null
            && bufferManager.get(lastBatch) == null
            && (lastBatch != renderType || !renderType.canConsolidateConsecutiveGeometry()))
        {
            // Only upload to the GPU if the last batch used the default builder, and one of:
            // - the last batch used a different render type
            // - the render type cannot consolidate geometry (this is usually the case)
            endBatch(lastBatch);
        }
        
        final var buffer = bufferManager.getOrDefault(renderType);
        if (startedTypes.putAndMoveToLast(renderType, buffer) == null)
        {
            // Set up the buffer for the given render type 
            buffer.begin(renderType.mode(), renderType.format());
        }

        return buffer;
    }

    private void endBatch(RenderType renderType)
    {
        final var buffer = startedTypes.remove(renderType);
        if (buffer != null)
        {
            renderType.end(buffer, 0, 0, 0);
        }
    }

    /**
     * Uploads all prepared geometry to the GPU, in the order each batch
     * was requested.
     */
    public void endAllBatches()
    {
        RenderType lastBatch = null;
        do
        {
            lastBatch = lastRenderType();
            endBatch(lastBatch);
        }
        while (lastBatch != null);
    }

    /**
     * Uploads the geometry of the last batch to the GPU and removes
     * it from the active set of batches.
     */
    public void endLastBatch()
    {
        final var lastBatch = lastRenderType();
        if (lastBatch != null)
        {
            endBatch(lastBatch);
        }
    }

    /**
     * Returns a compatability shim to enable using this with older code which uses {@link MultiBufferSource.BufferSource}.
     * @return a {@link MultiBufferSource.BufferSource} for compatibility with older code.
     */
    public MultiBufferSource.BufferSource asBufferSource()
    {
        return new BufferSourceShim();
    }

    private final class BufferSourceShim extends MultiBufferSource.BufferSource
    {
        protected BufferSourceShim()
        {
            super(null, null);
        }

        @Override
        public VertexConsumer getBuffer(RenderType type)
        {
            return BufferBatch.this.getBuffer(type);
        }

        @Override
        public void endLastBatch()
        {
            BufferBatch.this.endLastBatch();
        }

        @Override
        public void endBatch()
        {
            BufferBatch.this.endAllBatches();
        }

        @Override
        public void endBatch(RenderType type)
        {
            BufferBatch.this.endBatch(type);
        }
    }
}