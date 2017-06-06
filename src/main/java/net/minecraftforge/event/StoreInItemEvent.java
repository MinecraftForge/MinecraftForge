/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
package net.minecraftforge.event;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

/**
 * Event fired when an item is put into an inventory that can retain its contents as an item.
 * This includes shulker boxes as well as various backpacks.<br>
 * If the result of the event is set as {@link Event.Result#DENY} the item won't be allowed in the inventory.
 * The main use for this is to prevent NBT overflows caused by cascaded backpacks/shulker boxes/...<br>
 * Whether a specific item is allowed in a container must not change over time or with inventory content.<br>
 * <br>
 * {@link #input} the ItemStack that is to be placed in the inventory.<br>
 * {@link #container} A rough representation of the inventory the item is being placed into.
 * It is not guaranteed to contain the inventory content NBT data (see {@link #contents}) and may be a slight variation
 * of the actual container (e.g. a different color of shulker box).<br>
 * {@link #contents} The current contents of the inventory the item is being placed into.<br>
 */
@Event.HasResult
public class StoreInItemEvent extends Event
{
    @Nonnull
    private final ItemStack input;
    @Nonnull
    private final ItemStack container;
    @Nonnull
    private final NonNullList<ItemStack> contents;

    public StoreInItemEvent(@Nonnull ItemStack input, @Nonnull ItemStack container, @Nonnull NonNullList<ItemStack> contents)
    {
        this.input = input;
        this.container = container;
        this.contents = contents;
    }

    @Nonnull
    public ItemStack getInput()
    {
        return input;
    }

    @Nonnull
    public ItemStack getContainer()
    {
        return container;
    }

    @Nonnull
    public NonNullList<ItemStack> getContents()
    {
        return contents;
    }
}