package net.minecraftforge.debug.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("arrow_nock_event_test")
@Mod.EventBusSubscriber
public class ArrowNockEventTest
{
    private static final boolean ENABLE = false;
    private static final Logger LOGGER = LogManager.getLogger(ArrowNockEventTest.class);

    @SubscribeEvent
    public static void onArrowNocked(ArrowNockEvent event)
    {
        if (!ENABLE) return;
        LOGGER.info("The ArrowNockEvent has been called!");
        PlayerEntity player = event.getPlayer();

        if (player.abilities.isCreativeMode)
        {
            LOGGER.info("No Shoot for you!");
            event.setAction(ActionResult.resultFail(event.getAmmo()));
        }
    }
}
