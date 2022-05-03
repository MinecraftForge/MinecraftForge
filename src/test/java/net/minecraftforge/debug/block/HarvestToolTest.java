/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("block_harvest_tool_test")
@Mod.EventBusSubscriber
public class HarvestToolTest
{
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event)
    {
        PlayerEntity player = event.getPlayer();
        BlockState state = event.getState();
        for (ToolType toolType : player.getMainHandItem().getToolTypes()) {
            if (state.isToolEffective(toolType)) {
                player.sendMessage(new StringTextComponent(String.format("Tool was effective. tool type: %s | harvest level: %d", toolType.getName(), state.getHarvestLevel())), player.getUUID());
                break;
            }
        }
    }
}
