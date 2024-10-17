/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.function.Consumer;

/**
 * Used in {@link CapabilityFactoryRegisterEvent} to allow for registering
 * {@link ICapabilityProvider}'s to objects that are {@link CapabilityProviderWithFactory}
 * Includes a {@link Consumer} to be invoked when the Provider is invalidated.
 */
public record CapabilityProviderHolder<B extends ICapabilityProvider>(B provider, Consumer<B> listener) {}
