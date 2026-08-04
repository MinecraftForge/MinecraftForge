/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.entity.player;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

//@Mod(modid = ItemPickupEventTest.MODID, name = ItemPickupEventTest.NAME, version = ItemPickupEventTest.VERSION, acceptableRemoteVersions = "*")
public class ItemPickupEventTest
{

    private static final boolean ENABLED = true;
    public static final String MODID = "playeritempickupeventdebug";
    public static final String NAME = "Player.ItemPickup Event Debug";
    public static final String VERSION = "1.0.0";
    private static Logger logger;

    @EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void itemPickupEvent(PlayerEvent.ItemPickupEvent event)
    {
    	logger.info("Item picked up: " + event.getStack().getDisplayName() + "x" + event.getStack().getCount());
    }
}
*/
