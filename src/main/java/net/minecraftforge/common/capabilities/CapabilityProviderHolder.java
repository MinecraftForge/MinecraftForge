/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.function.Consumer;

public record CapabilityProviderHolder<B extends ICapabilityProvider>(B provider, Consumer<B> listener) {}
