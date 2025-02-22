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
import net.minecraftforge.eventbus.api.event.Event;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.SelfPosting;
import net.minecraftforge.fml.LogicalSide;

public sealed interface TickEvent {
    enum Type {
        LEVEL, PLAYER, CLIENT, SERVER, RENDER
    }

    Type type();
    LogicalSide side();

    sealed interface ServerTickEvent extends TickEvent {
        @Override
        default Type type() {
            return Type.SERVER;
        }

        @Override
        default LogicalSide side() {
            return LogicalSide.SERVER;
        }

        /**
         * @return {@code true} whether the server has enough time to perform any
         * additional tasks (usually IO related) during the current tick,
         * otherwise {@code false}
         */
        default boolean haveTime() {
            return haveTimeSupplier().getAsBoolean();
        }

        BooleanSupplier haveTimeSupplier();

        /**
         * {@return the server instance}
         */
        MinecraftServer server();

        record Pre(BooleanSupplier haveTimeSupplier, MinecraftServer server) implements ServerTickEvent, RecordEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);
        }

        record Post(BooleanSupplier haveTimeSupplier, MinecraftServer server) implements ServerTickEvent, RecordEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }

    sealed interface ClientTickEvent<T extends Event> extends TickEvent, SelfPosting<T> {
        @Override
        default Type type() {
            return Type.CLIENT;
        }

        @Override
        default LogicalSide side() {
            return LogicalSide.CLIENT;
        }

        final class Pre extends MutableEvent implements ClientTickEvent<Pre> {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);
            public static final Pre INSTANCE = new Pre();

            private Pre() {}

            @Override
            public EventBus<Pre> getDefaultBus() {
                return BUS;
            }
        }

        final class Post extends MutableEvent implements ClientTickEvent<Post> {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
            public static final Post INSTANCE = new Post();
            
            private Post() {}

            @Override
            public EventBus<Post> getDefaultBus() {
                return BUS;
            }
        }
    }

    sealed interface LevelTickEvent extends TickEvent {
        @Override
        default Type type() {
            return Type.LEVEL;
        }

        Level level();

        /**
         * @return {@code true} whether the server has enough time to perform any
         * additional tasks (usually IO related) during the current tick,
         * otherwise {@code false}
         * @see ServerTickEvent#haveTime()
         */
        default boolean haveTime() {
            return haveTimeSupplier().getAsBoolean();
        }

        BooleanSupplier haveTimeSupplier();

        record Pre(LogicalSide side, Level level, BooleanSupplier haveTimeSupplier) implements LevelTickEvent, RecordEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);
        }

        record Post(LogicalSide side, Level level, BooleanSupplier haveTimeSupplier) implements LevelTickEvent, RecordEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }

    sealed interface PlayerTickEvent extends TickEvent {
        @Override
        default Type type() {
            return Type.PLAYER;
        }

        Player player();

        record Pre(Player player, LogicalSide side) implements RecordEvent, PlayerTickEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);

            public Pre(Player player) {
                this(player, getSide(player));
            }
        }

        record Post(Player player, LogicalSide side) implements RecordEvent, PlayerTickEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);

            public Post(Player player) {
                this(player, getSide(player));
            }
        }

        private static LogicalSide getSide(Player player) {
            return player instanceof ServerPlayer ? LogicalSide.SERVER : LogicalSide.CLIENT;
        }
    }

    sealed interface RenderTickEvent extends TickEvent {
        @Override
        default Type type() {
            return Type.RENDER;
        }

        @Override
        default LogicalSide side() {
            return LogicalSide.CLIENT;
        }

        DeltaTracker timer();

        record Pre(DeltaTracker timer) implements RenderTickEvent, RecordEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);
        }

        record Post(DeltaTracker timer) implements RenderTickEvent, RecordEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }
}
