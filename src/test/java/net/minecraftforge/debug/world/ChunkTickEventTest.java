package net.minecraftforge.debug.world;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChunkTickEventTest.MODID)
@Mod.EventBusSubscriber
public class ChunkTickEventTest {
    static final String MODID = "chunk_tick_event_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    @SubscribeEvent
    public static void onChunkBlockTickPre(final ChunkEvent.BlockTick.Pre event)
    {
        if(event.getRandomTickSpeed() > 0)
        {
            LOGGER.info("Chunk at {} is ticking {} blocks per section.", event.getChunk().getPos(), event.getRandomTickSpeed());
        }
        else
        {
            LOGGER.info("Chunk at {} has no blocks to tick.", event.getChunk().getPos());
        }
    }

    @SubscribeEvent
    public static void onChunkBlockTickPost(final ChunkEvent.BlockTick.Post event)
    {
        LOGGER.info("Chunk at {} finished ticking blocks.", event.getChunk().getPos());
    }
}