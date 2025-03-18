/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.client.event;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import org.jetbrains.annotations.ApiStatus;

/**
 * Fired after all vanilla frame passes are added into the pass list.
 * 
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.
 */
public class AddFramePassEvent extends Event 
{
    public final FrameGraphBuilder builder;
    public final LevelTargetBundle bundle;
    
    @ApiStatus.Internal
    public AddFramePassEvent(FrameGraphBuilder builder, LevelTargetBundle bundle) 
    {
        this.builder = builder;
        this.bundle = bundle;
    }
    /**
     * Adds a frame pass to the end of the pass list.
     * 
     * @param rl Resource location for frame pass name. Use RLs to avoid duplicate names.
     * @return Reference to pass to add modder rendering to.
     */
    public FramePass createPass(ResourceLocation rl) 
    {
        return builder.addPass(rl.toString());
    }
    /**
     * Adds a frame pass relative to a different pass.
     * 
     * @param rl Resource location for frame pass name. Use RLs to avoid duplicate names.
     * @param other Use vanilla name of framepass to order against. See {@linkplain LevelRenderer.java}.
     * @param order Self explanatory, BEFORE to come before, AFTER to come after.
     * @return Reference to pass to add modder rendering to.
     */
    public FramePass createPass(ResourceLocation rl, String other, Order order) 
    {
        return builder.addOrderedPass(rl, other, order.ordinal());
    }
    /**
     * Adds a frame pass relative to a different (possibly modded) pass. Beware of footguns.
     * 
     * @param rl Resource location for frame pass name. Use RLs to avoid duplicate names.
     * @param other Use mod's RL of framepass to order against.
     * @param order Self explanatory, BEFORE to come before, AFTER to come after.
     * @return Reference to pass to add modder rendering to.
     */
    public FramePass createPass(ResourceLocation rl, ResourceLocation other, Order order) 
    {
        return builder.addOrderedPass(rl, other.toString(), order.ordinal());
    }
    
    public static enum Order 
    {
        BEFORE,
        AFTER
    }
}