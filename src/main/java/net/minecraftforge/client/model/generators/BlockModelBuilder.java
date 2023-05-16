/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators;

import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.util.TransformationHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Builder for block models, does not currently provide any additional
 * functionality over {@link ModelBuilder}, purely a stub class with a concrete
 * generic.
 *
 * @see ModelProvider
 * @see ModelBuilder
 */
public class BlockModelBuilder extends ModelBuilder<BlockModelBuilder>
{
    private final RootTransformBuilder rootTransform;

    public BlockModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper)
    {
        super(outputLocation, existingFileHelper);
        this.rootTransform = new RootTransformBuilder(super.rootTransforms());
    }

    /**
     * @deprecated Use {@link ModelBuilder#rootTransforms()} instead
     */
    @Deprecated(forRemoval = true, since = "1.19.4")
    public RootTransformBuilder rootTransform()
    {
        return rootTransform;
    }

    @Override
    public JsonObject toJson()
    {
        return super.toJson();
    }

    /**
     * @deprecated Use {@link ModelBuilder.RootTransformsBuilder} instead via {@link ModelBuilder#rootTransforms()}
     */
    @Deprecated(forRemoval = true, since = "1.19.4")
    public class RootTransformBuilder
    {
        private final ModelBuilder<BlockModelBuilder>.RootTransformsBuilder rootTransforms;

        private RootTransformBuilder(ModelBuilder<BlockModelBuilder>.RootTransformsBuilder rootTransforms)
        {
            this.rootTransforms = rootTransforms;
        }

        /**
         * Sets the translation of the root transform.
         *
         * @param translation the translation
         * @return this builder
         * @throws NullPointerException if {@code translation} is {@code null}
         */
        public RootTransformBuilder translation(Vector3f translation)
        {
            rootTransforms.translation(translation);
            return this;
        }

        /**
         * Sets the translation of the root transform.
         *
         * @param x x translation
         * @param y y translation
         * @param z z translation
         * @return this builder
         */
        public RootTransformBuilder translation(float x, float y, float z)
        {
            return translation(new Vector3f(x, y, z));
        }

        /**
         * Sets the left rotation of the root transform.
         *
         * @param rotation the left rotation
         * @return this builder
         * @throws NullPointerException if {@code rotation} is {@code null}
         */
        public RootTransformBuilder rotation(Quaternionf rotation)
        {
            rootTransforms.rotation(rotation);
            return this;
        }

        /**
         * Sets the left rotation of the root transform.
         *
         * @param x x rotation
         * @param y y rotation
         * @param z z rotation
         * @param isDegrees whether the rotation is in degrees or radians
         * @return this builder
         */
        public RootTransformBuilder rotation(float x, float y, float z, boolean isDegrees)
        {
            return rotation(TransformationHelper.quatFromXYZ(x, y, z, isDegrees));
        }

        /**
         * Sets the left rotation of the root transform.
         *
         * @param leftRotation the left rotation
         * @return this builder
         * @throws NullPointerException if {@code leftRotation} is {@code null}
         */
        public RootTransformBuilder leftRotation(Quaternionf leftRotation)
        {
            return rotation(leftRotation);
        }

        /**
         * Sets the left rotation of the root transform.
         *
         * @param x x rotation
         * @param y y rotation
         * @param z z rotation
         * @param isDegrees whether the rotation is in degrees or radians
         * @return this builder
         */
        public RootTransformBuilder leftRotation(float x, float y, float z, boolean isDegrees)
        {
            return leftRotation(TransformationHelper.quatFromXYZ(x, y, z, isDegrees));
        }

        /**
         * Sets the right rotation of the root transform.
         *
         * @param rightRotation the right rotation
         * @return this builder
         * @throws NullPointerException if {@code rightRotation} is {@code null}
         */
        public RootTransformBuilder rightRotation(Quaternionf rightRotation)
        {
            rootTransforms.rightRotation(rightRotation);
            return this;
        }

        /**
         * Sets the right rotation of the root transform.
         *
         * @param x x rotation
         * @param y y rotation
         * @param z z rotation
         * @param isDegrees whether the rotation is in degrees or radians
         * @return this builder
         */
        public RootTransformBuilder rightRotation(float x, float y, float z, boolean isDegrees)
        {
            return rightRotation(TransformationHelper.quatFromXYZ(x, y, z, isDegrees));
        }

        /**
         * Sets the right rotation of the root transform.
         *
         * @param postRotation the right rotation
         * @return this builder
         * @throws NullPointerException if {@code rightRotation} is {@code null}
         */
        public RootTransformBuilder postRotation(Quaternionf postRotation)
        {
            return rightRotation(postRotation);
        }

        /**
         * Sets the right rotation of the root transform.
         *
         * @param x x rotation
         * @param y y rotation
         * @param z z rotation
         * @param isDegrees whether the rotation is in degrees or radians
         * @return this builder
         */
        public RootTransformBuilder postRotation(float x, float y, float z, boolean isDegrees)
        {
            return postRotation(TransformationHelper.quatFromXYZ(x, y, z, isDegrees));
        }

        /**
         * Sets the scale of the root transform.
         *
         * @param scale the scale
         * @return this builder
         */
        public RootTransformBuilder scale(float scale)
        {
            return scale(new Vector3f(scale, scale, scale));
        }

        /**
         * Sets the scale of the root transform.
         *
         * @param xScale x scale
         * @param yScale y scale
         * @param zScale z scale
         * @return this builder
         */
        public RootTransformBuilder scale(float xScale, float yScale, float zScale)
        {
            return scale(new Vector3f(xScale, yScale, zScale));
        }

        /**
         * Sets the scale of the root transform.
         *
         * @param scale the scale vector
         * @return this builder
         * @throws NullPointerException if {@code scale} is {@code null}
         */
        public RootTransformBuilder scale(Vector3f scale)
        {
            rootTransforms.scale(scale);
            return this;
        }

        /**
         * Sets the root transform.
         *
         * @param transformation the transformation to use
         * @return this builder
         * @throws NullPointerException if {@code transformation} is {@code null}
         */
        public RootTransformBuilder transform(Transformation transformation)
        {
            rootTransforms.transform(transformation);
            return this;
        }

        /**
         * Sets the origin of the root transform.
         *
         * @param origin the origin vector
         * @return this builder
         * @throws NullPointerException if {@code origin} is {@code null}
         */
        public RootTransformBuilder origin(Vector3f origin)
        {
            rootTransforms.origin(origin);
            return this;
        }

        /**
         * Sets the origin of the root transform.
         *
         * @param origin the origin name
         * @return this builder
         * @throws NullPointerException if {@code origin} is {@code null}
         * @throws IllegalArgumentException if {@code origin} is not {@code center}, {@code corner} or {@code opposing-corner}
         */
        public RootTransformBuilder origin(TransformOrigin origin)
        {
            rootTransforms.origin(origin.toNewOrigin());
            return this;
        }

        /**
         * Finish configuring the parent builder
         * @return the parent block model builder
         */
        public BlockModelBuilder end()
        {
            return BlockModelBuilder.this;
        }

        public JsonObject toJson()
        {
            return rootTransforms.toJson();
        }

        /**
         * @deprecated Use {@link TransformationHelper.TransformOrigin} instead
         */
        @Deprecated(forRemoval = true, since = "1.19.4")
        public enum TransformOrigin implements StringRepresentable
        {
            CENTER(TransformationHelper.TransformOrigin.CENTER),
            CORNER(TransformationHelper.TransformOrigin.CORNER),
            OPPOSING_CORNER(TransformationHelper.TransformOrigin.OPPOSING_CORNER);

            private final TransformationHelper.TransformOrigin newOrigin;

            TransformOrigin(TransformationHelper.TransformOrigin newOrigin)
            {
                this.newOrigin = newOrigin;
            }

            public Vector3f getVector()
            {
                return newOrigin.getVector();
            }

            @Override
            @NotNull
            public String getSerializedName()
            {
                return newOrigin.getSerializedName();
            }

            private TransformationHelper.TransformOrigin toNewOrigin()
            {
                return newOrigin;
            }

            public static @Nullable TransformOrigin fromString(String originName)
            {
                if (CENTER.getSerializedName().equals(originName))
                {
                    return CENTER;
                }
                if (CORNER.getSerializedName().equals(originName))
                {
                    return CORNER;
                }
                if (OPPOSING_CORNER.getSerializedName().equals(originName))
                {
                    return OPPOSING_CORNER;
                }
                return null;
            }
        }
    }
}
