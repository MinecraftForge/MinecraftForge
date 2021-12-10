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

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.AnimatePlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("animate_player_test")
public class AnimatePlayerTest
{
    private static final boolean ENABLED = true;

    public AnimatePlayerTest()
    {
        if (ENABLED && FMLEnvironment.dist == Dist.CLIENT)
        {
            MinecraftForge.EVENT_BUS.addListener(this::onAnimatePlayerBlank);
            MinecraftForge.EVENT_BUS.addListener(this::onAnimatePlayerPre);
            MinecraftForge.EVENT_BUS.addListener(this::onAnimatePlayerPost);
        }
    }

    private void onAnimatePlayerBlank(AnimatePlayerEvent.Blank event)
    {
        Player player = event.getPlayer();
        if (player.getMainHandItem().getItem() == Items.ICE)
        {
            event.setCanceled(true);
        }
    }

    private void onAnimatePlayerPre(AnimatePlayerEvent.Pre event)
    {
        Player player = event.getPlayer();
        if (player.getMainHandItem().getItem() == Items.NETHERITE_HOE) // Static Pose
        {
            PlayerModel<?> model = event.getModel();
            model.rightArm.xRot = (float) Math.toRadians(-90F);
            model.leftArm.xRot = (float) Math.toRadians(-90F);
        }
        else if (player.getMainHandItem().getItem() == Items.COOKIE) // Wave
        {
            float deltaTicks = Minecraft.getInstance().getFrameTime();
            PlayerModel<?> model = event.getModel();
            float angle = (float) (Math.sin((player.tickCount + deltaTicks) % 20F) * 20F);
            model.rightArm.x -= 1;
            model.rightArm.zRot = (float) Math.toRadians(150F + angle);
        }
    }

    private void onAnimatePlayerPost(AnimatePlayerEvent.Post event)
    {
        Player player = event.getPlayer();
        if (player.getMainHandItem().getItem() == Items.CARROT_ON_A_STICK)
        {
            /* This event will do similar to the pre version example, the sleeves of the
             * player just won't be in the correct position. Modders will need to be more
             * explicit when using this event. */
            PlayerModel<?> model = event.getModel();
            model.rightArm.xRot = (float) Math.toRadians(-90F);
            model.leftArm.xRot = (float) Math.toRadians(-90F);
        }
    }
}
