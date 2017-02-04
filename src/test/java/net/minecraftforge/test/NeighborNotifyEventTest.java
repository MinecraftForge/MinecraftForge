package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="neighbornotifyeventtest", name="NeighborNotifyEventTest", version="0.0.0", acceptableRemoteVersions = "*")
public class NeighborNotifyEventTest
{

    public static final boolean ENABLE = false;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onNeighborNotify(NeighborNotifyEvent event)
    {
        if(ENABLE) {
            System.out.println(event.getPos().toString() + " with face information: " + event.getNotifiedSides());
            event.setCanceled(true);
        }
    }
}
