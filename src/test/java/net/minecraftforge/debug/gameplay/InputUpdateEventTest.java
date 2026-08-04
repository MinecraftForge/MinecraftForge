/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.gameplay;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.Mod;

//@Mod(modid = InputUpdateEventTest.MODID, name = "InputUpdateTest", version = "1.0", acceptableRemoteVersions = "*")
public class InputUpdateEventTest
{
    static final String MODID = "input_update_test";

    //@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class Registration
    {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void onInputUpdate(InputUpdateEvent evt)
        {
            EntityPlayer player = evt.getEntityPlayer();
            final int x = MathHelper.floor(player.posX);
            final int y = MathHelper.floor(player.getEntityBoundingBox().minY) - 1;
            final int z = MathHelper.floor(player.posZ);
            final BlockPos pos = new BlockPos(x, y, z);
            IBlockState blockUnder = player.world.getBlockState(pos);
            if (blockUnder.getBlock() == Blocks.BLACK_GLAZED_TERRACOTTA)
            {
                if (evt.getMovementInput().jump)
                {
                    player.sendMessage(new TextComponentString("NO JUMPING!"));
                    evt.getMovementInput().jump = false;
                }

                if (evt.getMovementInput().sneak)
                {
                    player.sendMessage(new TextComponentString("NO SNEAKING!"));
                    evt.getMovementInput().sneak = false;
                }
            }
        }
    }

}
*/
