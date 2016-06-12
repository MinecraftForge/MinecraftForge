package net.minecraftforge.fmp.multipart;

import java.util.EnumSet;

/**
 * Interface that allows {@link IMultipart} to occlude a slot it doesn't occupy.<br/>
 * Used for occlusion testing in wires, pipes, and other connecting parts.
 *
 * @see IMultipart
 * @see Multipart
 */
public interface ISlotOccludingPart extends ISlottedPart
{
    
    /**
     * Gets an {@link EnumSet} of slots that are occluded by this part, even though they aren't occupied by it.
     */
    public EnumSet<PartSlot> getOccludedSlots();

}