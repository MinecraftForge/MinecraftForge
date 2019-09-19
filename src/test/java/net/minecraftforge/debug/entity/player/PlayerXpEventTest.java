package net.minecraftforge.debug.entity.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("player_xp_event_test")
@Mod.EventBusSubscriber()
public class PlayerXpEventTest
{

    private static final boolean ENABLE = false;
    private static Logger logger = LogManager.getLogger(PlayerXpEventTest.class);

    @SubscribeEvent
    public static void onPlayerXpEvent(PlayerXpEvent event)
    {
        if (!ENABLE) return;
        logger.info("The PlayerXpEvent has been called!");
    }

    @SubscribeEvent
    public static void onPlayerPickupXpOld(PlayerPickupXpEvent event)
    {
        if (!ENABLE) return;
        logger.info("The deprecated PlayerPickupXpEvent has been called!");
    }

    @SubscribeEvent
    public static void onPlayerPickupXp(PlayerXpEvent.PickupXp event)
    {
        if (!ENABLE) return;
        logger.info("{} picked up an experience orb worth {}", event.getPlayer().getName().getString(), event.getOrb().getXpValue());
    }

    @SubscribeEvent
    public static void onPlayerXpChange(PlayerXpEvent.XpChange event)
    {
        if (!ENABLE) return;
        logger.info("{} has been given {} experience", event.getPlayer().getName().getString(), event.getAmount());
    }

    @SubscribeEvent
    public static void onPlayerLevelChange(PlayerXpEvent.LevelChange event)
    {
        if (!ENABLE) return;
        logger.info("{} has changed {} levels", event.getPlayer().getName().getString(), event.getLevels());
    }

}
