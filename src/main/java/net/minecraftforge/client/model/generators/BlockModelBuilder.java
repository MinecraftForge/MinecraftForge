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
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

/**
 * Builder for block models, does not currently provide any additional
 * functionality over {@link ModelBuilder}, purely a stub class with a concrete
 * generic.
 * 
 * @see ModelProvider
 * @see ModelBuilder
 */
public class BlockModelBuilder extends ModelBuilder<BlockModelBuilder> {

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

        // Write the transform to an object
        JsonObject transform = new JsonObject();

        if (!rootTransform.translation.equals(Vector3f.ZERO))
        {
            transform.add("translation", writeVec3(rootTransform.translation));
        }

        if (!rootTransform.scale.equals(new Vector3f(1, 1, 1)))
        {
            transform.add("scale", writeVec3(rootTransform.scale));
        }

        Quaternion leftRotation = rootTransform.leftRotation;
        if (!leftRotation.equals(Quaternion.ONE))
        {
            transform.add("left_rotation", writeQuaternion(leftRotation));
        }

        Quaternion rightRotation = rootTransform.rightRotation;
        if (!rightRotation.equals(Quaternion.ONE))
        {
            transform.add("right_rotation", writeQuaternion(rightRotation));
        }

        if (rootTransform.originName != null)
        {
            transform.addProperty("origin", rootTransform.originName);
        }
        else if (rootTransform.origin != null && rootTransform.origin != Vector3f.ZERO)
        {
            transform.add("origin", writeVec3(rootTransform.origin));
        }

        // If there were any transform properties set, add them to the output.
        if (transform.size() > 0)
        {
            json.add("transform", transform);
        }

        return json;
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

    public class RootTransformBuilder
    {
        private Vector3f translation = Vector3f.ZERO;
        private Quaternion leftRotation = Quaternion.ONE.copy();
        private Quaternion rightRotation = Quaternion.ONE.copy();
        private Vector3f scale = new Vector3f(1, 1, 1);

        private @Nullable String originName;
        private @Nullable Vector3f origin;

        /**
         * Sets the translation of the root transform.
         *
         * @param translation the translation
         * @return this builder
         * @throws NullPointerException if {@code translation} is {@code null}
         */
        public RootTransformBuilder translation(Vector3f translation)
        {
            Preconditions.checkNotNull(translation, "Translation must not be null");
            this.translation = translation;
            return this;
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
            Preconditions.checkNotNull(rotation, "Rotation must not be null");
            this.leftRotation = rotation;
            return this;
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
         * Sets the right rotation of the root transform.
         *
         * @param rightRotation the right rotation
         * @return this builder
         * @throws NullPointerException if {@code rightRotation} is {@code null}
         */
        public RootTransformBuilder rightRotation(Quaternion rightRotation)
        {
            Preconditions.checkNotNull(rightRotation, "Rotation must not be null");
            this.rightRotation = rightRotation;
            return this;
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
         * @param scale the scale vector
         * @return this builder
         * @throws NullPointerException if {@code scale} is {@code null}
         */
        public RootTransformBuilder scale(Vector3f scale)
        {
            Preconditions.checkNotNull(scale, "Scale must not be null");
            this.scale = scale;
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
         * Sets the origi of the root transform.
         *
         * @param origin the origin vector
         * @return this builder
         * @throws NullPointerException if {@code origin} is {@code null}
         */
        public RootTransformBuilder origin(Vector3f origin)
        {
            Preconditions.checkNotNull(origin, "Origin must not be null");
            this.origin = origin;
            this.originName = null;
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
        public RootTransformBuilder origin(String origin)
        {
            Preconditions.checkNotNull(origin, "Origin must not be null");
            Preconditions.checkArgument(origin.equals("center") || origin.equals("corner") || origin.equals("opposing-corner"), "Invalid value for origin name.");
            this.originName = origin;
            this.origin = null;
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
    }
}
