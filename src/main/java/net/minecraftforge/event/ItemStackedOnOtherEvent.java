/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;

import org.jetbrains.annotations.ApiStatus;

/**
 * This event provides the functionality of the pair of functions used for the Bundle, in one event:
 * <ul>
 *     <li>{@link Item#overrideOtherStackedOnMe(ItemStack, ItemStack, Slot, ClickAction, Player, SlotAccess)}</li>
 *     <li>{@link Item#overrideStackedOnOther(ItemStack, Slot, ClickAction, Player)}</li>
 * </ul>
 *
 *  This event is fired before either of the above are called, when a carried item is clicked on top of another in a GUI slot.
 *  This event (and items stacking on others in general) is fired on both {@linkplain LogicalSide sides}, but only on {@linkplain LogicalSide#CLIENT the client} in the creative menu.
 *  Practically, that means that listeners of this event should require the player to be in survival mode if using capabilities that are not synced.
 *  <p>
 *  This event is {@linkplain Cancellable cancellable}.
 *  If the event is cancelled, the container's logic halts, the carried item and the slot will not be swapped, and handling is assumed to have been done by the mod.
 *  This also means that the two vanilla checks described above will not be called.
 *
 * @param getCarriedItem The stack being carried by the mouse, which may be empty
 * @param getStackedOnItem The stack currently in the slot being clicked on, which may be empty
 * @param getSlot The slot being clicked on
 * @param getClickAction The click action being used. By default, {@linkplain ClickAction#PRIMARY} corresponds to left-click, and {@linkplain ClickAction#SECONDARY} is right-click.
 * @param getPlayer The player doing the item swap attempt
 * @param getCarriedSlotAccess A fake slot allowing the listener to see and change what item is being carried
 */
public record ItemStackedOnOtherEvent(
        ItemStack getCarriedItem,
        ItemStack getStackedOnItem,
        Slot getSlot,
        ClickAction getClickAction,
        Player getPlayer,
        SlotAccess getCarriedSlotAccess
) implements Cancellable, RecordEvent {
    public static final CancellableEventBus<ItemStackedOnOtherEvent> BUS = CancellableEventBus.create(ItemStackedOnOtherEvent.class);

    @ApiStatus.Internal
    public ItemStackedOnOtherEvent {}
}
