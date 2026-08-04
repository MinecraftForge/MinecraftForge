/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.entity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

//@Mod(modid = "entitytraveltodimensioneventtest", name = "EntityTravelToDimensionEventTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class EntityTravelToDimensionEventTest
{
    public static final boolean ENABLE = false;
    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            logger = event.getModLog();
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onDimensionTravel(EntityTravelToDimensionEvent event)
    {
        if (ENABLE)
        {
            logger.info("Travelling to Dimension {} Entity: {}", event.getDimension(), event.getEntity());
            event.setCanceled(true);
        }
    }
}
*/
