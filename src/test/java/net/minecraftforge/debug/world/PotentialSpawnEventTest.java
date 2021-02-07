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

import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(PotentialSpawnEventTest.MODID)
public class PotentialSpawnEventTest
{
    public static final String MODID = "potential_spawn_event_test";

    private static final boolean ENABLED = false;

    private MobSpawnInfo.Spawners ZOMBIFIED_PIGLIN_SPAWNER;

    public PotentialSpawnEventTest()
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.addListener(this::potentialSpawns);
            ZOMBIFIED_PIGLIN_SPAWNER = new MobSpawnInfo.Spawners(EntityType.ZOMBIFIED_PIGLIN, 1, 4, 4);
        }
    }

    public void potentialSpawns(WorldEvent.PotentialSpawns event)
    {
        event.getList().clear();
        event.getList().add(ZOMBIFIED_PIGLIN_SPAWNER);
    }
}
