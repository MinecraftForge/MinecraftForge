/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.event.village;

import javax.annotation.Nullable;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * MerchantTradeOffersEvent is fired when a list of villager trade offers is presented in
 * {@link IMerchant#getRecipes(EntityPlayer)}, allowing mods to modify trade offers depending
 * on the player. Be warned that this event is fired on both server and client; thus, modders
 * should ensure that they sync the needed data for this event themselves.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class MerchantTradeOffersEvent extends Event
{
    private final IMerchant merchant;
    private final EntityPlayer player;
    private @Nullable MerchantRecipeList list;
    
    public MerchantTradeOffersEvent(IMerchant merchant, EntityPlayer player, @Nullable MerchantRecipeList list)
    {
        this.merchant = merchant;
        this.player = player;
        this.list = list;
    }

    /**
     * The recipe list (if not {@code null}) returned from this function may be modified.
     * @return the recipe list
     */
    public @Nullable MerchantRecipeList getList()
    {
        return list;
    }

    public void setList(@Nullable MerchantRecipeList list)
    {
        this.list = list;
    }

    public IMerchant getMerchant()
    {
        return merchant;
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }

}
