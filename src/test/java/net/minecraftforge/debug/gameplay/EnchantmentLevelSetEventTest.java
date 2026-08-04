/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.gameplay;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

//@Mod(modid = "enchantmentlevelsettest", name = "EnchantmentLevelSetTest", version = "1.0", acceptableRemoteVersions = "*")
public class EnchantmentLevelSetEventTest
{
    public static final boolean ENABLE = false;
    private static Logger logger;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            logger = event.getModLog();
            logger.info("Wiring up enchantment level set test");
            MinecraftForge.EVENT_BUS.register(EnchantmentLevelSetEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event)
    {
        // invert enchantment level, just for fun
        logger.info("Enchantment row: {}, level: {}", event.getEnchantRow(), event.getLevel());
        event.setLevel(30 - event.getLevel());
    }
}
*/
