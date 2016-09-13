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

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class Context implements IContext
{
    private Map<String, Object> map;

    @Override
    public <T> T get(String key)
    {
        return map == null || map.isEmpty() ? null : (T) map.get(key);
    }

    @Override
    public boolean has(String key)
    {
        return map != null && !map.isEmpty() && map.containsKey(key);
    }

    @Override
    public <T> IContext set(String key, @Nullable T obj)
    {
        if(map == null)
        {
            map = new HashMap<String, Object>();
        }

        map.put(key, obj);
        return this;
    }
}