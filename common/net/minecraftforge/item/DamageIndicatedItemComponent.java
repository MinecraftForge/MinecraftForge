package net.minecraftforge.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * A component that has to be added for classes that want to implement the DamageIndicator behaviour.
 * 
 * Items can add this component (Composite design-pattern) to any item that needs to have a damage
 * indicator based on a custom value. Like for example the amount of energy left in an IC2 electrical
 * wrench, or the amount of MJ's left in a redstone energy cell from Thermal Expansion.
 * 
 * See {@link DamageIndicatedItemFluidContainer} for an example.
 * This could be for example an Item or an ItemFluidContainer.
 * 
 * @author rubensworks
 *
 */
public class DamageIndicatedItemComponent{
    
    /**
     * The item class on which the behaviour will be added.
     */
    public Item item;
    /**
     * The custom defined capacity this damage indicator must have.
     */
    public int capacity;

    /**
     * Create a new DamageIndicatedItemComponent
     * 
     * @param item
     *          The item class on which the behaviour will be added.
     * @param capacity
     *          The custom defined capacity this damage indicator must have.
     */
    public DamageIndicatedItemComponent(Item item, int capacity)
    {
        this.item = item;
        this.capacity = capacity;
        item.setMaxStackSize(1);
        item.setMaxDamage(102);
        item.setNoRepair();
    }
    
    /**
     * Updates the amount of the given stack with the given amount depending on the predefined item.
     * This method should be called whenever the contained amount of this container should be updated, 
     * together with the damage indicator.
     * 
     * @param itemStack
     *          The itemStack that will get an updated damage bar
     * @param amount
     *          The new amount this damage indicator must hold for the given itemStack.
     */
    public void updateAmount(ItemStack itemStack, int amount)
    {
        if(itemStack.getItem() == item && capacity > 0 && amount <= capacity)
        {
            item.setDamage(itemStack, 101 - ((amount * 100) / (capacity)));
        }
    }
    
}
