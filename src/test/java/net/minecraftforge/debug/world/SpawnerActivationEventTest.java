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

package net.minecraftforge.debug.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.SpawnerActivationEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod;

@Mod("spawner_activation_event_test")
public class SpawnerActivationEventTest
{
    public static final boolean enabled = false;

    public SpawnerActivationEventTest()
    {
         if (enabled)
         {
             MinecraftForge.EVENT_BUS.addListener(SpawnerActivationEventTest::changeSpawnerActivationBehavior);
         }
    }

    public static void changeSpawnerActivationBehavior(SpawnerActivationEvent event)
    {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Difficulty difficulty = level.getCurrentDifficultyAt(pos).getDifficulty();
        int playerRange = 0;

        switch(difficulty)
        {
            case PEACEFUL:
                event.setResult(Result.DENY);
                return;
            case EASY:
                playerRange = 4;
                break;
            case NORMAL:
                playerRange = 8;
                break;
            default: // We don't change anything for hard difficulty.
                return;
        }

        if (level.hasNearbyAlivePlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, playerRange))
        {
            event.setResult(Result.ALLOW);
            return;
        }

        event.setResult(Result.DENY);
    }
}
