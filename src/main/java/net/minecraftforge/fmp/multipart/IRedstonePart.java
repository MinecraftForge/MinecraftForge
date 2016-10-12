package net.minecraftforge.fmp.multipart;

import net.minecraft.util.EnumFacing;

/**
 * Interface that adds redstone support to {@link IMultipart}.<br/>
 * If your part also implements {@link ISlottedPart}, you may want to implement {@link ISlottedRedstonePart} instead.
 *
 * @see IMultipart
 * @see Multipart
 * @see ISlottedPart
 * @see ISlottedRedstonePart
 */
public interface IRedstonePart extends IMultipart
{
    
    /**
     * Whether or not redstone can connect to the specified side of this part.
     */
    public boolean canConnectRedstone(EnumFacing side);

    /**
     * Gets the weak redstone signal output by this part on the specified side.
     */
    public int getWeakSignal(EnumFacing side);

    /**
     * Gets the strong redstone signal output by this part on the specified side.
     */
    public int getStrongSignal(EnumFacing side);

    /**
     * Interface used to add redstone support to {@link ISlottedPart}.
     *
     * @see IMultipart
     * @see Multipart
     * @see IRedstonePart
     * @see ISlottedPart
     */
    public static interface ISlottedRedstonePart extends IRedstonePart, ISlottedPart
    {
        
    }

}
