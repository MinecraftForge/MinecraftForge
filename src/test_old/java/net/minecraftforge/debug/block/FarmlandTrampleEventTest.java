/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.block;

import net.minecraftsingularity.event.level.BlockEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;

@Mod("farmland_trample_test")
@Mod.EventBusSubscriber
public final class FarmlandTrampleEventTest {
    @SubscribeEvent
    public static void onFarmlandTrampled(BlockEvent.FarmlandTrampleEvent event) {
        return event.getEntity().isCrouching();
    }
}
