package net.minecraftforge.common.brewing;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Wraps a container representing a brewing stand
 */
public record BrewingContainerWrapper(Container delegate,
                                      int ingredientSlot,
                                      int[] potionSlots) implements Container
{
    @Override
    public void clearContent()
    {
        delegate.clearContent();
    }

    @Override
    public int getContainerSize()
    {
        return delegate.getContainerSize();
    }

    @Override
    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    @Override
    public ItemStack getItem(final int p_18941_)
    {
        return delegate.getItem(p_18941_);
    }

    @Override
    public ItemStack removeItem(final int p_18942_, final int p_18943_)
    {
        return delegate.removeItem(p_18942_, p_18943_);
    }

    @Override
    public ItemStack removeItemNoUpdate(final int p_18951_)
    {
        return delegate.removeItemNoUpdate(p_18951_);
    }

    @Override
    public void setItem(final int p_18944_, final ItemStack p_18945_)
    {
        delegate.setItem(p_18944_, p_18945_);
    }

    @Override
    public void setChanged()
    {
        delegate.setChanged();
    }

    @Override
    public boolean stillValid(final Player p_18946_)
    {
        return delegate.stillValid(p_18946_);
    }
}
