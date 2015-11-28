package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IColoredBakedQuad;

// advantages: non-fixed-length vertex format, no overhead of packing and unpacking attributes to transform the model
// disadvantages: (possibly) larger memory footprint, overhead on packing the attributes at the final rendering stage
public class UnpackedBakedQuad extends BakedQuad
{
    protected final float[][][] unpackedData;
    protected final VertexFormat format;
    protected boolean packed = false;

    public UnpackedBakedQuad(float[][][] unpackedData, int tint, EnumFacing orientation, VertexFormat format)
    {
        super(new int[format.getNextOffset() /* / 4 * 4 */], tint, orientation);
        this.unpackedData = unpackedData;
        this.format = format;
    }

    @Override
    public int[] getVertexData()
    {
        if(!packed)
        {
            packed = true;
            for(int v = 0; v < 4; v++)
            {
                for(int e = 0; e < format.getElementCount(); e++)
                {
                    LightUtil.pack(unpackedData[v][e], vertexData, format, v, e);
                }
            }
        }
        return vertexData;
    }

    @Override
    public void pipe(IVertexConsumer consumer)
    {
        int[] eMap = LightUtil.mapFormats(consumer.getVertexFormat(), format);

        if(hasTintIndex())
        {
            consumer.setQuadTint(getTintIndex());
        }
        consumer.setQuadOrientation(getFace());
        if(this instanceof IColoredBakedQuad)
        {
            consumer.setQuadColored();
        }
        for(int v = 0; v < 4; v++)
        {
            for(int e = 0; e < consumer.getVertexFormat().getElementCount(); e++)
            {
                if(eMap[e] != format.getElementCount())
                {
                    consumer.put(e, unpackedData[v][eMap[e]]);
                }
                else
                {
                    consumer.put(e);
                }
            }
        }
    }

    public static class Colored extends UnpackedBakedQuad implements IColoredBakedQuad
    {
        public Colored(float[][][] unpackedData, int tint, EnumFacing orientation, VertexFormat format)
        {
            super(unpackedData, tint, orientation, format);
        }
    }

    public static class Builder implements IVertexConsumer
    {
        private final VertexFormat format;
        private final float[][][] unpackedData;
        private int tint = -1;
        private EnumFacing orientation;
        private boolean isColored = false;

        private int vertices = 0;
        private int elements = 0;
        private boolean full = false;

        public Builder(VertexFormat format)
        {
            this.format = format;
            unpackedData = new float[4][format.getElementCount()][4];
        }

        public VertexFormat getVertexFormat()
        {
            return format;
        }

        public void setQuadTint(int tint)
        {
            this.tint = tint;
        }

        public void setQuadOrientation(EnumFacing orientation)
        {
            this.orientation = orientation;
        }

        public void setQuadColored()
        {
            this.isColored = true;
        }

        public void put(int element, float... data)
        {
            for(int i = 0; i < 4; i++)
            {
                if(i < data.length)
                {
                    unpackedData[vertices][element][i] = data[i];
                }
                else
                {
                    unpackedData[vertices][element][i] = 0;
                }
            }
            elements++;
            if(elements == format.getElementCount())
            {
                vertices++;
                elements = 0;
            }
            if(vertices == 4)
            {
                full = true;
            }
        }

        public UnpackedBakedQuad build()
        {
            if(!full)
            {
                throw new IllegalStateException("not enough data");
            }
            if(isColored)
            {
                return new Colored(unpackedData, tint, orientation, format);
            }
            return new UnpackedBakedQuad(unpackedData, tint, orientation, format);
        }
    }
}