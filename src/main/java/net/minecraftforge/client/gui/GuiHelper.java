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

package net.minecraftforge.client.gui;

import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.crafting.GrindingRecipe;

/**
 * A Class containing functions to help with gui's
 */
public class GuiHelper
{

    /**
     * Adds Grinding recipes to the grinder. Takes the container, the IWorldPosCallable, the input and output inventory.
     */
    public static void grindingHelper(GrindstoneContainer container, IWorldPosCallable access, IInventory input, IInventory output)
    {
        if (input.getItem(0).isEmpty() && input.getItem(1).isEmpty())
        {
            return;
        }
        access.execute((level, pos) -> {
            Optional<GrindingRecipe> optional = level.getRecipeManager().getRecipeFor(ForgeMod.GRINDING, input, level);
            if (optional.isPresent()) {
                GrindingRecipe grindingRecipe = optional.get();
                ItemStack result = grindingRecipe.assemble(input);
                output.setItem(0, result);
                container.broadcastChanges();
            }
        });
    }
}
