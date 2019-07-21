package net.minecraftforge.event.grindstone;

import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

/**
 * Fired when a player places items in both the top and bottom slots of the grindstone.
 * <p>
 * The {@link Pre} event fires before most vanilla logic, while the {@link Post} event fires after
 * vanilla calculates the normal output. {@code Post} is the better choice for making simple
 * changes, while {@code Pre} allows different behavior, such as combining different types of
 * items.
 * <p>
 * Canceling the event will clear the output slot.
 * <p>
 * If {@code xp} is less than zero, vanilla logic is used to calculate XP.
 */
@Cancelable
public class GrindstoneUpdateEvent extends Event
{
    /**
     * Fired before most of the grindstone's checks. This does <em>not</em> provide the vanilla
     * output. If the output is left empty, vanilla logic will continue.
     */
    public static class Pre extends GrindstoneUpdateEvent
    {
        public Pre(@Nonnull ItemStack first, @Nonnull ItemStack second)
        {
            super(first, second, ItemStack.EMPTY);
        }
    }

    /**
     * Fired after all vanilla checks. This provides the vanilla output.
     */
    public static class Post extends GrindstoneUpdateEvent
    {
        public Post(@Nonnull ItemStack first, @Nonnull ItemStack second, @Nonnull ItemStack output)
        {
            super(first, second, output);
        }
    }

    @Nonnull private final ItemStack first;
    @Nonnull private final ItemStack second;
    @Nonnull private ItemStack output;
    private int xp = -1;

    public GrindstoneUpdateEvent(@Nonnull ItemStack first, @Nonnull ItemStack second, @Nonnull ItemStack output)
    {
        this.first = first;
        this.second = second;
        this.output = output;
    }

    /**
     * Gets the item in the top slot. In vanilla, most of the first item's NBT is copied to the
     * output item.
     */
    @Nonnull
    public ItemStack getFirst()
    {
        return first;
    }

    /**
     * Gets the item in the bottom slot.
     */
    @Nonnull
    public ItemStack getSecond()
    {
        return second;
    }

    @Nonnull
    public ItemStack getOutput()
    {
        return output;
    }

    public void setOutput(@Nonnull ItemStack output)
    {
        this.output = output;
    }

    public int getXp()
    {
        return xp;
    }

    public void setXp(int xp)
    {
        this.xp = xp;
    }
}
