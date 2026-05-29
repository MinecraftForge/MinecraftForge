/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug;

import net.minecraft.world.level.block.Blocks;
import net.minecraftsingularity.event.level.AlterGroundEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;

@Mod("alter_ground_event_test")
@Mod.EventBusSubscriber
public class AlterGroundEventTest {
    public static final boolean ENABLE = true;

    @SubscribeEvent
    public static void onAlterGround(AlterGroundEvent event)
    {
        if (ENABLE) {
            if (event.getOriginalAlteredState().is(Blocks.PODZOL)) {
                event.setNewAlteredState(Blocks.REDSTONE_BLOCK.defaultBlockState());
            }
        }
    }
}
