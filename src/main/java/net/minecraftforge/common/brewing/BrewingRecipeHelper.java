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

package net.minecraftforge.common.brewing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;

import java.util.Optional;

public class BrewingRecipeHelper
{
    /**
     * Used by the brewing stand to determine if its contents can be brewed.
     * Extra parameters exist to allow modders to create bigger brewing stands
     * without much hassle
     */
    public static boolean canBrew(Level level, Container container, int reagentSlot, int[] baseSlots)
    {
        final ItemStack reagent = container.getItem(reagentSlot);
        if (reagent.isEmpty()) return false;
        final RecipeManager recipeManager = level.getRecipeManager();
        for (int baseSlot : baseSlots)
        {
            ItemStack base = container.getItem(baseSlot);
            final IBrewingContainer wrapper = new IBrewingContainer.Impl(base, reagent);
            Optional<IBrewingRecipe> recipeFor = recipeManager.getRecipeFor(ForgeMod.BREWING.get(), wrapper, level);
            if (recipeFor.isPresent()) return true;
        }
        return false;
    }

    /**
     * Used by the brewing stand to brew its inventory Extra parameters exist to
     * allow modders to create bigger brewing stands without much hassle
     */
    public static void brewPotions(Level level, NonNullList<ItemStack> container, int reagentSlot, int[] baseSlots)
    {
        final ItemStack reagent = container.get(reagentSlot);
        final RecipeManager recipeManager = level.getRecipeManager();
        for (int baseSlot : baseSlots)
        {
            final IBrewingContainer wrapper = new IBrewingContainer.Impl(container.get(baseSlot), reagent);
            recipeManager.getRecipeFor(ForgeMod.BREWING.get(), wrapper, level)
                    .ifPresent(recipe -> container.set(baseSlot, recipe.assemble(wrapper)));
        }
    }

    /**
     * Returns true if the passed ItemStack is a valid reagent for any of the
     * recipes in the registry.
     */
    public static boolean isValidIngredient(Level level, ItemStack stack)
    {
        if (stack.isEmpty()) return false;

        for (IBrewingRecipe recipe : level.getRecipeManager().getAllRecipesFor(ForgeMod.BREWING.get()))
        {
            if (recipe.isReagent(stack))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the passed ItemStack is a valid base for any of the
     * recipes in the registry.
     */
    public static boolean isValidInput(Level level, ItemStack stack)
    {
        if (stack.getCount() != 1) return false;

        for (IBrewingRecipe recipe : level.getRecipeManager().getAllRecipesFor(ForgeMod.BREWING.get()))
        {
            if (recipe.isBase(stack))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Proxy method to reduce patch size in {@link net.minecraft.world.level.block.entity.BrewingStandBlockEntity}.
     */
    public static void doBrew(Level level, BlockPos pos, NonNullList<ItemStack> items)
    {
        if (ForgeEventFactory.onPotionAttemptBrew(items)) return;
        ItemStack itemstack = items.get(3);

        brewPotions(level, items, 3, new int[]{0, 1, 2});
        ForgeEventFactory.onPotionBrewed(items);
        if (itemstack.hasContainerItem())
        {
            ItemStack itemstack1 = itemstack.getContainerItem();
            itemstack.shrink(1);
            if (itemstack.isEmpty()) itemstack = itemstack1;
            else Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemstack1);
        }
        else itemstack.shrink(1);

        items.set(3, itemstack);
        level.levelEvent(1035, pos, 0);
    }

    /**
     * Proxy method for BrewingStandMenu.IngredientSlot#mayPlace
     */
    public static boolean mayPlaceIngredient(Container container, ItemStack stack)
    {
        if (container instanceof BrewingStandBlockEntity stand)
            return isValidIngredient(stand.getLevel(), stack);
        else
            return LogicalSidedProvider.CLIENTWORLD.<Optional<Level>>get(EffectiveSide.get()).map(level -> isValidIngredient(level, stack)).orElse(false);
    }

    /**
     * Proxy method for BrewingStandMenu.PotionSlot#mayPlaceItem
     */
    public static boolean mayPlacePotion(Container container, ItemStack stack)
    {
        if (container instanceof BrewingStandBlockEntity stand)
            return isValidInput(stand.getLevel(), stack);
        else
            return LogicalSidedProvider.CLIENTWORLD.<Optional<Level>>get(EffectiveSide.get()).map(level -> isValidInput(level, stack)).orElse(false);
    }
}
