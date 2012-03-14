package net.minecraft.src.blocks;

import net.minecraft.src.*;

public class CarpetBlock extends Block
{
    public CarpetBlock(int i)
    {
        super(i, 64, Material.cloth);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if (j == 0)
        {
            return blockIndexInTexture;
        }
        else
        {
            j = ~(j & 0xf);
            return 113 + ((j & 8) >> 3) + (j & 7) * 16;
        }
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        int l = world.getBlockId(i, j - 1, k);
        if (l == 0 || !Block.blocksList[l].isOpaqueCube())
        {
            return false;
        }
        else
        {
            return world.getBlockMaterial(i, j - 1, k).blocksMovement();
        }
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int l)
    {
        func_314_h(world, i, j, k);
    }

    private boolean func_314_h(World world, int i, int j, int k)
    {
        if (!canPlaceBlockAt(world, i, j, k))
        {
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
            world.setBlockWithNotify(i, j, k, 0);
            return false;
        }
        else
        {
            return true;
        }
    }

    protected int damageDropped(int i)
    {
        return i;
    }

    public static int getBlockFromDye(int i)
    {
        return ~i & 0xf;
    }

    public static int getDyeFromBlock(int i)
    {
        return ~i & 0xf;
    }
}
