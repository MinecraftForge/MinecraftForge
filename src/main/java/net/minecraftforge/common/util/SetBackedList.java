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
package net.minecraftforge.common.util;

import javax.annotation.Nonnull;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * A List implementation with O(1) {@link #contains(Object)} and {@link #remove(Object)}.
 *
 * Sacrifices performance of ordered operations like {@link #get(int)} and {@link #remove(int)}.
 * Does not support {@link #set(int, Object)} or {@link #add(int, Object)}.
 * Does not support duplicate elements.
 *
 * Only useful for replacing vanilla Lists that are used exactly like Sets, without breaking binary compatibility.
 * If you're making a mod and need an ordered collection with fast contains and remove, use {@link LinkedHashSet} instead.
 */
public class SetBackedList<T> extends AbstractList<T>
{
    private final LinkedHashSet<T> set = new LinkedHashSet<T>();

    @Override
    public boolean add(T element)
    {
        return set.add(element);
    }

    @Override
    public boolean remove(Object o)
    {
        return set.remove(o);
    }

    @Override
    public T remove(int index)
    {
        T value = get(index);
        if (remove(value))
        {
            return value;
        }
        return null;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c)
    {
        return set.removeAll(c);
    }

    @Override
    public boolean contains(Object o)
    {
        return set.contains(o);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c)
    {
        return set.containsAll(c);
    }

    @Override
    public int size()
    {
        return set.size();
    }

    @Override
    public void clear()
    {
        set.clear();
    }

    @Nonnull
    @Override
    public Iterator<T> iterator()
    {
        return set.iterator();
    }

    @Override
    public T get(int index)
    {
        for (T value : set)
        {
            if (index == 0)
            {
                return value;
            }
            else
            {
                index--;
            }
        }
        throw new IndexOutOfBoundsException();
    }
}
