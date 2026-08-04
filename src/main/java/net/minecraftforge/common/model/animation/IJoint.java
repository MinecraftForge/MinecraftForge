/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.model.animation;

import java.util.Optional;

import net.minecraft.client.renderer.TransformationMatrix;

/**
 * Model part that's a part of the hierarchical skeleton.
 */
public interface IJoint
{
    TransformationMatrix getInvBindPose();

    Optional<? extends IJoint> getParent();
}
