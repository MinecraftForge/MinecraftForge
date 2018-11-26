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

package net.minecraftforge.event.entity;

import net.minecraft.scoreboard.Team;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TeamEvent extends Event
{
    private final Team team;

    public TeamEvent(Team team)
    {
        this.team = team;
    }

    public Team getTeam()
    {
        return this.team;
    }

    public static class CreateTeamEvent extends TeamEvent
    {
        public CreateTeamEvent(Team team)
        {
            super(team);
        }
    }

    public static class RemoveTeamEvent extends TeamEvent
    {
        public RemoveTeamEvent(Team team)
        {
            super(team);
        }
    }

    public static class EntityTeamEvent extends TeamEvent
    {
        private final String entityName;

        public EntityTeamEvent(Team team, String entityName)
        {
            super(team);
            this.entityName = entityName;
        }

        public String getEntityName()
        {
            return this.entityName;
        }
    }

    public static class JoinTeamEvent extends EntityTeamEvent
    {
        public JoinTeamEvent(Team team, String entity)
        {
            super(team, entity);
        }
    }

    public static class LeaveTeamEvent extends EntityTeamEvent
    {
        public LeaveTeamEvent(Team team, String entity)
        {
            super(team, entity);
        }
    }
}
