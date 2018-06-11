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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Called when a block's texture is going to be overlaid on the player's HUD. Cancel this event to prevent the overlay.
 */
@net.minecraftforge.eventbus.api.Cancelable
public class RenderBlockOverlayEvent extends net.minecraftforge.eventbus.api.Event
{

    public static enum OverlayType {
        FIRE, BLOCK, WATER
    }
    
    private final EntityPlayer player;
    private final float renderPartialTicks;
    private final OverlayType overlayType;
    private final IBlockState blockForOverlay;
    private final BlockPos blockPos;
    
    @Deprecated
    public RenderBlockOverlayEvent(EntityPlayer player, float renderPartialTicks, OverlayType type, Block block, int x, int y, int z)
    {
        this(player, renderPartialTicks, type, block.getDefaultState(), new BlockPos(x, y, z));
    }
    
    public RenderBlockOverlayEvent(EntityPlayer player, float renderPartialTicks, OverlayType type, IBlockState block, BlockPos blockPos)
    {
        this.player = player;
        this.renderPartialTicks = renderPartialTicks;
        this.overlayType = type;
        this.blockForOverlay = block;
        this.blockPos = blockPos;
        
    }

    /**
     * The player which the overlay will apply to
     */
    public EntityPlayer getPlayer() { return player; }
    public float getRenderPartialTicks() { return renderPartialTicks; }
    /**
     * The type of overlay to occur
     */
    public OverlayType getOverlayType() { return overlayType; }
    /**
     * If the overlay type is BLOCK, then this is the block which the overlay is getting it's icon from
     */
    public IBlockState getBlockForOverlay() { return blockForOverlay; }
    public BlockPos getBlockPos() { return blockPos; }
}
