/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import org.jetbrains.annotations.NotNull;
import java.util.function.Consumer;

/**
 * Equivalent to {@link Consumer}, except with nonnull contract.
 *
 * @see Consumer
 */
@FunctionalInterface
public interface NonNullConsumer<T>
{
    void accept(@NotNull T t);
}
