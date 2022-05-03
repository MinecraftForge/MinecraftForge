/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;

/**
 * This interface is to be implemented on Container objects.
 * For GUIs with recipe books, this allows their containers to have
 * recipe completion and ghost recipes in their craft matrices.
 */
public interface IRecipeContainer
{
    /**
     * The crafting result slot of your container, where you take out the crafted item.
     * The equivalent for {@link ContainerWorkbench} is {@link ContainerWorkbench#craftResult}.
     * The equivalent for {@link ContainerPlayer} is {@link ContainerPlayer#craftResult}.
     */
    CraftResultInventory getCraftResult();

    /**
     * The crafting matrix of your container, where ingredients go for crafting.
     * The equivalent for {@link ContainerWorkbench} is {@link ContainerWorkbench#craftMatrix}.
     * The equivalent for {@link ContainerPlayer} is {@link ContainerPlayer#craftMatrix}.
     */
    CraftingInventory getCraftMatrix();
}
