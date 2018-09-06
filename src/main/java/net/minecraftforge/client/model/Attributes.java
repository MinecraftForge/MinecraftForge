/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.client.model;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public class Attributes
{
    /*
     * Default format of the data in IBakedModel
     */
    public static final VertexFormat DEFAULT_BAKED_FORMAT;

    static
    {
        DEFAULT_BAKED_FORMAT = new VertexFormat();
        DEFAULT_BAKED_FORMAT.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.POSITION, 3));
        DEFAULT_BAKED_FORMAT.addElement(new VertexFormatElement(0, EnumType.UBYTE, EnumUsage.COLOR,    4));
        DEFAULT_BAKED_FORMAT.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.UV,       2));
        DEFAULT_BAKED_FORMAT.addElement(new VertexFormatElement(0, EnumType.BYTE,  EnumUsage.PADDING,  4));
    }

    /*
     * Can first format be used where second is expected
     */
    public static boolean moreSpecific(VertexFormat first, VertexFormat second)
    {
        int size = first.getSize();
        if(size != second.getSize()) return false;

        int padding = 0;
        int j = 0;
        for(VertexFormatElement firstAttr : first.getElements())
        {
            while(j < second.getElementCount() && second.getElement(j).getUsage() == EnumUsage.PADDING)
            {
                padding += second.getElement(j++).getSize();
            }
            if(j >= second.getElementCount() && padding == 0)
            {
                // if no padding is left, but there are still elements in first (we're processing one) - it doesn't fit
                return false;
            }
            if(padding == 0)
            {
                // no padding - attributes have to match
                VertexFormatElement secondAttr = second.getElement(j++);
                if(
                    firstAttr.getIndex() != secondAttr.getIndex() ||
                    firstAttr.getElementCount() != secondAttr.getElementCount() ||
                    firstAttr.getType() != secondAttr.getType() ||
                    firstAttr.getUsage() != secondAttr.getUsage())
                {
                    return false;
                }
            }
            else
            {
                // padding - attribute should fit in it
                padding -= firstAttr.getSize();
                if(padding < 0) return false;
            }
        }

        if(padding != 0 || j != second.getElementCount()) return false;
        return true;
    }
}
