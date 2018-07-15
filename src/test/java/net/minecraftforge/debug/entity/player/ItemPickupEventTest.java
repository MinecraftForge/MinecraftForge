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

package net.minecraftforge.debug.entity.player;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod(modid = ItemPickupEventTest.MODID, name = ItemPickupEventTest.NAME, version = ItemPickupEventTest.VERSION, acceptableRemoteVersions = "*")
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
