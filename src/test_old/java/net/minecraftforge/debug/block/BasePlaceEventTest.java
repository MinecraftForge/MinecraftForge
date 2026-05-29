/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.block;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraftsingularity.event.level.BlockEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;

@Mod("base_block_place_event_test")
@Mod.EventBusSubscriber
public final class BasePlaceEventTest {
    @SubscribeEvent
    public static boolean onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        return event.getEntity() instanceof FallingBlockEntity;
    }
}
