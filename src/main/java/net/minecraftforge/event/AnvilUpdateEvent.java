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

package net.minecraftforge.event;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * 
 * This event is fired in {@link net.minecraft.inventory.container.RepairContainer#updateRepairOutput} as long as an item stack is present in the left slot of the anvil.
 * Maximum cost and material cost will always be set from this event, even when not canceled.
 * 
 * If the output stack remains empty and the event is not canceled, vanilla behavior will be executed.
 * If the output stack is changed it will be set from this event.
 * If the event is then canceled no vanilla behaviour will apply.
 * If the event is not canceled vanilla logic for creating the output will be skipped, but the rest of the vanilla method will execute as normal.
 */
@Cancelable
public class AnvilUpdateEvent extends Event
{
    @Nonnull
    private final ItemStack left;               // The left side of the input
    @Nonnull
    private final ItemStack right;              // The right side of the input, can be empty
    private final String name;                  // The name of the left item stack
    private final boolean creativePlayer        // Whether the player is in creative mode
    @Nonnull
    private ItemStack output;                   // Set this to set the output stack
    private int cost;                           // The cost for this repair operation
    private int materialCost;                   // The number of items from the right stack to be consumed during the repair, leaving as 0 will consume the entire stack

    public AnvilUpdateEvent(@Nonnull ItemStack left, @Nonnull ItemStack right, String name, boolean creativePlayer)
    {
        this.left = left;
        this.right = right;
        this.name = name;
        this.creativePlayer = creativePlayer;
        this.output = ItemStack.EMPTY;
        this.cost = 0;
        this.materialCost = 0;
    }

    @Nonnull
    public ItemStack getLeft() { return left; }
    @Nonnull
    public ItemStack getRight() { return right; }
    public String getName() { return name; }
    public boolean isCreativePlayer() { return creativePlayer; }
    @Nonnull
    public ItemStack getOutput() { return output; }
    public void setOutput(@Nonnull ItemStack output) { this.output = output; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public int getMaterialCost() { return materialCost; }
    public void setMaterialCost(int materialCost) { this.materialCost = materialCost; }
}
