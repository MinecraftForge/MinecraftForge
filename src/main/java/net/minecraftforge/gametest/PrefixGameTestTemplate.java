/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.gametest;

import net.minecraft.gametest.framework.GameTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When used on a class, this sets the default state for whether
 * to prefix any contained game test templates with the {@link Class#getSimpleName() simple class name} or not.
 * <p>
 * When used on a method, this defines whether the specific method should be prefixed with the simple class name or not.
 * <p>
 * If this annotation cannot be found on a game test method or its containing class, the default behavior is to prefix the class name.
 * <p>
 * Method annotations override any class annotations.
 *
 * @see GameTestHolder
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PrefixGameTestTemplate
{
    /**
     * Whether to prefix the game test template with the containing class' {@link Class#getSimpleName() simple name}.
     * For example, true in a class named "MyTest" would result in "mytest.structure" while false would result in "structure".
     * <p>
     * Only applies to methods annotated with {@link GameTest}.
     */
    boolean value() default true;
}
