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
 * Marks a class as containing game tests that should be registered automatically.
 * All methods annotated with {@link GameTest} or {@link GameTestGenerator} will be registered.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GameTestNamespace {
    /**
     * The default namespace for all tests in this class that do not manually specify one.
     * It is recommended that this is your modid, but if you have multiple test mods that
     * you want to group together you can use this to do so.
     * <p>
     * This will default to your modid if it is on the same class that is annotated with {@link net.minecraftforge.fml.common.Mod @Mod}
     */
    String value() default "";
}
