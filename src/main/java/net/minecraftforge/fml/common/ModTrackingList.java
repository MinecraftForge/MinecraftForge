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

import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ForwardingListIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.RandomAccess;

/**
 * Wraps a list and keeps track of which active mod container added each element in the list.
 * Adds the method {@link #getModContainer(Object)}.
 */
public class ModTrackingList<T> extends ForwardingList<T> implements RandomAccess
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

    @Override
    protected List<T> delegate()
    {
        return delegate;
    }

    private void trackModContainer(@Nonnull T element)
    {
        ModContainer modContainer = Loader.instance().activeModContainer();
        if (modContainer != null)
        {
            modContainerMap.put(element, modContainer);
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
            ModContainer modContainer = Loader.instance().activeModContainer();
            if (modContainer != null)
            {
                for (T element : elements)
                {
                    modContainerMap.put(element, modContainer);
                }
            }
        }
        return changed;
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends T> collection)
    {
        boolean changed = delegate.addAll(collection);
        if (changed)
        {
            ModContainer modContainer = Loader.instance().activeModContainer();
            if (modContainer != null)
            {
                for (T element : collection)
                {
                    modContainerMap.put(element, modContainer);
                }
            }
        }
        return changed;
    }

    @Override
    public T remove(int index)
    {
        T removed = delegate.remove(index);
        if (removed != null)
        {
            modContainerMap.remove(removed);
        }
        return removed;
    }

    @Override
    public boolean remove(@Nonnull Object object)
    {
        boolean changed = delegate.remove(object);
        if (changed)
        {
            modContainerMap.remove(object);
        }
        return changed;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> collection)
    {
        boolean changed = delegate.removeAll(collection);
        if (changed)
        {
            modContainerMap.keySet().removeAll(collection);
        }
        return changed;
    }

    @Override
    public T set(int index, @Nonnull T element)
    {
        T previous = delegate.set(index, element);
        if (previous != null)
        {
            modContainerMap.remove(previous);
        }
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
    public boolean retainAll(@Nonnull Collection<?> collection)
    {
        boolean changed = delegate.retainAll(collection);
        if (changed)
        {
            modContainerMap.keySet().retainAll(collection);
        }
        return changed;
    }

    @Nonnull
    @Override
    public Iterator<T> iterator()
    {
        return new ModTrackedIterator(delegate.iterator());
    }

    @Nonnull
    @Override
    public ListIterator<T> listIterator()
    {
        return new ModTrackedListIterator(delegate.listIterator());
    }

    @Nonnull
    @Override
    public ListIterator<T> listIterator(int index)
    {
        return new ModTrackedListIterator(delegate.listIterator(index));
    }

    @Override
    public void clear()
    {
        delegate.clear();
        modContainerMap.clear();
    }

    private class ModTrackedIterator extends ForwardingIterator<T>
    {
        private final Iterator<T> delegate;
        @Nullable
        private T lastValue;

        public ModTrackedIterator(Iterator<T> delegate)
        {
            this.delegate = delegate;
        }

        @Override
        protected Iterator<T> delegate()
        {
            return delegate;
        }

        @Override
        public T next()
        {
            lastValue = delegate.next();
            return lastValue;
        }

        @Override
        public void remove()
        {
            delegate.remove();
            if (lastValue != null)
            {
                modContainerMap.remove(lastValue);
            }
        }
    }

    private class ModTrackedListIterator extends ForwardingListIterator<T>
    {
        private final ListIterator<T> delegate;
        @Nullable
        private T lastValue;

        public ModTrackedListIterator(ListIterator<T> delegate)
        {
            this.delegate = delegate;
        }
        @Override
        protected ListIterator<T> delegate()
        {
            return delegate;
        }

        @Override
        public T next()
        {
            lastValue = delegate.next();
            return lastValue;
        }

        @Override
        public T previous()
        {
            lastValue = delegate.previous();
            return lastValue;
        }

        @Override
        public void remove()
        {
            delegate.remove();
            if (lastValue != null)
            {
                modContainerMap.remove(lastValue);
            }
        }

        @Override
        public void add(@Nonnull T element)
        {
            delegate.add(element);
            trackModContainer(element);
        }

        @Override
        public void set(@Nonnull T element)
        {
            delegate.set(element);
            if (lastValue != null)
            {
                modContainerMap.remove(lastValue);
            }
            trackModContainer(element);
        }
    }
}
