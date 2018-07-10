/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = SpawnDimensionTest.MODID, name = "CustomSpawnDimensionTest", version = "0.1", acceptableRemoteVersions = "*")
public class SpawnDimensionTest
{
    public static final String MODID = "customspawndimensiontest";

    private static final boolean ENABLE = false;

    public SpawnDimensionTest()
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void enterDimension(EntityJoinWorldEvent event)
    {
        Entity e = event.getEntity();
        if (!(e instanceof EntityPlayer))
        {
            return;
        }

        BlockPos pos = e.getPosition();
        int dim = e.dimension;

        ((EntityPlayer) e).setSpawnDimension(dim);
        ((EntityPlayer) e).setSpawnChunk(pos, true, dim);
    }
}
