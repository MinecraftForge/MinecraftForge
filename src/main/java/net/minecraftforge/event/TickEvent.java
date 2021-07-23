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

package net.minecraftforge.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

public class TickEvent extends Event
{
    public enum Type
    {
        WORLD, PLAYER, CLIENT, SERVER, RENDER
    }

    public enum Phase
    {
        START, END
    }

    private final Type type;
    private final LogicalSide side;
    private final Phase phase;

    public TickEvent(Type type, LogicalSide side, Phase phase)
    {
        this.type = type;
        this.side = side;
        this.phase = phase;
    }

    public Type getType()
    {
        return type;
    }

    public LogicalSide getLogicalSide()
    {
        return side;
    }

    public Phase getTickPhase()
    {
        return phase;
    }

    public static class ServerTickEvent extends TickEvent
    {
        public ServerTickEvent(Phase phase)
        {
            super(Type.SERVER, LogicalSide.SERVER, phase);
        }
    }

    public static class ClientTickEvent extends TickEvent
    {
        public ClientTickEvent(Phase phase)
        {
            super(Type.CLIENT, LogicalSide.CLIENT, phase);
        }
    }

    public static class WorldTickEvent extends TickEvent
    {
        private final Level level;

        public WorldTickEvent(LogicalSide side, Phase phase, Level level)
        {
            super(Type.WORLD, side, phase);
            this.level = level;
        }

        public Level getLevel()
        {
            return level;
        }
    }

    public static class PlayerTickEvent extends TickEvent
    {
        private final Player player;

        public PlayerTickEvent(Phase phase, Player player)
        {
            super(Type.PLAYER, player instanceof ServerPlayer ? LogicalSide.SERVER : LogicalSide.CLIENT, phase);
            this.player = player;
        }

        public Player getPlayer()
        {
            return player;
        }
    }

    public static class RenderTickEvent extends TickEvent
    {
        private final float partialTick;

        public RenderTickEvent(Phase phase, float partialTick)
        {
            super(Type.RENDER, LogicalSide.CLIENT, phase);
            this.partialTick = partialTick;
        }

        public float getPartialTick()
        {
            return partialTick;
        }
    }
}
