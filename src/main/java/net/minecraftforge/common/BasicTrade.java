/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;

/**
 * A default, exposed implementation of ITrade.  All of the other implementations of ITrade (in VillagerTrades) are not public.
 * This class contains everything needed to make a MerchantOffer, the actual "trade" object shown in trading guis.
 */
public class BasicTrade implements ITrade
{

    protected final ItemStack price;
    protected final ItemStack price2;
    protected final ItemStack forSale;
    protected final int maxTrades;
    protected final int xp;
    protected final float priceMult;

    public BasicTrade(ItemStack price, ItemStack price2, ItemStack forSale, int maxTrades, int xp, float priceMult)
    {
        this.price = price;
        this.price2 = price2;
        this.forSale = forSale;
        this.maxTrades = maxTrades;
        this.xp = xp;
        this.priceMult = priceMult;
    }

    public BasicTrade(ItemStack price, ItemStack forSale, int maxTrades, int xp, float priceMult)
    {
        this(price, ItemStack.EMPTY, forSale, maxTrades, xp, priceMult);
    }

    public BasicTrade(int emeralds, ItemStack forSale, int maxTrades, int xp, float mult)
    {
        this(new ItemStack(Items.EMERALD, emeralds), forSale, maxTrades, xp, mult);
    }

    public BasicTrade(int emeralds, ItemStack forSale, int maxTrades, int xp)
    {
        this(new ItemStack(Items.EMERALD, emeralds), forSale, maxTrades, xp, 1);
    }

    @Override
    @Nullable
    public MerchantOffer getOffer(Entity merchant, Random rand)
    {
        return new MerchantOffer(price, price2, forSale, maxTrades, xp, priceMult);
    }

}