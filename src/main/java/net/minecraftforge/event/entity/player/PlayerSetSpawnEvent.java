/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired when a player's spawn point is set or reset.
 * <p>
 * The event can be canceled, which will prevent the spawn point from being changed.
 */
@Cancelable
public final class PlayerSetSpawnEvent extends PlayerEvent {
    private final @Nullable ServerPlayer.RespawnConfig config;

    @Deprecated(forRemoval = true, since = "1.21.5")
    private final ResourceKey<Level> spawnLevel;
    @Deprecated(forRemoval = true, since = "1.21.5")
    private final boolean forced;
    @Deprecated(forRemoval = true, since = "1.21.5")
    private final @Nullable BlockPos newSpawn;

    public PlayerSetSpawnEvent(ServerPlayer player, @Nullable ServerPlayer.RespawnConfig config) {
        super(player);
        this.config = config;

        boolean hasConfig = config != null;
        this.forced = hasConfig && config.forced();
        this.spawnLevel = hasConfig ? config.dimension() : Level.OVERWORLD;
        this.newSpawn = hasConfig ? config.pos() : null;
    }

    /** @return The config for the player respawn */
    public @Nullable ServerPlayer.RespawnConfig getConfig() {
        return this.config;
    }

    /**
     * @return If the new spawn point is forced
     * @deprecated Use {@link ServerPlayer.RespawnConfig#forced()} via {@link #getConfig()}
     */
    @Deprecated(forRemoval = true, since = "1.21.5")
    public boolean isForced() {
        return this.forced;
    }

    /**
     * @return The new spawn position, or {@code null} if the spawn position is being reset
     * @deprecated Use {@link ServerPlayer.RespawnConfig#pos()} via {@link #getConfig()}
     */
    @Deprecated(forRemoval = true, since = "1.21.5")
    public @Nullable BlockPos getNewSpawn() {
        return this.newSpawn;
    }

    /**
     * @return The new spawn dimension, defaulting to {@link Level#OVERWORLD} if it is {@code null}
     * @deprecated Use {@link ServerPlayer.RespawnConfig#dimension()} via {@link #getConfig()}
     */
    @Deprecated(forRemoval = true, since = "1.21.5")
    public ResourceKey<Level> getSpawnLevel() {
        return this.spawnLevel;
    }
}
