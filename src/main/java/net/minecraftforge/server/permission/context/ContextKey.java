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

import com.google.common.base.Preconditions;

public final class ContextKey<T>
{
    private final String ID;
    private final Class<T> typeClass;

    public static <E> ContextKey<E> create(String id, Class<E> c)
    {
        Preconditions.checkNotNull(id, "ContextKey's ID can't be null!");
        Preconditions.checkNotNull(c, "ContextKey's Type can't be null!");

        if(id.isEmpty())
        {
            throw new IllegalArgumentException("ContextKey's ID can't be blank!");
        }

        return new ContextKey<E>(id, c);
    }

    private ContextKey(String id, Class<T> c)
    {
        ID = id;
        typeClass = c;
    }

    public String toString()
    {
        return ID;
    }

    public int hashCode()
    {
        return ID.hashCode();
    }

    public boolean equals(Object o)
    {
        return o == this || (o != null && o.toString().equals(ID));
    }

    public Class<T> getTypeClass()
    {
        return typeClass;
    }
}