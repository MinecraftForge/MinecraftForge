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

package net.minecraftforge.client.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.MovementInput;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * This event is fired after player movement inputs are updated.<br>
 * Handlers can freely manipulate {@link MovementInput} to cancel movement.<br>
 */
public class InputUpdateEvent extends PlayerEvent
{
    private final MovementInput movementInput;

    public InputUpdateEvent(PlayerEntity player, MovementInput movementInput)
    {
        super(player);
        this.movementInput = movementInput;
    }

    public MovementInput getMovementInput()
    {
        return movementInput;
    }

}
