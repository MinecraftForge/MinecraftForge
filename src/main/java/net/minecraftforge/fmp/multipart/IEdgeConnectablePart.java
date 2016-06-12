package net.minecraftforge.fmp.multipart;

/**
 * Interface that represents an {@link IMultipart} that can connect through an edge of the block. Used to change the
 * shape of microblocks.
 *
 * @see IMultipart
 * @see Multipart
 */
public interface IEdgeConnectablePart extends IMultipart
{
    
    /**
     * Gets the width of the part in pixels.
     */
    public int getHoleWidth(PartSlot slot);

    /**
     * Gets the height of the part in pixels.
     */
    public int getHoleHeight(PartSlot slot);

}
