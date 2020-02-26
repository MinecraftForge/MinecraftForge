/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Direction;

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
        stack.push();

        Vector3f trans = getTransformaion().getTranslation();
        stack.translate(trans.getX(), trans.getY(), trans.getZ());

        stack.rotate(getTransformaion().getRotationLeft());

        Vector3f scale = getTransformaion().getScale();
        stack.scale(scale.getX(), scale.getY(), scale.getZ());

        stack.rotate(getTransformaion().getRightRot());

    }

    default TransformationMatrix compose(TransformationMatrix other)
    {
        if (getTransformaion().isIdentity()) return other;
        if (other.isIdentity()) return getTransformaion();
        Matrix4f m = getTransformaion().getMatrix();
        m.mul(other.getMatrix());
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
        return Direction.rotateFace(getTransformaion().getMatrix(), facing);
    }

    /**
     * convert transformation from assuming center-block system to corner-block system
     */
    default TransformationMatrix blockCenterToCorner()
    {
        TransformationMatrix transform = getTransformaion();
        if (transform.isIdentity()) return TransformationMatrix.identity();

        Matrix4f ret = transform.getMatrix();
        Matrix4f tmp = Matrix4f.makeTranslate(.5f, .5f, .5f);
        ret.multiplyBackward(tmp);
        tmp.setIdentity();
        tmp.setTranslation(-.5f, -.5f, -.5f);
        ret.mul(tmp);
        return new TransformationMatrix(ret);
    }

    /**
     * convert transformation from assuming corner-block system to center-block system
     */
    default TransformationMatrix blockCornerToCenter()
    {
        TransformationMatrix transform = getTransformaion();
        if (transform.isIdentity()) return TransformationMatrix.identity();

        Matrix4f ret = transform.getMatrix();
        Matrix4f tmp = Matrix4f.makeTranslate(-.5f, -.5f, -.5f);
        ret.multiplyBackward(tmp);
        tmp.setIdentity();
        tmp.setTranslation(.5f, .5f, .5f);
        ret.mul(tmp);
        return new TransformationMatrix(ret);
    }

}
