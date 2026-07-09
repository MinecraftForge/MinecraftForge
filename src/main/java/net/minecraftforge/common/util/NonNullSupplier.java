/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

/**
 * Equivalent to {@link Supplier}, except with nonnull contract.
 * 
 * @see Supplier
 */
@FunctionalInterface
public interface NonNullSupplier<T>
{
    @Nonnull T get();
}
