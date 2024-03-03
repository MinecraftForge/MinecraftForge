/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class AreaContext extends PlayerContext
{
    private final AABB area;

    public AreaContext(Player ep, AABB aabb)
    {
        super(ep);
        area = Preconditions.checkNotNull(aabb, "AxisAlignedBB can't be null in AreaContext!");
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T get(ContextKey<T> key)
    {
        return key.equals(ContextKeys.AREA) ? (T) area : super.get(key);
    }

    @Override
    protected boolean covers(ContextKey<?> key)
    {
        return key.equals(ContextKeys.AREA);
    }
}
