/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.client.event.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired before a block texture will be overlaid on the player's view.
 *
 * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
 * If this event is cancelled, then the overlay will not be rendered. </p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see ForgeEventFactory#renderBlockOverlay(Player, PoseStack, OverlayType, BlockState, BlockPos)
 */
@Cancelable
public class RenderBlockOverlayEvent extends Event
{
    /**
     * The type of the block overlay to be rendered.
     *
     * @see RenderBlockOverlayEvent
     */
    public enum OverlayType
    {
        /**
         * The type of the overlay when the player is burning / on fire.
         */
        FIRE,
        /**
         * The type of overlay when the player is suffocating inside a solid block.
         */
        BLOCK,
        /**
         * The type of overlay when the player is underwater.
         */
        WATER
    }
    
    private final Player player;
    private final PoseStack poseStack;
    private final OverlayType overlayType;
    private final BlockState blockForOverlay;
    private final BlockPos blockPos;
    
    public RenderBlockOverlayEvent(Player player, PoseStack poseStack, OverlayType type, BlockState block, BlockPos blockPos)
    {
        this.player = player;
        this.poseStack = poseStack;
        this.overlayType = type;
        this.blockForOverlay = block;
        this.blockPos = blockPos;
    }

    /**
     * {@return the player which the overlay will apply to}
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    /**
     * {@return the type of the overlay}
     */
    public OverlayType getOverlayType()
    {
        return overlayType;
    }

    /**
     * {@return the block which the overlay is gotten from}
     */
    public BlockState getBlockForOverlay()
    {
        return blockForOverlay;
    }

    /**
     * {@return the position of the block which the overlay is gotten from}
     */
    public BlockPos getBlockPos()
    {
        return blockPos;
    }
}
