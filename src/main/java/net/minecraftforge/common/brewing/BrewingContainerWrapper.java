package net.minecraftforge.common.brewing;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Wraps a container representing a brewing stand
 */
public record BrewingContainerWrapper(ItemStack base, ItemStack reagent) implements Container
{
    @Override
    public void clearContent()
    {
    }

    @Override
    public int getContainerSize()
    {
        return 2;
    }

    @Override
    public boolean isEmpty()
    {
        return base().isEmpty() && reagent().isEmpty();
    }

    @Override
    public ItemStack getItem(final int slot)
    {
        return switch (slot) {
            case 0 -> base();
            case 1 -> reagent();
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public ItemStack removeItem(final int slot, final int amount)
    {
        return getItem(slot).split(amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(final int slot)
    {
        return getItem(slot);
    }

    @Override
    public void setItem(final int p_18944_, final ItemStack p_18945_)
    {
    }

    @Override
    public void setChanged()
    {
    }

    @Override
    public boolean stillValid(final Player p_18946_)
    {
        return true;
    }
}
