package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.vertex.VertexFormat;

public abstract class QuadGatheringTransformer implements IVertexConsumer
{
    protected IVertexConsumer parent;
    protected VertexFormat format;
    protected int vertices = 0;
    protected int[] dataLength;
    protected float[][][] quadData = null;

    public void setParent(IVertexConsumer parent)
    {
        this.parent = parent;
    }

    public void setVertexFormat(VertexFormat format)
    {
        this.format = format;
        quadData = new float[format.getElementCount()][4][4];
        dataLength = new int[format.getElementCount()];
    }

    @Override
    public VertexFormat getVertexFormat()
    {
        return format;
    }

    @Override
    public void put(int element, float... data)
    {
        dataLength[element] = data.length;
        System.arraycopy(data, 0, quadData[element][vertices], 0, data.length);
        if(element == getVertexFormat().getElementCount() - 1) vertices++;
        if(vertices == 4)
        {
            vertices = 0;
            processQuad();
        }
    }

    protected abstract void processQuad();
}
