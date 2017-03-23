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

package net.minecraftforge.fml.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

/**
 * Wraps a list and keeps track of which active mod container added each element in the list.
 * Adds the method {@link #getModContainer(Object)}.
 */
public class ModTrackingList<T> extends AbstractList<T> implements RandomAccess
{
    private final List<T> delegate;
    private final Map<T, ModContainer> modContainerMap;

    public <D extends List<T> & RandomAccess> ModTrackingList(@Nonnull D delegate)
    {
        this(delegate, new IdentityHashMap<T, ModContainer>());
    }

    private ModTrackingList(@Nonnull List<T> delegate, @Nonnull Map<T, ModContainer> modContainerMap)
    {
        this.delegate = delegate;
        this.modContainerMap = modContainerMap;
    }

    @Nullable
    public ModContainer getModContainer(@Nonnull T element)
    {
        return modContainerMap.get(element);
    }

    private void trackModContainer(@Nonnull T element)
    {
        if (!modContainerMap.containsKey(element))
        {
            ModContainer modContainer = Loader.instance().activeModContainer();
            if (modContainer != null)
            {
                modContainerMap.put(element, modContainer);
            }
        }
    }

    private void trackModContainer(@Nonnull Collection<? extends T> elements)
    {
        ModContainer modContainer = Loader.instance().activeModContainer();
        if (modContainer != null)
        {
            for (T element : elements)
            {
                if (!modContainerMap.containsKey(element))
                {
                    modContainerMap.put(element, modContainer);
                }
            }
        }
    }

    @Override
    public boolean add(@Nonnull T t)
    {
        boolean changed = delegate.add(t);
        if (changed)
        {
            trackModContainer(t);
        }
        return changed;
    }

    @Override
    public T get(int index)
    {
        return delegate.get(index);
    }

    @Override
    public void add(int index, @Nonnull T element)
    {
        delegate.add(index, element);
        trackModContainer(element);
    }

    @Override
    public boolean addAll(int index, @Nonnull Collection<? extends T> elements)
    {
        boolean changed = delegate.addAll(index, elements);
        if (changed)
        {
            trackModContainer(elements);
        }
        return changed;
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends T> collection)
    {
        boolean changed = delegate.addAll(collection);
        if (changed)
        {
            trackModContainer(collection);
        }
        return changed;
    }

    @Override
    public T remove(int index)
    {
        return delegate.remove(index);
    }

    @Override
    public boolean remove(@Nonnull Object object)
    {
        return delegate.remove(object);
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> collection)
    {
        return delegate.removeAll(collection);
    }

    @Override
    public T set(int index, @Nonnull T element)
    {
        T previous = delegate.set(index, element);
        trackModContainer(element);
        return previous;
    }

    @Nonnull
    @Override
    public List<T> subList(int fromIndex, int toIndex)
    {
        List<T> delegateSubList = delegate.subList(fromIndex, toIndex);
        return new ModTrackingList<T>(delegateSubList, modContainerMap);
    }

    @Override
    public int size()
    {
        return delegate.size();
    }

    @Override
    public void clear()
    {
        delegate.clear();
    }
}
