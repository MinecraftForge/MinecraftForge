package net.minecraftforge.event.brewing;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnegative;

/**
 * A {@link BrewingStandFuelEvent} is fired when determining the fuel value for an item stack
 * in a {@link net.minecraft.tileentity.TileEntityBrewingStand brewing stand}. <br>
 * <br>
 * The fuel value is defined in {@link Item#getBrewingStandFuel(ItemStack)}.
 * <br>
 * This event is meant to allow mods overriding brewing stand fuel values of items from vanilla
 * or other mods.<br>
 * <br>
 * To set the brewing stand fuel value of your own item, override {@link Item#getBrewingStandFuel(ItemStack)}
 * instead.<br>
 * <br>
 * This event is fired from {@link ForgeEventFactory#getBrewingStandFuel(ItemStack)}.<br>
 * <br>
 * This event is {@link Cancelable} to prevent later handlers from changing the value.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class BrewingStandFuelEvent extends Event
{
    private final ItemStack stack;
    @Nonnegative
    private final int defaultValue;
    @Nonnegative
    private int currentValue;

    /**
     * The constructor.
     * <p>
     * <p>Users: call {@link ForgeEventFactory#getBrewingStandFuel(ItemStack)} to reduce extra code.
     *
     * @param stack the item stack to check
     */
    public BrewingStandFuelEvent(ItemStack stack)
    {
        this.stack = stack;
        this.defaultValue = stack.getItem().getBrewingStandFuel(stack);
        this.currentValue = defaultValue;
    }

    /**
     * Returns the item stack involved in this event.
     * <p>
     * <p>This is the original item stack instead of a copy. Modders should
     * not mutate this stack.
     *
     * @return the item stack
     */
    public ItemStack getItemStack()
    {
        return stack;
    }

    /**
     * Gets the brewing stand fuel value of the item stack in the event, as defined by
     * {@link Item#getBrewingStandFuel(ItemStack)}.
     * <p>
     * <p>This value is the default result. It is not modified by any event listener.
     *
     * @return the original brewing stand fuel value
     */
    @Nonnegative
    public int getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Gets the current fuel value of the item stack in the event.
     * <p>
     * <p>This value may have been modified by other event listeners. To access the
     * value without pollution from other event listeners, call {@link #getDefaultValue()}
     * instead.
     *
     * @return the current brewing stand fuel value
     */
    @Nonnegative
    public int getValue()
    {
        return currentValue;
    }

    /**
     * Sets the new fuel value of the item stack in the event.<br>
     * <br>
     * The value cannot be negative.<br>
     * <br>
     * A {@code 0} value indicates that the item stack is not a fuel.<br>
     * <br>
     * A positive value is the fuel value defined in {@link Item#getBrewingStandFuel(ItemStack)}.
     */
    public void setValue(@Nonnegative int fuel)
    {
        currentValue = fuel;
    }
}
