package net.minecraftforge.event.grindstone;

import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

/**
 * Fired when a player places items in both the top and bottom slots of the grindstone. Very similar
 * to AnvilUpdateEvent.
 * If the event is canceled, vanilla behavior will not run and the output slot will be cleared.
 * If the event is not canceled and the output is not empty, it will set the output and not run vanilla behavior.
 * If the event is not canceled and the output is empty, normal vanilla behavior will run.
 */
@Cancelable
public class GrindstoneUpdateEvent extends Event
{
    @Nonnull private final ItemStack first;
    @Nonnull private final ItemStack second;
    @Nonnull private ItemStack output;

    public GrindstoneUpdateEvent(@Nonnull ItemStack first, @Nonnull ItemStack second)
    {
        this.first = first;
        this.second = second;
        this.output = ItemStack.EMPTY;
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
}
