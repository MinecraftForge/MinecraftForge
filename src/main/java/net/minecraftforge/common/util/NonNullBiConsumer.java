/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * Equivalent to {@link BiConsumer}, except with nonnull contract.
 *
 * @see BiConsumer
 */
@FunctionalInterface
public interface NonNullBiConsumer<T, U>
{
    void accept(@NotNull T t, @NotNull U u);
}
