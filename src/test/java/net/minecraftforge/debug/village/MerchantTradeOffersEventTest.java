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

package net.minecraftforge.debug.village;

import java.util.ListIterator;

import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.MerchantTradeOffersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Tests {@link MerchantTradeOffersEvent}. When enabled, the item that the villager sells to
 * the player will be maxed out in stack size.
 */
//@Mod(modid = MerchantTradeOffersEventTest.MODID, name = MerchantTradeOffersEventTest.NAME, version = "0.0.0", acceptableRemoteVersions = "*")
public class MerchantTradeOffersEventTest
{
    public static final String MODID = "merchanttradeofferseventtest";
    public static final String NAME = "Merchant Trade Offers Event Test";
    public static final boolean ENABLED = false;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(MerchantTradeOffersEventTest.class);
        }
    }
    
    @SubscribeEvent
    public static void onGetRecipes(MerchantTradeOffersEvent event)
    {
        MerchantRecipeList list = event.getList();
        if (list != null)
        {
            ListIterator<MerchantRecipe> it = list.listIterator();
            while (it.hasNext())
            {
                MerchantRecipe recipe = it.next();
                recipe.getItemToSell().setCount(recipe.getItemToSell().getMaxStackSize());
            }
        }
    }
}
