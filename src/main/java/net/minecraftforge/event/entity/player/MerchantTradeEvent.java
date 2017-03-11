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

package net.minecraftforge.event.entity.player;

import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.SlotMerchantResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.IMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * MerchantTradeEvent is fired whenever an event involving merchant trades occurs. <br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #merchant} contains the merchant with whom to barter.<br>
 * {@link #trade} contains the recipe being traded.<br>
 * {@link #left} contains the itemstack in the left side.<br>
 * {@link #right} contains the itemstack in the right side.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class MerchantTradeEvent extends PlayerEvent
{
    private final IMerchant merchant;
    private final MerchantRecipe trade;
    private final ItemStack left;
    private final ItemStack right;

    public MerchantTradeEvent(@Nonnull IMerchant merchant, @Nonnull MerchantRecipe trade, @Nonnull ItemStack left, @Nonnull ItemStack right)
    {
        super(merchant.getCustomer());
        this.merchant = merchant;
        this.trade = trade;
        this.left = left;
        this.right = right;
    }

    @Nonnull
    public IMerchant getMerchant()
    {
        return this.merchant;
    }

    @Nonnull
    public MerchantRecipe getTrade()
    {
        return this.trade;
    }

    @Nonnull
    public ItemStack getLeft()
    {
        return this.left.copy();
    }

    @Nonnull
    public ItemStack getRight()
    {
        return this.right.copy();
    }


    /**
     * MerchantTradeEvent.SetupOffer is fired before the result of trade appears.
     * <br>
     * The event is fired during the {@link InventoryMerchant#resetRecipeAndSlots()} method invocation.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If this event is canceled, the result of trade does not appear.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    @Cancelable
    public static class SetupOffer extends MerchantTradeEvent
    {
        public SetupOffer(@Nonnull IMerchant merchant, @Nonnull MerchantRecipe trade, @Nonnull ItemStack first, @Nonnull ItemStack second)
        {
            super(merchant, trade, first, second);
        }
    }

    /**
     * MerchantTradeEvent.Transact is fired after the player takes itemstack.
     * <br>
     * The event is fired during the {@link SlotMerchantResult#onTake(EntityPlayer, ItemStack)} method invocation.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If this event is canceled, the itemstack being about to be sold by the player is not shrunk.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    @Cancelable
    public static class Transact extends MerchantTradeEvent
    {
        private ItemStack changedLeft = null;
        private ItemStack changedRight = null;

        public Transact(@Nonnull IMerchant merchant, @Nonnull MerchantRecipe trade, @Nonnull ItemStack first, @Nonnull ItemStack second)
        {
            super(merchant, trade, first, second);
        }

        public void setLeft(@Nullable ItemStack left)
        {
            this.changedLeft = left;
        }

        public void setRight(@Nullable ItemStack right)
        {
            this.changedRight = right;
        }

        @Nullable
        public ItemStack getChangedLeft()
        {
            return this.changedLeft;
        }

        @Nullable
        public ItemStack getChangedRight()
        {
            return this.changedRight;
        }
    }
}
