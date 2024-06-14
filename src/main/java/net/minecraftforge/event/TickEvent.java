/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.DeltaTracker;
import net.minecraft.server.MinecraftServer;

import java.util.function.BooleanSupplier;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

public class TickEvent extends Event {
    public enum Type {
        LEVEL, PLAYER, CLIENT, SERVER, RENDER;
    }

    public enum Phase {
        START, END;
    }

    public final Type type;
    public final LogicalSide side;
    public final Phase phase;

    public TickEvent(Type type, LogicalSide side, Phase phase) {
        this.type = type;
        this.side = side;
        this.phase = phase;
    }

    public static class ServerTickEvent extends TickEvent {
        private final BooleanSupplier haveTime;
        private final MinecraftServer server;

        protected ServerTickEvent(BooleanSupplier haveTime, MinecraftServer server, Phase phase) {
            super(Type.SERVER, LogicalSide.SERVER, phase);
            this.haveTime = haveTime;
            this.server = server;
        }

        /**
         * @return {@code true} whether the server has enough time to perform any
         * additional tasks (usually IO related) during the current tick,
         * otherwise {@code false}
         */
        public boolean haveTime() {
            return this.haveTime.getAsBoolean();
        }

        /**
         * {@return the server instance}
         */
        public MinecraftServer getServer() {
            return server;
        }

        public static class Pre extends ServerTickEvent {
            public Pre(BooleanSupplier haveTime, MinecraftServer server) {
                super(haveTime, server, Phase.START);
            }
        }

        public static class Post extends ServerTickEvent {
            public Post(BooleanSupplier haveTime, MinecraftServer server) {
                super(haveTime, server, Phase.END);
            }
        }
    }

    public static class ClientTickEvent extends TickEvent {
        protected ClientTickEvent(Phase phase) {
            super(Type.CLIENT, LogicalSide.CLIENT, phase);
        }

        public static class Pre extends ClientTickEvent {
            public Pre() {
                super(Phase.START);
            }
        }

        public static class Post extends ClientTickEvent {
            public Post() {
                super(Phase.END);
            }
        }
    }

    public static class LevelTickEvent extends TickEvent {
        public final Level level;
        private final BooleanSupplier haveTime;

        protected LevelTickEvent(LogicalSide side, Level level, BooleanSupplier haveTime, Phase phase) {
            super(Type.LEVEL, side, phase);
            this.level = level;
            this.haveTime = haveTime;
        }

        /**
         * @return {@code true} whether the server has enough time to perform any
         * additional tasks (usually IO related) during the current tick,
         * otherwise {@code false}
         * @see ServerTickEvent#haveTime()
         */
        public boolean haveTime() {
            return this.haveTime.getAsBoolean();
        }

        public static class Pre extends LevelTickEvent {
            public Pre(LogicalSide side, Level level, BooleanSupplier haveTime) {
                super(side, level, haveTime, Phase.START);
            }
        }

        public static class Post extends LevelTickEvent {
            public Post(LogicalSide side, Level level, BooleanSupplier haveTime) {
                super(side, level, haveTime, Phase.END);
            }
        }
    }

    public static class PlayerTickEvent extends TickEvent {
        public final Player player;

        protected PlayerTickEvent(Player player, Phase phase) {
            super(Type.PLAYER, player instanceof ServerPlayer ? LogicalSide.SERVER : LogicalSide.CLIENT, phase);
            this.player = player;
        }

        public static class Pre extends PlayerTickEvent {
            public Pre(Player player) {
                super(player, Phase.START);
            }
        }

        public static class Post extends PlayerTickEvent {
            public Post(Player player) {
                super(player, Phase.END);
            }
        }
    }

    public static class RenderTickEvent extends TickEvent {
        private final DeltaTracker timer;

        private RenderTickEvent(Phase phase, DeltaTracker timer) {
            super(Type.RENDER, LogicalSide.CLIENT, phase);
            this.timer = timer;
        }

        public DeltaTracker getTimer() {
            return this.timer;
        }

        public static class Pre extends RenderTickEvent {
            public Pre(DeltaTracker timer) {
                super(Phase.START, timer);
            }
        }

        public static class Post extends RenderTickEvent {
            public Post(DeltaTracker timer) {
                super(Phase.END, timer);
            }
        }

    }
}
