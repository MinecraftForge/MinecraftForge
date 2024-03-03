/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * Equivalent to {@link Predicate}, except with nonnull contract.
 * 
 * @see Predicate
 */
@FunctionalInterface
public interface NonNullPredicate<T>
{
    boolean test(@Nonnull T t);
}
