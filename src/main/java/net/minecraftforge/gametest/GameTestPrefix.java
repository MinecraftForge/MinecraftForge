/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.gametest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This sets the prefix to prepend to test names and templates.
 * <p>
 * If this annotation cannot be found the default behavior is to
 * look for the {@ Mod @Mod} annotation and use the mod id. If that is
 * not found the {@link Class#getSimpleName() simple class name} is used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GameTestPrefix {
    /**
     * The prefix to use instead of {@link Class#getSimpleName() simple class name}
     */
    String value();
}
