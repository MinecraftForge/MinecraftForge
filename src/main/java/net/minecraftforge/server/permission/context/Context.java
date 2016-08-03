/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Context implements IContext
{
    private Map<ContextKey<?>, Object> map;

    @Override
    public <T> T get(@Nonnull ContextKey<T> key)
    {
        return map == null || map.isEmpty() ? null : (T) map.get(key);
    }

    @Override
    public boolean has(@Nonnull ContextKey<?> key)
    {
        return map != null && !map.isEmpty() && map.containsKey(key);
    }

    @Nonnull
    @Override
    public <T> Context set(@Nonnull ContextKey<T> key, @Nullable T obj)
    {
        if(map == null)
        {
            map = new HashMap<ContextKey<?>, Object>();
        }

        map.put(key, obj);
        return this;
    }
}