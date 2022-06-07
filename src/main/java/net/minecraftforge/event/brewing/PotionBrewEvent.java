/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.brewing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

public class PotionBrewEvent extends Event
{
    private NonNullList<ItemStack> stacks;

    protected PotionBrewEvent(NonNullList<ItemStack> stacks)
    {
        this.stacks = stacks;
    }

    @NotNull
    public ItemStack getItem(int index)
    {
        if (index < 0 || index >= stacks.size()) return ItemStack.EMPTY;
        return stacks.get(index);
    }

    public void setItem(int index, @NotNull ItemStack stack)
    {
        if (index < stacks.size())
        {
            stacks.set(index, stack);
        }
    }

    public int getLength()
    {
        return stacks.size();
    }

    /**
     * PotionBrewEvent.Pre is fired before vanilla brewing takes place.
     * All changes made to the event's array will be made to the TileEntity if the event is canceled.
     * <br>
     * The event is fired during the {@code BrewingStandBlockEntity#doBrew(Level, BlockPos, NonNullList)} method invocation.<br>
     * <br>
     * {@link #stacks} contains the itemstack array from the TileEntityBrewer holding all items in Brewer.<br>
     * <br>
     * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * If the event is not canceled, the vanilla brewing will take place instead of modded brewing.
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     * <br>
     * If this event is canceled, and items have been modified, PotionBrewEvent.Post will automatically be fired.
     **/
    @Cancelable
    public static class Pre extends PotionBrewEvent
    {
        public Pre(NonNullList<ItemStack> stacks)
        {
            super(stacks);
        }
    }

    /**
     * PotionBrewEvent.Post is fired when a potion is brewed in the brewing stand.
     * <br>
     * The event is fired during the {@code BrewingStandBlockEntity#doBrew(Level, BlockPos, NonNullList)} method invocation.<br>
     * <br>
     * {@link #stacks} contains the itemstack array from the TileEntityBrewer holding all items in Brewer.<br>
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Post extends PotionBrewEvent
    {
        public Post(NonNullList<ItemStack> stacks)
        {
            super(stacks);
        }
    }
}
