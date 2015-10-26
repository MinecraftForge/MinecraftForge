package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

public class VertexTransformer implements IVertexConsumer
{
    protected final IVertexConsumer parent;

    public VertexTransformer(IVertexConsumer parent)
    {
        this.parent = parent;
    }

    public VertexFormat getVertexFormat()
    {
        return parent.getVertexFormat();
    }

    public void setQuadTint(int tint)
    {
        parent.setQuadTint(tint);
    }

    public void setQuadOrientation(EnumFacing orientation)
    {
        parent.setQuadOrientation(orientation);
    }

    public void setQuadColored()
    {
        parent.setQuadColored();
    }

    public void put(int element, float... data)
    {
        parent.put(element, data);
    }
}
