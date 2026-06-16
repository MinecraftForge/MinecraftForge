/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

/**
 * Internal registry for tracking {@link ObjectHolder} references
 */
@ApiStatus.Internal
class ObjectHolderRegistry {
    /**
     * Exposed to allow modders to register their own notification handlers.
     * This runnable will be called after a registry snapshot has been injected and finalized.
     * The internal list is backed by a HashSet so it is HIGHLY recommended you implement a proper equals
     * and hashCode function to de-duplicate callers here.
     * The default @ObjectHolder implementation uses the hashCode/equals for the field the annotation is on.
     */
    static synchronized void addHandler(Consumer<Predicate<Identifier>> ref) {
        objectHolders.add(ref);
    }

    /**
     * Removed the specified handler from the notification list.
     *
     * The internal list is backed by a hash set, and so proper hashCode and equals operations are required for success.
     *
     * The default @ObjectHolder implementation uses the hashCode/equals for the field the annotation is on.
     *
     * @return true if handler was matched and removed.
     */
    static synchronized boolean removeHandler(Consumer<Predicate<Identifier>> ref) {
        return objectHolders.remove(ref);
    }

    //==============================================================
    // Everything below is internal, do not use.
    //==============================================================

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<Consumer<Predicate<Identifier>>> objectHolders = new HashSet<>();

    static void applyObjectHolders() {
        try {
            LOGGER.debug(ForgeRegistry.REGISTRIES, "Applying holder lookups");
            applyObjectHolders(_ -> true);
            LOGGER.debug(ForgeRegistry.REGISTRIES, "Holder lookups applied");
        } catch (RuntimeException e) {
            // It is more important that the calling contexts continue without exception to prevent further cascading errors
            LOGGER.error("", e);
        }
    }

    static void applyObjectHolders(Predicate<Identifier> filter) {
        RuntimeException aggregate = new RuntimeException("Failed to apply some object holders, see suppressed exceptions for details");
        for (Consumer<Predicate<Identifier>> objectHolder : objectHolders) {
            try {
                objectHolder.accept(filter);
            } catch (Exception e) {
                aggregate.addSuppressed(e);
            }
        }

        if (aggregate.getSuppressed().length > 0)
            throw aggregate;
    }
}
