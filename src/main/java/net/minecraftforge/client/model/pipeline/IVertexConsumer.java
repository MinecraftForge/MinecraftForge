package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

/**
 * Assumes that the data length is not less than e.getElementCount().
 * Also assumes that element index passed will increment from 0 to format.getElementCount() - 1.
 * Normal, Color and UV are assumed to be in 0-1 range.
 */
public interface IVertexConsumer
{
    /**
     * @return the format that should be used for passed data.
     */
    VertexFormat getVertexFormat();

    void setQuadTint(int tint);
    void setQuadOrientation(EnumFacing orientation);
    void setQuadColored();

    void put(int element, float... data);
}
