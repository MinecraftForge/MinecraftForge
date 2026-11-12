/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.test;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import net.minecraftforge.fml.loading.toposort.CyclePresentException;
import net.minecraftforge.fml.loading.toposort.StronglyConnectedComponentDetector;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link TopologicalSort} and {@link StronglyConnectedComponentDetector}
 */
class TopologicalSortTest {
    @Test
    void resultProperlySorted() {
        MutableGraph<String> graph = GraphBuilder.directed().build();
        graph.putEdge("a", "b");
        graph.putEdge("a", "c");
        graph.putEdge("b", "c");
        var sorted = TopologicalSort.topologicalSort(graph, Comparator.naturalOrder());
        assertEquals(List.of("a", "b", "c"), sorted);
    }

    @Test
    void throwsOnCycle() {
        MutableGraph<String> graph = GraphBuilder.directed().build();
        graph.putEdge("a", "b");
        graph.putEdge("b", "a");

        var exception = Assertions.assertThrows(CyclePresentException.class, () -> TopologicalSort.topologicalSort(graph, Comparator.naturalOrder()));
        assertEquals(Set.of(
            Set.of("a", "b")
        ), exception.getCycles());
    }

    @Test
    void simpleCycle() {
        MutableGraph<String> graph = GraphBuilder.directed().build();
        // a -> b -> c -> a
        graph.putEdge("a", "b");
        graph.putEdge("b", "c");
        graph.putEdge("c", "a");

        var cycles = cyclesOf(graph);
        assertEquals(Set.of(
            Set.of("a", "b", "c")
        ), cycles);
    }

    @Test
    void noCycle() {
        MutableGraph<String> graph = GraphBuilder.directed().build();
        graph.putEdge("a", "b");
        graph.putEdge("a", "c");
        graph.putEdge("b", "d");
        graph.putEdge("c", "d");
        graph.putEdge("d", "e");

        var cycles = cyclesOf(graph);
        assertTrue(cycles.isEmpty());
    }

    /**
     * Regression test: the previous implementation lowered the lowlink of a node from every already-visited
     * successor, including successors whose component was already completed. That merged nodes into the wrong
     * component, so a member of the real cycle ({@code c}) was missing from the reported cycle.
     */
    @Test
    void reportsAllMembersOfCycle() {
        MutableGraph<String> graph = GraphBuilder.directed().build();
        graph.addNode("a");
        graph.addNode("b");
        graph.addNode("c");
        graph.putEdge("a", "b");
        graph.putEdge("a", "c");
        graph.putEdge("b", "a");
        graph.putEdge("c", "b");

        var cycles = cyclesOf(graph);
        assertEquals(Set.of(
            Set.of("a", "b", "c")
        ), cycles);
    }

    /**
     * Regression test: the previous implementation invented a cycle out of {@code c -> d -> a}, which is a plain
     * path, because it compared against the DFS depth instead of a discovery counter and ignored whether a
     * successor was still on the stack.
     */
    @Test
    void doesNotInventCycles() {
        MutableGraph<String> graph = GraphBuilder.directed().build();
        graph.addNode("a");
        graph.addNode("b");
        graph.addNode("c");
        graph.addNode("d");
        graph.putEdge("a", "b");
        graph.putEdge("b", "a");
        graph.putEdge("c", "d");
        graph.putEdge("d", "a");

        var cycles = cyclesOf(graph);
        assertEquals(Set.of(
            Set.of("a", "b")
        ), cycles);
    }

    /**
     * Reproduces the shape of the mod graphs that reported bogus cycles in the wild: one real three-node cycle,
     * with nodes that merely depend on members of the cycle and a node on a plain path into it.
     *
     * <p>Every reported component must be the real cycle itself; nodes such as {@code dependent_a} only depend on
     * the cycle and must not be reported.
     *
     * @see <a href="https://github.com/MinecraftForge/MinecraftForge/pull/10924">related GitHub issue</a>
     */
    @Test
    void reportsOnlyRealCycles() {
        MutableGraph<String> graph = GraphBuilder.directed().build();
        // Mirrors the insertion order of ModSorter: every mod is added as a node first, edges are added afterwards.
        for (String mod : List.of("upstream", "cycle_a", "cycle_b", "dependent_a", "cycle_c", "dependent_b")) {
            graph.addNode(mod);
        }
        // a -> b -> a
        graph.putEdge("cycle_a", "cycle_b");
        graph.putEdge("cycle_b", "cycle_a");
        // a -> c -> b -> a
        graph.putEdge("cycle_a", "cycle_c");
        graph.putEdge("cycle_c", "cycle_b");
        // dA -> b -> a - Valid
        graph.putEdge("dependent_a", "cycle_b");
        // dB -> b -> a - Valid
        graph.putEdge("dependent_b", "cycle_b");
        // u -> a -> b -> a - Not valid, but u is not part of the cycle so shouldn't be reported
        graph.putEdge("upstream", "cycle_a");

        var cycles = cyclesOf(graph);
        assertEquals(Set.of(
            Set.of("cycle_a", "cycle_b", "cycle_c")
        ), cycles);

        var exception = Assertions.assertThrows(CyclePresentException.class, () -> TopologicalSort.topologicalSort(graph, Comparator.naturalOrder()));
        assertEquals(Set.of(
            Set.of("cycle_a", "cycle_b", "cycle_c")
        ), exception.getCycles());
    }

    private static Set<Set<String>> cyclesOf(Graph<String> graph) {
        var detector = new StronglyConnectedComponentDetector<>(graph);
        var connected = detector.getComponents();
        var cycles = new HashSet<>(connected);
        cycles.removeIf(component -> component.size() < 2);
        return cycles;
    }
}

