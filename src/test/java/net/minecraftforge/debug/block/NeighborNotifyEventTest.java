/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.debug.block;

import net.minecraft.block.Blocks;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Disables update notification from comparators.

@Mod("neighbor_notify_event_test")
@Mod.EventBusSubscriber
public class NeighborNotifyEventTest
{
    private static Logger logger = LogManager.getLogger(NeighborNotifyEventTest.class);

    @SubscribeEvent
    public static void onNeighborNotify(NeighborNotifyEvent event)
    {
        if (event.getState().getBlock() == Blocks.COMPARATOR) {
            logger.info("{} with face information: {}", event.getPos().toString(), event.getNotifiedSides());
            event.setCanceled(true);
        }
    }
}
