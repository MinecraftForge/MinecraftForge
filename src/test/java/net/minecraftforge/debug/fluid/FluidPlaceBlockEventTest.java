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

package net.minecraftforge.debug.fluid;

import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeHills;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This test will:
 *  - Cause lava to turn into gold when touched by water.
 *  - Replace the result of a cobblestone generator with granite.
 *  - Replace the result of a stone generator with either diamond, or emerald when in a biome where emerald spawns naturally.
 *  - Prevent lava from setting surrounding blocks on fire.
 */
@Mod(modid = "fluidplaceblocktest", name = "FluidPlaceBlockTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class FluidPlaceBlockEventTest
{
    private static final boolean ENABLED = false;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        if (!ENABLED) return;
        MinecraftForge.EVENT_BUS.register(FluidPlaceBlockEventTest.class);
    }

    @SubscribeEvent @SuppressWarnings("unused")
    public static void onFluidPlaceBlockEvent(BlockEvent.FluidPlaceBlockEvent event)
    {
        if (event.getState().getBlock() == Blocks.OBSIDIAN) event.setNewState(Blocks.GOLD_BLOCK.getDefaultState());
        if (event.getState().getBlock() == Blocks.COBBLESTONE) event.setNewState(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));
        if (event.getState() == Blocks.STONE.getDefaultState())
        {
            Biome biome = event.getWorld().getBiome(event.getPos());
            if (biome instanceof BiomeHills) event.setNewState(Blocks.EMERALD_BLOCK.getDefaultState());
            else event.setNewState(Blocks.DIAMOND_BLOCK.getDefaultState());
        }
        if (event.getState().getBlock() == Blocks.FIRE) event.setNewState(event.getOriginalState());
    }
}
