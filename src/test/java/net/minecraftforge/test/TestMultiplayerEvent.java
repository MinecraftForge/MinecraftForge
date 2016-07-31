package net.minecraftforge.test;

import net.minecraftforge.client.event.MultiplayerConnectionEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="clientmultiplayereventtest", name="Client Multiplayer Event Test", version="0.0.0", clientSideOnly = true)
public class TestMultiplayerEvent
{

    @Mod.Instance("clientmultiplayereventtest")
    public static TestMultiplayerEvent INSTANCE;

    private final boolean ENABLED = false;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if(!ENABLED) return;
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onServerConnectStart(MultiplayerConnectionEvent.Pre preConnectEvent)
    {
        if(preConnectEvent.getServerData().serverName.equalsIgnoreCase("test_event"))
            preConnectEvent.setCanceled(true);
    }
}
