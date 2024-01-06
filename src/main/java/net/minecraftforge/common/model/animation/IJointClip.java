/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.model.animation;

import com.mojang.math.Transformation;

/**
 * Returns Local joint pose; animation clip for specific model part.
 */
public interface IJointClip
{
    Transformation apply(float time);
}
