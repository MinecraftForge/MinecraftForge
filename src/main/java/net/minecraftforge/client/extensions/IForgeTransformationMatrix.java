/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.*;

/*
 * Replacement interface for ModelRotation to allow custom transformations of vanilla models.
 * You should probably use TRSRTransformation directly.
 */
public interface IForgeTransformationMatrix
{
    default TransformationMatrix getTransformaion()
    {
        return (TransformationMatrix)this;
    }

    default boolean isIdentity()
    {
        return getTransformaion().equals(TransformationMatrix.identity());
    }

    default void push(MatrixStack stack)
    {
        stack.pushPose();

        Vector3f trans = getTransformaion().getTranslation();
        stack.translate(trans.x(), trans.y(), trans.z());

        stack.mulPose(getTransformaion().getLeftRotation());

        Vector3f scale = getTransformaion().getScale();
        stack.scale(scale.x(), scale.y(), scale.z());

        stack.mulPose(getTransformaion().getRightRot());

    }

    default TransformationMatrix compose(TransformationMatrix other)
    {
        if (getTransformaion().isIdentity()) return other;
        if (other.isIdentity()) return getTransformaion();
        Matrix4f m = getTransformaion().getMatrix();
        m.multiply(other.getMatrix());
        return new TransformationMatrix(m);
    }

    default TransformationMatrix inverse()
    {
        if (isIdentity()) return getTransformaion();
        Matrix4f m = getTransformaion().getMatrix().copy();
        m.invert();
        return new TransformationMatrix(m);
    }

    default void transformPosition(Vector4f position)
    {
        position.transform(getTransformaion().getMatrix());
    }

    default void transformNormal(Vector3f normal)
    {
        normal.transform(getTransformaion().getNormalMatrix());
        normal.normalize();
    }

    default Direction rotateTransform(Direction facing)
    {
        return Direction.rotate(getTransformaion().getMatrix(), facing);
    }

    /**
     * convert transformation from assuming center-block system to opposing-corner-block system
     */
    default TransformationMatrix blockCenterToCorner()
    {
        return applyOrigin(new Vector3f(.5f, .5f, .5f));
    }

    /**
     * convert transformation from assuming opposing-corner-block system to center-block system
     */
    default TransformationMatrix blockCornerToCenter()
    {
        return applyOrigin(new Vector3f(-.5f, -.5f, -.5f));
    }

    /**
     * Apply this transformation to a different origin.
     * Can be used for switching between coordinate systems.
     * Parameter is relative to the current origin.
     */
    default TransformationMatrix applyOrigin(Vector3f origin) {
        TransformationMatrix transform = getTransformaion();
        if (transform.isIdentity()) return TransformationMatrix.identity();

        Matrix4f ret = transform.getMatrix();
        Matrix4f tmp = Matrix4f.createTranslateMatrix(origin.x(), origin.y(), origin.z());
        ret.multiplyBackward(tmp);
        tmp.setIdentity();
        tmp.setTranslation(-origin.x(), -origin.y(), -origin.z());
        ret.multiply(tmp);
        return new TransformationMatrix(ret);
    }
}
