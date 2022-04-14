/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

/**
 * Equivalent to {@link Supplier}, except with nonnull contract.
 *
 * @see Supplier
 */
@FunctionalInterface
public interface NonNullSupplier<T>
{
    @NotNull T get();
}
