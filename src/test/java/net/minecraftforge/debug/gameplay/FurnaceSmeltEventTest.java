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

package net.minecraftforge.debug.gameplay;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceSmeltEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraftforge.debug.gameplay.FurnaceSmeltEventTest.MODID;

@Mod(MODID)
public class FurnaceSmeltEventTest {
	static final String MODID = "furnace_smelt_event_test";
	
	public FurnaceSmeltEventTest() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void preSmelt(FurnaceSmeltEvent.Pre event) {
		AbstractFurnaceTileEntity furnace = event.getFurnace();
		//If the furnace is "under" water, cancel the smelt and play the explosion sound effect
		System.out.println("FURNACE SMALET");
		if(furnace.getWorld().getBlockState(furnace.getPos().up()).getBlock() == Blocks.WATER) {
			event.setCanceled(true);
			furnace.getWorld().playSound((PlayerEntity)null, furnace.getPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 3.0f, 1f);
		}
	}
	
	@SubscribeEvent
	public void postSmelt(FurnaceSmeltEvent.Post event) {
		AbstractFurnaceTileEntity furnace = event.getFurnace();
		//If the furnace is under a bell, play the bell sound effect every smelt.
		if(furnace.getWorld().getBlockState(furnace.getPos().up()).getBlock() == Blocks.BELL) {
			furnace.getWorld().playSound((PlayerEntity)null, furnace.getPos(), SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 3.0f, 1f);
		}
	}
}
