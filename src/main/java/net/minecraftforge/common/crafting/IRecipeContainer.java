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
     * The equivalent for {@link ContainerWorkbench} is {@link ContainerWorkbench#craftResult}.
     * The equivalent for {@link ContainerPlayer} is {@link ContainerPlayer#craftResult}.
     */
    ResultContainer getCraftResult();

    /**
     * The crafting matrix of your container, where ingredients go for crafting.
     * The equivalent for {@link ContainerWorkbench} is {@link ContainerWorkbench#craftMatrix}.
     * The equivalent for {@link ContainerPlayer} is {@link ContainerPlayer#craftMatrix}.
     */
    CraftingContainer getCraftMatrix();
}
