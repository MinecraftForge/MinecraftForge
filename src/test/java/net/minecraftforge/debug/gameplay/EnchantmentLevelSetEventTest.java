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

package net.minecraftforge.debug.gameplay;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "enchantmentlevelsettest", name = "EnchantmentLevelSetTest", version = "1.0", acceptableRemoteVersions = "*")
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
