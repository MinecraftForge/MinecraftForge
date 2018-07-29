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

import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = WorldProviderCanSleepAtTest.MODID, name = "Can Sleep At Test", version = "0.0.0", acceptableRemoteVersions = "*")
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
