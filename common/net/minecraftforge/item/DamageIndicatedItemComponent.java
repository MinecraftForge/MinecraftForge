package net.minecraftforge.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * A component that has to be added for classes that want to implement the DamageIndicator behaviour.
 * See DamageIndicatedItemFluidContainer.java for an example.
 * This could be for example an Item or an ItemFluidContainer.
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
     * @param item The item class on which the behaviour will be added.
     * @param capacity The custom defined capacity this damage indicator must have.
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
     * @param itemStack The itemStack that will get an updated damage bar
     * @param amount The new amount this damage indicator must hold for the given itemStack.
     */
    public void updateAmount(ItemStack itemStack, int amount)
    {
        if(itemStack.getItem() == item && this.capacity > 0 && amount <= this.capacity)
        {
            item.setDamage(itemStack, 101 - ((amount * 100) / (this.capacity)));
        }
    }
    
}
