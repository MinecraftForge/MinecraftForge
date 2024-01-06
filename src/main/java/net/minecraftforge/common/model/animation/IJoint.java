/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.model.animation;

import com.mojang.math.Transformation;

import java.util.Optional;

/**
 * Model part that's a part of the hierarchical skeleton.
 */
public interface IJoint
{
    Transformation getInvBindPose();

    Optional<? extends IJoint> getParent();
}
