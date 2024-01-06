/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;


/**
 * A registry delegate for holding references to items or blocks
 * These should be safe to use in things like lists though aliased items and blocks will not
 * have object identity with respect to their delegate.
 *
 * @author cpw
 *
 * @param <T> the type of thing we're holding onto
 */
public interface IRegistryDelegate<T> extends Supplier<T> {
    /**
     * Get the referent pointed at by this delegate. This will be the currently active item or block, and will change
     * as world saves come and go. Note that item.delegate.get() may NOT be the same object as item, due to item and
     * block substitution.
     *
     * @return The referred object
     */
    @Override
    T get();

    /**
     * Get the unique resource location for this delegate. Completely static after registration has completed, and
     * will never change.
     * @return The name
     */
    ResourceLocation name();

    /**
     * Get the delegate type. It will be dependent on the registry this delegate is sourced from.
     * @return The type of delegate
     */
    Class<T> type();
}
