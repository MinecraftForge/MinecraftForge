package net.minecraftforge.test;

import net.minecraftforge.client.event.ClientWorldChangedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="clientworldchangedtest", name="Client World Changed Test", version="1.0")
public class ClientWorldChangedTest
{
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientWorldChanged(ClientWorldChangedEvent event)
    {
        if (event.newWorld == null)
        {
            System.out.println("Client changed to null world");
            if (event.oldWorld != null)
            {
                event.loadingMessage = "Disconnecting";
            }
        }
        else
        {
            System.out.println("Client changed to dimension " + event.newWorld.provider.getDimensionName());
            if (event.oldWorld == null)
            {
                event.loadingMessage = "Connecting to the server";
            }
            else
            {
                if (event.oldWorld.provider.getDepartMessage() != null)
                {
                    event.loadingMessage = event.oldWorld.provider.getDepartMessage();
                }
                else if (event.newWorld.provider.getWelcomeMessage() != null)
                {
                    event.loadingMessage = event.newWorld.provider.getWelcomeMessage();
                }
            }
        }
    }
}
