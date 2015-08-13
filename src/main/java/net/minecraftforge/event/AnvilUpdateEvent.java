package net.minecraftforge.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;

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
    public final ItemStack left;  // The left side of the input
    public final ItemStack right; // The right side of the input
    public final String name;     // The name to set the item, if the user specified one.
    public ItemStack output;      // Set this to set the output stack
    public int cost;              // The base cost, set this to change it if output != null
    public int materialCost; // The number of items from the right slot to be consumed during the repair. Leave as 0 to consume the entire stack.

    public AnvilUpdateEvent(ItemStack left, ItemStack right, String name, int cost)
    {
        this.left = left;
        this.right = right;
        this.name = name;
        this.cost = cost;
        this.materialCost = 0;
    }
}
