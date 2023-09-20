/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.test;

import it.unimi.dsi.fastutil.Hash.Strategy;
import net.minecraftforge.common.util.MutableHashedLinkedMap;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class MutableHashedLinkedMapTest {
    private static final Strategy<? super String> FIRST_CHARACTER = strat(
        k -> k == null ? 0 : Character.hashCode(k.charAt(0)),
        (a, b) -> a == null ? b == null : b == null ? false : a.charAt(0) == b.charAt(0)
    );

    @Test
    void retainsInsertOrder() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        assertArrayEquals(arr("first", "second", "third"), keysArray(map));
        assertArrayEquals(arr(1, 2, 3), valuesArray(map));
    }

    @Test
    void retainsInsertOrderAfterOverride() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var old = map.put("second", 5);

        assertEquals(old, 2);
        assertArrayEquals(arr("first", "second", "third"), keysArray(map));
        assertArrayEquals(arr(1, 5, 3), valuesArray(map));
    }

    @Test
    void retainsInsertOrderAfterOverrideWithStrategy() {
        var map = new MutableHashedLinkedMap<String, Integer>(FIRST_CHARACTER);
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var old = map.put("should override", 5);

        assertEquals(old, 2);
        assertArrayEquals(arr("first", "second", "third"), keysArray(map));
        assertArrayEquals(arr(1, 5, 3), valuesArray(map));
    }


    @Test
    void strategyCollisionDoesNotOverwriteKey() {
        var map = new MutableHashedLinkedMap<String, Integer>(FIRST_CHARACTER);
        map.put("static", 1);

        var old = map.put("shouldn't change the name, but should change the value", 5);

        assertEquals(old, 1);
        assertArrayEquals(arr("static"), keysArray(map));
        assertArrayEquals(arr(5), valuesArray(map));
    }

    @Test
    void putFirst() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("second", 2);
        map.put("third", 3);

        var old = map.putFirst("first", 1);

        assertNull(old);
        assertArrayEquals(arr("first", "second", "third"), keysArray(map));
        assertArrayEquals(arr(1, 2, 3), valuesArray(map));
    }

    @Test
    void putAfter() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("third", 3);

        var old = map.putAfter("first", "second", 2);

        assertNull(old);
        assertArrayEquals(arr("first", "second", "third"), keysArray(map));
        assertArrayEquals(arr(1, 2, 3), valuesArray(map));
    }

    @Test
    void putAfterWithStrategy() {
        var map = new MutableHashedLinkedMap<String, Integer>(FIRST_CHARACTER);
        map.put("first", 1);
        map.put("third", 3);

        var old = map.putAfter("first entry", "second", 2);

        assertNull(old);
        assertArrayEquals(arr("first", "second", "third"), keysArray(map));
        assertArrayEquals(arr(1, 2, 3), valuesArray(map));
    }

    @Test
    void putBefore() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("third", 3);

        var old = map.putBefore("third", "second", 2);

        assertNull(old);
        assertArrayEquals(arr("first", "second", "third"), keysArray(map));
        assertArrayEquals(arr(1, 2, 3), valuesArray(map));
    }

    @Test
    void putBeforeWithStrategy() {
        var map = new MutableHashedLinkedMap<String, Integer>(FIRST_CHARACTER);
        map.put("first", 1);
        map.put("third", 3);

        var old = map.putBefore("third entry", "second", 2);

        assertNull(old);
        assertArrayEquals(arr("first", "second", "third"), keysArray(map));
        assertArrayEquals(arr(1, 2, 3), valuesArray(map));
    }

    @Test
    void isEmpty() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        assertTrue(map.isEmpty(), "Freshly initalized map is not empty!");
        map.put("entry", 1);
        assertFalse(map.isEmpty(), "Map is empty, but contains an element");
        map.remove("entry");
        assertTrue(map.isEmpty(), "Map is not empty after removing its only entry");
    }

    @Test
    void get() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        assertEquals(1, map.get("first"));
        assertEquals(2, map.get("second"));
        assertEquals(3, map.get("third"));
    }

    @Test
    void getWithStrategy() {
        var map = new MutableHashedLinkedMap<String, Integer>(FIRST_CHARACTER);
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        assertEquals(1, map.get("first entry"));
        assertEquals(2, map.get("second entry"));
        assertEquals(3, map.get("third entry"));
    }

    @Test
    void contains() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        assertTrue(map.contains("first"));
        assertTrue(map.contains("second"));
        assertTrue(map.contains("third"));
    }

    @Test
    void containsWithStrategy() {
        var map = new MutableHashedLinkedMap<String, Integer>(FIRST_CHARACTER);
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        assertTrue(map.contains("first entry"));
        assertTrue(map.contains("second entry"));
        assertTrue(map.contains("third entry"));
    }

    @Test
    void removeHead() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var old = map.remove("first");

        assertEquals(1, old);
        assertArrayEquals(arr("second", "third"), keysArray(map));
        assertArrayEquals(arr(2, 3), valuesArray(map));
    }

    @Test
    void removeHeadWithStrategy() {
        var map = new MutableHashedLinkedMap<String, Integer>(FIRST_CHARACTER);
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var old = map.remove("first entry");

        assertEquals(1, old);
        assertArrayEquals(arr("second", "third"), keysArray(map));
        assertArrayEquals(arr(2, 3), valuesArray(map));
    }

    @Test
    void removeMiddle() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var old = map.remove("second");

        assertEquals(2, old);
        assertArrayEquals(arr("first", "third"), keysArray(map));
        assertArrayEquals(arr(1, 3), valuesArray(map));
    }

    @Test
    void removeMiddleWithStrategy() {
        var map = new MutableHashedLinkedMap<String, Integer>(FIRST_CHARACTER);
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var old = map.remove("second entry");

        assertEquals(2, old);
        assertArrayEquals(arr("first", "third"), keysArray(map));
        assertArrayEquals(arr(1, 3), valuesArray(map));
    }

    @Test
    void removeMultipleMiddle() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);
        map.put("forth", 4);
        map.put("fifth", 5);
        map.put("sixth", 6);

        assertEquals(2, map.remove("second"));
        assertEquals(3, map.remove("third"));
        assertEquals(4, map.remove("forth"));

        assertArrayEquals(arr("first", "fifth", "sixth"), keysArray(map));
        assertArrayEquals(arr(1, 5, 6), valuesArray(map));
    }

    @Test
    void removeMultipleMiddleWithStrategy() {
        var map = new MutableHashedLinkedMap<String, Integer>(FIRST_CHARACTER);
        map.put("1st", 1);
        map.put("2nd", 2);
        map.put("3rd", 3);
        map.put("4th", 4);
        map.put("5th", 5);
        map.put("6th", 6);

        assertEquals(2, map.remove("2nd entry"));
        assertEquals(3, map.remove("3rd entry"));
        assertEquals(4, map.remove("4th entry"));

        assertArrayEquals(arr("1st", "5th", "6th"), keysArray(map));
        assertArrayEquals(arr(1, 5, 6), valuesArray(map));
    }

    @Test
    void removeTail() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var old = map.remove("third");

        assertEquals(3, old);
        assertArrayEquals(arr("first", "second"), keysArray(map));
        assertArrayEquals(arr(1, 2), valuesArray(map));
    }

    @Test
    void removeTailWithStrategy() {
        var map = new MutableHashedLinkedMap<String, Integer>(FIRST_CHARACTER);
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var old = map.remove("third entry");

        assertEquals(3, old);
        assertArrayEquals(arr("first", "second"), keysArray(map));
        assertArrayEquals(arr(1, 2), valuesArray(map));
    }

    @Test
    void iteratorInOrder() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var itr = map.iterator();

        assertEquals("first", itr.next().getKey());
        assertEquals("second", itr.next().getKey());
        assertEquals("third", itr.next().getKey());
        assertFalse(itr.hasNext());
    }

    @Test
    void iteratorRemoveHead() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var itr = map.iterator();
        while (itr.hasNext()) {
            var next = itr.next();
            if (next.getKey().equals("first"))
                itr.remove();
        }

        assertArrayEquals(arr("second", "third"), keysArray(map));
        assertArrayEquals(arr(2, 3), valuesArray(map));
    }

    @Test
    void iteratorRemoveMiddle() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var itr = map.iterator();
        while (itr.hasNext()) {
            var next = itr.next();
            if (next.getKey().equals("second"))
                itr.remove();
        }

        assertArrayEquals(arr("first", "third"), keysArray(map));
        assertArrayEquals(arr(1, 3), valuesArray(map));
    }

    @Test
    void iteratorRemoveEnd() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var itr = map.iterator();
        while (itr.hasNext()) {
            var next = itr.next();
            if (next.getKey().equals("third"))
                itr.remove();
        }

        assertArrayEquals(arr("first", "second"), keysArray(map));
        assertArrayEquals(arr(1, 2), valuesArray(map));
    }

    @Test
    void iteratorRemoveAll() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var itr = map.iterator();
        while (itr.hasNext()) {
            itr.next();
            itr.remove();
        }

        assertTrue(map.isEmpty());
        assertArrayEquals(arr(), keysArray(map));
        assertArrayEquals(arr(), valuesArray(map));
    }

    @Test
    void iteratorRemoveRequiresNext() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var itr = map.iterator();
        assertThrows(IllegalStateException.class, () -> itr.remove());
    }

    @Test
    void iteratorNextThrowsOnAdded() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var itr = map.iterator();
        map.put("forth", 4);

        assertThrows(ConcurrentModificationException.class, () -> itr.next());
    }

    @Test
    void iteratorNextThrowsOnRemoved() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var itr = map.iterator();
        map.remove("first");

        assertThrows(ConcurrentModificationException.class, () -> itr.next());
    }

    @Test
    void iteratorRemoveThrowsOnAdded() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var itr = map.iterator();
        itr.next();
        map.put("forth", 4);

        assertThrows(ConcurrentModificationException.class, () -> itr.remove());
    }

    @Test
    void iteratorRemoveThrowsOnRemoved() {
        var map = new MutableHashedLinkedMap<String, Integer>();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        var itr = map.iterator();
        itr.next();
        map.remove("first");

        assertThrows(ConcurrentModificationException.class, () -> itr.remove());
    }

    @Test
    void paintingsTest() {
        var map = new MutableHashedLinkedMap<Character, Integer>();
        for (char x = 'a'; x <= 'z'; x++)
            map.put(x, (int)x);
        map.remove('i');
        map.remove('j');
        map.remove('k');
        map.remove('l');
        var keys = stream(map).map(e->e.getKey()).collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString));
        assertEquals("abcdefghmnopqrstuvwxyz", keys);
    }



    private static <K> Strategy<K> strat(ToIntFunction<K> hash, BiPredicate<K,K> equals) {
        return new Strategy<K>() {
            @Override public int hashCode(K o) { return hash.applyAsInt(o); }
            @Override public boolean equals(K a, K b) { return equals.test(a, b); }
        };
    }

    @SafeVarargs
    private static <T> T[] arr(T... values) {
         return values;
    }

    private static <K,V> Stream<Map.Entry<K,V>> stream(MutableHashedLinkedMap<K,V> map) {
        return StreamSupport.stream(map.spliterator(), false);
    }

    @SuppressWarnings("unchecked")
    private static <K> K[] keysArray(MutableHashedLinkedMap<K, ?> map) {
        return (K[])stream(map).map(e -> e.getKey()).toArray();
    }
    @SuppressWarnings("unchecked")
    private static <V> V[] valuesArray(MutableHashedLinkedMap<?, V> map) {
        return (V[])stream(map).map(e -> e.getValue()).toArray();
    }
}
