/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.toposort;

import java.util.Set;

/**
 * An exception thrown for graphs with cycles as an argument for topological sort.
 */
public final class CyclePresentException extends IllegalArgumentException {
    private final Set<Set<?>> cycles;

    /**
     * Creates the exception.
     *
     * @param cycles the cycles present
     */
    CyclePresentException(Set<Set<?>> cycles) {
        super("Cycles present in graph: " + cycles);
        this.cycles = cycles;
    }

    /**
     * Accesses the cycles present in the sorted graph.
     *
     * <p>Each element in the outer set represents a cycle; each cycle, or the inner set,
     * forms a strongly connected component with two or more elements.
     *
     * @param <T> the type of node sorted
     * @return the cycles identified
     */
    @SuppressWarnings("unchecked")
    public <T> Set<Set<T>> getCycles() {
        return (Set<Set<T>>) (Set<?>) cycles;
    }
}
