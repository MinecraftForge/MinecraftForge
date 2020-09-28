package net.minecraftforge.debug.world;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(StructureSpawnListGatherEventTest.MODID)
public class StructureSpawnListGatherEventTest
{

    static final String MODID = "structure_spawn_list_event_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public StructureSpawnListGatherEventTest() {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onStructureSpawnListGather);
    }

    private void onStructureSpawnListGather(StructureSpawnListGatherEvent event)
    {
        if (event.getStructure() == Structure.field_236375_k_)
        {
            event.getEntitySpawns(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(EntityType.WITHER_SKELETON, 100, 5, 15));
            LOGGER.info("Adding wither skeleton spawns to strong holds");
        }
        else if (event.getStructure() == Structure.field_236373_i_)
        {
            event.allowSpawnsOutside();
            event.getEntitySpawns(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(EntityType.GUARDIAN, 100, 1, 5));
            LOGGER.info("Adding guardians spawns to shipwrecks");
        }
    }
}
