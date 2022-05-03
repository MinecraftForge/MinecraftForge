/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.model.animation;

import net.minecraft.util.math.vector.TransformationMatrix;

/**
 * Returns Local joint pose; animation clip for specific model part.
 */
public interface IJointClip
{
    TransformationMatrix apply(float time);
}
