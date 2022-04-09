/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
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
