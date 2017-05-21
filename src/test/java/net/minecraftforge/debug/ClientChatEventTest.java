package net.minecraftforge.debug;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "clientchateventtest", name = "Client Chat Event Test", version = "0.0.0", acceptableRemoteVersions = "*")
public class ClientChatEventTest
{

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerAttemptChat(ClientChatEvent event)
    {
        if (event.getMessage().equals("Cancel"))
        {
            event.setCanceled(true);
        }
        else if (event.getMessage().equals("Replace this text"))
        {
            event.setMessage("Text replaced.");
        }
    }
}
