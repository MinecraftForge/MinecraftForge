/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import org.jetbrains.annotations.NotNull;
import java.util.function.Function;

/**
 * Equivalent to {@link Function}, except with nonnull contract.
 *
 * @see Function
 */
@FunctionalInterface
public interface NonNullFunction<T, R>
{
    @NotNull
    R apply(@NotNull T t);
}
