package net.minecraftforge.client.model;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector4f;

import net.minecraft.client.renderer.block.model.BakedQuad;
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
        DEFAULT_BAKED_FORMAT.setElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.POSITION, 3));
        DEFAULT_BAKED_FORMAT.setElement(new VertexFormatElement(0, EnumType.UBYTE, EnumUsage.COLOR,    4));
        DEFAULT_BAKED_FORMAT.setElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.UV,       2));
        DEFAULT_BAKED_FORMAT.setElement(new VertexFormatElement(0, EnumType.BYTE,  EnumUsage.PADDING,  4));
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

    public static void put(ByteBuffer buf, VertexFormatElement e, boolean denormalize, Number fill, Number... ns)
    {
        if(e.getElementCount() > ns.length && fill == null) throw new IllegalArgumentException("not enough elements");
        Number n;
        for(int i = 0; i < e.getElementCount(); i++)
        {
            if(i < ns.length) n = ns[i];
            else n = fill;
            switch(e.getType())
            {
            case BYTE:
                buf.put(denormalize ? (byte)(n.floatValue() * (Byte.MAX_VALUE - 1)) : n.byteValue());
                break;
            case UBYTE:
                buf.put(denormalize ? (byte)(n.floatValue() * ((1 << Byte.SIZE) - 1)) : n.byteValue());
                break;
            case SHORT:
                buf.putShort(denormalize ? (short)(n.floatValue() * (Short.MAX_VALUE - 1)) : n.shortValue());
                break;
            case USHORT:
                buf.putShort(denormalize ? (short)(n.floatValue() * ((1 << Short.SIZE) - 1)) : n.shortValue());
                break;
            case INT:
                buf.putInt(denormalize ? (int)(n.doubleValue() * (Integer.MAX_VALUE - 1)) : n.intValue());
                break;
            case UINT:
                buf.putInt(denormalize ? (int)(n.doubleValue() * ((1L << Integer.SIZE) - 1)) : n.intValue());
                break;
            case FLOAT:
                buf.putFloat(n.floatValue());
                break;
            }
        }
    }

    public static BakedQuad transform(TRSRTransformation transform, BakedQuad quad, VertexFormat format)
    {
        for (VertexFormatElement e : (List<VertexFormatElement>)format.getElements())
        {
            if (e.getUsage() == VertexFormatElement.EnumUsage.POSITION)
            {
                if (e.getType() != VertexFormatElement.EnumType.FLOAT)
                {
                    throw new IllegalArgumentException("can only transform float position");
                }
                int[] data = Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length);
                float[] pos = new float[] { 0f, 0f, 0f, 1f };
                for (int i = 0; i < Math.min(4, e.getElementCount()); i++)
                {
                    pos[i] = Float.intBitsToFloat(data[e.getOffset() / 4 + i]);
                }
                Vector4f vec = new Vector4f(pos);
                transform.getMatrix().transform(vec);
                vec.get(pos);
                for (int i = 0; i < Math.min(4, e.getElementCount()); i++)
                {
                    data[e.getOffset() / 4 + i] = Float.floatToRawIntBits(pos[i]);
                }
                return new BakedQuad(data, quad.getTintIndex(), quad.getFace());
            }
        }
        return quad;
    }
}
