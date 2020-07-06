/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
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
    
    private final PlayerEntity player;
    private final MatrixStack mat;
    private final OverlayType overlayType;
    private final BlockState blockForOverlay;
    private final BlockPos blockPos;
    
    public RenderBlockOverlayEvent(PlayerEntity player, MatrixStack mat, OverlayType type, BlockState block, BlockPos blockPos)
    {
        this.player = player;
        this.mat = mat;
        this.overlayType = type;
        this.blockForOverlay = block;
        this.blockPos = blockPos;
        
    }

    /**
     * The player which the overlay will apply to
     */
    public PlayerEntity getPlayer() { return player; }
    public MatrixStack getMatrixStack() { return mat; }
    /**
     * The type of overlay to occur
     */
    public OverlayType getOverlayType() { return overlayType; }
    /**
     * If the overlay type is BLOCK, then this is the block which the overlay is getting it's icon from
     */
    public BlockState getBlockForOverlay() { return blockForOverlay; }
    public BlockPos getBlockPos() { return blockPos; }
}
