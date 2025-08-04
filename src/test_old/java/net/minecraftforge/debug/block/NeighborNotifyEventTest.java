/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Disables update notification from comparators.

@Mod("neighbor_notify_event_test")
@Mod.EventBusSubscriber
public final class NeighborNotifyEventTest {
    private static final Logger LOGGER = LogManager.getLogger(NeighborNotifyEventTest.class);

    @SubscribeEvent
    public static boolean onNeighborNotify(NeighborNotifyEvent event) {
        if (event.getState().getBlock() == Blocks.COMPARATOR) {
            LOGGER.info("{} with face information: {}", event.getPos().toString(), event.getNotifiedSides());
            return true;
        }
        return false;
    }
}
