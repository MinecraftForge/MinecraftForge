/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.test;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import net.minecraftforge.fml.loading.toposort.CyclePresentException;
import net.minecraftforge.fml.loading.toposort.StronglyConnectedComponentDetector;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Tests for topological sort.
 */
public class TopologicalSortTests {

    @Test
    @DisplayName("strongly connected components")
    @SuppressWarnings("unchecked")
    void testScc() {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.putEdge(2, 4);
        graph.putEdge(4, 7);
        graph.putEdge(4, 6);
        graph.putEdge(6, 1);
        graph.putEdge(3, 4);
        graph.putEdge(1, 4);
        graph.putEdge(3, 1);
        Set<Set<Integer>> components = new StronglyConnectedComponentDetector<>(graph).getComponents();
        assertThat(components, containsInAnyOrder(contains(2), contains(3), contains(7), containsInAnyOrder(1, 4, 6)));
    }

    @Test
    @DisplayName("strongly connected components 2")
    @SuppressWarnings("unchecked")
    void testScc2() {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.putEdge(2, 4);
        graph.putEdge(4, 8);
        graph.putEdge(8, 2);
        graph.putEdge(2, 1);
        graph.putEdge(2, 3);
        graph.putEdge(1, 9);
        graph.putEdge(9, 7);
        graph.putEdge(7, 5);
        graph.putEdge(5, 1);
        graph.putEdge(5, 3);
        graph.putEdge(3, 6);
        graph.putEdge(6, 5);
        graph.putEdge(9, 3);
        Set<Set<Integer>> components = new StronglyConnectedComponentDetector<>(graph).getComponents();
        assertThat(components, containsInAnyOrder(containsInAnyOrder(1, 3, 5, 6, 7, 9), containsInAnyOrder(2, 4, 8)));
    }

    @Test
    @DisplayName("basic sort")
    void testBasicSort() {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.putEdge(2, 4);
        graph.putEdge(4, 7);
        graph.putEdge(4, 6);
        graph.putEdge(3, 4);
        graph.putEdge(1, 4);
        graph.putEdge(3, 1);
        List<Integer> list = TopologicalSort.topologicalSort(graph, Comparator.naturalOrder());
        assertThat(list, contains(2, 3, 1, 4, 6, 7));
    }

    @Test
    @DisplayName("basic sort 2")
    void testBasicSort2() {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.putEdge(7, 6);
        graph.putEdge(8, 6);
        graph.putEdge(8, 2);
        graph.putEdge(12, 2);
        graph.putEdge(2, 6);
        graph.putEdge(6, 1);
        graph.putEdge(2, 4);
        graph.putEdge(2, 1);
        graph.putEdge(1, 4);
        graph.putEdge(1, 3);
        graph.putEdge(4, 5);
        graph.putEdge(1, 5);
        graph.putEdge(5, 3);
        graph.putEdge(1, 10);
        graph.putEdge(3, 11);
        graph.putEdge(5, 9);
        graph.putEdge(11, 9);
        graph.putEdge(11, 13);
        graph.putEdge(10, 13);
        List<Integer> list = TopologicalSort.topologicalSort(graph, Collections.reverseOrder());
        assertThat(list, contains(12, 8, 7, 2, 6, 1, 10, 4, 5, 3, 11, 13, 9));
    }

    @Test
    @DisplayName("loop sort")
    void testLoopSort() {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.putEdge(2, 4);
        graph.putEdge(4, 7);
        graph.putEdge(4, 6);
        graph.putEdge(6, 1);
        graph.putEdge(3, 4);
        graph.putEdge(1, 4);
        graph.putEdge(3, 1);
        CyclePresentException ex = Assertions.assertThrows(CyclePresentException.class, () -> {
            TopologicalSort.topologicalSort(graph, Comparator.naturalOrder());
        });
        assertThat(ex.getCycles(), contains(containsInAnyOrder(1, 4, 6)));
    }
}
