package net.minecraftforge.debug.block;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "farmlandwatertest", name = "Farmland Water Test", version = "1.0.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class FarmlandWaterTest
{
    private static final boolean ENABLED = true;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            logger = event.getModLog();
        }
    }

    /**
     * Will not allow watering farmland in desert and will always water in swampland and won't accept rain as water source in river
     */
    @SubscribeEvent
    public static void onFarmlandWaterCheck(BlockEvent.FarmlandWaterCheckEvent event)
    {
        if (!ENABLED)
        {
            return;
        }
        Biome biome = event.getWorld().getBiome(event.getPos());
        if (biome == Biomes.SWAMPLAND || biome == Biomes.MUTATED_SWAMPLAND) // always accept
        {
            logger.info("Overriding farmland check for biome " + biome + " to always be watered");
            event.setResult(Event.Result.ALLOW);
        }
        else if (biome == Biomes.DESERT || biome == Biomes.DESERT_HILLS || biome == Biomes.MUTATED_DESERT) // never accept
        {
            logger.info("Overriding farmland check for biome " + biome + " to never be watered");
            event.setResult(Event.Result.DENY);
        }
        else if (biome == Biomes.RIVER || biome == Biomes.FROZEN_RIVER)
        {
            logger.info("Overriding farmland check for biome " + biome + " to only be watered by source blocks");
            event.setResult(event.hasNearbyWaterBlock() ? Event.Result.DEFAULT : Event.Result.DENY);
        }
    }
}
