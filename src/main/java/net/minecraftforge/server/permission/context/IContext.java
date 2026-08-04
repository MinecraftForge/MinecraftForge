/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.context;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Use {@link BlockPosContext} or {@link PlayerContext} when possible
 */
public interface IContext
{
    /**
     * World from where permission is requested. Can be null
     */
    @Nullable
    World getWorld();

    /**
     * @return Player requesting permission. Can be null
     */
    @Nullable
    PlayerEntity getPlayer();

    /**
     * @param key Context key
     * @return Context object
     */
    @Nullable
    <T> T get(ContextKey<T> key);

    /**
     * @param key Context key
     * @return true if context contains this key
     */
    boolean has(ContextKey<?> key);
}
