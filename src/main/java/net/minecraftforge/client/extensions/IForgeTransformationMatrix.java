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
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.util.Direction;
import net.minecraftforge.common.model.TransformationHelper;

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
        return getTransformaion().equals(TransformationMatrix.func_227983_a_());
    }

    default void push(MatrixStack stack)
    {
        stack.func_227860_a_();
        stack.func_227863_a_(getTransformaion().func_227989_d_());
        Vector3f trans = getTransformaion().getTranslation();
        stack.func_227861_a_(trans.getX(), trans.getY(), trans.getZ());
        stack.func_227863_a_(getTransformaion().getRightRot());
        Vector3f scale = getTransformaion().getScale();
        stack.func_227861_a_(scale.getX(), scale.getY(), scale.getZ());
    }

    default TransformationMatrix compose(TransformationMatrix other)
    {
        if (getTransformaion().isIdentity()) return other;
        if (other.isIdentity()) return getTransformaion();
        Matrix4f m = getTransformaion().func_227988_c_();
        m.func_226595_a_(other.func_227988_c_());
        return new TransformationMatrix(m);
    }

    default TransformationMatrix inverse()
    {
        if (isIdentity()) return getTransformaion();
        javax.vecmath.Matrix4f m = TransformationHelper.toVecmath(getTransformaion().func_227988_c_());
        m.invert();
        return new TransformationMatrix(TransformationHelper.toMojang(m));
    }

    default void transformPosition(Vector4f position)
    {
        TransformationHelper.transform(getTransformaion().func_227988_c_(), position);
    }

    default void transformNormal(Vector3f normal)
    {
        javax.vecmath.Vector3f copy = TransformationHelper.toVecmath(normal);
        transformNormal(copy);
        normal.set(copy.x, copy.y, copy.z);
    }

    default void transformNormal(javax.vecmath.Vector3f normal)
    {
        getTransformaion().getNormalMatrix().transform(normal);
        normal.normalize();
    }

    default Direction rotateTransform(Direction facing)
    {
        return Direction.func_229385_a_(getTransformaion().func_227988_c_(), facing);
    }

}
