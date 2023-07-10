/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.AlterGroundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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