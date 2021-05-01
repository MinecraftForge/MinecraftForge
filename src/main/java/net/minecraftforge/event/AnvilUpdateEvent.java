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

package net.minecraftforge.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * 
 * AnvilUpdateEvent is fired when the inputs (either input stack, or the name) to an anvil are changed. <br> 
 * It is called from {@link RepairContainer#updateRepairOutput}. <br>
 * If the event is canceled, vanilla behavior will not run, and the output will be set to {@link ItemStack#EMPTY}. <br>
 * If the event is not canceled, but the output is not empty, it will set the output and not run vanilla behavior. <br>
 * if the output is empty, and the event is not canceled, vanilla behavior will execute. <br>
 */
@Cancelable
public class AnvilUpdateEvent extends Event
{

    private final ItemStack left;
    private final ItemStack right;
    private final String name;
    private ItemStack output;
    private int cost;
    private int materialCost;
    @Nullable // TODO: Remove 1.17 - Nullable only in the instance that a mod uses the deprecated ctor.
    private final PlayerEntity player;

    @Deprecated //TODO: Remove 1.17 - Use Player-contextual constructor below.
    public AnvilUpdateEvent(ItemStack left, ItemStack right, String name, int cost)
    {
        this(left, right, name, cost, null);
    }

    public AnvilUpdateEvent(ItemStack left, ItemStack right, String name, int cost, PlayerEntity player)
    {
        this.left = left;
        this.right = right;
        this.output = ItemStack.EMPTY;
        this.name = name;
        this.player = player;
        this.setCost(cost);
        this.setMaterialCost(0);
    }

    /**
     * @return The item in the left input (leftmost) anvil slot.
     */
    public ItemStack getLeft()
    {
        return left;
    }

    /**
     * @return The item in the right input (center) anvil slot.
     */
    public ItemStack getRight()
    {
        return right;
    }

    /**
     * This is the name as sent by the client.  It may be null if none has been sent. <br>
     * If empty, it indicates the user wishes to clear the custom name from the item.
     * @return The name that the output item will be set to, if applicable.
     */
    @Nullable
    public String getName()
    {
        return name;
    }

    /**
     * This is the output as determined by the event, not by the vanilla behavior between these two items. <br>
     * If you are the first receiver of this event, it is guaranteed to be empty. <br>
     * It will only be non-empty if changed by an event handler. <br>
     * If this event is cancelled, this output stack is discarded.
     * @return The item to set in the output (rightmost) anvil slot.
     */
    public ItemStack getOutput() 
    {
        return output;
    }

    /**
     * Sets the output slot to a specific itemstack.
     * @param output The stack to change the output to.
     */
    public void setOutput(ItemStack output)
    {
        this.output = output;
    }

    /**
     * This is the level cost of this anvil operation. <br> 
     * When unchanged, it is guaranteed to be left.getRepairCost() + right.getRepairCost().
     * @return The level cost of this anvil operation.
     */
    public int getCost()
    {
        return cost;
    }

    /**
     * Changes the level cost of this operation. <br>
     * The level cost does prevent the output from being available.  <br>
     * That is, a player without enough experience may not take the output.
     * @param cost The new level cost.
     */
    public void setCost(int cost)
    {
        this.cost = cost;
    }

    /**
     * The material cost is how many units of the right input stack are consumed.
     * @return The material cost of this anvil operation.
     */
    public int getMaterialCost()
    {
        return materialCost;
    }

    /**
     * Sets how many right inputs are consumed. <br>
     * A material cost of zero consumes the entire stack. <br>
     * A material cost higher than the count of the right stack
     * consumes the entire stack. <br>
     * The material cost does not prevent the output from being available.
     * @param materialCost The new material cost.
     */
    public void setMaterialCost(int materialCost)
    {
        this.materialCost = materialCost;
    }

    /**
     * Nullable in the case someone is using the deprecated constructor.
     * @return The player using this anvil container.
     */
    @Nullable // TODO: Remove 1.17
    public PlayerEntity getPlayer()
    {
        return this.player;
    }
}
