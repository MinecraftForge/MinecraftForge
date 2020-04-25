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

package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class PlayerContainerEvent extends PlayerEvent
{
    private final Container container;
    public PlayerContainerEvent(PlayerEntity player, Container container)
    {
        super(player);
        this.container = container;
    }

    public static class Open extends PlayerContainerEvent
    {
        public Open(PlayerEntity player, Container container)
        {
            super(player, container);
        }
    }
    public static class Close extends PlayerContainerEvent
    {
        public Close(PlayerEntity player, Container container)
        {
            super(player, container);
        }
    }

    public Container getContainer()
    {
        return container;
    }
}
