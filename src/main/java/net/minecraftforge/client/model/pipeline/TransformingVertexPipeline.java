/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Vertex pipeline element that applies a transformation to incoming geometry.
 */
public class TransformingVertexPipeline extends VertexConsumerWrapper
{
    private final Transformation transformation;

    public TransformingVertexPipeline(VertexConsumer parent, Transformation transformation)
    {
        super(parent);
        this.transformation = transformation;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z)
    {
        var vec = new Vector4f((float) x, (float) y, (float) z, 1);
        transformation.transformPosition(vec);
        vec.div(vec.w);
        return super.addVertex(vec.x(), vec.y(), vec.z());
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z)
    {
        var vec = new Vector3f(x, y, z);
        transformation.transformNormal(vec);
        vec.normalize();
        return super.setNormal(vec.x(), vec.y(), vec.z());
    }

}
