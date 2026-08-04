/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//@Mod(modid = SpawnDimensionTest.MODID, name = "CustomSpawnDimensionTest", version = "0.1", acceptableRemoteVersions = "*")
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
*/
