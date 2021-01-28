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

package net.minecraftforge.fml.loading.toposort;

import com.google.common.base.Preconditions;
import com.google.common.graph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Provides a topological sort algorithm.
 *
 * <p>While this algorithm is used for mod loading in Forge, it can be
 * utilized in other fashions, e.g. topology-based registry loading, prioritization
 * for renderers, and even mod module loading.
 */
public final class TopologicalSort {

    /**
     * A breath-first-search based topological sort.
     *
     * <p>Compared to the depth-first-search version, it does not reverse the graph
     * and supports custom secondary ordering specified by a comparator. It also utilizes the
     * recently introduced Guava Graph API, which is more straightforward than the old directed
     * graph.
     *
     * <p>The graph to sort must be directed, must not allow self loops, and must not contain
     * cycles. {@link IllegalArgumentException} will be thrown otherwise.
     *
     * <p>When {@code null} is used for the comparator and multiple nodes have no
     * prerequisites, the order depends on the iteration order of the set returned by the
     * {@link Graph#successors(Object)} call, which is random by default.
     *
     * <p>Given the number of edges {@code E} and the number of vertexes {@code V},
     * the time complexity of a sort without a secondary comparator is {@code O(E + V)}.
     * With a secondary comparator of time complexity {@code O(T)}, the overall time
     * complexity would be {@code O(E + TV log(V))}. As a result, the comparator should
     * be as efficient as possible.
     *
     * <p>Examples of topological sort usage can be found in Forge test code.
     *
     * @param graph      the graph to sort
     * @param comparator the secondary comparator, may be null
     * @param <T>        the node type of the graph
     * @return the ordered nodes from the graph
     * @throws IllegalArgumentException if the graph is undirected or allows self loops
     * @throws CyclePresentException    if the graph contains cycles
     */
    public static <T> List<T> topologicalSort(Graph<T> graph, @Nullable Comparator<? super T> comparator) throws IllegalArgumentException {
        Preconditions.checkArgument(graph.isDirected(), "Cannot topologically sort an undirected graph!");
        Preconditions.checkArgument(!graph.allowsSelfLoops(), "Cannot topologically sort a graph with self loops!");

        final Queue<T> queue = comparator == null ? new ArrayDeque<>() : new PriorityQueue<>(comparator);
        final Map<T, Integer> degrees = new HashMap<>();
        final List<T> results = new ArrayList<>();

        for (final T node : graph.nodes()) {
            final int degree = graph.inDegree(node);
            if (degree == 0) {
                queue.add(node);
            } else {
                degrees.put(node, degree);
            }
        }

        while (!queue.isEmpty()) {
            final T current = queue.remove();
            results.add(current);
            for (final T successor : graph.successors(current)) {
                final int updated = degrees.compute(successor, (node, degree) -> Objects.requireNonNull(degree, () -> "Invalid degree present for " + node) - 1);
                if (updated == 0) {
                    queue.add(successor);
                    degrees.remove(successor);
                }
            }
        }

        if (!degrees.isEmpty()) {
            Set<Set<T>> components = new StronglyConnectedComponentDetector<>(graph).getComponents();
            components.removeIf(set -> set.size() < 2);
            throwCyclePresentException(components);
        }

        return results;
    }

    @SuppressWarnings("unchecked") // for unchecked annotation
    private static <T> void throwCyclePresentException(Set<Set<T>> components) {
        throw new CyclePresentException((Set<Set<?>>) (Set<?>) components);
    }
}
