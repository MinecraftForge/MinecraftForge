package net.minecraftforge.fmp.multipart;

import java.util.EnumSet;

/**
 * Interface that allows {@link IMultipart} to occupy one or more slots in a container.
 *
 * @see IMultipart
 * @see Multipart
 */
public interface ISlottedPart extends IMultipart
{
    
    /**
     * Gets an {@link EnumSet} of the slots occupied by this part.
     */
    public EnumSet<PartSlot> getSlotMask();

}
