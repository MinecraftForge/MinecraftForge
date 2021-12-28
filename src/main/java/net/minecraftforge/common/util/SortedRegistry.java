/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import com.google.common.collect.*;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A {@link ResourceLocation} to T registry where elements may define arbitrary ordering rules.
 * @param <T> The type of elements in the registry
 */
public class SortedRegistry<T>
{
    private final ImmutableList<T> elements;
    private final ImmutableBiMap<ResourceLocation, T> names;

    private SortedRegistry(ImmutableList<T> elements, ImmutableBiMap<ResourceLocation, T> names) {
        this.elements = elements;
        this.names = names;
    }

    /**
     * Returns the list of elements in ascending order as defined by the dependencies.
     * @return An immutable list of sorted elements
     */
    public ImmutableList<T> getElements()
    {
        return elements;
    }

    /**
     * Returns the unordered collection of entries.
     * @return An immutable bimap of unordered entries
     */
    public ImmutableBiMap<ResourceLocation, T> getUnorderedEntries() {
        return names;
    }

    /**
     * Returns the element associated with the given name.
     * @param name The name to look up
     * @return The element, or null if not registered
     */
    @Nullable
    public T get(ResourceLocation name)
    {
        return names.get(name);
    }

    /**
     * Returns the name with which the given element was registered.
     * @param element The element to look up
     * @return The name of the element, or null if not registered
     */
    @Nullable
    public ResourceLocation getName(T element)
    {
        return names.inverse().get(element);
    }

    /**
     * Checks whether the given name is contained in the registry.
     * @param name The name to look up
     * @return True if registered, false otherwise
     */
    public boolean contains(ResourceLocation name)
    {
        return names.containsKey(name);
    }

    /**
     * Checks whether the given element is contained in the registry.
     * @param element The element to look up
     * @return True if registered, false otherwise
     */
    public boolean contains(T element)
    {
        return names.containsValue(element);
    }

    /**
     * Returns the ordered list of elements that appear before the specified one.
     * If the element is not found, an empty list is returned.
     * @param element The element to look up
     * @return An immutable list of predecessors in ascending order
     */
    public ImmutableList<T> getAllBefore(T element)
    {
        var index = elements.indexOf(element);
        if (index == -1) return ImmutableList.of();
        return elements.subList(0, index);
    }

    /**
     * Returns the ordered list of elements that appear after the specified one.
     * If the element is not found, an empty list is returned.
     * @param element The element to look up
     * @return An immutable list of successors in ascending order
     */
    public ImmutableList<T> getAllAfter(T element)
    {
        var index = elements.indexOf(element);
        if (index == -1) return ImmutableList.of();
        return elements.subList(index + 1, elements.size());
    }

    /**
     * Returns the ordered list of elements that appear between the specified ones.
     * If the first element is null or not found, the registry is indexed from the start.
     * If the last element is null or not found, the registry is indexed until the end.
     * @param first The lower bound
     * @param last The upper bound
     * @return An immutable list of elements between the arguments in ascending order
     */
    public ImmutableList<T> getAllBetween(@Nullable T first, @Nullable T last)
    {
        var firstIndex = first != null ? elements.indexOf(first) : -1;
        var lastIndex = last != null ? elements.indexOf(last) : elements.size();
        if (firstIndex >= lastIndex) return ImmutableList.of();
        return elements.subList(firstIndex + 1, lastIndex);
    }

    public static class Builder<T>
    {
        private final Class<T> type;
        private final BiMap<ResourceLocation, T> entries = HashBiMap.create();
        private final Multimap<ResourceLocation, ResourceLocation> edges = HashMultimap.create();

        public Builder(Class<T> type) {
            this.type = type;
        }

        /**
         * Adds an element to the sorted registry.
         * @param element The element
         * @param name The name of the element
         * @param predecessors A list of elements that must appear before this element (String, ResourceName, and T are allowed)
         * @param successors A list of elements that must appear after this element (String, ResourceName, and T are allowed)
         * @param <T2> The type of the element
         * @return The element
         */
        public synchronized <T2 extends T> T2 add(T2 element, ResourceLocation name, List<Object> predecessors, List<Object> successors)
        {
            Objects.requireNonNull(predecessors, "Predecessor list may not be null.");
            Objects.requireNonNull(successors, "Successor list may not be null.");
            if (entries.containsKey(name))
                throw new IllegalArgumentException("An entry has already been registered with this name: " + name);
            entries.put(name, element);
            for(var other : predecessors)
                edges.put(getName(other), name);
            for(var other : successors)
                edges.put(name, getName(other));
            return element;
        }

        private ResourceLocation getName(Object element)
        {
            Objects.requireNonNull(element, "Attempted to get name of a null object.");
            if (element instanceof ResourceLocation rl)
                return rl;
            if (element instanceof String s)
                return new ResourceLocation(s);
            if (type.isAssignableFrom(element.getClass()))
            {
                var name = entries.inverse().get(type.cast(element));
                return Objects.requireNonNull(name, "Cannot add a dependency on an element that is not registered.");
            }
            throw new IllegalStateException("Invalid object type passed into dependencies: " + element.getClass());
        }

        /**
         * Creates a new {@link SortedRegistry} from the registered elements and dependencies.
         * @return A new registry
         */
        @SuppressWarnings("UnstableApiUsage")
        public SortedRegistry<T> build()
        {
            var graph = GraphBuilder.directed().nodeOrder(ElementOrder.<T>insertion()).build();
            for(var element : entries.values())
            {
                graph.addNode(element);
            }
            edges.forEach((from, to) -> {
                if (entries.containsKey(from) && entries.containsKey(to))
                    graph.putEdge(entries.get(from), entries.get(to));
            });
            var sortedElements = TopologicalSort.topologicalSort(graph, null);
            return new SortedRegistry<>(ImmutableList.copyOf(sortedElements), ImmutableBiMap.copyOf(entries));
        }

    }

}
