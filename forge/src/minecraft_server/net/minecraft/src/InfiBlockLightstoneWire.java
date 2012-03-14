package net.minecraft.src;

import java.util.*;

import net.minecraft.src.forge.IConnectRedstone;

public class InfiBlockLightstoneWire extends Block
{
    private boolean wiresProvidePower;
    private Set blocksNeedingUpdate;

    public InfiBlockLightstoneWire(int i, int j)
    {
        super(i, j, Material.circuits);
        wiresProvidePower = true;
        blocksNeedingUpdate = new HashSet();
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        return blockIndexInTexture;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
        return null;
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
        return 5;
    }

    public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k)
    {
        return 0x909000;
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        return world.isBlockSolidOnSide(i, j - 1, k, 1);
    }

    private void updateAndPropagateCurrentStrength(World world, int i, int j, int k)
    {
        calculateCurrentChanges(world, i, j, k, i, j, k);
        ArrayList arraylist = new ArrayList(blocksNeedingUpdate);
        blocksNeedingUpdate.clear();
        for (int l = 0; l < arraylist.size(); l++)
        {
            ChunkPosition chunkposition = (ChunkPosition)arraylist.get(l);
            world.notifyBlocksOfNeighborChange(chunkposition.x, chunkposition.y, chunkposition.z, blockID);
        }
    }

    private void calculateCurrentChanges(World world, int i, int j, int k, int l, int i1, int j1)
    {
        int k1 = world.getBlockMetadata(i, j, k);
        int l1 = 0;
        wiresProvidePower = false;
        boolean flag = world.isBlockIndirectlyGettingPowered(i, j, k);
        wiresProvidePower = true;
        if (flag)
        {
            l1 = 15;
        }
        else
        {
            for (int i2 = 0; i2 < 4; i2++)
            {
                int k2 = i;
                int i3 = k;
                if (i2 == 0)
                {
                    k2--;
                }
                if (i2 == 1)
                {
                    k2++;
                }
                if (i2 == 2)
                {
                    i3--;
                }
                if (i2 == 3)
                {
                    i3++;
                }
                if (k2 != l || j != i1 || i3 != j1)
                {
                    l1 = getMaxCurrentStrength(world, k2, j, i3, l1);
                }
                if (world.isBlockNormalCube(k2, j, i3) && !world.isBlockNormalCube(i, j + 1, k))
                {
                    if (k2 != l || j + 1 != i1 || i3 != j1)
                    {
                        l1 = getMaxCurrentStrength(world, k2, j + 1, i3, l1);
                    }
                    continue;
                }
                if (!world.isBlockNormalCube(k2, j, i3) && (k2 != l || j - 1 != i1 || i3 != j1))
                {
                    l1 = getMaxCurrentStrength(world, k2, j - 1, i3, l1);
                }
            }

            if (l1 > 0)
            {
                l1--;
            }
            else
            {
                l1 = 0;
            }
        }
        if (k1 != l1)
        {
            world.editingBlocks = true;
            world.setBlockMetadataWithNotify(i, j, k, l1);
            world.markBlocksDirty(i, j, k, i, j, k);
            world.editingBlocks = false;
            for (int j2 = 0; j2 < 4; j2++)
            {
                int l2 = i;
                int j3 = k;
                int k3 = j - 1;
                if (j2 == 0)
                {
                    l2--;
                }
                if (j2 == 1)
                {
                    l2++;
                }
                if (j2 == 2)
                {
                    j3--;
                }
                if (j2 == 3)
                {
                    j3++;
                }
                if (world.isBlockNormalCube(l2, j, j3))
                {
                    k3 += 2;
                }
                int l3 = 0;
                l3 = getMaxCurrentStrength(world, l2, j, j3, -1);
                l1 = world.getBlockMetadata(i, j, k);
                if (l1 > 0)
                {
                    l1--;
                }
                if (l3 >= 0 && l3 != l1)
                {
                    calculateCurrentChanges(world, l2, j, j3, i, j, k);
                }
                l3 = getMaxCurrentStrength(world, l2, k3, j3, -1);
                l1 = world.getBlockMetadata(i, j, k);
                if (l1 > 0)
                {
                    l1--;
                }
                if (l3 >= 0 && l3 != l1)
                {
                    calculateCurrentChanges(world, l2, k3, j3, i, j, k);
                }
            }

            if (k1 < l1 || l1 == 0)
            {
                blocksNeedingUpdate.add(new ChunkPosition(i, j, k));
                blocksNeedingUpdate.add(new ChunkPosition(i - 1, j, k));
                blocksNeedingUpdate.add(new ChunkPosition(i + 1, j, k));
                blocksNeedingUpdate.add(new ChunkPosition(i, j - 1, k));
                blocksNeedingUpdate.add(new ChunkPosition(i, j + 1, k));
                blocksNeedingUpdate.add(new ChunkPosition(i, j, k - 1));
                blocksNeedingUpdate.add(new ChunkPosition(i, j, k + 1));
            }
        }
    }

    private void notifyWireNeighborsOfNeighborChange(World world, int i, int j, int k)
    {
        if (world.getBlockId(i, j, k) != blockID)
        {
            return;
        }
        else
        {
            world.notifyBlocksOfNeighborChange(i, j, k, blockID);
            world.notifyBlocksOfNeighborChange(i - 1, j, k, blockID);
            world.notifyBlocksOfNeighborChange(i + 1, j, k, blockID);
            world.notifyBlocksOfNeighborChange(i, j, k - 1, blockID);
            world.notifyBlocksOfNeighborChange(i, j, k + 1, blockID);
            world.notifyBlocksOfNeighborChange(i, j - 1, k, blockID);
            world.notifyBlocksOfNeighborChange(i, j + 1, k, blockID);
            return;
        }
    }

    public void onBlockAdded(World world, int i, int j, int k)
    {
        super.onBlockAdded(world, i, j, k);
        if (world.isRemote)
        {
            return;
        }
        updateAndPropagateCurrentStrength(world, i, j, k);
        world.notifyBlocksOfNeighborChange(i, j + 1, k, blockID);
        world.notifyBlocksOfNeighborChange(i, j - 1, k, blockID);
        notifyWireNeighborsOfNeighborChange(world, i - 1, j, k);
        notifyWireNeighborsOfNeighborChange(world, i + 1, j, k);
        notifyWireNeighborsOfNeighborChange(world, i, j, k - 1);
        notifyWireNeighborsOfNeighborChange(world, i, j, k + 1);
        if (world.isBlockNormalCube(i - 1, j, k))
        {
            notifyWireNeighborsOfNeighborChange(world, i - 1, j + 1, k);
        }
        else
        {
            notifyWireNeighborsOfNeighborChange(world, i - 1, j - 1, k);
        }
        if (world.isBlockNormalCube(i + 1, j, k))
        {
            notifyWireNeighborsOfNeighborChange(world, i + 1, j + 1, k);
        }
        else
        {
            notifyWireNeighborsOfNeighborChange(world, i + 1, j - 1, k);
        }
        if (world.isBlockNormalCube(i, j, k - 1))
        {
            notifyWireNeighborsOfNeighborChange(world, i, j + 1, k - 1);
        }
        else
        {
            notifyWireNeighborsOfNeighborChange(world, i, j - 1, k - 1);
        }
        if (world.isBlockNormalCube(i, j, k + 1))
        {
            notifyWireNeighborsOfNeighborChange(world, i, j + 1, k + 1);
        }
        else
        {
            notifyWireNeighborsOfNeighborChange(world, i, j - 1, k + 1);
        }
    }

    public void onBlockRemoval(World world, int i, int j, int k)
    {
        super.onBlockRemoval(world, i, j, k);
        if (world.isRemote)
        {
            return;
        }
        world.notifyBlocksOfNeighborChange(i, j + 1, k, blockID);
        world.notifyBlocksOfNeighborChange(i, j - 1, k, blockID);
        world.notifyBlocksOfNeighborChange(i + 1, j, k, blockID);
        world.notifyBlocksOfNeighborChange(i - 1, j, k, blockID);
        world.notifyBlocksOfNeighborChange(i, j, k + 1, blockID);
        world.notifyBlocksOfNeighborChange(i, j, k - 1, blockID);
        updateAndPropagateCurrentStrength(world, i, j, k);
        notifyWireNeighborsOfNeighborChange(world, i - 1, j, k);
        notifyWireNeighborsOfNeighborChange(world, i + 1, j, k);
        notifyWireNeighborsOfNeighborChange(world, i, j, k - 1);
        notifyWireNeighborsOfNeighborChange(world, i, j, k + 1);
        if (world.isBlockNormalCube(i - 1, j, k))
        {
            notifyWireNeighborsOfNeighborChange(world, i - 1, j + 1, k);
        }
        else
        {
            notifyWireNeighborsOfNeighborChange(world, i - 1, j - 1, k);
        }
        if (world.isBlockNormalCube(i + 1, j, k))
        {
            notifyWireNeighborsOfNeighborChange(world, i + 1, j + 1, k);
        }
        else
        {
            notifyWireNeighborsOfNeighborChange(world, i + 1, j - 1, k);
        }
        if (world.isBlockNormalCube(i, j, k - 1))
        {
            notifyWireNeighborsOfNeighborChange(world, i, j + 1, k - 1);
        }
        else
        {
            notifyWireNeighborsOfNeighborChange(world, i, j - 1, k - 1);
        }
        if (world.isBlockNormalCube(i, j, k + 1))
        {
            notifyWireNeighborsOfNeighborChange(world, i, j + 1, k + 1);
        }
        else
        {
            notifyWireNeighborsOfNeighborChange(world, i, j - 1, k + 1);
        }
    }

    private int getMaxCurrentStrength(World world, int i, int j, int k, int l)
    {
        if (world.getBlockId(i, j, k) != blockID)
        {
            return l;
        }
        int i1 = world.getBlockMetadata(i, j, k);
        if (i1 > l)
        {
            return i1;
        }
        else
        {
            return l;
        }
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int l)
    {
        if (world.isRemote)
        {
            return;
        }
        int i1 = world.getBlockMetadata(i, j, k);
        boolean flag = canPlaceBlockAt(world, i, j, k);
        if (!flag)
        {
            dropBlockAsItem(world, i, j, k, i1, 0);
            world.setBlockWithNotify(i, j, k, 0);
        }
        else
        {
            updateAndPropagateCurrentStrength(world, i, j, k);
        }
        super.onNeighborBlockChange(world, i, j, k, l);
    }

    public int idDropped(int i, Random random, int j)
    {
        return Item.redstone.shiftedIndex;
    }

    public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l)
    {
        if (!wiresProvidePower)
        {
            return false;
        }
        else
        {
            return isPoweringTo(world, i, j, k, l);
        }
    }

    public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        if (!wiresProvidePower)
        {
            return false;
        }
        if (iblockaccess.getBlockMetadata(i, j, k) == 0)
        {
            return false;
        }
        if (l == 1)
        {
            return true;
        }
        boolean flag = isPoweredOrRepeater(iblockaccess, i - 1, j, k, 1) || !iblockaccess.isBlockNormalCube(i - 1, j, k) && isPoweredOrRepeater(iblockaccess, i - 1, j - 1, k, -1);
        boolean flag1 = isPoweredOrRepeater(iblockaccess, i + 1, j, k, 3) || !iblockaccess.isBlockNormalCube(i + 1, j, k) && isPoweredOrRepeater(iblockaccess, i + 1, j - 1, k, -1);
        boolean flag2 = isPoweredOrRepeater(iblockaccess, i, j, k - 1, 2) || !iblockaccess.isBlockNormalCube(i, j, k - 1) && isPoweredOrRepeater(iblockaccess, i, j - 1, k - 1, -1);
        boolean flag3 = isPoweredOrRepeater(iblockaccess, i, j, k + 1, 0) || !iblockaccess.isBlockNormalCube(i, j, k + 1) && isPoweredOrRepeater(iblockaccess, i, j - 1, k + 1, -1);
        if (!iblockaccess.isBlockNormalCube(i, j + 1, k))
        {
            if (iblockaccess.isBlockNormalCube(i - 1, j, k) && isPoweredOrRepeater(iblockaccess, i - 1, j + 1, k, -1))
            {
                flag = true;
            }
            if (iblockaccess.isBlockNormalCube(i + 1, j, k) && isPoweredOrRepeater(iblockaccess, i + 1, j + 1, k, -1))
            {
                flag1 = true;
            }
            if (iblockaccess.isBlockNormalCube(i, j, k - 1) && isPoweredOrRepeater(iblockaccess, i, j + 1, k - 1, -1))
            {
                flag2 = true;
            }
            if (iblockaccess.isBlockNormalCube(i, j, k + 1) && isPoweredOrRepeater(iblockaccess, i, j + 1, k + 1, -1))
            {
                flag3 = true;
            }
        }
        if (!flag2 && !flag1 && !flag && !flag3 && l >= 2 && l <= 5)
        {
            return true;
        }
        if (l == 2 && flag2 && !flag && !flag1)
        {
            return true;
        }
        if (l == 3 && flag3 && !flag && !flag1)
        {
            return true;
        }
        if (l == 4 && flag && !flag2 && !flag3)
        {
            return true;
        }
        return l == 5 && flag1 && !flag2 && !flag3;
    }

    public boolean canProvidePower()
    {
        return wiresProvidePower;
    }

    public void randomDisplayTick(World world, int i, int j, int k, Random random)
    {
        int l = world.getBlockMetadata(i, j, k);
        if (l > 0)
        {
            double d = (double)i + 0.5D + ((double)random.nextFloat() - 0.5D) * 0.20000000000000001D;
            double d1 = (float)j + 0.0625F;
            double d2 = (double)k + 0.5D + ((double)random.nextFloat() - 0.5D) * 0.20000000000000001D;
            float f = (float)l / 15F;
            float f1 = f * 0.6F + 0.4F;
            if (l == 0)
            {
                f1 = 0.0F;
            }
            float f2 = f * f * 0.7F - 0.5F;
            float f3 = f * f * 0.6F - 0.7F;
            if (f2 < 0.0F)
            {
                f2 = 0.0F;
            }
            if (f3 < 0.0F)
            {
                f3 = 0.0F;
            }
            world.spawnParticle("reddust", d, d1, d2, f1, f2, f3);
        }
    }

    public static boolean isPowerProviderOrWire(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        int i1 = iblockaccess.getBlockId(i, j, k);
        if (i1 == Block.redstoneWire.blockID)
        {
            return true;
        }
        if (i1 == 0)
        {
            return false;
        }
        if (i1 == Block.redstoneRepeaterIdle.blockID || i1 == Block.redstoneRepeaterActive.blockID)
        {
            int j1 = iblockaccess.getBlockMetadata(i, j, k);
            return l == (j1 & 3) || l == Direction.footInvisibleFaceRemap[j1 & 3];
        }
        
        if (Block.blocksList[i1] instanceof IConnectRedstone)
        {
        	return ((IConnectRedstone)Block.blocksList[i1]).canConnectRedstone(iblockaccess, i, j, k, l);
        }
        
        return Block.blocksList[i1].canProvidePower() && l != -1;
    }

    public static boolean isPoweredOrRepeater(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        if (isPowerProviderOrWire(iblockaccess, i, j, k, l))
        {
            return true;
        }
        int i1 = iblockaccess.getBlockId(i, j, k);
        if (i1 == Block.redstoneRepeaterActive.blockID)
        {
            int j1 = iblockaccess.getBlockMetadata(i, j, k);
            return l == (j1 & 3);
        }
        else
        {
            return false;
        }
    }
}
