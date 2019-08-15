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


package net.minecraftforge.debug.entity.player;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraftforge.common.PlayerSize;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * This test mod verifies that the player pose/size can be changed.
 * How to check:
 * 1) Make sure that ENABLED is true
 * 2) Spawn in a world, set your gamemode to creative and verify that you do not fit through a door.
 * 3) Change your gamemode to survival and verify that you can walk though doors again
 */
@Mod.EventBusSubscriber(modid = PlayerPoseTest.MODID)
@Mod(value = PlayerPoseTest.MODID)
public class PlayerPoseTest
{
    public static final String MODID = "player_pose_test";
    public static final boolean ENABLED = false;
    private static final Pose widePose = Pose.create("wide");

    public PlayerPoseTest()
    {
        PlayerSize.specifySize(widePose, EntitySize.flexible(2f,1.8f));
    }

    @SubscribeEvent
    public static void onUpdatePlayerPose(PlayerEvent.PlayerUpdatePoseEvent event)
    {
        if(ENABLED&& event.getPlayer().isCreative()){
            event.setOverriddenPose(widePose);
            event.setCanceled(true);
        }
    }

}
