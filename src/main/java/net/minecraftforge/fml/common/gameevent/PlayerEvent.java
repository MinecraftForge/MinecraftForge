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

package net.minecraftforge.fml.common.gameevent;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerEvent extends Event {
    public final EntityPlayer player;
    private PlayerEvent(EntityPlayer player)
    {
        this.player = player;
    }

    public static class ItemPickupEvent extends PlayerEvent {
        public final EntityItem pickedUp;
        public ItemPickupEvent(EntityPlayer player, EntityItem pickedUp)
        {
            super(player);
            this.pickedUp = pickedUp;
        }
    }

    public static class ItemCraftedEvent extends PlayerEvent {
        public final ItemStack crafting;
        public final IInventory craftMatrix;
        public ItemCraftedEvent(EntityPlayer player, ItemStack crafting, IInventory craftMatrix)
        {
            super(player);
            this.crafting = crafting;
            this.craftMatrix = craftMatrix;
        }
    }
    public static class ItemSmeltedEvent extends PlayerEvent {
        public final ItemStack smelting;
        public ItemSmeltedEvent(EntityPlayer player, ItemStack crafting)
        {
            super(player);
            this.smelting = crafting;
        }
    }

    public static class PlayerLoggedInEvent extends PlayerEvent {
        public PlayerLoggedInEvent(EntityPlayer player)
        {
            super(player);
        }
    }

    public static class PlayerLoggedOutEvent extends PlayerEvent {
        public PlayerLoggedOutEvent(EntityPlayer player)
        {
            super(player);
        }
    }

    public static class PlayerRespawnEvent extends PlayerEvent {
        public PlayerRespawnEvent(EntityPlayer player)
        {
            super(player);
        }
    }

    public static class PlayerChangedDimensionEvent extends PlayerEvent {
        public final int fromDim;
        public final int toDim;
        public PlayerChangedDimensionEvent(EntityPlayer player, int fromDim, int toDim)
        {
            super(player);
            this.fromDim = fromDim;
            this.toDim = toDim;
        }
    }
}
