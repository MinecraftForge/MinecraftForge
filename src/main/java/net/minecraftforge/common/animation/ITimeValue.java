/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.animation;

/**
 * Time-varying value associated with the animation.
 * Return value should be constant with the respect to the input and reasonable context (current render frame).
 * Simplest example is the input time itself.
 * Unity calls them Parameters, Unreal calls them Variables.
 */
public interface ITimeValue
{
    float apply(float input);
}
