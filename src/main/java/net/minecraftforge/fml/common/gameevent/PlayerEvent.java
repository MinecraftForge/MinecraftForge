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

package net.minecraftforge.fml.common.gameevent;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

public class PlayerEvent extends Event {
    public final EntityPlayer player;
    private PlayerEvent(EntityPlayer player)
    {
        this.player = player;
    }

    public static class ItemPickupEvent extends PlayerEvent {
        @Deprecated
        public final EntityItem pickedUp;
        /**
        * Original EntityItem with current remaining stack size
        */
        private final EntityItem originalEntity;
        /**
         * Clone item stack, containing the item and amount picked up
         */
        private final ItemStack stack;
        public ItemPickupEvent(EntityPlayer player, EntityItem entPickedUp, ItemStack stack)
        {
            super(player);
            this.originalEntity = entPickedUp;
            this.pickedUp = entPickedUp;
            this.stack = stack;
        }

        public ItemStack getStack() {
            return stack;
        }

        public EntityItem getOriginalEntity() {
            return originalEntity;
        }
    }

    public static class ItemCraftedEvent extends PlayerEvent {
        @Nonnull
        public final ItemStack crafting;
        public final IInventory craftMatrix;
        public ItemCraftedEvent(EntityPlayer player, @Nonnull ItemStack crafting, IInventory craftMatrix)
        {
            super(player);
            this.crafting = crafting;
            this.craftMatrix = craftMatrix;
        }
    }
    public static class ItemSmeltedEvent extends PlayerEvent {
        @Nonnull
        public final ItemStack smelting;
        public ItemSmeltedEvent(EntityPlayer player, @Nonnull ItemStack crafting)
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
        private final boolean endConquered;

        public PlayerRespawnEvent(EntityPlayer player, boolean endConquered)
        {
            super(player);
            this.endConquered = endConquered;
        }

        /**
         * Did this respawn event come from the player conquering the end?
         * @return if this respawn was because the player conquered the end
         */
        public boolean isEndConquered()
        {
            return this.endConquered;
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
