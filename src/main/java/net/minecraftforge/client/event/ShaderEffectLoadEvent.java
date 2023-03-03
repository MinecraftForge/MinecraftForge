/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * ShaderEffectEvent is triggered when a new shader effect is trying to load
 * at {@link net.minecraft.client.renderer.GameRenderer#loadEffect(ResourceLocation)}
 *
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}, and
 * does not have a {@link net.minecraftforge.eventbus.api.Event.Result}
 */
public class ShaderEffectLoadEvent extends Event
{
    @Nullable
    private final ResourceLocation currentEffect;
    private ResourceLocation newShaderEffect;

    public ShaderEffectLoadEvent(@Nullable ResourceLocation currentEffect, ResourceLocation newShaderEffect)
    {
        this.currentEffect = currentEffect;
        this.newShaderEffect = newShaderEffect;
    }

    /**
     * Gets the name of the current active effect,
     * e.g. ResourceLocation("minecraft:shaders/post/spider.json")
     *
     * The current effect may be null if there's no
     * effect active
     *
     * @return Current active effect, or null if none
     */
    @Nullable
    public ResourceLocation getCurrentEffect()
    {
        return currentEffect;
    }

    /**
     * Gets the new effect that is trying to be loaded
     * e.g. ResourceLocation("minecraft:shaders/post/spider.json")
     *
     * @return The new loading effect
     */
    public ResourceLocation getNewShaderEffect()
    {
        return newShaderEffect;
    }

    /**
     * Sets the new effect that is going to be loaded
     *
     * If set to null, instead of unloading existing effect, no changes
     * will be made, i.e. old effect preserved & new effect not applied
     *
     * To unload the current effect without a new effect,
     * use {@link net.minecraft.client.renderer.GameRenderer#shutdownEffect} directly
     *
     * @param newShaderEffect The new effect to be loaded, null to cancel the load
     */
    public void setNewShaderEffect(@Nullable ResourceLocation newShaderEffect)
    {
        this.newShaderEffect = newShaderEffect;
    }
}
