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
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.fml.LogicalSide;

public sealed class TickEvent extends MutableEvent implements InheritableEvent {
    /**
     * @deprecated Use one of the subclasses instead, as subscribing to the abstract {@link TickEvent} class will call
     * your listener possibly hundreds of times per tick which is inefficient/slow and probably not what you want
     */
    @Deprecated(forRemoval = true, since = "1.21.6")
    public static final EventBus<TickEvent> BUS = EventBus.create(TickEvent.class);

    public enum Type {
        LEVEL, PLAYER, CLIENT, SERVER, RENDER
    }

    public enum Phase {
        START, END
    }

    public final Type type;
    public final LogicalSide side;

    /**
     * @deprecated For better performance, add listeners to the Pre or Post event subclasses instead of checking the phase
     */
    @Deprecated(forRemoval = true, since = "1.21.6")
    public final Phase phase;

    public TickEvent(Type type, LogicalSide side, Phase phase) {
        this.type = type;
        this.side = side;
        this.phase = phase;
    }

    public static sealed class ServerTickEvent extends TickEvent {
        /**
         * @deprecated Use {@link ServerTickEvent.Pre} or {@link ServerTickEvent.Post} instead
         */
        @Deprecated(forRemoval = true, since = "1.21.6")
        public static final EventBus<ServerTickEvent> BUS = EventBus.create(ServerTickEvent.class);

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

        public static final class Pre extends ServerTickEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);

            public Pre(BooleanSupplier haveTime, MinecraftServer server) {
                super(haveTime, server, Phase.START);
            }
        }

        public static final class Post extends ServerTickEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);

            public Post(BooleanSupplier haveTime, MinecraftServer server) {
                super(haveTime, server, Phase.END);
            }
        }
    }

    public static sealed class ClientTickEvent extends TickEvent {
        /**
         * @deprecated Use {@link ClientTickEvent.Pre} or {@link ClientTickEvent.Post} instead
         */
        @Deprecated(forRemoval = true, since = "1.21.6")
        public static final EventBus<ClientTickEvent> BUS = EventBus.create(ClientTickEvent.class);

        protected ClientTickEvent(Phase phase) {
            super(Type.CLIENT, LogicalSide.CLIENT, phase);
        }

        public static final class Pre extends ClientTickEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);
            public static final Pre INSTANCE = new Pre();

            public Pre() {
                super(Phase.START);
            }
        }

        public static final class Post extends ClientTickEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
            public static final Post INSTANCE = new Post();

            public Post() {
                super(Phase.END);
            }
        }
    }

    public static sealed class LevelTickEvent extends TickEvent {
        /**
         * @deprecated Use {@link LevelTickEvent.Pre} or {@link LevelTickEvent.Post} instead
         */
        @Deprecated(forRemoval = true, since = "1.21.6")
        public static final EventBus<LevelTickEvent> BUS = EventBus.create(LevelTickEvent.class);

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

        public static final class Pre extends LevelTickEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);

            public Pre(LogicalSide side, Level level, BooleanSupplier haveTime) {
                super(side, level, haveTime, Phase.START);
            }
        }

        public static final class Post extends LevelTickEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);

            public Post(LogicalSide side, Level level, BooleanSupplier haveTime) {
                super(side, level, haveTime, Phase.END);
            }
        }
    }

    public static sealed class PlayerTickEvent extends TickEvent {
        /**
         * @deprecated Use {@link PlayerTickEvent.Pre} or {@link PlayerTickEvent.Post} instead
         */
        @Deprecated(forRemoval = true, since = "1.21.6")
        public static final EventBus<PlayerTickEvent> BUS = EventBus.create(PlayerTickEvent.class);

        public final Player player;

        protected PlayerTickEvent(Player player, Phase phase) {
            super(Type.PLAYER, player instanceof ServerPlayer ? LogicalSide.SERVER : LogicalSide.CLIENT, phase);
            this.player = player;
        }

        public static final class Pre extends PlayerTickEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);

            public Pre(Player player) {
                super(player, Phase.START);
            }
        }

        public static final class Post extends PlayerTickEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);

            public Post(Player player) {
                super(player, Phase.END);
            }
        }
    }

    public static sealed abstract class RenderTickEvent extends TickEvent {
        /**
         * @deprecated Use {@link RenderTickEvent.Pre} or {@link RenderTickEvent.Post} instead
         */
        @Deprecated(forRemoval = true, since = "1.21.6")
        public static final EventBus<RenderTickEvent> BUS = EventBus.create(RenderTickEvent.class);

        private final DeltaTracker timer;

        private RenderTickEvent(Phase phase, DeltaTracker timer) {
            super(Type.RENDER, LogicalSide.CLIENT, phase);
            this.timer = timer;
        }

        public DeltaTracker getTimer() {
            return this.timer;
        }

        public static final class Pre extends RenderTickEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);

            public Pre(DeltaTracker timer) {
                super(Phase.START, timer);
            }
        }

        public static final class Post extends RenderTickEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);

            public Post(DeltaTracker timer) {
                super(Phase.END, timer);
            }
        }

    }
}
