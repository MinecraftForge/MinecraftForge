package net.minecraftforge.client.model.pipeline;

import java.util.Arrays;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Assumes VertexFormatElement is present in the WorlRenderer's vertex format.
 */
public class WorldRendererConsumer implements IVertexConsumer
{
    private final WorldRenderer renderer;
    private final int[] quadData;
    private int v = 0;
    private BlockPos offset = BlockPos.ORIGIN;

    public WorldRendererConsumer(WorldRenderer renderer)
    {
        super();
        this.renderer = renderer;
        quadData = new int[renderer.getVertexFormat().getNextOffset()/* / 4 * 4 */];
    }

    public VertexFormat getVertexFormat()
    {
        return renderer.getVertexFormat();
    }

    public void put(int e, float... data)
    {
        LightUtil.pack(data, quadData, getVertexFormat(), v, e);
        if(e == getVertexFormat().getElementCount() - 1)
        {
            v++;
            if(v == 4)
            {
                renderer.addVertexData(quadData);
                renderer.putPosition(offset.getX(), offset.getY(), offset.getZ());
                Arrays.fill(quadData, 0);
                v = 0;
            }
        }
    }

    public void setOffset(BlockPos offset)
    {
        this.offset = new BlockPos(offset);
    }

    public void setQuadTint(int tint) {}
    public void setQuadOrientation(EnumFacing orientation) {}
    public void setQuadColored() {}
}