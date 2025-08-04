/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("block_place_event_test")
@Mod.EventBusSubscriber
public final class PlaceEventTest {
    @SubscribeEvent
    public static boolean onBlockPlaced(EntityPlaceEvent event) {
        return event.getPlacedBlock().getBlock() == Blocks.CHEST && event.getPlacedAgainst().getBlock() != Blocks.DIAMOND_BLOCK;
    }
}
