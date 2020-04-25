/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

public class TickEvent extends Event
{
    public enum Type {
        WORLD, PLAYER, CLIENT, SERVER, RENDER;
    }

    public enum Phase {
        START, END;
    }
    public final Type type;
    public final LogicalSide side;
    public final Phase phase;
    public TickEvent(Type type, LogicalSide side, Phase phase)
    {
        this.type = type;
        this.side = side;
        this.phase = phase;
    }

    public static class ServerTickEvent extends TickEvent {
        public ServerTickEvent(Phase phase)
        {
            super(Type.SERVER, LogicalSide.SERVER, phase);
        }
    }

    public static class ClientTickEvent extends TickEvent {
        public ClientTickEvent(Phase phase)
        {
            super(Type.CLIENT, LogicalSide.CLIENT, phase);
        }
    }

    public static class WorldTickEvent extends TickEvent {
        public final World world;
        public WorldTickEvent(LogicalSide side, Phase phase, World world)
        {
            super(Type.WORLD, side, phase);
            this.world = world;
        }
    }
    public static class PlayerTickEvent extends TickEvent {
        public final PlayerEntity player;

        public PlayerTickEvent(Phase phase, PlayerEntity player)
        {
            super(Type.PLAYER, player instanceof ServerPlayerEntity ? LogicalSide.SERVER : LogicalSide.CLIENT, phase);
            this.player = player;
        }
    }

    public static class RenderTickEvent extends TickEvent {
        public final float renderTickTime;
        public RenderTickEvent(Phase phase, float renderTickTime)
        {
            super(Type.RENDER, LogicalSide.CLIENT, phase);
            this.renderTickTime = renderTickTime;
        }
    }
}
