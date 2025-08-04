/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

public interface IForgeWeightedList<E> {
    private WeightedList<E> self() {
        return (WeightedList<E>) this;
    }

    /**
     * Removes any existing elements from this list. If removal takes place, this will create a new list.
     * <p>To check if the builder elements have changed, simply check if the old and new lists are equal.</p>
     * {@snippet :
     * private static void test() {
     *     var list = WeightedList.<String>builder().add("Hello, World!", 1).build();
     *
     *     // if builder has no removals
     *     assert list == list.removeIf(String::isEmpty);
     *
     *     // if builder has removals
     *     assert list != list.removeIf(s -> s.equals("Hello, World!"));
     * }
     *}
     *
     * @param predicate The predicate to remove elements with
     * @return The builder
     * @apiNote This is an <strong>expensive operation</strong> and primarily exists to help port legacy systems that
     * used older list-based systems from 1.21.4 and older.
     * @see java.util.List#removeIf(Predicate)
     */
    default WeightedList<E> removeIf(Predicate<E> predicate) {
        var list = new ArrayList<>(this.self().unwrap());
        return list.removeIf(w -> predicate.test(w.value()))
            ? WeightedList.<E>builder().addAll(list).build()
            : this.self();
    }

    interface Builder<E> {
        private WeightedList.Builder<E> self() {
            return (WeightedList.Builder<E>) this;
        }

        /** @see com.google.common.collect.ImmutableList.Builder#addAll(Iterable) */
        default WeightedList.Builder<E> addAll(Iterable<Weighted<E>> iterable) {
            for (var w : iterable) this.self().add(w.value(), w.weight());
            return this.self();
        }

        /** @see com.google.common.collect.ImmutableList.Builder#addAll(Iterator) */
        default WeightedList.Builder<E> addAll(Iterator<Weighted<E>> iterator) {
            return this.addAll(() -> iterator);
        }

        /**
         * Removes any existing elements from the builder. If removal takes place, this will create a new builder.
         * <p>To check if the builder elements have changed, simply check if the old and new builders are equal.</p>
         * {@snippet :
         * private static void test() {
         *     var builder = WeightedList.<String>builder();
         *     builder.add("Hello, World!", 1);
         *
         *     // if builder has no removals
         *     assert builder == builder.removeIf(String::isEmpty);
         *
         *     // if builder has removals
         *     assert builder != builder.removeIf(s -> s.equals("Hello, World!"));
         * }
         *}
         *
         * @param predicate The predicate to remove elements with
         * @return The builder
         * @apiNote This is an <strong>expensive operation</strong> and primarily exists to help port legacy systems
         * that used the older arraylist-based building from 1.21.4 and older.
         * @see IForgeWeightedList#removeIf(Predicate)
         * @see java.util.List#removeIf(Predicate)
         */
        default WeightedList.Builder<E> removeIf(Predicate<E> predicate) {
            var list = new ArrayList<>(this.self().build().unwrap());
            return list.removeIf(w -> predicate.test(w.value()))
                ? WeightedList.<E>builder().addAll(list)
                : this.self();
        }
    }
}
