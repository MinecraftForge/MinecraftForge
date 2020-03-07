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
 */

package net.minecraftforge.debug.block;

import net.minecraft.block.*;
import net.minecraft.util.Direction;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.Validate;

@Mod(SeaPlantPlaceTest.MODID)
@Mod.EventBusSubscriber
public class SeaPlantPlaceTest
{
    static final String MODID = "sea_plant_place_test";

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event){

        // Check to see if the Sea Pickle or Sea Grass placed on a block was in the appropriate environment
        // i.e., the sea plant is surrounded by water. If it is, we can cancel the event, signalling the successful
        // end to the test case
        if (event.getPlacedBlock().getBlock() == Blocks.SEA_PICKLE
                || event.getPlacedBlock().getBlock() == Blocks.SEAGRASS)
        {
            Validate.isTrue(event.getPlacedAgainst().canSustainPlant(event.getWorld(), event.getPos(), Direction.UP,
                    (IPlantable)Blocks.SEAGRASS));
        }
    }
}
