/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import javax.annotation.Nonnull;

/**
 * This event is called before the pushOutOfBlocks calls in EntityPlayerSP.
 *
 * Cancelling the event will prevent pushOutOfBlocks from being called.
 */
@Cancelable
public class PlayerSPPushOutOfBlocksEvent extends PlayerEvent
{
    private AxisAlignedBB entityBoundingBox;

    public PlayerSPPushOutOfBlocksEvent(EntityPlayer player, AxisAlignedBB entityBoundingBox)
    {
        super(player);
        this.entityBoundingBox = entityBoundingBox;
    }

    public AxisAlignedBB getEntityBoundingBox() { return entityBoundingBox; }
    public void setEntityBoundingBox(@Nonnull AxisAlignedBB entityBoundingBox) { this.entityBoundingBox = entityBoundingBox; }
}