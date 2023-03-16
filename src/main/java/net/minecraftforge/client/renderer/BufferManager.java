/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.renderer;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.event.RegisterRenderTypesEvent;

/**
 * Manages vertex buffers for uploading geometry to the GPU for rendering.
 */
public final class BufferManager
{
    // Sub-managers used for static geometry phases
    private final StaticBufferManager registeredSolidBuffers;
    private final StaticBufferManager registeredTranslucentBuffers;
    private final StaticBufferManager registeredTripwireBuffers;
    // Buffers used for batching dynamic geometry (i.e. entities)
    private final DynamicBufferManager registeredDynamicBuffers;

    private final BufferBatch dynamicBufferBatch;
    private final ImmutableList<RenderType> chunkRenderTypes;

    public BufferManager()
    {
        registeredSolidBuffers = new StaticBufferManager(
            List.of(RenderType.solid(), RenderType.cutoutMipped(), RenderType.cutout()),
            RegisterRenderTypesEvent.Solid::new);

        registeredTranslucentBuffers = new StaticBufferManager(
            List.of(RenderType.translucent()),
            RegisterRenderTypesEvent.Translucent::new);

        registeredTripwireBuffers = new StaticBufferManager(
            List.of(RenderType.tripwire()),
            RegisterRenderTypesEvent.Tripwire::new);

        registeredDynamicBuffers = new DynamicBufferManager(
            rt -> BufferManager.getFixedBuffer(rt, registeredSolidBuffers, registeredTranslucentBuffers, registeredTripwireBuffers),
            Map.ofEntries(
                Map.entry(new ResourceLocation("solid"), new RenderTypeGroup(RenderType.solid(), ForgeRenderTypes.ITEM_LAYERED_SOLID.get())),
                Map.entry(new ResourceLocation("cutout"), new RenderTypeGroup(RenderType.cutout(), ForgeRenderTypes.ITEM_LAYERED_CUTOUT.get())),
                // Generally entity/item rendering shouldn't use mipmaps, so cutout_mipped has them off by default. To enforce them, use cutout_mipped_all.
                Map.entry(new ResourceLocation("cutout_mipped"), new RenderTypeGroup(RenderType.cutoutMipped(), ForgeRenderTypes.ITEM_LAYERED_CUTOUT.get())),
                Map.entry(new ResourceLocation("cutout_mipped_all"), new RenderTypeGroup(RenderType.cutoutMipped(), ForgeRenderTypes.ITEM_LAYERED_CUTOUT_MIPPED.get())),
                Map.entry(new ResourceLocation("translucent"), new RenderTypeGroup(RenderType.translucent(), ForgeRenderTypes.ITEM_LAYERED_TRANSLUCENT.get())),
                Map.entry(new ResourceLocation("tripwire"), new RenderTypeGroup(RenderType.tripwire(), ForgeRenderTypes.ITEM_LAYERED_TRANSLUCENT.get()))));
        
        dynamicBufferBatch = new BufferBatch(registeredDynamicBuffers);
        chunkRenderTypes = Stream.concat(Stream.concat(
            getSolidRenderTypes().stream(),
            getTranslucentRenderTypes().stream()),
            getTripwireRenderTypes().stream())
            .collect(ImmutableList.toImmutableList());
    }
    
    private static BufferBuilder getFixedBuffer(RenderType renderType, StaticBufferManager... managers)
    {
        for (final var manager : managers)
        {
            final var buffer = manager.getBuilder(renderType);
            if (buffer != null)
                return buffer;
        }
        
        return null;
    }
    
    /**
     * Returns the list of all registered chunk {@link RenderType} objects, in render order.
     * @return The list of registered static {@link RenderType render types} used to render chunk geometry.
     */
    public ImmutableList<RenderType> getChunkRenderTypes()
    {
        return chunkRenderTypes;
    }

    /**
     * Returns the list of registered solid {@link RenderType} objects, in render order.
     * @return The list of registered {@link RenderType render types} used to render solid geometry.
     */
    public ImmutableList<RenderType> getSolidRenderTypes()
    {
        return registeredSolidBuffers.getRenderTypes();
    }

    /**
     * Returns the list of registered translucent {@link RenderType} objects, in render order.
     * @return The list of registered {@link RenderType render types} used to render translucent geometry.
     */
    public ImmutableList<RenderType> getTranslucentRenderTypes()
    {
        return registeredTranslucentBuffers.getRenderTypes();
    }

    /**
     * Returns the list of registered tripwire {@link RenderType} objects, in render order.
     * @return The list of registered {@link RenderType render types} used to render tripwire geometry.
     */
    public ImmutableList<RenderType> getTripwireRenderTypes()
    {
        return registeredTripwireBuffers.getRenderTypes();
    }

    /**
     * Gets or creates the buffer batch used for batching dynamic geometry to the GPU.
     * @return The {@link BufferBatch} to use when uploading batched geometry.
     */
    public BufferBatch getDynamicBufferBatch()
    {
        return dynamicBufferBatch;
    }
    
    /**
     * Returns the {@link RenderTypeGroup render type group} for the given named render type.
     * @param location The {@link ResourceLocation} identifying the render type to return.
     * @return The {@link RenderTypeGroup} or {@link RenderTypeGroup.EMPTY} if no render type with the given name was found.
     */
    public RenderTypeGroup getNamedRenderType(ResourceLocation location)
    {
        return registeredDynamicBuffers.getGroup(location);
    }

    /**
     * Returns a compatability shim to enable using this with older code which uses {@link RenderBuffers}.
     * @return a {@link RenderBuffers} for compatibility with older code.
     */
    public RenderBuffers asRenderBuffers()
    {
        return new RenderBuffersShim();
    }

    private final class RenderBuffersShim extends RenderBuffers
    {
        private final OutlineBufferSource outlineBufferSource;
        private final ChunkBufferBuilderPack fixedBufferPack;

        protected RenderBuffersShim()
        {
            outlineBufferSource = new OutlineBufferSource(
                BufferManager.this.getDynamicBufferBatch().asBufferSource());
            fixedBufferPack = BufferManager.this.new ChunkBufferBuilderShim();
        }

        @Override
        public ChunkBufferBuilderPack fixedBufferPack()
        {
            return fixedBufferPack;
        }

        @Override
        public MultiBufferSource.BufferSource bufferSource()
        {
            return BufferManager.this.getDynamicBufferBatch().asBufferSource();
        }

        @Override
        public OutlineBufferSource outlineBufferSource()
        {
            return outlineBufferSource;
        }
    }

    private final class ChunkBufferBuilderShim extends ChunkBufferBuilderPack
    {
        protected ChunkBufferBuilderShim()
        { }

        @Override
        public BufferBuilder builder(RenderType type)
        {
            return getBuilderFrom(type,
                BufferManager.this.registeredSolidBuffers,
                BufferManager.this.registeredTranslucentBuffers,
                BufferManager.this.registeredTripwireBuffers);
        }

        private static BufferBuilder getBuilderFrom(RenderType type, StaticBufferManager... managers)
        {
            for (final var manager : managers)
            {
                final var buffer = manager.getBuilder(type);
                if (buffer != null)
                {
                    return buffer;
                }
            }

            return null;
        }

        @Override
        public void clearAll()
        {
            performIn(BufferBuilder::clear,
                BufferManager.this.registeredSolidBuffers,
                BufferManager.this.registeredTranslucentBuffers,
                BufferManager.this.registeredTripwireBuffers);
        }

        @Override
        public void discardAll() {
            performIn(BufferBuilder::discard,
                BufferManager.this.registeredSolidBuffers,
                BufferManager.this.registeredTranslucentBuffers,
                BufferManager.this.registeredTripwireBuffers);
        }

        private static void performIn(Consumer<BufferBuilder> operation, StaticBufferManager... managers)
        {
            for (final var manager : managers)
            {
                manager.getBuffers().forEach(operation);
            }
        }
    }
}
