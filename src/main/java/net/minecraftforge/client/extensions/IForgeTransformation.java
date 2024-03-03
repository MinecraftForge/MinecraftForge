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

public interface IForgeTransformation
{
    private Transformation self()
    {
        return (Transformation)this;
    }

    default boolean isIdentity()
    {
        return self().equals(Transformation.identity());
    }

    default void push(PoseStack stack)
    {
        stack.pushPose();

        Vector3f trans = self().getTranslation();
        stack.translate(trans.x(), trans.y(), trans.z());

        stack.mulPose(self().getLeftRotation());

        Vector3f scale = self().getScale();
        stack.scale(scale.x(), scale.y(), scale.z());

        stack.mulPose(self().getRightRotation());

    }

    default void transformPosition(Vector4f position)
    {
        position.transform(self().getMatrix());
    }

    default void transformNormal(Vector3f normal)
    {
        normal.transform(self().getNormalMatrix());
        normal.normalize();
    }

    default Direction rotateTransform(Direction facing)
    {
        return Direction.rotate(self().getMatrix(), facing);
    }

    /**
     * convert transformation from assuming center-block system to opposing-corner-block system
     */
    default Transformation blockCenterToCorner()
    {
        return applyOrigin(new Vector3f(.5f, .5f, .5f));
    }

    /**
     * convert transformation from assuming opposing-corner-block system to center-block system
     */
    default Transformation blockCornerToCenter()
    {
        return applyOrigin(new Vector3f(-.5f, -.5f, -.5f));
    }

    /**
     * Apply this transformation to a different origin.
     * Can be used for switching between coordinate systems.
     * Parameter is relative to the current origin.
     */
    default Transformation applyOrigin(Vector3f origin) {
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
