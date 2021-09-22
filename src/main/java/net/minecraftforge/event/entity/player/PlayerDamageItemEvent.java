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
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * PlayerDamageItemEvent is fired when a player damages an item.<br>
 * This event is fired whenever a player damages an item in
 * {@link ItemStack#hurt(int, java.util.Random, ServerPlayer)}.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If the event is canceled, it will prevent the item from being damaged.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class PlayerDamageItemEvent extends Event
{
	@Nullable
    private final ServerPlayer player;
    private final ItemStack itemStack;
    private final int damageValue;
    private final int newDamageValue;

    public PlayerDamageItemEvent(@Nullable ServerPlayer player, ItemStack itemStack, int damageValue, int newDamageValue)
    {
        this.player = player;
        this.itemStack = itemStack;
        this.damageValue = damageValue;
        this.newDamageValue = newDamageValue;
    }
    
    /**
     * @return the player which is damage the item
     **/
    @Nullable
    public ServerPlayer getPlayer() 
    {
        return this.player;
    }

    /**
     * @return the ItemStack which is damaged
     **/
    public ItemStack getStack() 
    {
        return this.itemStack;
    }

    /**
     * @return the damage value of the ItemStack before it will damaged
     **/
    public int getDamageValue() 
    {
        return this.damageValue;
    }

    /**
     * @return the damage value of the ItemStack after it was damaged
     **/
    public int getNewDamageValue() 
    {
        return this.newDamageValue;
    } 
}
