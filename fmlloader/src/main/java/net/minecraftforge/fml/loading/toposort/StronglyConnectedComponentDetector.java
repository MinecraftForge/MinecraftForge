/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.toposort;

import com.google.common.graph.Graph;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An object that splits a graph into strongly connected components lazily with
 * Tarjan's Strongly Connected Components Algorithm.
 *
 * <p>This algorithm allows to detect all cycles in dependencies that prevent topological
 * sorting.
 *
 * <p>This detector evaluates the graph lazily and won't reflect the modifications in the
 * graph after initial evaluation.
 *
 * <p> Implementation based on <a href="https://en.wikipedia.org/w/index.php?title=Tarjan%27s_strongly_connected_components_algorithm&oldid=1384955226">Wikipedia Psudocode</a>
 */
public class StronglyConnectedComponentDetector<T> {
    private final Graph<T> graph;
    private final Map<T, Integer> ids = new HashMap<>();
    private T[] elements;
    private int[] indexes;
    private int[] lowlinks;
    private int[] stack;
    private int top;
    private int index;
    private BitSet onStack;
    private Set<Set<T>> components;

    public StronglyConnectedComponentDetector(Graph<T> graph) {
        this.graph = graph;
    }

    public Set<Set<T>> getComponents() {
        if (components == null) {
            calculate();
        }
        return components;
    }

    @SuppressWarnings("unchecked")
    private void calculate() {
        components = new HashSet<>();
        int t = 0;
        Set<T> nodes = graph.nodes();
        elements = (T[]) new Object[nodes.size()];
        for (T node : nodes) {
            ids.put(node, t);
            elements[t] = node;
            t++;
        }

        final int n = nodes.size();
        indexes = new int[n];
        lowlinks = new int[n];
        stack = new int[n];
        onStack = new BitSet(n);
        top = -1;
        index = 1;
        for (int i = 0; i < n; i++) {
            if (indexes[i] == 0)
                dfs(i);
        }
    }

    private void dfs(int now) {
        indexes[now] = lowlinks[now] = index++;
        stack[++top] = now;
        onStack.set(now);

        for (T each : graph.successors(elements[now])) {
            int to = ids.get(each);
            if (indexes[to] == 0) { // 0 is unvisited
                dfs(to);
                if (lowlinks[now] > lowlinks[to])
                    lowlinks[now] = lowlinks[to];
            } else if (onStack.get(to)) {
                if (lowlinks[now] > indexes[to])
                    lowlinks[now] = indexes[to];
            }
        }

        if (indexes[now] == lowlinks[now]) {
            Set<T> component = new HashSet<>();
            while (top >= 0) {
                final int w = stack[top--];
                onStack.clear(w);
                component.add(elements[w]);
                if (w == now)
                    break;
            }
            components.add(component);
        }
    }

}
