/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;

/**
 * A mutable linked map with a hashing strategy and a merge function.
 *
 * @param <K> the type of keys
 * @param <V> the type of mapped values
 */
public class MutableHashedLinkedMap<K, V> implements Iterable<Map.Entry<K, V>>
{
    /**
     * A strategy that uses {@link Objects#hashCode(Object)} and {@link Object#equals(Object)}.
     */
    public static final Strategy<? super Object> BASIC = new BasicStrategy();

    /**
     * A strategy that uses {@link System#identityHashCode(Object)} and {@code a == b} comparisons.
     */
    public static final Strategy<? super Object> IDENTITY = new IdentityStrategy();

    private final Strategy<? super K> strategy;
    private final Map<K, Entry> entries;
    private final MergeFunction<K, V> merge;
    private Entry head = null;
    private Entry last = null;
    /*
     * Number of changes done to the flow of the map, we do not care about value changes.
     * This is to allow the iterator to fast fail with concurrent modification exceptions if needed.
     */
    private transient int changes = 0;

    /**
     * Creates a new instance using the {@link #BASIC} strategy.
     */
    public MutableHashedLinkedMap()
    {
        this(BASIC);
    }

    /**
     * Creates a mutable linked map with a default new-value-selecting merge function.
     *
     * @param strategy the hashing strategy
     */
    public MutableHashedLinkedMap(Strategy<? super K> strategy)
    {
        this(strategy, (k, v1, v2) -> v2);
    }

    /**
     * Creates a mutable linked map with a custom merge function.
     *
     * @param strategy the hashing strategy
     * @param merge the function used when merging an existing value and a new value
     */
    public MutableHashedLinkedMap(Strategy<? super K> strategy, MergeFunction<K, V> merge)
    {
        this.strategy = strategy;
        this.entries = new Object2ObjectOpenCustomHashMap<>(strategy);
        this.merge = merge;
    }

    /**
     * Inserts the mapping with the specified key and value pair.
     *
     * <p>If there is a mapping already associated with this key, then the previous value and the specified (new) value
     * are merged according to this collection's merge function, and the position of the entry is not modified. If there
     * is no such mapping, then the key-value mapping is inserted at the end of this collection.</p>
     *
     * @param key key to be inserted
     * @param value (new) value to be associated with the key
     * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the key
     */
    @Nullable
    public V put(K key, V value)
    {
        var old = entries.get(key);
        if (old != null)
        {
            V ret = old.value;
            old.value = merge.apply(key, ret, value);
            return ret;
        }

        changes++;

        var self = new Entry(key, value);
        var l = last;
        self.previous = l;
        if (l == null)
            head = self;
        else
            l.next = self;
        last = self;

        entries.put(key, self);
        return null;
    }

    public boolean contains(K key) { return this.entries.containsKey(key); }
    public boolean isEmpty() { return this.entries.isEmpty(); }

    @Nullable
    public V remove(K key)
    {
        var ret = this.entries.remove(key);
        if (ret == null)
            return null;

        remove(ret);
        return ret.getValue();
    }

    @Nullable
    public V get(K key)
    {
        var entry = entries.get(key);
        return entry == null ? null : entry.getValue();
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator()
    {
        return new Iterator<>()
        {
            private Entry current = head;
            private Entry last = null;
            private int expectedChanges = changes;

            @Override public boolean hasNext() { return current != null; }
            @Override
            public Map.Entry<K, V> next()
            {
                if (changes != expectedChanges)
                    throw new ConcurrentModificationException();

                if (!hasNext())
                    throw new NoSuchElementException();

                last = current;
                current = current.next;
                return last;
            }

            @Override
            public void remove()
            {
                if (last == null)
                    throw new IllegalStateException("Invalid remove() call, must call next() first");

                if (changes != expectedChanges)
                    throw new ConcurrentModificationException();

                var removed = entries.remove(last.getKey());
                if (removed != last)
                    throw new ConcurrentModificationException();

                expectedChanges++;
                MutableHashedLinkedMap.this.remove(last);
                last = null;
            }
        };
    }

    /**
     * Inserts the mapping with the specified key and value pair at the beginning of this map.
     *
     * <p>If there is a mapping already associated with this key, then the previous value and the specified (new) value
     * are first merged according to this map's merge function, then the entry is moved to the beginning of the map.</p>
     *
     * @param key key to be inserted at the beginning
     * @param value (new) value to be associated with the key
     * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the key
     *
     * @see #putBefore(Object, Object, Object)
     */
    @Nullable
    public V putFirst(K key, V value)
    {
        if (head != null)
            return putBefore(head.getKey(), key, value);
        return put(key, value);
    }

    /**
     * Inserts the mapping with this key and value pair immediately after the entry with the specified positioning key.
     *
     * <p>If the specified positioning key is not present within this map, then this method behaves like
     * {@link #put(Object, Object)}. If there is a mapping already associated with this key, then the previous value
     * and the specified (new) value are first merged according to this map's merge function, then the entry is
     * moved to directly after the entry with the specified positioning key.</p>
     *
     * @param after the key to position this new entry afterwards
     * @param key key to be inserted at the beginning
     * @param value (new) value to be associated with the key
     * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the key
     *
     * @see #putBefore(Object, Object, Object)
     */
    @Nullable
    public V putAfter(K after, K key, V value)
    {
        var target = entries.get(after);
        if (target == null)
            return put(key, value);

        V ret = null;
        var entry = entries.get(key);
        if (entry != null)
        {
            ret = entry.value;
            entry.value = merge.apply(key, ret, value);
            remove(entry);
        }
        else
        {
            entry = new Entry(key, value);
            entries.put(key, entry);
        }

        changes++;

        entry.previous = target;
        if (target.next == null)
            last = target;
        else
            target.next.previous = entry;
        entry.next = target.next;

        target.next = entry;
        return ret;
    }

    /**
     * Inserts the mapping with this key and value pair immediately before the entry with the specified positioning key.
     *
     * <p>If the specified positioning key is not present within this map, then this method behaves like
     * {@link #put(Object, Object)}. If there is a mapping already associated with this key, then the previous value
     * and the specified (new) value are first merged according to this map's merge function, then the entry is
     * moved to directly before the entry with the specified positioning key.</p>
     *
     * @param before the key to position this new entry afterwards
     * @param key key to be inserted at the beginning
     * @param value (new) value to be associated with the key
     * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the key
     *
     * @see #putAfter(Object, Object, Object)
     */
    @Nullable
    public V putBefore(K before, K key, V value)
    {
        var target = entries.get(before);
        if (target == null)
            return put(key, value);

        V ret = null;
        var entry = entries.get(key);
        if (entry != null)
        {
            ret = entry.value;
            entry.value = merge.apply(key, ret, value);
            remove(entry);
        }
        else
        {
            entry = new Entry(key, value);
            entries.put(key, entry);
        }

        changes++;

        entry.previous = target.previous;
        if (target.previous == null)
            head = entry;
        else
            target.previous.next = entry;
        entry.next = target;

        target.previous = entry;

        return ret;
    }

    private void remove(Entry e)
    {
        changes++;

        var previous = e.previous;

        if (head == e)
            head = e.next;
        else if (e.previous != null) // Should never be null, but just in case.
        {
            e.previous.next = e.next;
            e.previous = null;
        }

        if (last == e)
            last = previous;
        else if (e.next != null) // Should never be null, but just in case.
        {
            e.next.previous = previous;
            e.next = null;
        }
    }

    public static interface MergeFunction<Key, Value>
    {
        Value apply(Key key, Value left, Value right);
    }

    private class Entry implements Map.Entry<K, V>
    {
        private final K key;
        private V value;

        private Entry previous;
        private Entry next;

        private Entry(K key, V value)
        {
            this.key = key;
            this.value = value;
        }

        @Override public K getKey() { return this.key; }
        @Override public V getValue() { return this.value; }
        @Override
        public V setValue(V value)
        {
            var old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public String toString()
        {
            return "Entry[" + this.key + ", " + this.value + "]";
        }

        @Override
        public boolean equals(Object o)
        {
            if (!(o instanceof Map.Entry))
                return false;

            Map.Entry<?,?> e = (Map.Entry<?, ?>)o;
            return (key == null ? e.getKey() == null : key.equals(e.getKey())) &&
                   (value == null ? e.getValue() == null : value.equals(e.getValue()));
        }

        @Override
        public int hashCode()
        {
            return (key == null ? 0 : strategy.hashCode(key)) ^
                   (value == null ? 0 : value.hashCode());
        }
    }

    private static class BasicStrategy implements Strategy<Object> {
        @Override
        public int hashCode(Object o) {
            return Objects.hashCode(o);
        }

        @Override
        public boolean equals(Object a, Object b) {
            return Objects.equals(a, b);
        }
    }

    private static class IdentityStrategy implements Strategy<Object> {
        @Override
        public int hashCode(Object o) {
            return System.identityHashCode(o);
        }

        @Override
        public boolean equals(Object a, Object b) {
            return a == b;
        }
    }
}
