package net.minecraftforge.debug.entity.player;

import net.minecraftforge.event.entity.player.PlayerVillagerTradeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This tests for {@link PlayerVillagerTradeEvent} and fires when
 * the player completes a trade with either a Villager or WanderingTrader(must inherit from AbstractVillager).
 * This test shows the player name involved along with the villager name and what the result of the trade is(Count and Item).
 */
@Mod("player_villager_trade_event_test")
@Mod.EventBusSubscriber()
public class PlayerVillagerTradeEventTest {

    private static final boolean ENABLE = false;
    private static final Logger LOGGER = LogManager.getLogger(PlayerVillagerTradeEventTest.class);

    @SubscribeEvent
    public static void onPlayerVillagerTrade(PlayerVillagerTradeEvent event) {
        if (!ENABLE) return;
        LOGGER.info("Player {} traded with villager {} and exchanged for {} {}.", event.getEntity().getName().getString(), event.getAbstractVillager().getName().getString(), event.getMerchantOffer().getResult().getCount(), event.getMerchantOffer().getResult().getDisplayName().getString());
    }
}
