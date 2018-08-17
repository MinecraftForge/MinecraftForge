/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import javax.annotation.Nonnull;

/**
 * This event is called when a player clicks on an item in a container.
 * The event can be canceled, and no further processing will be done.
 *
 * This event is fired in {@link net.minecraft.inventory.Slot#canTakeStack(EntityPlayer)} and {@link net.minecraftforge.items.SlotItemHandler#canTakeStack(EntityPlayer)} when a player clicks on an item.
 */
@Cancelable
@Event.HasResult
public class CanTakeItemEvent extends PlayerEvent
{
    private final ItemStack itemStack;
    private final Slot slot;
    private final IInventory inventory;
    private final boolean vanillaResult;

    public CanTakeItemEvent(@Nonnull ItemStack itemStack, Slot slot, IInventory inventory, EntityPlayer entityPlayer, boolean vanillaResult) {
        super(entityPlayer);
        this.itemStack = itemStack;
        this.slot = slot;
        this.inventory = inventory;
        this.vanillaResult = vanillaResult;
    }

    /**
     * The {@link ItemStack} being moved.
     */
    @Nonnull
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    /**
     * The {@link Slot} the item is in.
     */
    @Nonnull
    public Slot getSlot()
    {
        return slot;
    }

    /**
     * The {@link IInventory} the item is in.
     */
    @Nonnull
    public IInventory getInventory()
    {
        return inventory;
    }

    /**
     * If the item is allowed to be moved normally.
     */
    public boolean getVanillaResult()
    {
        return vanillaResult;
    }
}
