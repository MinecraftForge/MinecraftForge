/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.CraftingContainer;

/**
 * This interface is to be implemented on Container objects.
 * For GUIs with recipe books, this allows their containers to have
 * recipe completion and ghost recipes in their craft matrices.
 */
public interface IRecipeContainer
{
    /**
     * The crafting result slot of your container, where you take out the crafted item.
     * The equivalent for {@link CraftingMenu} is {@code CraftingMenu#resultSlots}.
     * The equivalent for {@link InventoryMenu} is {@code InventoryMenu#resultSlots}.
     */
    ResultContainer getCraftResult();

    /**
     * The crafting matrix of your container, where ingredients go for crafting.
     * The equivalent for {@link CraftingMenu} is {@code CraftingMenu#craftSlots}.
     * The equivalent for {@link InventoryMenu} is {@code InventoryMenu#craftSlots}.
     */
    CraftingContainer getCraftMatrix();
}
