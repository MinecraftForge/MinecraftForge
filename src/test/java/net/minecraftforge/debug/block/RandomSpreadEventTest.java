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

import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.world.BlockEvent.RandomSpreadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod;

@Mod("random_spread_event_test")
@Mod.EventBusSubscriber(modid = "random_spread_event_test")
/**
 * This test mod:
 * - Will print before spreading of blocks, their blockstate, the outcome, and the affected positions to all players wearing leather helmets in the same world
 * - Will print spreading of blocks, the changed positions, and the spreaded block's state to all players wearing leather helmets in the same world
 * - Will force all block spreading onto stone blocks to happen
 * - Will prevent any block from spreading onto dirt
 */
public class RandomSpreadEventTest {

    @SubscribeEvent
    public static void randomPre(RandomSpreadEvent.Pre event) {
        event.getWorld().getPlayers().forEach(player -> {
            if(player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == Items.LEATHER_HELMET) {
                player.sendMessage(new StringTextComponent("Pre-spread of " + event.getState() + " to positions " + event.getGeneratedBlockPositions().toString() + ", " + (event.willSpread() ? "will spread" : "will not spread")));
            }
        });
        for(BlockPos pos : event.getGeneratedBlockPositions()) {
            if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.STONE) {
                event.getWorld().getPlayers().forEach(player -> {
                    if(player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == Items.LEATHER_HELMET) {
                        player.sendMessage(new StringTextComponent("Forcing " + event.getState() + " spread onto stone at " + pos));
                    }
                });
                event.setResult(Result.ALLOW);
            }
        }
        for(BlockPos pos : event.getGeneratedBlockPositions()) {
            if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.DIRT) {
                event.getWorld().getPlayers().forEach(player -> {
                    if(player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == Items.LEATHER_HELMET) {
                        player.sendMessage(new StringTextComponent("Blocking " + event.getState() + " spread onto dirt at " + pos));
                    }
                });
                event.setResult(Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void randomPost(RandomSpreadEvent.Post event) {
        event.getWorld().getPlayers().forEach(player -> {
            if(player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == Items.LEATHER_HELMET) {
                player.sendMessage(new StringTextComponent("Post-spread of " + event.getState() + " to positions " + event.getGeneratedBlockPositions().toString()));
            }
        });
    }

}
