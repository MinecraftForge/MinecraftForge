/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.brewing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public sealed abstract class PotionBrewEvent extends MutableEvent implements InheritableEvent {
    public static final EventBus<PotionBrewEvent> BUS = EventBus.create(PotionBrewEvent.class);

    private final NonNullList<ItemStack> stacks;

    protected PotionBrewEvent(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public ItemStack getItem(int index) {
        if (index < 0 || index >= stacks.size()) return ItemStack.EMPTY;
        return stacks.get(index);
    }

    public void setItem(int index, ItemStack stack) {
        if (index < stacks.size()) {
            stacks.set(index, stack);
        }
    }

    public int getLength() {
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
     * This event is {@link Cancellable}.<br>
     * If the event is not cancelled, the vanilla brewing will take place instead of modded brewing.
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     * <br>
     * If this event is canceled, and items have been modified, PotionBrewEvent.Post will automatically be fired.
     **/
    public static final class Pre extends PotionBrewEvent implements Cancellable {
        public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);

        public Pre(NonNullList<ItemStack> stacks) {
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
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static final class Post extends PotionBrewEvent {
        public static final EventBus<Post> BUS = EventBus.create(Post.class);

        public Post(NonNullList<ItemStack> stacks) {
            super(stacks);
        }
    }
}
