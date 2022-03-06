/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(StructureSpawnListGatherEventTest.MODID)
public class StructureSpawnListGatherEventTest
{

    public static final String MODID = "structure_spawn_list_event_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public StructureSpawnListGatherEventTest()
    {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onStructureSpawnListGather);
    }

    private void onStructureSpawnListGather(StructureSpawnListGatherEvent event)
    {
        if (event.getStructure() == StructureFeature.STRONGHOLD)
        {
            event.addEntitySpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 100, 5, 15));
            LOGGER.info("Adding wither skeleton spawns to strong holds");
        }
        else if (event.getStructure() == StructureFeature.SHIPWRECK)
        {
            event.setInsideOnly(false);
            event.addEntitySpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.GUARDIAN, 100, 5, 15));
            LOGGER.info("Adding guardians spawns to shipwrecks");
        }
    }
}
