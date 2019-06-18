/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

public class PlayerEvent extends Event {
    private final PlayerEntity player;
    private PlayerEvent(PlayerEntity player)
    {
        this.player = player;
    }

    public PlayerEntity getPlayer()
    {
        return this.player;
    }

    public static class ItemPickupEvent extends PlayerEvent {
        /**
        * Original EntityItem with current remaining stack size
        */
        private final ItemEntity originalEntity;
        /**
         * Clone item stack, containing the item and amount picked up
         */
        private final ItemStack stack;
        public ItemPickupEvent(PlayerEntity player, ItemEntity entPickedUp, ItemStack stack)
        {
            super(player);
            this.originalEntity = entPickedUp;
            this.stack = stack;
        }

        public ItemStack getStack() {
            return stack;
        }

        public ItemEntity getOriginalEntity() {
            return originalEntity;
        }
    }

    public static class ItemCraftedEvent extends PlayerEvent {
        @Nonnull
        private final ItemStack crafting;
        private final IInventory craftMatrix;
        public ItemCraftedEvent(PlayerEntity player, @Nonnull ItemStack crafting, IInventory craftMatrix)
        {
            super(player);
            this.crafting = crafting;
            this.craftMatrix = craftMatrix;
        }

        @Nonnull
        public ItemStack getCrafting()
        {
            return this.crafting;
        }

        public IInventory getInventory()
        {
            return this.craftMatrix;
        }
    }
    public static class ItemSmeltedEvent extends PlayerEvent {
        @Nonnull
        private final ItemStack smelting;
        public ItemSmeltedEvent(PlayerEntity player, @Nonnull ItemStack crafting)
        {
            super(player);
            this.smelting = crafting;
        }

        @Nonnull
        public ItemStack getSmelting()
        {
            return this.smelting;
        }
    }

    public static class PlayerLoggedInEvent extends PlayerEvent {
        public PlayerLoggedInEvent(PlayerEntity player)
        {
            super(player);
        }
    }

    public static class PlayerLoggedOutEvent extends PlayerEvent {
        public PlayerLoggedOutEvent(PlayerEntity player)
        {
            super(player);
        }
    }

    public static class PlayerRespawnEvent extends PlayerEvent {
        private final boolean endConquered;

        public PlayerRespawnEvent(PlayerEntity player, boolean endConquered)
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
        private final DimensionType fromDim;
        private final DimensionType toDim;
        public PlayerChangedDimensionEvent(PlayerEntity player, DimensionType fromDim, DimensionType toDim)
        {
            super(player);
            this.fromDim = fromDim;
            this.toDim = toDim;
        }

        public DimensionType getFrom()
        {
            return this.fromDim;
        }

        public DimensionType getTo()
        {
            return this.toDim;
        }
    }
}
