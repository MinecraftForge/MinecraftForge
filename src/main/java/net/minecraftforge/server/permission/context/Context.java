/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.context;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Context implements IContext
{
    private Map<ContextKey<?>, Object> map;

    @Override
    @Nullable
    public World getWorld()
    {
        return null;
    }

    @Override
    @Nullable
    public PlayerEntity getPlayer()
    {
        return null;
    }

    @Override
    @Nullable
    public <T> T get(ContextKey<T> key)
    {
        return map == null || map.isEmpty() ? null : (T) map.get(key);
    }

    @Override
    public boolean has(ContextKey<?> key)
    {
        return covers(key) || (map != null && !map.isEmpty() && map.containsKey(key));
    }

    /**
     * Sets Context object
     *
     * @param key Context key
     * @param obj Context object. Can be null
     * @return itself, for easy context chaining
     */
    public <T> Context set(ContextKey<T> key, @Nullable T obj)
    {
        if(covers(key))
        {
            return this;
        }

        if(map == null)
        {
            map = new HashMap<ContextKey<?>, Object>();
        }

        map.put(key, obj);
        return this;
    }

    protected boolean covers(ContextKey<?> key)
    {
        return false;
    }
}
