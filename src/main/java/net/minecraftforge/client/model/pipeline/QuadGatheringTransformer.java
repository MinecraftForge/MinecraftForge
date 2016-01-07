package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.vertex.VertexFormat;

public abstract class QuadGatheringTransformer implements IVertexConsumer
{
    protected IVertexConsumer parent;
    protected VertexFormat format;
    protected int vertices = 0;

    protected byte[] dataLength = null;
    protected float[][][] quadData = null;

    public void setParent(IVertexConsumer parent)
    {
        this.parent = parent;
    }

    public void setVertexFormat(VertexFormat format)
    {
        this.format = format;
        dataLength = new byte[format.getElementCount()];
        quadData = new float[format.getElementCount()][4][4];
    }

    @Override
    public VertexFormat getVertexFormat()
    {
        return format;
    }

    @Override
    public void put(int element, float... data)
    {
        System.arraycopy(data, 0, quadData[element][vertices], 0, data.length);
        if(element == getVertexFormat().getElementCount() - 1) vertices++;
        if(vertices == 0)
        {
            dataLength[element] = (byte)data.length;
        }
        else if(vertices == 4)
        {
            vertices = 0;
            processQuad();
        }
    }

    protected abstract void processQuad();
}
