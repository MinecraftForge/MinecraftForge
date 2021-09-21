/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * PlayerDamageItemEvent is fired when a player damage an item.<br>
 * This event is fired whenever a player damage an item in
 * {@link ItemStack#hurt(int, java.util.Random, ServerPlayer)}.<br>
 * <br>
 * {@link #player} contains the player which is damage the item. <br>
 * {@link #itemStack} contains the ItemStack which is damaged.<br>
 * {@link #durability} contains the durability of the ItemStack before it will damaged. <br>
 * {@link #newDurability} contains the durability of the ItemStack after it was damaged.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired from {@link ForgeEventFactory#onPlayerDamageItem(ServerPlayer, ItemStack, int, int)}.<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class PlayerDamageItemEvent extends Event
{
	@Nullable // copy vanilla logic
    private final ServerPlayer player;
    private final ItemStack itemStack;
    private final int durability;
    private final int newDurability;

    public PlayerDamageItemEvent(@Nullable ServerPlayer player, ItemStack itemStack, int durability, int newDurability)
    {
        this.player = player;
        this.itemStack = itemStack;
        this.durability = durability;
        this.newDurability = newDurability;
    }

    @Nullable
    public ServerPlayer getPlayer() 
    {
        return this.player;
    }

    public ItemStack getStack() 
    {
        return this.itemStack;
    }

    public int getDurability() 
    {
        return this.durability;
    }

    public int getNewDurability() 
    {
        return this.newDurability;
    } 
}
