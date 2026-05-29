/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.DeltaTracker;
import net.minecraft.server.MinecraftServer;

import java.util.function.BooleanSupplier;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.fml.LogicalSide;
import org.jspecify.annotations.NullMarked;

@NullMarked
public sealed interface TickEvent {
    sealed interface ServerTickEvent extends TickEvent {
        BooleanSupplier haveTimeSupplier();

        /**
         * @return {@code true} whether the server has enough time to perform any
         * additional tasks (usually IO related) during the current tick,
         * otherwise {@code false}
         */
        default boolean haveTime() {
            return haveTimeSupplier().getAsBoolean();
        }

        /**
         * {@return the server instance}
         */
        MinecraftServer server();

        record Pre(BooleanSupplier haveTimeSupplier, MinecraftServer server) implements RecordEvent, ServerTickEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);
        }

        record Post(BooleanSupplier haveTimeSupplier, MinecraftServer server) implements RecordEvent, ServerTickEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }

    sealed interface ClientTickEvent extends TickEvent {
        record Pre() implements RecordEvent, ClientTickEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);
            public static final Pre INSTANCE = new Pre();
        }

        record Post() implements RecordEvent, ClientTickEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
            public static final Post INSTANCE = new Post();
        }
    }

    sealed interface LevelTickEvent extends TickEvent {
        LogicalSide side();

        Level level();

        BooleanSupplier haveTimeSupplier();

        /**
         * @return {@code true} whether the server has enough time to perform any
         * additional tasks (usually IO related) during the current tick,
         * otherwise {@code false}
         * @see ServerTickEvent#haveTime()
         */
        default boolean haveTime() {
            return haveTimeSupplier().getAsBoolean();
        }

        record Pre(LogicalSide side, Level level, BooleanSupplier haveTimeSupplier) implements RecordEvent, LevelTickEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);
        }

        record Post(LogicalSide side, Level level, BooleanSupplier haveTimeSupplier) implements RecordEvent, LevelTickEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }

    sealed interface PlayerTickEvent extends TickEvent {
        LogicalSide side();

        record Pre(Player player, LogicalSide side) implements RecordEvent, PlayerTickEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);

            public Pre(Player player) {
                this(player, player instanceof ServerPlayer ? LogicalSide.SERVER : LogicalSide.CLIENT);
            }
        }

        record Post(Player player, LogicalSide side) implements RecordEvent, PlayerTickEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);

            public Post(Player player) {
                this(player, player instanceof ServerPlayer ? LogicalSide.SERVER : LogicalSide.CLIENT);
            }
        }
    }

    sealed interface RenderTickEvent extends TickEvent {
        record Pre(DeltaTracker timer) implements RecordEvent, RenderTickEvent {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);
        }

        record Post(DeltaTracker timer) implements RecordEvent, RenderTickEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }
}
