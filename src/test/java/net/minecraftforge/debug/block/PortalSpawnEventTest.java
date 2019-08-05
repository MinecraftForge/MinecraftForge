/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
 *//*


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
