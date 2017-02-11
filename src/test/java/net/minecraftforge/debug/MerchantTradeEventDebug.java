package net.minecraftforge.debug;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.MerchantTradeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Locale;

@Mod.EventBusSubscriber
@Mod(modid = MerchantTradeEventDebug.MODID, name = "MerchantTradeEventDebug", version = "1.0", acceptableRemoteVersions = "*")
public class MerchantTradeEventDebug
{
    public static final String MODID = "merchanttradeeventdebug";

    @SubscribeEvent
    public void onMerchantTradeDone(MerchantTradeEvent.Done event)
    {
        event.getEntityPlayer().sendMessage(new TextComponentString(String.format(Locale.ENGLISH, "Trade: %s + %s = %s", event.getTrade().getItemToBuy(), event.getTrade().getSecondItemToBuy(), event.getTrade().getItemToSell())));
        // Not a practical code. Only for debug.
        boolean flag;
        if (flag = event.getLeft().getItem().hasContainerItem(event.getLeft()))
            event.setLeft(ForgeHooks.getContainerItem(event.getLeft()));
        if (flag |= event.getRight().getItem().hasContainerItem(event.getRight()))
            event.setRight(ForgeHooks.getContainerItem(event.getRight()));
        if (flag) event.setCanceled(true);
    }

    @SubscribeEvent
    public void checkMerchantTrade(MerchantTradeEvent.CanTrade event)
    {
        if(event.getEntityPlayer().experienceLevel < 1){
            event.setResult(Event.Result.DENY);
            event.getEntityPlayer().sendMessage(new TextComponentString("You lack experience."));
        }
    }
}
