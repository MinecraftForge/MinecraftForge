package net.minecraftforge.debug;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "neighbornotifyeventtest", name = "NeighborNotifyEventTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class NeighborNotifyEventTest
{

    public static final boolean ENABLE = false;
    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            logger = event.getModLog();
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onNeighborNotify(NeighborNotifyEvent event)
    {
        logger.info("{} with face information: {}", event.getPos().toString(), event.getNotifiedSides());
        event.setCanceled(true);
    }
}
