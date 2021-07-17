/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
 */

package net.minecraftforge.debug.fluid;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("finite_water_test")
@Mod.EventBusSubscriber()
public class FiniteWaterTest
{
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void handleFiniteWaterSource(BlockEvent.CreateFluidSourceEvent event)
    {
        if (ENABLED)
        {
            BlockState state = event.getState();
            FluidState fluidState = state.getFluidState();
            if (fluidState.getType().isSame(Fluids.WATER))
            {
                event.setResult(Event.Result.DENY);
            }
            else if (fluidState.getType().isSame(Fluids.LAVA))
            {
                event.setResult(Event.Result.ALLOW);
            }
        }
    }
}
