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
@Mod(modid = MerchantTradeOffersEventTest.MODID, name = MerchantTradeOffersEventTest.NAME, version = "0.0.0", acceptableRemoteVersions = "*")
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
