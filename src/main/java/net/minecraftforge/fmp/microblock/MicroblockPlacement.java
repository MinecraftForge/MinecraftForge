package net.minecraftforge.fmp.microblock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fmp.multipart.IMultipartContainer;
import net.minecraftforge.fmp.multipart.MultipartHelper;

/**
 * Class that handles the placement of microblocks. Instances should be created in {@link MicroblockClass}.
 *
 * @see IMicroblock
 * @see MicroblockClass
 * @see MicroblockPlacementDefault
 * @see MicroblockPlacementExpand
 */
public abstract class MicroblockPlacement
{
    
    /**
     * Places the microblock at the specified position.
     */
    public abstract boolean place(World world, BlockPos pos, boolean doPlace);

    /**
     * Gets the part that will be placed at the specified position.
     */
    public abstract IMicroblock getPlacedPart(World world, BlockPos pos);

    /**
     * Class that handles normal microblock placement.
     *
     * @see MicroblockPlacement
     */
    public static class MicroblockPlacementDefault extends MicroblockPlacement
    {
        
        private final IMicroblock microblock;

        public MicroblockPlacementDefault(IMicroblock microblock)
        {
            this.microblock = microblock;
        }

        @Override
        public boolean place(World world, BlockPos pos, boolean doPlace)
        {
            if (doPlace)
            {
                return MultipartHelper.addPartIfPossible(world, pos, microblock);
            }
            else
            {
                return MultipartHelper.canAddPart(world, pos, microblock);
            }
        }

        @Override
        public IMicroblock getPlacedPart(World world, BlockPos pos)
        {
            return microblock;
        }

    }

    /**
     * Class that handles the expension of an already existing microblock.
     *
     * @see MicroblockPlacement
     */
    public static class MicroblockPlacementExpand extends MicroblockPlacement
    {
        
        private final IMicroblock microblock, expanded;
        private final int amount;

        public MicroblockPlacementExpand(IMicroblock microblockToExpand, IMicroblock expanded, int amount)
        {
            this.microblock = microblockToExpand;
            this.expanded = expanded;
            this.amount = amount;
        }

        @Override
        public boolean place(World world, BlockPos pos, boolean doPlace)
        {
            IMultipartContainer container = MultipartHelper.getPartContainer(world, pos);
            if (container != null)
            {
                int oldSize = microblock.getSize();
                if (container.canReplacePart(microblock, expanded))
                {
                    if (doPlace)
                    {
                        microblock.setSize(oldSize + amount);
                        microblock.onPartChanged(microblock);
                        microblock.sendUpdatePacket();
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public IMicroblock getPlacedPart(World world, BlockPos pos)
        {
            return expanded;
        }

    }

}