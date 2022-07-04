/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

/**
 * A list that concatenates multiple other lists for efficient iteration.<p/>
 * You may use this in place of creating a new list and calling {@link List#addAll(Collection)}
 * for each of your collections.<p/>
 * This list does not support modification operations, but the underlying lists may be mutated safely externally.
 */
public class ConcatenatedListView<T> implements List<T>
{
    public static <T> List<T> of(List<? extends List<? extends T>> members)
    {
        return switch (members.size()) {
            case 0 -> List.of();
            case 1 -> Collections.unmodifiableList(members.get(0));
            default -> new ConcatenatedListView<>(members);
        };
    }

    private final List<? extends List<? extends T>> lists;

    private ConcatenatedListView(List<? extends List<? extends T>> lists)
    {
        this.lists = lists;
    }

    @Override
    public int size()
    {
        int size = 0;
        for (var list : lists)
            size += list.size();
        return size;
    }

    @Override
    public boolean isEmpty()
    {
        for (List<? extends T> list : lists)
            if (!list.isEmpty())
                return false;
        return true;
    }

    @Override
    public boolean contains(Object o)
    {
        for (var list : lists)
            if (list.contains(o))
                return true;
        return false;
    }

    @Override
    public T get(int index)
    {
        for (var list : lists)
        {
            int size = list.size();
            if (index < size)
                return list.get(index);
            index -= size;
        }
        throw new IndexOutOfBoundsException(index);
    }

    @Override
    public int indexOf(Object o)
    {
        int offset = 0;
        for (var list : lists)
        {
            int foundIndex = list.indexOf(o);
            if (foundIndex >= 0)
                return offset + foundIndex;
            offset += list.size();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o)
    {
        int offset = 0;
        for (var list : Lists.reverse(lists))
        {
            int foundIndex = list.lastIndexOf(o);
            if (foundIndex >= 0)
                return offset + foundIndex;
            offset += list.size();
        }
        return -1;
    }

    @NotNull
    @Override
    public Iterator<T> iterator()
    {
        return Iterables.unmodifiableIterable(Iterables.concat(lists)).iterator();
    }

    @Override
    public Spliterator<T> spliterator()
    {
        return Iterables.unmodifiableIterable(Iterables.concat(lists)).spliterator();
    }

    // Delegate to a concatenated collection
    private <C extends Collection<T>> C concatenate(Supplier<C> collectionFactory)
    {
        var concat = collectionFactory.get();
        for (var list : lists)
            concat.addAll(list);
        return concat;
    }
    @NotNull @Override public Object[] toArray() { return concatenate(ArrayList::new).toArray(); }
    @NotNull @Override public <T1> T1[] toArray(@NotNull T1[] a) { return concatenate(ArrayList::new).toArray(a); }
    @Override public boolean containsAll(@NotNull Collection<?> c) { return concatenate(HashSet::new).containsAll(c); }

    // No mutations allowed
    @Override public boolean add(T t) { throw new UnsupportedOperationException(); }
    @Override public void add(int index, T element) { throw new UnsupportedOperationException(); }
    @Override public T set(int index, T element) { throw new UnsupportedOperationException(); }
    @Override public boolean addAll(@NotNull Collection<? extends T> c) { throw new UnsupportedOperationException(); }
    @Override public boolean addAll(int index, @NotNull Collection<? extends T> c) { throw new UnsupportedOperationException(); }
    @Override public boolean remove(Object o) { throw new UnsupportedOperationException(); }
    @Override public T remove(int index) { throw new UnsupportedOperationException(); }
    @Override public boolean removeAll(@NotNull Collection<?> c) { throw new UnsupportedOperationException(); }
    @Override public boolean retainAll(@NotNull Collection<?> c) { throw new UnsupportedOperationException(); }
    @Override public void clear() { throw new UnsupportedOperationException(); }

    // Other unsupported operations - we could support these, but effort
    @NotNull @Override public ListIterator<T> listIterator() { throw new UnsupportedOperationException(); }
    @NotNull @Override public ListIterator<T> listIterator(int index) { throw new UnsupportedOperationException(); }
    @NotNull @Override public List<T> subList(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
}
