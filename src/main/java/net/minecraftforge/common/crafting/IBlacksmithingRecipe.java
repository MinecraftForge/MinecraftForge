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
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;

/**
 * Interface representing a recipe for the anvil
 */
public interface IBlacksmithingRecipe extends Recipe<IBlacksmithingRecipe.AnvilContainerWrapper>
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

    default boolean canCraft(Level level, Player player, AnvilContainerWrapper container)
    {
        return this.matches(container, level) && player.getAbilities().instabuild || player.experienceLevel >= getXpCost(container);
    }

    int getXpCost(AnvilContainerWrapper wrapper);

    default float getBreakChance(Player player, AnvilContainerWrapper wrapper, ItemStack result)
    {
        return 0.12F;
    }

    @Override
    default RecipeType<?> getType()
    {
        return ForgeMod.BLACKSMITHING;
    }

    int getBaseItemCost(Level level, AnvilContainerWrapper wrapper);

    int getAdditionalItemCost(Level level, AnvilContainerWrapper wrapper);

    record AnvilContainerWrapper(Container inputSlots, String itemName) implements Container
    {
        @Override
        public void clearContent()
        {
            throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
        }

        @Override
        public ItemStack removeItemNoUpdate(final int p_18951_)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setItem(final int p_18944_, final ItemStack p_18945_)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setChanged()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean stillValid(final Player p_18946_)
        {
            return inputSlots.stillValid(p_18946_);
        }
    }
}
