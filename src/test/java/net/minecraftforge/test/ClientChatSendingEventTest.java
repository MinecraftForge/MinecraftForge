package net.minecraftforge.test;

import net.minecraftforge.client.event.ClientChatSendingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="clientchatsendingeventtest", name="Client Chat Sending Event Test", version="0.0.0", acceptableRemoteVersions = "*")
public class ClientChatSendingEventTest {

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerAttemptChat(ClientChatSendingEvent event)
    {
        if(event.getMessage().equals("Cancel"))
            event.setCanceled(true);
        else if(event.getMessage().equals("Replace this text"))
            event.setMessage("Text replaced.");
    }
}
