package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.src.forge.IConnectRedstone;

public class BlockRedstoneWire extends Block
{
    /**
     * When false, power transmission methods do not look at other redstone wires.  Used internally during
     * updateCurrentStrength.
     */
    private boolean wiresProvidePower = true;
    private Set blocksNeedingUpdate = new HashSet();

    public BlockRedstoneWire(int par1, int par2)
    {
        super(par1, par2, Material.circuits);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return this.blockIndexInTexture;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 5;
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return 8388608;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return par1World.isBlockSolidOnSide(par2, par3 - 1, par4, 1) || par1World.getBlockId(par2, par3 - 1, par4) == Block.glowStone.blockID;
    }

    /**
     * Sets the strength of the wire current (0-15) for this block based on neighboring blocks and propagates to
     * neighboring redstone wires
     */
    private void updateAndPropagateCurrentStrength(World par1World, int par2, int par3, int par4)
    {
        this.calculateCurrentChanges(par1World, par2, par3, par4, par2, par3, par4);
        ArrayList var5 = new ArrayList(this.blocksNeedingUpdate);
        this.blocksNeedingUpdate.clear();

        for (int var6 = 0; var6 < var5.size(); ++var6)
        {
            ChunkPosition var7 = (ChunkPosition)var5.get(var6);
            par1World.notifyBlocksOfNeighborChange(var7.x, var7.y, var7.z, this.blockID);
        }
    }

    private void calculateCurrentChanges(World par1World, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        int var8 = par1World.getBlockMetadata(par2, par3, par4);
        int var9 = 0;
        this.wiresProvidePower = false;
        boolean var10 = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4);
        this.wiresProvidePower = true;
        int var11;
        int var12;
        int var13;

        if (var10)
        {
            var9 = 15;
        }
        else
        {
            for (var11 = 0; var11 < 4; ++var11)
            {
                var12 = par2;
                var13 = par4;

                if (var11 == 0)
                {
                    var12 = par2 - 1;
                }

                if (var11 == 1)
                {
                    ++var12;
                }

                if (var11 == 2)
                {
                    var13 = par4 - 1;
                }

                if (var11 == 3)
                {
                    ++var13;
                }

                if (var12 != par5 || par3 != par6 || var13 != par7)
                {
                    var9 = this.getMaxCurrentStrength(par1World, var12, par3, var13, var9);
                }

                if (par1World.isBlockNormalCube(var12, par3, var13) && !par1World.isBlockNormalCube(par2, par3 + 1, par4))
                {
                    if (var12 != par5 || par3 + 1 != par6 || var13 != par7)
                    {
                        var9 = this.getMaxCurrentStrength(par1World, var12, par3 + 1, var13, var9);
                    }
                }
                else if (!par1World.isBlockNormalCube(var12, par3, var13) && (var12 != par5 || par3 - 1 != par6 || var13 != par7))
                {
                    var9 = this.getMaxCurrentStrength(par1World, var12, par3 - 1, var13, var9);
                }
            }

            if (var9 > 0)
            {
                --var9;
            }
            else
            {
                var9 = 0;
            }
        }

        if (var8 != var9)
        {
            par1World.editingBlocks = true;
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var9);
            par1World.markBlocksDirty(par2, par3, par4, par2, par3, par4);
            par1World.editingBlocks = false;

            for (var11 = 0; var11 < 4; ++var11)
            {
                var12 = par2;
                var13 = par4;
                int var14 = par3 - 1;

                if (var11 == 0)
                {
                    var12 = par2 - 1;
                }

                if (var11 == 1)
                {
                    ++var12;
                }

                if (var11 == 2)
                {
                    var13 = par4 - 1;
                }

                if (var11 == 3)
                {
                    ++var13;
                }

                if (par1World.isBlockNormalCube(var12, par3, var13))
                {
                    var14 += 2;
                }

                boolean var15 = false;
                int var16 = this.getMaxCurrentStrength(par1World, var12, par3, var13, -1);
                var9 = par1World.getBlockMetadata(par2, par3, par4);

                if (var9 > 0)
                {
                    --var9;
                }

                if (var16 >= 0 && var16 != var9)
                {
                    this.calculateCurrentChanges(par1World, var12, par3, var13, par2, par3, par4);
                }

                var16 = this.getMaxCurrentStrength(par1World, var12, var14, var13, -1);
                var9 = par1World.getBlockMetadata(par2, par3, par4);

                if (var9 > 0)
                {
                    --var9;
                }

                if (var16 >= 0 && var16 != var9)
                {
                    this.calculateCurrentChanges(par1World, var12, var14, var13, par2, par3, par4);
                }
            }

            if (var8 < var9 || var9 == 0)
            {
                this.blocksNeedingUpdate.add(new ChunkPosition(par2, par3, par4));
                this.blocksNeedingUpdate.add(new ChunkPosition(par2 - 1, par3, par4));
                this.blocksNeedingUpdate.add(new ChunkPosition(par2 + 1, par3, par4));
                this.blocksNeedingUpdate.add(new ChunkPosition(par2, par3 - 1, par4));
                this.blocksNeedingUpdate.add(new ChunkPosition(par2, par3 + 1, par4));
                this.blocksNeedingUpdate.add(new ChunkPosition(par2, par3, par4 - 1));
                this.blocksNeedingUpdate.add(new ChunkPosition(par2, par3, par4 + 1));
            }
        }
    }

    /**
     * Calls World.notifyBlocksOfNeighborChange() for all neighboring blocks, but only if the given block is a redstone
     * wire.
     */
    private void notifyWireNeighborsOfNeighborChange(World par1World, int par2, int par3, int par4)
    {
        if (par1World.getBlockId(par2, par3, par4) == this.blockID)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
        }
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);

        if (!par1World.isRemote)
        {
            this.updateAndPropagateCurrentStrength(par1World, par2, par3, par4);
            par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2 - 1, par3, par4);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2 + 1, par3, par4);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3, par4 - 1);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3, par4 + 1);

            if (par1World.isBlockNormalCube(par2 - 1, par3, par4))
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 - 1, par3 + 1, par4);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 - 1, par3 - 1, par4);
            }

            if (par1World.isBlockNormalCube(par2 + 1, par3, par4))
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 + 1, par3 + 1, par4);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 + 1, par3 - 1, par4);
            }

            if (par1World.isBlockNormalCube(par2, par3, par4 - 1))
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 + 1, par4 - 1);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 - 1, par4 - 1);
            }

            if (par1World.isBlockNormalCube(par2, par3, par4 + 1))
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 + 1, par4 + 1);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 - 1, par4 + 1);
            }
        }
    }

    /**
     * Called whenever the block is removed.
     */
    public void onBlockRemoval(World par1World, int par2, int par3, int par4)
    {
        super.onBlockRemoval(par1World, par2, par3, par4);

        if (!par1World.isRemote)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
            this.updateAndPropagateCurrentStrength(par1World, par2, par3, par4);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2 - 1, par3, par4);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2 + 1, par3, par4);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3, par4 - 1);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3, par4 + 1);

            if (par1World.isBlockNormalCube(par2 - 1, par3, par4))
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 - 1, par3 + 1, par4);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 - 1, par3 - 1, par4);
            }

            if (par1World.isBlockNormalCube(par2 + 1, par3, par4))
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 + 1, par3 + 1, par4);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 + 1, par3 - 1, par4);
            }

            if (par1World.isBlockNormalCube(par2, par3, par4 - 1))
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 + 1, par4 - 1);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 - 1, par4 - 1);
            }

            if (par1World.isBlockNormalCube(par2, par3, par4 + 1))
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 + 1, par4 + 1);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 - 1, par4 + 1);
            }
        }
    }

    /**
     * Returns the current strength at the specified block if it is greater than the passed value, or the passed value
     * otherwise.  Signature: (world, x, y, z, strength)
     */
    private int getMaxCurrentStrength(World par1World, int par2, int par3, int par4, int par5)
    {
        if (par1World.getBlockId(par2, par3, par4) != this.blockID)
        {
            return par5;
        }
        else
        {
            int var6 = par1World.getBlockMetadata(par2, par3, par4);
            return var6 > par5 ? var6 : par5;
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!par1World.isRemote)
        {
            int var6 = par1World.getBlockMetadata(par2, par3, par4);
            boolean var7 = this.canPlaceBlockAt(par1World, par2, par3, par4);

            if (!var7)
            {
                this.dropBlockAsItem(par1World, par2, par3, par4, var6, 0);
                par1World.setBlockWithNotify(par2, par3, par4, 0);
            }
            else
            {
                this.updateAndPropagateCurrentStrength(par1World, par2, par3, par4);
            }

            super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Item.redstone.shiftedIndex;
    }

    /**
     * Is this block indirectly powering the block on the specified side
     */
    public boolean isIndirectlyPoweringTo(World par1World, int par2, int par3, int par4, int par5)
    {
        return !this.wiresProvidePower ? false : this.isPoweringTo(par1World, par2, par3, par4, par5);
    }

    /**
     * Is this block powering the block on the specified side
     */
    public boolean isPoweringTo(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        if (!this.wiresProvidePower)
        {
            return false;
        }
        else if (par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 0)
        {
            return false;
        }
        else if (par5 == 1)
        {
            return true;
        }
        else
        {
            boolean var6 = isPoweredOrRepeater(par1IBlockAccess, par2 - 1, par3, par4, 1) || !par1IBlockAccess.isBlockNormalCube(par2 - 1, par3, par4) && isPoweredOrRepeater(par1IBlockAccess, par2 - 1, par3 - 1, par4, -1);
            boolean var7 = isPoweredOrRepeater(par1IBlockAccess, par2 + 1, par3, par4, 3) || !par1IBlockAccess.isBlockNormalCube(par2 + 1, par3, par4) && isPoweredOrRepeater(par1IBlockAccess, par2 + 1, par3 - 1, par4, -1);
            boolean var8 = isPoweredOrRepeater(par1IBlockAccess, par2, par3, par4 - 1, 2) || !par1IBlockAccess.isBlockNormalCube(par2, par3, par4 - 1) && isPoweredOrRepeater(par1IBlockAccess, par2, par3 - 1, par4 - 1, -1);
            boolean var9 = isPoweredOrRepeater(par1IBlockAccess, par2, par3, par4 + 1, 0) || !par1IBlockAccess.isBlockNormalCube(par2, par3, par4 + 1) && isPoweredOrRepeater(par1IBlockAccess, par2, par3 - 1, par4 + 1, -1);

            if (!par1IBlockAccess.isBlockNormalCube(par2, par3 + 1, par4))
            {
                if (par1IBlockAccess.isBlockNormalCube(par2 - 1, par3, par4) && isPoweredOrRepeater(par1IBlockAccess, par2 - 1, par3 + 1, par4, -1))
                {
                    var6 = true;
                }

                if (par1IBlockAccess.isBlockNormalCube(par2 + 1, par3, par4) && isPoweredOrRepeater(par1IBlockAccess, par2 + 1, par3 + 1, par4, -1))
                {
                    var7 = true;
                }

                if (par1IBlockAccess.isBlockNormalCube(par2, par3, par4 - 1) && isPoweredOrRepeater(par1IBlockAccess, par2, par3 + 1, par4 - 1, -1))
                {
                    var8 = true;
                }

                if (par1IBlockAccess.isBlockNormalCube(par2, par3, par4 + 1) && isPoweredOrRepeater(par1IBlockAccess, par2, par3 + 1, par4 + 1, -1))
                {
                    var9 = true;
                }
            }

            return !var8 && !var7 && !var6 && !var9 && par5 >= 2 && par5 <= 5 ? true : (par5 == 2 && var8 && !var6 && !var7 ? true : (par5 == 3 && var9 && !var6 && !var7 ? true : (par5 == 4 && var6 && !var8 && !var9 ? true : par5 == 5 && var7 && !var8 && !var9)));
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return this.wiresProvidePower;
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        int var6 = par1World.getBlockMetadata(par2, par3, par4);

        if (var6 > 0)
        {
            double var7 = (double)par2 + 0.5D + ((double)par5Random.nextFloat() - 0.5D) * 0.2D;
            double var9 = (double)((float)par3 + 0.0625F);
            double var11 = (double)par4 + 0.5D + ((double)par5Random.nextFloat() - 0.5D) * 0.2D;
            float var13 = (float)var6 / 15.0F;
            float var14 = var13 * 0.6F + 0.4F;

            if (var6 == 0)
            {
                var14 = 0.0F;
            }

            float var15 = var13 * var13 * 0.7F - 0.5F;
            float var16 = var13 * var13 * 0.6F - 0.7F;

            if (var15 < 0.0F)
            {
                var15 = 0.0F;
            }

            if (var16 < 0.0F)
            {
                var16 = 0.0F;
            }

            par1World.spawnParticle("reddust", var7, var9, var11, (double)var14, (double)var15, (double)var16);
        }
    }

    /**
     * Calls canProvidePower() on the specified block and returns the result, but bypasses the wiresProvidePower flag
     */
    public static boolean isPowerProviderOrWire(IBlockAccess par0IBlockAccess, int par1, int par2, int par3, int par4)
    {
        int var5 = par0IBlockAccess.getBlockId(par1, par2, par3);

        if (var5 == Block.redstoneWire.blockID)
        {
            return true;
        }
        else if (var5 == 0)
        {
            return false;
        }
        else if (var5 != Block.redstoneRepeaterIdle.blockID && var5 != Block.redstoneRepeaterActive.blockID)
        {
            if (Block.blocksList[var5] instanceof IConnectRedstone)
            {
                return ((IConnectRedstone)Block.blocksList[var5]).canConnectRedstone(par0IBlockAccess, par1, par2, par3, par4);
            }
            return Block.blocksList[var5].canProvidePower() && par4 != -1;
        }
        else
        {
            int var6 = par0IBlockAccess.getBlockMetadata(par1, par2, par3);
            return par4 == (var6 & 3) || par4 == Direction.footInvisibleFaceRemap[var6 & 3];
        }
    }

    /**
     * Returns true if the block coordinate passed can provide power, or is a redstone wire, or if its a repeater that
     * is powered.
     */
    public static boolean isPoweredOrRepeater(IBlockAccess par0IBlockAccess, int par1, int par2, int par3, int par4)
    {
        if (isPowerProviderOrWire(par0IBlockAccess, par1, par2, par3, par4))
        {
            return true;
        }
        else
        {
            int var5 = par0IBlockAccess.getBlockId(par1, par2, par3);

            if (var5 == Block.redstoneRepeaterActive.blockID)
            {
                int var6 = par0IBlockAccess.getBlockMetadata(par1, par2, par3);
                return par4 == (var6 & 3);
            }
            else
            {
                return false;
            }
        }
    }
}
