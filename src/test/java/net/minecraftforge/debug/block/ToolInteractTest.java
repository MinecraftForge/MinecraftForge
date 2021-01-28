/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BlockToolInteractEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("tool_interact_test")
public class ToolInteractTest
{

    private static final Logger LOGGER = LogManager.getLogger();

    public ToolInteractTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onToolInteraction);
    }

    private void onToolInteraction(final BlockToolInteractEvent event)
    {
        //Test 1: No Changes, just test if event is called. State and Final State should be the same
        LOGGER.info("BlockState {} is modified to {} at position {} by {} with {}", event.getState(), event.getFinalState(), event.getPos(), event.getPlayer(), event.getHeldItemStack());

        //Test 2: Canceling, nothing in game should change
        /*event.setCanceled(true);
        LOGGER.info("Event has been canceled: {}", event.isCanceled());
        */

        //Test 3: Altering
        /*event.setFinalState(Blocks.ACACIA_FENCE.getDefaultState());
        LOGGER.info("BlockState {} is modified to {} at position {} by {} with {}", event.getState(), event.getFinalState(), event.getPos(), event.getPlayer(), event.getHeldItemStack());
        */
    }
}
