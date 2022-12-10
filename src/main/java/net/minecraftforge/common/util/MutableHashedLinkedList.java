/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.common.util;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;

public class MutableHashedLinkedList<K, V> implements Iterable<Map.Entry<K, V>>
{
    private final Map<K, Entry> entries;
    private final MergeFunction<K, V> merge;
    private final MutableObject<Entry> head = new MutableObject<>();
    private final MutableObject<Entry> last = new MutableObject<>();

    public MutableHashedLinkedList(Strategy<? super K> strategy) {
        this(strategy, (k, v1, v2) -> v2);
    }

    public MutableHashedLinkedList(Strategy<? super K> strategy, MergeFunction<K, V> merge) {
        this.entries = new Object2ObjectOpenCustomHashMap<>(strategy);
        this.merge = merge;
    }

    public V put(K key, V value) {
        var old = entries.get(key);
        if (old != null) {
            V ret = old.value;
            old.value = merge.apply(key, ret, value);
            return ret;
        }

        var self = new Entry(key, value);

        var l = last.getValue();
        self.previous = l;
        if (l == null)
            head.setValue(self);
        else
            l.next = self;
        last.setValue(self);

        entries.put(key, self);
        return null;
    }

    public boolean contains(K key) { return this.entries.containsKey(key); }
    public boolean isEmpty() { return this.entries.isEmpty(); }

    @Nullable
    public V remove(K key) {
        var ret = this.entries.remove(key);
        if (ret == null)
            return null;

        ret.remove();
        return ret.getValue();
    }

    @Nullable
    public V get(K key) {
        var entry = entries.get(key);
        return entry == null ? null : entry.getValue();
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new Iterator<Map.Entry<K, V>>() {
            private Entry current = head.getValue();

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Map.Entry<K, V> next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                var ret = current;
                current = current.next;
                return ret;
            }
        };
    }

    @Nullable
    public V putFirst(K key, V value) {
        if (head.getValue() != null)
            return putBefore(head.getValue().getKey(), key, value);
        return put(key, value);
    }

    @Nullable
    public V putAfter(K after, K key, V value) {
        var target = entries.get(after);
        if (target == null)
            return put(key, value);

        V ret = null;
        var entry = entries.get(key);
        if (entry != null) {
            ret = entry.value;
            entry.value = merge.apply(key, ret, value);
            entry.remove();
        } else {
            entry = new Entry(key, value);
            entries.put(key, entry);
        }

        entry.previous = target;
        if (target.next == null)
            last.setValue(target);
        else
            target.next.previous = entry;
        entry.next = target.next;

        target.next = entry;
        return ret;
    }

    @Nullable
    public V putBefore(K before, K key, V value) {
        var target = entries.get(before);
        if (target == null)
            return put(key, value);

        V ret = null;
        var entry = entries.get(key);
        if (entry != null) {
            ret = entry.value;
            entry.value = merge.apply(key, ret, value);
            entry.remove();
        } else {
            entry = new Entry(key, value);
            entries.put(key, entry);
        }

        entry.previous = target.previous;
        if (target.previous == null)
            head.setValue(entry);
        else
            target.previous.next = entry;
        entry.next = target;

        target.previous = entry;

        return ret;
    }

    public static interface MergeFunction<Key, Value> {
        Value apply(Key key, Value left, Value right);
    }

    private class Entry implements Map.Entry<K, V> {
        private final K key;
        private V value;

        private Entry previous;
        private Entry next;

        private Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        private void remove() {
            if (this.previous == null)
                head.setValue(this.next);
            else {
                this.previous.next = this.next;
                this.previous = null;
            }

            if (this.next == null)
                last.setValue(this.previous);
            else {
                this.next.previous = this.previous;
                this.next = null;
            }
        }

        @Override public K getKey() { return this.key; }
        @Override public V getValue() { return this.value; }
        @Override
        public V setValue(V value) {
            var old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public String toString() {
            return "Entry[" + this.key + ", " + this.value + "]";
        }
    }
}
