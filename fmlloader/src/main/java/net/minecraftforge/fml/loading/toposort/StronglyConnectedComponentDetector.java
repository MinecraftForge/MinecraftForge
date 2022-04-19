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
 */
public class StronglyConnectedComponentDetector<T> {
    private final Graph<T> graph;
    private Map<T, Integer> ids;
    private T[] elements;
    private int[] dfn;
    private int[] low;
    private int[] stack;
    private int top;
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
        ids = new HashMap<>();
        Set<T> nodes = graph.nodes();
        elements = (T[]) new Object[nodes.size()];
        for (T node : nodes) {
            ids.put(node, t);
            elements[t] = node;
            t++;
        }

        final int n = nodes.size();
        dfn = new int[n];
        low = new int[n];
        stack = new int[n];
        onStack = new BitSet(n);
        top = -1;
        for (int i = 0; i < n; i++) {
            if (dfn[i] == 0) {
                dfs(i, 1);
            }
        }
    }

    private void dfs(int now, int depth) {
        dfn[now] = depth;
        low[now] = depth;
        top++;
        stack[top] = now;
        onStack.set(now);
        for (T each : graph.successors(elements[now])) {
            int to = ids.get(each);
            if (dfn[to] != 0) {
                if (low[now] > dfn[to]) {
                    low[now] = dfn[to];
                }
            } else {
                dfs(to, depth + 1);
                if (low[now] > low[to]) {
                    low[now] = low[to];
                }
            }
        }

        if (dfn[now] == low[now]) {
            Set<T> component = new HashSet<>();
            while (top >= 0) {
                final int t = stack[top];
                component.add(elements[t]);
                onStack.clear(t);
                top--;
                if (t == now) {
                    break;
                }
            }
            components.add(component);
        }
    }

}
