package net.minecraftforge.event.world;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired serverside whenever a Detector Rail looks for a minecart to get a comparator value from
 * in {@link net.minecraft.block.BlockRailDetector#getComparatorInputOverride}.
 * This event is not cancelable.
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 */
public class DetectorRailComparatorEvent extends Event
{

    private final BlockPos pos;
    private final AxisAlignedBB searchBox;
    private int redstonePower;

    public DetectorRailComparatorEvent(BlockPos pos, AxisAlignedBB searchBox)
    {
        this.pos = pos;
        this.searchBox = searchBox;
        this.redstonePower = -1;
    }

    /**
     * @return The position of the Detector Rail
     */
    public BlockPos getPos()
    {
        return pos;
    }

    /**
     * @return The bounding box that is to be searched for minecarts
     */
    public AxisAlignedBB getSearchBox()
    {
        return searchBox;
    }

    /**
     * @return The redstone signal strength to give the comparator. Values below 0 mean vanilla logic will run.
     */
    public int getRedstonePower()
    {
        return redstonePower;
    }

    /**
     * Set the redstone power to give the comparator. Values below 0 mean vanilla logic will run.
     */
    public void setRedstonePower(int redstonePower)
    {
        this.redstonePower = redstonePower;
    }

}
