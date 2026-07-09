/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.util.function.Function;

import javax.annotation.Nonnull;

/**
 * Equivalent to {@link Function}, except with nonnull contract.
 * 
 * @see Function
 */
@FunctionalInterface
public interface NonNullFunction<T, R>
{
    @Nonnull
    R apply(@Nonnull T t);
}
