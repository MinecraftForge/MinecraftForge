package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.vertex.VertexFormat;

public abstract class TransformerConsumer implements IVertexConsumer {
    private IVertexConsumer parent;

    protected TransformerConsumer(IVertexConsumer parent)
    {
        this.parent = parent;
    }

    @Override
    public VertexFormat getVertexFormat()
    {
        return parent.getVertexFormat();
    }

    @Override
    public void put(int element, float... data)
    {
        float[] newData = transform(element, data);
        parent.put(element, newData);
    }

    protected abstract float[] transform(int element, float... data);
}