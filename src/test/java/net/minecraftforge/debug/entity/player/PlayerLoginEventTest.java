package net.minecraftforge.debug.entity.player;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("player_login_event_test")
@Mod.EventBusSubscriber()
public class PlayerLoginEventTest
{
    private static final Logger LOGGER = LogManager.getLogger(PlayerLoginEventTest.class);
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onPlayerChangeGameModeEvent(PlayerEvent.PrePlayerLoggedInEvent event)
    {
        LOGGER.info("{} logged in with connection {}.", event.getPlayer().getName(), event.getConnection());
        if (ENABLED) {
            event.setDenyMessage(new TextComponent("This is a test disconnect\nTesting the new line").withStyle(ChatFormatting.BLUE));
            event.setResult(Event.Result.DENY);
        }
    }
}
