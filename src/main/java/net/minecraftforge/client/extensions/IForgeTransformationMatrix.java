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
        return getTransformaion().equals(TransformationMatrix.func_227983_a_());
    }

    default void push(MatrixStack stack)
    {
        stack.func_227860_a_();

        Vector3f trans = getTransformaion().getTranslation();
        stack.func_227861_a_(trans.getX(), trans.getY(), trans.getZ());

        stack.func_227863_a_(getTransformaion().func_227989_d_());

        Vector3f scale = getTransformaion().getScale();
        stack.func_227862_a_(scale.getX(), scale.getY(), scale.getZ());

        stack.func_227863_a_(getTransformaion().getRightRot());

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
        Matrix4f m = getTransformaion().func_227988_c_().func_226601_d_();
        m.func_226600_c_();
        return new TransformationMatrix(m);
    }

    default void transformPosition(Vector4f position)
    {
        position.func_229372_a_(getTransformaion().func_227988_c_());
    }

    default void transformNormal(Vector3f normal)
    {
        normal.func_229188_a_(getTransformaion().getNormalMatrix());
        normal.func_229194_d_();
    }

    default Direction rotateTransform(Direction facing)
    {
        return Direction.func_229385_a_(getTransformaion().func_227988_c_(), facing);
    }

    /**
     * convert transformation from assuming center-block system to corner-block system
     */
    default TransformationMatrix blockCenterToCorner()
    {
        TransformationMatrix transform = getTransformaion();
        if (transform.isIdentity()) return TransformationMatrix.func_227983_a_();

        Matrix4f ret = transform.func_227988_c_();
        Matrix4f tmp = Matrix4f.func_226599_b_(.5f, .5f, .5f);
        ret.multiplyBackward(tmp);
        tmp.func_226591_a_();
        tmp.setTranslation(-.5f, -.5f, -.5f);
        ret.func_226595_a_(tmp);
        return new TransformationMatrix(ret);
    }

    /**
     * convert transformation from assuming corner-block system to center-block system
     */
    default TransformationMatrix blockCornerToCenter()
    {
        TransformationMatrix transform = getTransformaion();
        if (transform.isIdentity()) return TransformationMatrix.func_227983_a_();

        Matrix4f ret = transform.func_227988_c_();
        Matrix4f tmp = Matrix4f.func_226599_b_(-.5f, -.5f, -.5f);
        ret.multiplyBackward(tmp);
        tmp.func_226591_a_();
        tmp.setTranslation(.5f, .5f, .5f);
        ret.func_226595_a_(tmp);
        return new TransformationMatrix(ret);
    }

}
