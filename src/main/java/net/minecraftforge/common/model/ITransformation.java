/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.model;

import javax.vecmath.Matrix4f;

import net.minecraft.util.Direction;

/*
 * Replacement interface for ModelRotation to allow custom transformations of vanilla models.
 * You should probably use TRSRTransformation directly.
 */
public interface ITransformation
{
    Matrix4f getMatrixVec();

    Direction rotateTransform(Direction facing);

    int rotate(Direction facing, int vertexIndex);
}
