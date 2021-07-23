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

package net.minecraftforge.debug.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("block_harvest_tool_test")
@Mod.EventBusSubscriber
public class HarvestToolTest
{
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event)
    {
        Player player = event.getPlayer();
        BlockState state = event.getState();
        for (ToolType toolType : player.getMainHandItem().getToolTypes()) {
            if (state.isToolEffective(toolType)) {
                player.sendMessage(new TextComponent(String.format("Tool was effective. tool type: %s | harvest level: %d", toolType.getName(), state.getHarvestLevel())), player.getUUID());
                break;
            }
        }
    }
}
