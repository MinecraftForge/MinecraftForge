package net.minecraftforge.debug.entity.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.event.entity.player.PlayerGiveXpEvent;
import net.minecraftforge.event.entity.player.PlayerChangeLevelEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("player_xp_event_test")
@Mod.EventBusSubscriber()
public class PlayerXpEventTest
{

    private static final boolean ENABLE = true;
    private static Logger logger = LogManager.getLogger(PlayerXpEventTest.class);
    
    @SubscribeEvent
    public static void onPlayerPickupXp(PlayerPickupXpEvent event) {
        if (!ENABLE) return;
        logger.info("{} picked up an experience orb worth {}", event.getPlayer().getName().getString(), event.getOrb().getXpValue());
    }
    
    @SubscribeEvent
    public static void onPlayerGiveXp(PlayerGiveXpEvent event) {
        if (!ENABLE) return;
        logger.info("{} has been given {} experience", event.getPlayer().getName().getString(), event.getAmount());
    }
    
    @SubscribeEvent
    public static void onPlayerLevelChange(PlayerChangeLevelEvent event) {
        if (!ENABLE) return;
        logger.info("{} has changed {} levels", event.getPlayer().getName().getString(), event.getLevels());
    }

}
