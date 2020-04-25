/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
