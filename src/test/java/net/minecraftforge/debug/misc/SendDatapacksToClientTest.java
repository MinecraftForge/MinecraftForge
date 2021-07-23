package net.minecraftforge.debug.misc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.event.SendDatapacksToClientEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("send_datapacks_to_client")
@Mod.EventBusSubscriber
public class SendDatapacksToClientTest
{
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    protected static void onSendDataToClient(final SendDatapacksToClientEvent event)
    {
        LOGGER.info("Sending modded datapack data to {}", event.getPlayer().getName().getString());
    }
}
