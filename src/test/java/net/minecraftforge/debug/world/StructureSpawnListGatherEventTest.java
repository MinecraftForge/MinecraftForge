/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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

    public static final String MODID = "structure_spawn_list_event_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public StructureSpawnListGatherEventTest()
    {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onStructureSpawnListGather);
    }

    private void onStructureSpawnListGather(StructureSpawnListGatherEvent event)
    {
        if (event.getStructure() == Structure.STRONGHOLD)
        {
            event.addEntitySpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.WITHER_SKELETON, 100, 5, 15));
            LOGGER.info("Adding wither skeleton spawns to strong holds");
        }
        else if (event.getStructure() == Structure.SHIPWRECK)
        {
            event.setInsideOnly(false);
            event.addEntitySpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.GUARDIAN, 100, 5, 15));
            LOGGER.info("Adding guardians spawns to shipwrecks");
        }
    }
}
