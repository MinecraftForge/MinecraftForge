/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.world.entity.player.Player;

import java.util.function.BooleanSupplier;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
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

        private final BooleanSupplier haveTime;

        /**
         * TODO: Remove in 1.19
         * 
         * @deprecated Use {@link ServerTickEvent#ServerTickEvent(Phase, BooleanSupplier)}
         */
        @Deprecated(forRemoval = true, since = "1.18.1")
        public ServerTickEvent(Phase phase)
        {
            this(phase, () -> false);
        }

        public ServerTickEvent(Phase phase, BooleanSupplier haveTime)
        {
            super(Type.SERVER, LogicalSide.SERVER, phase);
            this.haveTime = haveTime;
        }

        /**
         * @return {@code true} whether the server has enough time to perform any
         *         additional tasks (usually IO related) during the current tick,
         *         otherwise {@code false}
         */
        public boolean haveTime()
        {
            return this.haveTime.getAsBoolean();
        }
    }

    public static class ClientTickEvent extends TickEvent {
        public ClientTickEvent(Phase phase)
        {
            super(Type.CLIENT, LogicalSide.CLIENT, phase);
        }
    }

    public static class WorldTickEvent extends TickEvent {
        public final Level world;
        private final BooleanSupplier haveTime;

        /**
         * TODO: Remove in 1.19
         * 
         * @deprecated Use {@link WorldTickEvent#WorldTickEvent(LogicalSide, Phase, Level, BooleanSupplier)}
         */
        @Deprecated(forRemoval = true, since = "1.18.1")
        public WorldTickEvent(LogicalSide side, Phase phase, Level world)
        {
            this(side, phase, world, () -> false);
        }

        public WorldTickEvent(LogicalSide side, Phase phase, Level world, BooleanSupplier haveTime)
        {
            super(Type.WORLD, side, phase);
            this.world = world;
            this.haveTime = haveTime;
        }

        /**
         * @return {@code true} whether the server has enough time to perform any
         *         additional tasks (usually IO related) during the current tick,
         *         otherwise {@code false}
         * 
         * @see ServerTickEvent#haveTime()
         */
        public boolean haveTime()
        {
            return this.haveTime.getAsBoolean();
        }
    }
    public static class PlayerTickEvent extends TickEvent {
        public final Player player;

        public PlayerTickEvent(Phase phase, Player player)
        {
            super(Type.PLAYER, player instanceof ServerPlayer ? LogicalSide.SERVER : LogicalSide.CLIENT, phase);
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
