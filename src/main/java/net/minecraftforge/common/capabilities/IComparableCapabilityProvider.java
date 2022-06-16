/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import org.jetbrains.annotations.Nullable;

/**
 * Defines a Capability Provider that can be checked for equivalence with another provider.
 */
public interface IComparableCapabilityProvider<B extends IComparableCapabilityProvider<B>> extends ICapabilityProvider
{

    /**
     * @see {@link #areCapsCompatible(CapabilityDispatcher)}
     */
    default boolean areCapsCompatible(IComparableCapabilityProvider<B> other)
    {
        return areCapsCompatible(other.getCapDispatcher());
    }

    /**
     * Documentation for this method is available at these two references, and is not duplicated.
     * @see {@link CapabilityDispatcher#isEquivalentTo(CapabilityDispatcher)}
     * @see {@link IAttachedCapabilityProvider#isEquivalentTo(IAttachedCapabilityProvider)}
     */
    default boolean areCapsCompatible(@Nullable CapabilityDispatcher<B> other)
    {
        var disp = getCapDispatcher();
        if (disp == null)
        {
            if (other == null)
            {
                return true;
            }
            else
            {
                return other.isEquivalentTo(null);
            }
        }
        else
        {
            return disp.isEquivalentTo(other);
        }
    }
    
    @Nullable CapabilityDispatcher<B> getCapDispatcher(); // Should be lazy
}