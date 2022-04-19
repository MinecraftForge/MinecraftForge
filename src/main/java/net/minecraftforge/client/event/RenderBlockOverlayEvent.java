/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Called when a block's texture is going to be overlaid on the player's HUD. Cancel this event to prevent the overlay.
 */
@Cancelable
public class RenderBlockOverlayEvent extends Event
{

    public static enum OverlayType {
        FIRE, BLOCK, WATER
    }
    
    private final Player player;
    private final PoseStack poseStack;
    private final OverlayType overlayType;
    private final BlockState blockState;
    private final BlockPos blockPos;
    
    public RenderBlockOverlayEvent(Player player, PoseStack poseStack, OverlayType type, BlockState block, BlockPos blockPos)
    {
        this.player = player;
        this.poseStack = poseStack;
        this.overlayType = type;
        this.blockState = block;
        this.blockPos = blockPos;
        
    }

    /**
     * The player which the overlay will apply to
     */
    public Player getPlayer() { return player; }
    public PoseStack getPoseStack() { return poseStack; }
    /**
     * The type of overlay to occur
     */
    public OverlayType getOverlayType() { return overlayType; }
    /**
     * If the overlay type is BLOCK, then this is the block which the overlay is getting it's icon from
     */
    public BlockState getBlockState() { return blockState; }
    public BlockPos getBlockPos() { return blockPos; }
}
