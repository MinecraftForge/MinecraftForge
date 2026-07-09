/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ObjectHolder can be used to automatically populate public static final fields with entries
 * from the registry. These values can then be referred within mod code directly.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface ObjectHolder
{
    /**
     * If used on a class, this represents a modid only.
     * If used on a field, it represents a name, which can be abbreviated or complete.
     * Abbreviated names derive their modid from an enclosing ObjectHolder at the class level.
     *
     * @return either a modid or a name based on the rules above
     */
    String value();
}
