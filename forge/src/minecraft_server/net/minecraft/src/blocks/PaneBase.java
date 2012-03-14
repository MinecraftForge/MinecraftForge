package net.minecraft.src.blocks;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Random;

public class PaneBase extends Block
{
    private int sideTextureIndex;

    public PaneBase(int i, int j, int k, Material material)
    {
        super(i, j, material);
        sideTextureIndex = k;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public int getRenderType()
    {
        return mod_InfiBlocks.paneModelID;
    }

    public void getCollidingBoundingBoxes(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, ArrayList arraylist)
    {
        boolean flag = canConnectTo(world.getBlockId(i, j, k - 1));
        boolean flag1 = canConnectTo(world.getBlockId(i, j, k + 1));
        boolean flag2 = canConnectTo(world.getBlockId(i - 1, j, k));
        boolean flag3 = canConnectTo(world.getBlockId(i + 1, j, k));
        if (flag2 && flag3 || !flag2 && !flag3 && !flag && !flag1)
        {
            setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
        }
        else if (flag2 && !flag3)
        {
            setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
            super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
        }
        else if (!flag2 && flag3)
        {
            setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
        }
        if (flag && flag1 || !flag2 && !flag3 && !flag && !flag1)
        {
            setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
            super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
        }
        else if (flag && !flag1)
        {
            setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
            super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
        }
        else if (!flag && flag1)
        {
            setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
            super.getCollidingBoundingBoxes(world, i, j, k, axisalignedbb, arraylist);
        }
    }

    public void setBlockBoundsForItemRender()
    {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k)
    {
        float f = 0.4375F;
        float f1 = 0.5625F;
        float f2 = 0.4375F;
        float f3 = 0.5625F;
        boolean flag = canConnectTo(iblockaccess.getBlockId(i, j, k - 1));
        boolean flag1 = canConnectTo(iblockaccess.getBlockId(i, j, k + 1));
        boolean flag2 = canConnectTo(iblockaccess.getBlockId(i - 1, j, k));
        boolean flag3 = canConnectTo(iblockaccess.getBlockId(i + 1, j, k));
        if (flag2 && flag3 || !flag2 && !flag3 && !flag && !flag1)
        {
            f = 0.0F;
            f1 = 1.0F;
        }
        else if (flag2 && !flag3)
        {
            f = 0.0F;
        }
        else if (!flag2 && flag3)
        {
            f1 = 1.0F;
        }
        if (flag && flag1 || !flag2 && !flag3 && !flag && !flag1)
        {
            f2 = 0.0F;
            f3 = 1.0F;
        }
        else if (flag && !flag1)
        {
            f2 = 0.0F;
        }
        else if (!flag && flag1)
        {
            f3 = 1.0F;
        }
        setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
    }

    public int getSideTextureIndex(int md)
    {
        return sideTextureIndex + md;
    }

    public final boolean canConnectTo(int bID)
    {
        return Block.opaqueCubeLookup[bID] || Block.blocksList[bID] instanceof PaneBase 
        		|| Block.blocksList[bID] instanceof MagicSlabBase || bID == Block.glass.blockID
        		|| bID == mod_InfiBlocks.stainedGlass.blockID;
    }
}
