/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private final RootTransformBuilder rootTransform = new RootTransformBuilder();

    public BlockModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper)
    {
        super(outputLocation, existingFileHelper);
    }

    public RootTransformBuilder rootTransform()
    {
        return rootTransform;
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject json = super.toJson();

        // If there were any transform properties set, add them to the output.
        JsonObject transform = rootTransform.toJson();
        if (transform.size() > 0)
        {
            json.add("transform", transform);
        }

        return json;
    }

    public class RootTransformBuilder
    {
        private static final Vector3f ONE = new Vector3f(1, 1, 1);

        private Vector3f translation = Vector3f.ZERO;
        private Quaternion leftRotation = Quaternion.ONE.copy();
        private Quaternion rightRotation = Quaternion.ONE.copy();
        private Vector3f scale = ONE;

        private @Nullable TransformOrigin origin;
        private @Nullable Vector3f originVec;

        private RootTransformBuilder()
        {
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
            this.translation = Preconditions.checkNotNull(translation, "Translation must not be null");
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
        public RootTransformBuilder rotation(Quaternion rotation)
        {
            this.leftRotation = Preconditions.checkNotNull(rotation, "Rotation must not be null");
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
            return rotation(new Quaternion(x, y, z, isDegrees));
        }

        /**
         * Sets the left rotation of the root transform.
         *
         * @param leftRotation the left rotation
         * @return this builder
         * @throws NullPointerException if {@code leftRotation} is {@code null}
         */
        public RootTransformBuilder leftRotation(Quaternion leftRotation)
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
            return leftRotation(new Quaternion(x, y, z, isDegrees));
        }

        /**
         * Sets the right rotation of the root transform.
         *
         * @param rightRotation the right rotation
         * @return this builder
         * @throws NullPointerException if {@code rightRotation} is {@code null}
         */
        public RootTransformBuilder rightRotation(Quaternion rightRotation)
        {
            this.rightRotation = Preconditions.checkNotNull(rightRotation, "Rotation must not be null");
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
            return rightRotation(new Quaternion(x, y, z, isDegrees));
        }

        /**
         * Sets the right rotation of the root transform.
         *
         * @param postRotation the right rotation
         * @return this builder
         * @throws NullPointerException if {@code rightRotation} is {@code null}
         */
        public RootTransformBuilder postRotation(Quaternion postRotation)
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
            return postRotation(new Quaternion(x, y, z, isDegrees));
        }

        /**
         * Sets the scale of the root transform.
         *
         * @param scale the scale
         * @return this builder
         * @throws NullPointerException if {@code scale} is {@code null}
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
            this.scale = Preconditions.checkNotNull(scale, "Scale must not be null");
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
            Preconditions.checkNotNull(transformation, "Transformation must not be null");
            this.translation = transformation.getTranslation();
            this.leftRotation = transformation.getLeftRotation();
            this.rightRotation = transformation.getRightRotation();
            this.scale = transformation.getScale();
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
            this.originVec = Preconditions.checkNotNull(origin, "Origin must not be null");
            this.origin = null;
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
            this.origin = Preconditions.checkNotNull(origin, "Origin must not be null");
            this.originVec = null;
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

        public JsonObject toJson() {
            // Write the transform to an object
            JsonObject transform = new JsonObject();

            if (!translation.equals(Vector3f.ZERO))
            {
                transform.add("translation", writeVec3(translation));
            }

            if (!scale.equals(ONE))
            {
                transform.add("scale", writeVec3(scale));
            }

            if (!leftRotation.equals(Quaternion.ONE))
            {
                transform.add("rotation", writeQuaternion(leftRotation));
            }

            if (!rightRotation.equals(Quaternion.ONE))
            {
                transform.add("post_rotation", writeQuaternion(rightRotation));
            }

            if (origin != null)
            {
                transform.addProperty("origin", origin.getSerializedName());
            }
            else if (originVec != null && !originVec.equals(Vector3f.ZERO))
            {
                transform.add("origin", writeVec3(originVec));
            }

            return transform;
        }

        public enum TransformOrigin implements StringRepresentable
        {
            CENTER(new Vector3f(.5f, .5f, .5f), "center"),
            CORNER(Vector3f.ZERO, "corner"),
            OPPOSING_CORNER(ONE, "opposing-corner");

            private final Vector3f vec;
            private final String name;

            TransformOrigin(Vector3f vec, String name)
            {
                this.vec = vec;
                this.name = name;
            }

            public Vector3f getVector()
            {
                return vec;
            }

            @Override
            @NotNull
            public String getSerializedName()
            {
                return name;
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

        private JsonArray writeVec3(Vector3f vector)
        {
            JsonArray array = new JsonArray();
            array.add(vector.x());
            array.add(vector.y());
            array.add(vector.z());
            return array;
        }

        private JsonArray writeQuaternion(Quaternion quaternion)
        {
            JsonArray array = new JsonArray();
            array.add(quaternion.i());
            array.add(quaternion.j());
            array.add(quaternion.k());
            array.add(quaternion.r());
            return array;
        }
    }
}
