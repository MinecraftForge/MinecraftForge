/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;

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
                data[0] = pos.getX();
                data[1] = pos.getY();
                data[2] = pos.getZ();
                data[3] = pos.getW();
                break;
            case NORMAL:
                Vector3f normal = new Vector3f(data);
                transform.transformNormal(normal);
                data[0] = normal.getX();
                data[1] = normal.getY();
                data[2] = normal.getZ();
                break;
        }
        super.put(element, data);
    }
}
