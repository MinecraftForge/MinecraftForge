/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.world;

import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;

//@Mod(modid = WorldProviderCanSleepAtTest.MODID, name = "Can Sleep At Test", version = "0.0.0", acceptableRemoteVersions = "*")
public class WorldProviderCanSleepAtTest
{
    public static final String MODID = "can_sleep_at_test";
    public static final boolean ENABLED = false;
    public static DimensionType dimType = null;
    public static int dimId;
    private static Logger logger;

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            logger = event.getModLog();
            dimId = DimensionManager.getNextFreeDimId();
            dimType = DimensionType.register("CanSleepAtTest", "_cansleepattest", dimId, WorldProviderTest.class, false);
            DimensionManager.registerDimension(dimId, dimType);
            logger.info("Registered CanSleepAtTest dimension as DIM {}", dimId);
        }
    }

    public static class WorldProviderTest extends WorldProvider
    {
        @Override
        public DimensionType getDimensionType()
        {
            return WorldProviderCanSleepAtTest.dimType;
        }

        @Override
        public WorldSleepResult canSleepAt(EntityPlayer player, BlockPos pos)
        {
            // Creates a 5x5 blocks wide grid of the different sleep results
            return WorldSleepResult.values()[((pos.getX() / 5) + (pos.getZ() / 5)) % WorldSleepResult.values().length];
        }
    }
}
*/
