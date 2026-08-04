/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.resource;

import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.Sets;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Holds methods to create standard predicates to select {@link IResourceType}s that should be reloaded.
 */
@OnlyIn(Dist.CLIENT)
public final class ReloadRequirements
{
    /**
     * Creates a reload predicate accepting all resource types.
     *
     * @return a predicate accepting all types
     */
    public static Predicate<IResourceType> all()
    {
        return type -> true;
    }

    /**
     * Creates an inclusive reload predicate. Only given resource types will be loaded along with this.
     *
     * @param inclusion the set of resource types to be included in the reload
     * @return an inclusion predicate based on the given types
     */
    public static Predicate<IResourceType> include(IResourceType... inclusion)
    {
        Set<IResourceType> inclusionSet = Sets.newHashSet(inclusion);
        return inclusionSet::contains;
    }
}
