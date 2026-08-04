/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.entity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

//@Mod(modid = "projectile_event_test", name = "ProjectileImpactEvent test mod", version = "1.0", acceptableRemoteVersions = "*")
public class ProjectileImpactEventTest
{
    private static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(ProjectileImpactEventTest.class);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event)
    {
        logger.info("projectile: {}, impact: {}", event.getEntity().getName(), event.getRayTraceResult());
    }
}
*/
