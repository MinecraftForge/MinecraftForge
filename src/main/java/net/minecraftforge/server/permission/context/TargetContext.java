/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.context;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class TargetContext extends PlayerContext
{
    private final Entity target;

    public TargetContext(Player ep, @Nullable Entity entity)
    {
        super(ep);
        target = entity;
    }

    @Override
    @Nullable
    public <T> T get(ContextKey<T> key)
    {
        return key.equals(ContextKeys.TARGET) ? (T) target : super.get(key);
    }

    @Override
    protected boolean covers(ContextKey<?> key)
    {
        return target != null && key.equals(ContextKeys.TARGET);
    }
}
