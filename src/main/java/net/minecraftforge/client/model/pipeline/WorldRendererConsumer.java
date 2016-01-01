package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Assumes VertexFormatElement is present in the WorlRenderer's vertex format.
 */
public class WorldRendererConsumer implements IVertexConsumer
{
    private static final float[] dummyColor = new float[]{ 1, 1, 1, 1 };
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
        VertexFormat format = getVertexFormat();
        if(renderer.isColorDisabled() && format.getElement(e).getUsage() == EnumUsage.COLOR)
        {
            data = dummyColor;
        }
        LightUtil.pack(data, quadData, format, v, e);
        if(e == format.getElementCount() - 1)
        {
            v++;
            if(v == 4)
            {
                renderer.addVertexData(quadData);
                renderer.putPosition(offset.getX(), offset.getY(), offset.getZ());
                //Arrays.fill(quadData, 0);
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
