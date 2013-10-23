package net.minecraftforge.event.anvil;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.HasResult;


@HasResult
public class AnvilRepairUpdateEvent extends Event
{
    /** The left hand input item in the anvil repair GUI. */
    public ItemStack inputStack1;
    /** The right hand input item in the anvil repair GUI. */
    public ItemStack inputStack2;
    /** The output item in the anvil repair GUI.
     *  If you update this*/
    public ItemStack outputStack;
    /** The maximum cost of the repair. */
    public int maximumCost;

    /** This event runs when the output item in the anvil is updated.
     *  Be sure to set result to allow if you are updating the item,
     *  or cancel the event if you want no output.
     */
    public AnvilRepairUpdateEvent(ItemStack inputStack1, ItemStack inputStack2, ItemStack outputStack, int defaultCost) {
    	this.inputStack1 = inputStack1;
        this.inputStack2 = inputStack2;

        this.outputStack = outputStack;
        this.maximumCost = defaultCost;
    }

}

