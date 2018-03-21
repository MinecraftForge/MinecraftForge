package net.minecraftforge.debug;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ChunkWatchWorldTest.MODID, name = "Chunk Watch World Test", version = "1.0", acceptableRemoteVersions = "*")
public class ChunkWatchWorldTest
{
    public static final String MODID = "chunkwatchworldtest";

    private static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(ChunkWatchWorldTest.class);
        }
    }

    @SubscribeEvent
    public static void onUnwatch(ChunkWatchEvent.UnWatch event)
    {
        logger.info("Unwatching chunk {} in dimension {}. Player's dimension: {} ", event.getChunk(), event.getChunkInstance().getWorld().provider.getDimension(), event.getPlayer().getEntityWorld().provider.getDimension());
    }

    @SubscribeEvent
    public static void onWatch(ChunkWatchEvent.Watch event)
    {
        logger.info("Watching chunk {} in dimension {}. Player's dimension: {} ", event.getChunk(), event.getChunkInstance().getWorld().provider.getDimension(), event.getPlayer().getEntityWorld().provider.getDimension());
    }
}
