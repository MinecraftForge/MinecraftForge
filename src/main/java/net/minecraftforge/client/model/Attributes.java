package net.minecraftforge.client.model;

import java.util.List;

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
        int size = first.getNextOffset();
        if(size != second.getNextOffset()) return false;

        int padding = 0;
        int j = 0;
        for(VertexFormatElement firstAttr : (List<VertexFormatElement>)first.getElements())
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
