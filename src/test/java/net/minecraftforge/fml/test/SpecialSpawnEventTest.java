/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.test;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod(modid = SpecialSpawnEventTest.MOD_ID, version = "1.0", acceptableRemoteVersions="*")
public class SpecialSpawnEventTest {
    private static final boolean ENABLED = false;
    public static final String MOD_ID = "spawnerduratest";

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent e)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }

    }

    @SubscribeEvent
    public void specialSpawnEvent(LivingSpawnEvent.SpecialSpawn e)
    {
        MobSpawnerBaseLogic spawner = e.getSpawner();
        if (spawner == null) return;
        if (spawner.getSpawnerEntity() != null)
        {
            Entity spawn = new EntityChicken(e.getWorld());
            spawn.copyLocationAndAnglesFrom(spawner.getSpawnerEntity());
            e.getWorld().spawnEntity(spawn);
            spawner.getSpawnerEntity().setDead();
        }
        else
        {
             e.getWorld().setBlockState(spawner.getSpawnerPosition(), Blocks.FIRE.getDefaultState());
        }
    }
}
