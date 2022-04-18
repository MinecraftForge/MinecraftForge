/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;

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
public @interface GameTestHolder
{
    /**
     * Used as the default {@link GameTest#templateNamespace() template namespace} for any game tests in the class that do not specify one.
     */
    String value() default "minecraft";
}
