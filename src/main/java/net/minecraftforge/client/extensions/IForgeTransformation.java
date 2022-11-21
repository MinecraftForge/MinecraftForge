/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;

import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Extension interface for {@link Transformation}.
 */
// TODO - 1.20: Transformation is not client-only, move this extension outside the client package
public interface IForgeTransformation
{
    private Transformation self()
    {
        return (Transformation) this;
    }

    /**
     * {@return whether this transformation is the identity transformation}
     *
     * @see Transformation#identity()
     */
    default boolean isIdentity()
    {
        return self().equals(Transformation.identity());
    }

    /**
     * Pushes and applies this transformation to the pose stack. The effects of this method can be reversed by a
     * corresponding {@link PoseStack#popPose()}.
     *
     * @deprecated Use {@linkplain PoseStack#pushTransformation(Transformation)}, as {@linkplain Transformation} can be present in common code.
     *
     * @param stack the pose stack to modify
     */
    @OnlyIn(Dist.CLIENT) // TODO - 1.20: Remove in favour of client-only PoseStack extension
    @Deprecated(forRemoval = true, since = "1.19.2")
    default void push(PoseStack stack)
    {
        stack.pushTransformation(self());
    }

    /**
     * Transforms the position according to this transformation.
     *
     * @param position the position to transform
     */
    default void transformPosition(Vector4f position)
    {
        position.transform(self().getMatrix());
    }

    /**
     * Transforms the normal according to this transformation and normalizes it.
     *
     * @param normal the normal to transform
     */
    default void transformNormal(Vector3f normal)
    {
        normal.transform(self().getNormalMatrix());
        normal.normalize();
    }

    /**
     * Rotates the direction according to this transformation and returns the nearest {@code Direction} to the
     * resulting direction.
     *
     * @param facing the direction to transform
     * @return the {@code Direction} value nearest to the resulting transformed direction
     * @see Direction#rotate(Matrix4f, Direction)
     */
    default Direction rotateTransform(Direction facing)
    {
        return Direction.rotate(self().getMatrix(), facing);
    }

    /**
     * Converts and returns a new transformation based on this transformation from assuming a center-block system to an
     * opposing-corner-block system.
     *
     * @return a new transformation using the opposing-corner-block system
     */
    default Transformation blockCenterToCorner()
    {
        return applyOrigin(new Vector3f(.5f, .5f, .5f));
    }

    /**
     * Converts and returns a new transformation based on this transformation from assuming an opposing-corner-block
     * system to a center-block system.
     *
     * @return a new transformation using the center-block system
     */
    default Transformation blockCornerToCenter()
    {
        return applyOrigin(new Vector3f(-.5f, -.5f, -.5f));
    }

    /**
     * Returns a new transformation with a changed origin by applying the given parameter (which is relative to the
     * current origin). This can be used for switching between coordinate systems.
     *
     * @param origin the new origin as relative to the current origin
     * @return a new transformation with a changed origin
     */
    default Transformation applyOrigin(Vector3f origin)
    {
        Transformation transform = self();
        if (transform.isIdentity()) return Transformation.identity();

        Matrix4f ret = transform.getMatrix();
        Matrix4f tmp = Matrix4f.createTranslateMatrix(origin.x(), origin.y(), origin.z());
        ret.multiplyBackward(tmp);
        tmp.setIdentity();
        tmp.setTranslation(-origin.x(), -origin.y(), -origin.z());
        ret.multiply(tmp);
        return new Transformation(ret);
    }
}
