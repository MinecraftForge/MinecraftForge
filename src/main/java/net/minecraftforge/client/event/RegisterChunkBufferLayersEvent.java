/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.List;
import java.util.Objects;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.jetbrains.annotations.ApiStatus;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

/**
 * Allows users to register custom chunk buffer layers.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class RegisterChunkBufferLayersEvent extends Event implements IModBusEvent
{
    private final List<RenderType> solidRenderTypes;
    private final List<RenderType> translucentRenderTypes;
    private final List<RenderType> tripwireRenderTypes;

    @ApiStatus.Internal
    public RegisterChunkBufferLayersEvent(List<RenderType> solidRenderTypes, List<RenderType> translucentRenderTypes, List<RenderType> tripwireRenderTypes)
    {
        this.solidRenderTypes = solidRenderTypes;
        this.translucentRenderTypes = translucentRenderTypes;
        this.tripwireRenderTypes = tripwireRenderTypes;
    }

    /**
     * Registers a {@link RenderType} as a solid chunk buffer layer.
     *
     * @param after The existing solid render type to register after.
     * @param value The render type to register as a new solid chunk layer.
     */
    public void registerSolid(RenderType after, RenderType value)
    {
        registerToList(solidRenderTypes, after, value);
    }

    /**
     * Registers a {@link RenderType} as a translucent chunk buffer layer.
     *
     * @param after The existing translucent render type to register after.
     * @param value The render type to register as a new translucent chunk layer.
     */
    public void registerTranslucent(RenderType after, RenderType value)
    {
        registerToList(translucentRenderTypes, after, value);
    }

    /**
     * Registers a {@link RenderType} as a tripwire chunk buffer layer.
     *
     * @param after The existing tripwire render type to register after.
     * @param value The render type to register as a new tripwire chunk layer.
     */
    public void registerTripwire(RenderType after, RenderType value)
    {
        registerToList(tripwireRenderTypes, after, value);
    }

    private void registerToList(List<RenderType> list, RenderType after, RenderType value)
    {
        Objects.requireNonNull(after);
        Objects.requireNonNull(value);
        Preconditions.checkArgument(list.contains(after), "Render type to register after is not registered");
        Preconditions.checkArgument(!list.contains(value), "Render type already registered.");
        Preconditions.checkArgument(after.format() == DefaultVertexFormat.BLOCK, "The render type to register after must use the BLOCK vertex format.");
        Preconditions.checkArgument(value.format() == DefaultVertexFormat.BLOCK, "The render type to register must use the BLOCK vertex format.");

        final var index = list.indexOf(after);
        list.add(index + 1, value);
    }
}
