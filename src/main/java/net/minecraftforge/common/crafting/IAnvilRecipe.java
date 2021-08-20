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

package net.minecraftforge.common.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;

public interface IAnvilRecipe extends Recipe<IAnvilRecipe.ContainerWrapper>
{
    @Override
    default boolean canCraftInDimensions(final int p_43999_, final int p_44000_)
    {
        return true;
    }

    @Override
    default ItemStack getResultItem()
    {
        return ItemStack.EMPTY;
    }

    @Override
    default ResourceLocation getId()
    {
        return id();
    }

    ResourceLocation id();

    @Override
    default RecipeType<?> getType()
    {
        return ForgeMod.ANVIL;
    }

    record AnvilResult(ItemStack result, int xpCost, int baseItemCountCost, int repairItemCountCost)
    {
        public static AnvilResult EMPTY = new AnvilResult(ItemStack.EMPTY, 0, 0, 0);
    }

    class ContainerWrapper implements Container
    {
        private final Container inputSlots;
        private final Container resultSlots;
        private final Player player;
        private final AnvilMenu menu;
        private final int[] itemCost;
        private String itemName;
        private int xpCost;

        public ContainerWrapper(AnvilMenu menu, Container inputSlots, Container resultSlots, Player player, String itemName)
        {
            this.menu = menu;
            this.inputSlots = inputSlots;
            this.resultSlots = resultSlots;
            this.player = player;
            this.itemName = itemName;
            this.itemCost = new int[inputSlots.getContainerSize()];
        }

        public void setItemName(String itemName)
        {
            this.itemName = itemName;
        }

        public String getItemName()
        {
            return itemName;
        }

        public void setXpCost(int cost)
        {
            this.xpCost = cost;
        }

        public int getXpCost()
        {
            return xpCost;
        }

        public void setItemCost(int slot, int cost)
        {
            this.itemCost[slot] = cost;
        }

        public int getItemCost(int slot)
        {
            return this.itemCost[slot];
        }

        @Override
        public void clearContent()
        {
        }

        @Override
        public int getContainerSize()
        {
            return inputSlots.getContainerSize();
        }

        @Override
        public boolean isEmpty()
        {
            return inputSlots.isEmpty();
        }

        @Override
        public ItemStack getItem(final int p_18941_)
        {
            return inputSlots.getItem(p_18941_);
        }

        @Override
        public ItemStack removeItem(final int p_18942_, final int p_18943_)
        {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(final int p_18951_)
        {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(final int p_18944_, final ItemStack p_18945_)
        {
        }

        @Override
        public void setChanged()
        {
        }

        @Override
        public boolean stillValid(final Player p_18946_)
        {
            return inputSlots.stillValid(p_18946_);
        }

        public Container getResultSlots()
        {
            return resultSlots;
        }

        @Nullable
        public Player getPlayer()
        {
            return player;
        }

        public AnvilMenu getMenu()
        {
            return menu;
        }
    }
}
