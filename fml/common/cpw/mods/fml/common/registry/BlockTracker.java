package cpw.mods.fml.common.registry;

import java.util.BitSet;

import net.minecraft.block.Block;

class BlockTracker
{
    private static final BlockTracker INSTANCE = new BlockTracker();
    private BitSet allocatedBlocks;

    private BlockTracker()
    {
        allocatedBlocks = new BitSet(4096);
        allocatedBlocks.set(0, 4096);
        for (int i = 0; i < Block.field_71973_m.length; i++)
        {
            if (Block.field_71973_m[i]!=null)
            {
                allocatedBlocks.clear(i);
            }
        }
    }
    public static int nextBlockId()
    {
        return instance().getNextBlockId();
    }

    private int getNextBlockId()
    {
        int idx = allocatedBlocks.nextSetBit(0);
        allocatedBlocks.clear(idx);
        return idx;
    }
    private static BlockTracker instance()
    {
        return INSTANCE;
    }
    public static void reserveBlockId(int id)
    {
        instance().doReserveId(id);
    }
    private void doReserveId(int id)
    {
        allocatedBlocks.clear(id);
    }


}
