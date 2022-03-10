/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("farmland_trample_test")
@Mod.EventBusSubscriber
public class FarmlandTrampleEventTest
{
    @SubscribeEvent
    public static void onFarmlandTrampled(BlockEvent.FarmlandTrampleEvent event)
    {
        if(event.getEntity().isCrouching())
            event.setCanceled(true);
    }
}
