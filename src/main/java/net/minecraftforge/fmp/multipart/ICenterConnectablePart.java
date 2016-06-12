package net.minecraftforge.fmp.multipart;

import net.minecraft.util.EnumFacing;

/**
 * Interface that represents an {@link IMultipart} that can connect through the center of a side. Used to change the
 * shape of microblocks.
 *
 * @see IMultipart
 * @see Multipart
 */
public interface ICenterConnectablePart extends IMultipart
{
    
    /**
     * Gets the radius of the hole in pixels.
     */
    public int getHoleRadius(EnumFacing side);

}
