/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.entity.player;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

//@Mod(modid = "playersetspawntest", name = "Player Set Spawn Test", version = "0.0.0", acceptableRemoteVersions = "*")
public class PlayerSetSpawnTest
{
    private static final boolean ENABLE = false;
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
    public void onPlayerSetSpawn(PlayerSetSpawnEvent event)
    {
        logger.info("{} {}", event.isForced(), event.getNewSpawn().toString());
        event.setCanceled(true);
    }
}
*/
