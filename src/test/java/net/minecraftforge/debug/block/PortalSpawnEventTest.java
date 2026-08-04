/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.block;

import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("portal_spawn_event_test")
@Mod.EventBusSubscriber
public class PortalSpawnEventTest
{
    @SubscribeEvent
    public static void onTrySpawnPortal(BlockEvent.PortalSpawnEvent event)
    {
        IWorld world = event.getWorld();
        if (world.getWorld().getDimension().getType() == DimensionType.OVERWORLD && world.getBiome(event.getPos()) != Biomes.field_222371_ax)
            event.setCanceled(true);
    }
}
*/
