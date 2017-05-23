package net.minecraftforge.debug;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.MerchantTradeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Locale;

@Mod(modid = MerchantTradeEventDebug.MODID, name = "MerchantTradeEventDebug", version = "1.0", acceptableRemoteVersions = "*")
public class MerchantTradeEventDebug
{
    public static final String MODID = "merchanttradeeventdebug";
    public static final boolean DEBUG = false;

    @Mod.EventHandler
    private void preInit(FMLPreInitializationEvent event)
    {
        if(!DEBUG) return;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onMerchantTradeDone(MerchantTradeEvent.Transact event)
    {
        event.getEntityPlayer().sendMessage(new TextComponentString(String.format(Locale.ENGLISH, "Trade: %s(%d) + %s(%d) = %s", event.getTrade().getItemToBuy(), event.getLeft().getCount(), event.getTrade().getSecondItemToBuy(), event.getRight().getCount(), event.getTrade().getItemToSell())));
        // Discount for experts.
        if (event.getEntityPlayer().experienceLevel > 20)
        {
            event.setCanceled(true);
            ItemStack left = event.getLeft(), right = event.getRight();
            left.shrink(1);
            right.shrink(1);
            event.setLeft(left);
            event.setRight(right);
        }
    }

    @SubscribeEvent
    public void checkMerchantTrade(MerchantTradeEvent.SetupOffer event)
    {
        // Beginners can't trade.
        if (event.getEntityPlayer().experienceLevel < 1)
        {
            event.setCanceled(true);
        }
    }
}
