package net.minecraft.src.core;
import net.minecraft.src.forge.*;
import java.util.Random;
import net.minecraft.src.*;

public class InfiBlockMoss extends Block
	implements ITextureProvider
{
    public InfiBlockMoss(int i, int j)
    {
        super(i, j, Material.vine);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        this.setTickRandomly(true);
        blockIndexInTexture = 255;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
        int l = world.getBlockMetadata(i, j, k) & 7;
        if (l >= 3)
        {
            return AxisAlignedBB.getBoundingBoxFromPool((double)i + minX, (double)j + minY, (double)k + minZ, (double)i + maxX, (float)j + 0.5F, (double)k + maxZ);
        }
        else
        {
            return null;
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

    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k)
    {
        int l = iblockaccess.getBlockMetadata(i, j, k) & 7;
        float f = (float)(2 * (1 + l)) / 16F;
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f / 2.0F, 1.0F);
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
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 1);
            world.setBlockWithNotify(i, j, k, 0);
            return false;
        }
        else
        {
            return true;
        }
    }

    public int idDropped(int i, Random random, int j)
    {
        return mod_InfiTools.mossBall.shiftedIndex;
    }

    public int quantityDropped(Random random)
    {
        return 1;
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (world.isRemote)
        {
            return;
        }
        if (world.getBlockId(i, j - 1, k) == Block.grass.blockID)
        {
            world.setBlockWithNotify(i, j, k, 0);
        }
        if (random.nextInt(12) == 0 && world.getBlockLightValue(i, j, k) <= 12)
        {
            int l = (i + random.nextInt(3)) - 1;
            int i1 = (j + random.nextInt(3)) - 1;
            int j1 = (k + random.nextInt(3)) - 1;
            int k1 = world.getBlockId(l, i1, j1);
            int l1 = world.getBlockId(l, i1 - 1, j1);
            int i2 = (l - i) + (j1 - k);
            if ((i2 == 1 || i2 == -1) && k1 == 0 && l1 != 0 && l1 != mod_InfiTools.blockMoss.blockID && Block.blocksList[l1].renderAsNormalBlock())
            {
                world.setBlockWithNotify(l, i1, j1, mod_InfiTools.blockMoss.blockID);
            }
        }
    }

    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        if (l == 1)
        {
            return true;
        }
        else
        {
            return super.shouldSideBeRendered(iblockaccess, i, j, k, l);
        }
    }

	@Override
	public String getTextureFile() {
		return "/infitools/infitems.png";
	}
}
