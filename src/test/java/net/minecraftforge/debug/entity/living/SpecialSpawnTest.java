/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.entity.living;

import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//@Mod(modid = SpecialSpawnTest.MOD_ID, name = "Special Spawn Test", version = "1.0", acceptableRemoteVersions = "*")
//@Mod.EventBusSubscriber(modid = SpecialSpawnTest.MOD_ID)
public class SpecialSpawnTest
{
    static final String MOD_ID = "special_spawn_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn event)
    {
        if (!ENABLED)
        {
            return;
        }

        if (event.getEntity() instanceof EntityPigZombie)
        {
            event.getEntity().setCustomNameTag("Called SpecialSpawn");
        }
    }
}*/
