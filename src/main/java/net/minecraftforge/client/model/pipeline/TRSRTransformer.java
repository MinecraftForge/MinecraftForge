/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
