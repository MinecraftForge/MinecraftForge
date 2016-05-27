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
    private final ItemStack left;  // The left side of the input
    private final ItemStack right; // The right side of the input
    private final String name;     // The name to set the item, if the user specified one.
    private ItemStack output;      // Set this to set the output stack
    private int cost;              // The base cost, set this to change it if output != null
    private int materialCost; // The number of items from the right slot to be consumed during the repair. Leave as 0 to consume the entire stack.

    public AnvilUpdateEvent(ItemStack left, ItemStack right, String name, int cost)
    {
        this.left = left;
        this.right = right;
        this.name = name;
        this.setCost(cost);
        this.setMaterialCost(0);
    }

    public ItemStack getLeft() { return left; }
    public ItemStack getRight() { return right; }
    public String getName() { return name; }
    public ItemStack getOutput() { return output; }
    public void setOutput(ItemStack output) { this.output = output; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public int getMaterialCost() { return materialCost; }
    public void setMaterialCost(int materialCost) { this.materialCost = materialCost; }
}
