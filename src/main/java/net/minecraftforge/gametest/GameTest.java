/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.gametest;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.minecraft.gametest.framework.GameTestEnvironments;
import net.minecraft.world.level.block.Rotation;

@Retention(RUNTIME)
@Target(METHOD)
public @interface GameTest {
    String name() default "";

    static final String DEFAULT_STRUCTURE = "forge:empty3x3x3";
    String environment() default GameTestEnvironments.DEFAULT;
    String structure() default DEFAULT_STRUCTURE;
    int maxTicks() default 100;
    int setupTicks() default 0;
    boolean required() default true;
    Rotation rotation() default Rotation.NONE;
    boolean manualOnly() default false;
    int maxAttempts() default 1;
    int requiredSuccesses() default 1;
    boolean skyAccess() default false;
}
