package net.minecraftforge.client.model;

import java.nio.ByteBuffer;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

/**
 * Assumes that the data length is not less than e.getElementCount().
 * Also assumes that element index passed will increment from 0 to format.getElementCount() - 1.
 * Normal, Color and UV are assumed to be in 0-1 range.
 */
public interface IVertexConsumer
{
    void setVertexFormat(VertexFormat format);
    void put(int element, float... data);

    /**
     * Assumes VertexFormatElement is present in the WorlRenderer's vertex format.
     */
    public static class WorldRendererImpl implements IVertexConsumer
    {
        private final WorldRenderer renderer;
        private VertexFormat format;
        private float x, y, z;

        public WorldRendererImpl(WorldRenderer renderer)
        {
            super();
            this.renderer = renderer;
        }

        public void setVertexFormat(VertexFormat format)
        {
            renderer.setVertexFormat(format);
            this.format = new VertexFormat(format);
        }

        public void put(int element, float... data)
        {
            VertexFormatElement e = format.getElement(element);
            if(e.getElementCount() > data.length) throw new IllegalArgumentException("not enough elements");
            switch(e.getUsage())
            {
            case POSITION:
                x = data[0];
                y = data[1];
                z = data[2];
                break;
            case COLOR:
                renderer.setColorRGBA_F(data[0], data[1], data[2], data[3]);
                break;
            case UV:
                if (e.getIndex() == 0)
                {
                    renderer.setTextureUV(data[0], data[1]);
                }
                else if (e.getIndex() == 1)
                {
                    // assume the full range is used
                    int b = (int)(data[0] * 0x10000) << 16 | (int)(data[1] * 0x10000);
                    renderer.setBrightness(b);
                }
                else throw new IllegalArgumentException("WorldRenderer only has 2 texture units");
                break;
            case NORMAL:
                renderer.setNormal(data[0], data[1], data[2]);
                break;
            default:
                // WorldRenderer doesn't know about anything else
                break;
            }
            if(element == format.getElementCount() - 1) // we're done
            {
                renderer.addVertex(x, y, z);
            }
        }
    }

    public static class ByteBufferImpl implements IVertexConsumer
    {
        private final ByteBuffer buf;
        private VertexFormat format;

        public ByteBufferImpl(ByteBuffer buf)
        {
            this.buf = buf;
        }

        public void setVertexFormat(VertexFormat format)
        {
            this.format = new VertexFormat(format);
        }

        public void put(int element, float... data)
        {
            VertexFormatElement e = format.getElement(element);
            if(e.getElementCount() > data.length) throw new IllegalArgumentException("not enough elements");
            for(int i = 0; i < e.getElementCount(); i++)
            {
                float f = data[i];
                switch(e.getType())
                {
                case BYTE:
                    buf.put((byte)(f * (Byte.MAX_VALUE - 1)));
                    break;
                case UBYTE:
                    buf.put((byte)(f * ((1 << Byte.SIZE) - 1)));
                    break;
                case SHORT:
                    buf.putShort((short)(f * (Short.MAX_VALUE - 1)));
                    break;
                case USHORT:
                    buf.putShort((short)(f * ((1 << Short.SIZE) - 1)));
                    break;
                case INT:
                    buf.putInt((int)(f * (Integer.MAX_VALUE - 1)));
                    break;
                case UINT:
                    buf.putInt((int)(f * ((1L << Integer.SIZE) - 1)));
                    break;
                case FLOAT:
                    buf.putFloat(f);
                    break;
                }
            }
        }
    }
}
