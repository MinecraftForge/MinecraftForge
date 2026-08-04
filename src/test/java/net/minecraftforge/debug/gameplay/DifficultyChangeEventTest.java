/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.gameplay;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.DifficultyChangeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

//@Mod(modid = "difficultychangeeventtest", name = "DifficultyChangeEventTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class DifficultyChangeEventTest
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

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onDifficultyChange(DifficultyChangeEvent event)
    {
        logger.info("Difficulty changed from {} to {}", event.getOldDifficulty(), event.getDifficulty());
    }
}*/
