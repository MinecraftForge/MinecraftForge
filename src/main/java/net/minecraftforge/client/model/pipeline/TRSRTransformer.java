/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class TRSRTransformer extends VertexTransformer
{
    private final TRSRTransformation transform;

    public TRSRTransformer(IVertexConsumer parent, TRSRTransformation transform)
    {
        super(parent);
        this.transform = transform;
    }

    @Override
    public void put(int element, float... data)
    {
        switch (getVertexFormat().getElement(element).getUsage())
        {
            case POSITION:
                Vector4f pos = new Vector4f(data);
                transform.transformPosition(pos);
                pos.get(data);
                break;
            case NORMAL:
                Vector3f normal = new Vector3f(data);
                transform.transformNormal(normal);
                normal.get(data);
                break;
        }
        super.put(element, data);
    }
}
