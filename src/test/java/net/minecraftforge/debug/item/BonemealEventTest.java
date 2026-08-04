/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.item;

import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

//@Mod(modid = BonemealEventTest.MODID, name = "Bonemeal Event Test", version = "1.0", acceptableRemoteVersions = "*")
public class BonemealEventTest
{
    public static final String MODID = "bonemealeventtest";

    private static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(BonemealEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event)
    {
        if (event.getHand() == EnumHand.MAIN_HAND)
        {
            // If the bone meal is being used from the main hand, set the result to ALLOW to use up the bone meal without growing the crop
            event.setResult(net.minecraftforge.eventbus.api.Event.Result.ALLOW);
            logger.info("Prevented bone meal growth effect from main hand");
        }
        else if (event.getHand() == EnumHand.OFF_HAND)
        {
            // If the bone meal is being used from the off hand, cancel the event to prevent it being used at all
            event.setCanceled(true);
            logger.info("Prevented bone meal use from off hand");
        }
        else
        {
            // If the bone meal is being used from a dispenser or something similar, allow it
            logger.info("Allowed bone meal use from dispenser");
        }
    }
}
*/
