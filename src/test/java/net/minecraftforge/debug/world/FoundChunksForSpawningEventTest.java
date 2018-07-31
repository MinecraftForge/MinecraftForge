package net.minecraftforge.debug.world;

import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.FoundChunksForSpawningEvent;
import net.minecraftforge.event.world.FoundChunksForSpawningEventCreatureTypeData;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Simple mod to test net.minecraftforge.event.world.FoundChunksForSpawningEvent Prints event data to the log every tick.
 */
@Mod(modid = FoundChunksForSpawningEventTest.MODID, name = FoundChunksForSpawningEventTest.NAME, version = FoundChunksForSpawningEventTest.VERSION,
        acceptableRemoteVersions = "*")
public class FoundChunksForSpawningEventTest
{
    public static final String MODID = "foundchunksforspawningeventtest";
    public static final String NAME = "FoundChunksForSpawningEvent Test";
    public static final String VERSION = "1.0";

    private static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            logger = event.getModLog();
            MinecraftForge.EVENT_BUS.register(FoundChunksForSpawningEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onFoundChunksForSpawning(FoundChunksForSpawningEvent event)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("FoundChunksForSpawningEvent");
        sb.append("\n  numChunks: ").append(event.getEligibleChunksForSpawning().size()).append("/").append(event.getI());
        sb.append("\n  typeData:");
        for (Entry<EnumCreatureType, FoundChunksForSpawningEventCreatureTypeData> entry : event.getCreatureTypeData().entrySet())
        {
            FoundChunksForSpawningEventCreatureTypeData v = entry.getValue();
            sb.append("\n    ").append(entry.getKey().name()).append(": ").append(v.getk4()).append("/").append(v.getl4());
        }
        Exception ex = event.getNewEntityException();
        sb.append("\n  ex: ").append(ex == null ? "null" : ex.getMessage());
        logger.info(sb);
    }
}
