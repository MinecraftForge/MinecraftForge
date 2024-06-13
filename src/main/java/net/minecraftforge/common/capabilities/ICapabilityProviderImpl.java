/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal // Modders should use ICapabilityProvider, this is for Forge
public interface ICapabilityProviderImpl<B extends ICapabilityProviderImpl<B>> extends ICapabilityProvider {
    void invalidateCaps();
    void reviveCaps();
}
