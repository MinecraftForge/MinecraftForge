/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.event;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * 
 * AnvilUpdateEvent is fired when a player places items in both the left and right slots of a anvil.
 * If the event is canceled, vanilla behavior will not run, and the output will be set to null.
 * If the event is not canceled, but the output is not null, it will set the output and not run vanilla behavior.
 * if the output is null, and the event is not canceled, vanilla behavior will execute.
 */
@Cancelable
public class AnvilUpdateEvent extends Event
{
    @Nonnull
    private final ItemStack left;  // The left side of the input
    @Nonnull
    private final ItemStack right; // The right side of the input
    private final String name;     // The name to set the item, if the user specified one.
    @Nonnull
    private ItemStack output;      // Set this to set the output stack
    private int cost;              // The base cost, set this to change it if output != null
    private int materialCost; // The number of items from the right slot to be consumed during the repair. Leave as 0 to consume the entire stack.

    public AnvilUpdateEvent(@Nonnull ItemStack left, @Nonnull ItemStack right, String name, int cost)
    {
        this.left = left;
        this.right = right;
        this.output = ItemStack.EMPTY;
        this.name = name;
        this.setCost(cost);
        this.setMaterialCost(0);
    }

    @Nonnull
    public ItemStack getLeft() { return left; }
    @Nonnull
    public ItemStack getRight() { return right; }
    public String getName() { return name; }
    @Nonnull
    public ItemStack getOutput() { return output; }
    public void setOutput(@Nonnull ItemStack output) { this.output = output; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public int getMaterialCost() { return materialCost; }
    public void setMaterialCost(int materialCost) { this.materialCost = materialCost; }
}
