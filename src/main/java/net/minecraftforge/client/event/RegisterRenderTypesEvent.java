/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.jetbrains.annotations.ApiStatus;

/**
 * Allows users to register custom {@link RenderType render types}.
 * 
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public abstract class RegisterRenderTypesEvent extends Event implements IModBusEvent
{
    /**
     * Allows users to register custom named {@link RenderType render types} for rendering dynamic geometry.
     * 
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p> 
     */
    public static final class Named extends RegisterRenderTypesEvent
    {
        private final Map<ResourceLocation, RenderTypeGroup> renderTypes;
        
        @ApiStatus.Internal
        public Named(Map<ResourceLocation, RenderTypeGroup> renderTypes)
        {
            this.renderTypes = renderTypes;
        }
        
        /**
         * Registers a named {@link RenderTypeGroup}.
         *
         * @param name             The name
         * @param blockRenderType  One of the values returned by {@link RenderType#chunkBufferLayers()}
         * @param entityRenderType A {@link RenderType} using {@link DefaultVertexFormat#NEW_ENTITY}
         */
        public void register(String name, RenderType blockRenderType, RenderType entityRenderType)
        {
            register(name, blockRenderType, entityRenderType, entityRenderType);
        }

        /**
         * Registers a named {@link RenderTypeGroup}.
         *
         * @param name                     The name
         * @param blockRenderType          One of the values returned by {@link RenderType#chunkBufferLayers()}
         * @param entityRenderType         A {@link RenderType} using {@link DefaultVertexFormat#NEW_ENTITY}
         * @param fabulousEntityRenderType A {@link RenderType} using {@link DefaultVertexFormat#NEW_ENTITY} for use when
         *                                 "fabulous" rendering is enabled
         */
        public void register(String name, RenderType blockRenderType, RenderType entityRenderType, RenderType fabulousEntityRenderType)
        {
            var key = new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name);
            Preconditions.checkArgument(!renderTypes.containsKey(key), "Render type already registered: " + key);
            Preconditions.checkArgument(blockRenderType.format() == DefaultVertexFormat.BLOCK, "The block render type must use the BLOCK vertex format.");
            //Preconditions.checkArgument(blockRenderType.getChunkLayerId() >= 0, "Only chunk render types can be used for block rendering. Query RenderType#chunkBufferLayers() for a list.");
            Preconditions.checkArgument(entityRenderType.format() == DefaultVertexFormat.NEW_ENTITY, "The entity render type must use the NEW_ENTITY vertex format.");
            Preconditions.checkArgument(fabulousEntityRenderType.format() == DefaultVertexFormat.NEW_ENTITY, "The fabulous entity render type must use the NEW_ENTITY vertex format.");
            renderTypes.put(key, new RenderTypeGroup(blockRenderType, entityRenderType, fabulousEntityRenderType));
        }
    }
    
    public static abstract class Static extends RegisterRenderTypesEvent
    {
        private final List<RenderType> renderTypes;
        
        @ApiStatus.Internal
        protected Static(List<RenderType> renderTypes)
        {
            this.renderTypes = renderTypes;
        }
        
        /**
         * Registers the given {@link RenderType} to be rendered.
         * 
         * @param after The {@link RenderType} to render after.
         * @param value The {@link RenderType} to register.
         */
        public void register(RenderType after, RenderType value)
        {
            Objects.requireNonNull(after);
            Objects.requireNonNull(value);
            Preconditions.checkArgument(renderTypes.contains(after), "Render type to register after is not registered");
            Preconditions.checkArgument(!renderTypes.contains(value), "Render type already registered.");
            Preconditions.checkArgument(after.format() == DefaultVertexFormat.BLOCK, "The render type to register after must use the BLOCK vertex format.");
            Preconditions.checkArgument(value.format() == DefaultVertexFormat.BLOCK, "The render type to register must use the BLOCK vertex format.");

            final var index = renderTypes.indexOf(after);
            renderTypes.add(index + 1, value);
        }
    }
    
    /**
     * Allows users to register custom {@link RenderType render types} for rendering solid chunk geometry.
     * 
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p> 
     */
    public static final class Solid extends Static
    {
        @ApiStatus.Internal
        public Solid(List<RenderType> renderTypes)
        {
            super(renderTypes);
        }
    }
    
    /**
     * Allows users to register custom {@link RenderType render types} for rendering translucent chunk geometry.
     * 
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p> 
     */
    public static final class Translucent extends Static
    {
        @ApiStatus.Internal
        public Translucent(List<RenderType> renderTypes)
        {
            super(renderTypes);
        }
    }
    
    /**
     * Allows users to register custom {@link RenderType render types} for rendering tripwire chunk geometry.
     * 
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p> 
     */
    public static final class Tripwire extends Static
    {
        @ApiStatus.Internal
        public Tripwire(List<RenderType> renderTypes)
        {
            super(renderTypes);
        }
    }
}
