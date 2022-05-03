/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

public class TRSRTransformer extends VertexTransformer
{
    private final TransformationMatrix transform;

    public TRSRTransformer(IVertexConsumer parent, TransformationMatrix transform)
    {
        super(parent);
        this.transform = transform;
    }

    @Override
    public void put(int element, float... data)
    {
        switch (getVertexFormat().getElements().get(element).getUsage())
        {
            case POSITION:
                Vector4f pos = new Vector4f(data[0], data[1], data[2], data[3]);
                transform.transformPosition(pos);
                data[0] = pos.x();
                data[1] = pos.y();
                data[2] = pos.z();
                data[3] = pos.w();
                break;
            case NORMAL:
                Vector3f normal = new Vector3f(data);
                transform.transformNormal(normal);
                data[0] = normal.x();
                data[1] = normal.y();
                data[2] = normal.z();
                break;
        }
        super.put(element, data);
    }
}
