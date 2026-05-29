/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.capabilities;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal // Modders should use ICapabilityProvider, this is for singularity
public interface ICapabilityProviderImpl<B extends ICapabilityProviderImpl<B>> extends ICapabilityProvider {
    void invalidateCaps();
    void reviveCaps();
}
