package net.minecraftforge.fmp.multipart;

import net.minecraft.util.EnumFacing;

/**
 * Interface used to add solidity to the sides of an {@link IMultipart}.<br/>
 * For slotted parts, only the faces occupied by the part are checked. For non-slotted parts, all faces are checked.
 *
 * @see IMultipart
 * @see Multipart
 * @see ISolidTopPart
 */
public interface ISolidPart extends IMultipart
{
    
    /**
     * Checks whether or not the specified side of this part is solid.
     */
    public boolean isSideSolid(EnumFacing side);

    /**
     * Interface used to allow torches to be placed on top of an {@link IMultipart}. (The side doesn't have to be solid)
     *
     * @see IMultipart
     * @see Multipart
     * @see ISolidPart
     */
    public interface ISolidTopPart extends IMultipart
    {
        
        /**
         * Checks whether or not a torch can be placed on top of this part.
         */
        public boolean canPlaceTorchOnTop();

    }

}
