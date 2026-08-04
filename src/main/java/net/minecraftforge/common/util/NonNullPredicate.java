/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

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
