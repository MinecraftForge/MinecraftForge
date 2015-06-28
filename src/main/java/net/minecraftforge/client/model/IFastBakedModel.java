package net.minecraftforge.client.model;

import java.nio.ByteBuffer;

import net.minecraft.util.EnumFacing;

public interface IFastBakedModel extends IFlexibleBakedModel, IQuadInfo
{
    /**
     * @return the buffer containing the vertex data for this model.
     * data will be read from the buffer according to the buffer's position and limit.
     */
    ByteBuffer getDataBuffer();
}
