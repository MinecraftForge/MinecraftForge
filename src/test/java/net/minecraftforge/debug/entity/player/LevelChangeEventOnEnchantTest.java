package net.minecraftforge.debug.entity.player;

import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("level_change_event_on_enchant_test")
@Mod.EventBusSubscriber()
public class LevelChangeEventOnEnchantTest {
   private static final boolean ENABLE = true;
   private static final Logger logger = LogManager.getLogger(LevelChangeEventOnEnchantTest.class);

   @SubscribeEvent
   public static void onPlayerLevelChange(PlayerXpEvent.LevelChange event)
   {
      if (!ENABLE) return;
      logger.info("{} has changed {} levels", event.getPlayer().getName().getString(), event.getLevels());
   }
}
