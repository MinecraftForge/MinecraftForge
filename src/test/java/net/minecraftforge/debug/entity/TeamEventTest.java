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

package net.minecraftforge.debug.entity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.TeamEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "team_event_test", name = "TeamEvent Test Mod", version = "1.0", acceptableRemoteVersions = "*")
public class TeamEventTest
{
    private static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(TeamEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onTeamCreated(TeamEvent.CreateTeamEvent event)
    {
        logger.info("Team Created: {}", event.getTeam().getName());
    }

    @SubscribeEvent
    public static void onTeamRemoved(TeamEvent.RemoveTeamEvent event)
    {
        logger.info("Team Removed: {}", event.getTeam().getName());
    }

    @SubscribeEvent
    public static void onTeamEntityLeave(TeamEvent.LeaveTeamEvent event)
    {
        logger.info("Entity {} left Team {}", event.getEntityName(), event.getTeam().getName());
    }

    @SubscribeEvent
    public static void onTeamEntityJoin(TeamEvent.JoinTeamEvent event)
    {
        logger.info("Entity {} joined Team {}", event.getEntityName(), event.getTeam().getName());
    }
}
