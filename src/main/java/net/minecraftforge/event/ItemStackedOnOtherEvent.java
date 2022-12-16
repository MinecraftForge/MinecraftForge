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

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
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
 *  This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 *  If the event is cancelled, the container's logic halts, the carried item and the slot will not be swapped, and handling is assumed to have been done by the mod.
 *  This also means that the two vanilla checks described above will not be called.
 */
@Cancelable
public class ItemStackedOnOtherEvent extends Event
{
    private final ItemStack carriedItem;
    private final ItemStack stackedOnItem;
    private final Slot slot;
    private final ClickAction action;
    private final Player player;
    private final SlotAccess carriedSlotAccess;

    @ApiStatus.Internal
    public ItemStackedOnOtherEvent(ItemStack carriedItem, ItemStack stackedOnItem, Slot slot, ClickAction action, Player player, SlotAccess carriedSlotAccess)
    {
        this.carriedItem = carriedItem;
        this.stackedOnItem = stackedOnItem;
        this.slot = slot;
        this.action = action;
        this.player = player;
        this.carriedSlotAccess = carriedSlotAccess;
    }

    /**
     * {@return the stack being carried by the mouse} This may be empty!
     */
    public ItemStack getCarriedItem()
    {
        return carriedItem;
    }

    /**
     * {@return the stack currently in the slot being clicked on} This may be empty!
     */
    public ItemStack getStackedOnItem()
    {
        return stackedOnItem;
    }

    /**
     * {@return the slot being clicked on}
     */
    public Slot getSlot()
    {
        return slot;
    }

    /**
     * {@return the click action being used} By default {@linkplain ClickAction#PRIMARY} corresponds to left-click, and {@linkplain ClickAction#SECONDARY} is right-click.
     */
    public ClickAction getClickAction()
    {
        return action;
    }

    /**
     * {@return the player doing the item swap attempt}
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * {@return a fake slot allowing the listener to see and change what item is being carried}
     */
    public SlotAccess getCarriedSlotAccess()
    {
        return carriedSlotAccess;
    }
}
