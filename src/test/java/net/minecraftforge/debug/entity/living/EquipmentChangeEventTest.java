/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.entity.living;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

//@Mod(modid = "equipment_change_test", name = "Equipment Change Test", version = "1.0.0", acceptableRemoteVersions = "*")
public class EquipmentChangeEventTest
{
    private static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            logger = event.getModLog();
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    */
/**
     * the Method handling the {@link LivingEquipmentChangeEvent}
     * Serverside only!
     *//*

    @SubscribeEvent
    public void onEquipmentChange(LivingEquipmentChangeEvent event)
    {
        //a debug console print
        logger.info("[Equipment-Change] {} changed their Equipment in {} from {} to {}", event.getEntity(), event.getSlot(), event.getFrom(), event.getTo());
    }

}
*/
