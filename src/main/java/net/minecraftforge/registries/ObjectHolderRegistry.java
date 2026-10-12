/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

/**
 * Internal registry for tracking {@link RegistryObject} references
 */
@ApiStatus.Internal
class ObjectHolderRegistry {
    /**
     * Registers a handler that is run after the named registry has been injected and finalized.
     * Handlers are kept in a HashSet per registry, so the same instance is only ever registered once.
     */
    static synchronized void addHandler(Identifier registry, Runnable ref) {
        namedHandlers.computeIfAbsent(registry, _ -> new HashSet<>()).add(ref);
    }

    /**
     * Removes a handler registered with {@link #addHandler(Identifier, Runnable)}.
     *
     * @return true if handler was matched and removed.
     */
    static synchronized boolean removeHandler(Identifier registry, Runnable ref) {
        var handlers = namedHandlers.get(registry);
        return handlers != null && handlers.remove(ref);
    }

    /**
     * Registers a check that is run once, before the handlers of the next holder pass.
     * Used to report holders that point at a registry that does not exist.
     */
    static synchronized void addValidator(Runnable ref) {
        validators.add(ref);
    }

    //==============================================================
    // Everything below is internal, do not use.
    //==============================================================

    private static final Logger LOGGER = LogManager.getLogger();
    // Order is not guaranteed, the old implementation kept every handler in one HashSet.
    private static final Map<Identifier, Set<Runnable>> namedHandlers = new HashMap<>();
    private static final List<Runnable> validators = new ArrayList<>();

    /** Runs the handlers of every registry. */
    static void applyObjectHolders() {
        try {
            LOGGER.debug(ForgeRegistry.REGISTRIES, "Applying holder lookups");
            var exceptions = new ArrayList<Exception>();
            validate(exceptions);
            for (var handlers : namedHandlers.values())
                apply(handlers, exceptions);
            throwIfNeeded(exceptions);
            LOGGER.debug(ForgeRegistry.REGISTRIES, "Holder lookups applied");
        } catch (RuntimeException e) {
            // It is more important that the calling contexts continue without exception to prevent further cascading errors
            LOGGER.error("", e);
        }
    }

    /** Runs the handlers of one registry. */
    static void applyObjectHolders(Identifier registry) {
        var exceptions = new ArrayList<Exception>();
        validate(exceptions);
        var handlers = namedHandlers.get(registry);
        if (handlers != null)
            apply(handlers, exceptions);
        throwIfNeeded(exceptions);
    }

    // Each validator runs once, at the first holder pass after it was registered.
    private static void validate(List<Exception> exceptions) {
        List<Runnable> pending;
        synchronized (ObjectHolderRegistry.class) {
            if (validators.isEmpty())
                return;
            pending = new ArrayList<>(validators);
            validators.clear();
        }

        for (var validator : pending) {
            try {
                validator.run();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
    }

    private static void apply(Set<Runnable> handlers, List<Exception> exceptions) {
        for (var handler : handlers) {
            try {
                handler.run();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
    }

    private static void throwIfNeeded(List<Exception> exceptions) {
        if (exceptions.isEmpty())
            return;

        var aggregate = new RuntimeException("Failed to apply some object holders, see suppressed exceptions for details");
        for (var e : exceptions)
            aggregate.addSuppressed(e);
        throw aggregate;
    }
}
