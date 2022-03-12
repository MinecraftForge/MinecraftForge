/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Equivalent to {@link Consumer}, except with nonnull contract.
 * 
 * @see Consumer
 */
@FunctionalInterface
public interface NonNullConsumer<T>
{
    void accept(@Nonnull T t);
}
