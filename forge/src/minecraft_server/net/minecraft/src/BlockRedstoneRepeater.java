package net.minecraft.src;

import java.util.Random;

public class BlockRedstoneRepeater extends BlockDirectional
{
    public static final double[] repeaterTorchOffset = new double[] { -0.0625D, 0.0625D, 0.1875D, 0.3125D};
    private static final int[] repeaterState = new int[] {1, 2, 3, 4};
    private final boolean isRepeaterPowered;

    protected BlockRedstoneRepeater(int par1, boolean par2)
    {
        super(par1, 6, Material.circuits);
        this.isRepeaterPowered = par2;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    /**
     * If this block doesn't render as an ordinary block it will return false (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return !par1World.isBlockSolidOnSide(par2, par3 - 1, par4, 1) ? false : super.canPlaceBlockAt(par1World, par2, par3, par4);
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(World par1World, int par2, int par3, int par4)
    {
        return !par1World.isBlockSolidOnSide(par2, par3 - 1, par4, 1) ? false : super.canBlockStay(par1World, par2, par3, par4);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        int var6 = par1World.getBlockMetadata(par2, par3, par4);
        boolean var7 = this.ignoreTick(par1World, par2, par3, par4, var6);

        if (this.isRepeaterPowered && !var7)
        {
            par1World.setBlockAndMetadataWithNotify(par2, par3, par4, Block.redstoneRepeaterIdle.blockID, var6);
        }
        else if (!this.isRepeaterPowered)
        {
            par1World.setBlockAndMetadataWithNotify(par2, par3, par4, Block.redstoneRepeaterActive.blockID, var6);

            if (!var7)
            {
                int var8 = (var6 & 12) >> 2;
                par1World.scheduleBlockUpdate(par2, par3, par4, Block.redstoneRepeaterActive.blockID, repeaterState[var8] * 2);
            }
        }
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return par1 == 0 ? (this.isRepeaterPowered ? 99 : 115) : (par1 == 1 ? (this.isRepeaterPowered ? 147 : 131) : 5);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 15;
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int getBlockTextureFromSide(int par1)
    {
        return this.getBlockTextureFromSideAndMetadata(par1, 0);
    }

    /**
     * Is this block indirectly powering the block on the specified side
     */
    public boolean isIndirectlyPoweringTo(World par1World, int par2, int par3, int par4, int par5)
    {
        return this.isPoweringTo(par1World, par2, par3, par4, par5);
    }

    /**
     * Is this block powering the block on the specified side
     */
    public boolean isPoweringTo(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        if (!this.isRepeaterPowered)
        {
            return false;
        }
        else
        {
            int var6 = func_48132_b(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
            return var6 == 0 && par5 == 3 ? true : (var6 == 1 && par5 == 4 ? true : (var6 == 2 && par5 == 2 ? true : var6 == 3 && par5 == 5));
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!this.canBlockStay(par1World, par2, par3, par4))
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockWithNotify(par2, par3, par4, 0);
        }
        else
        {
            int var6 = par1World.getBlockMetadata(par2, par3, par4);
            boolean var7 = this.ignoreTick(par1World, par2, par3, par4, var6);
            int var8 = (var6 & 12) >> 2;

            if (this.isRepeaterPowered && !var7)
            {
                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, repeaterState[var8] * 2);
            }
            else if (!this.isRepeaterPowered && var7)
            {
                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, repeaterState[var8] * 2);
            }
        }
    }

    private boolean ignoreTick(World par1World, int par2, int par3, int par4, int par5)
    {
        int var6 = func_48132_b(par5);

        switch (var6)
        {
            case 0:
                return par1World.isBlockIndirectlyProvidingPowerTo(par2, par3, par4 + 1, 3) || par1World.getBlockId(par2, par3, par4 + 1) == Block.redstoneWire.blockID && par1World.getBlockMetadata(par2, par3, par4 + 1) > 0;

            case 1:
                return par1World.isBlockIndirectlyProvidingPowerTo(par2 - 1, par3, par4, 4) || par1World.getBlockId(par2 - 1, par3, par4) == Block.redstoneWire.blockID && par1World.getBlockMetadata(par2 - 1, par3, par4) > 0;

            case 2:
                return par1World.isBlockIndirectlyProvidingPowerTo(par2, par3, par4 - 1, 2) || par1World.getBlockId(par2, par3, par4 - 1) == Block.redstoneWire.blockID && par1World.getBlockMetadata(par2, par3, par4 - 1) > 0;

            case 3:
                return par1World.isBlockIndirectlyProvidingPowerTo(par2 + 1, par3, par4, 5) || par1World.getBlockId(par2 + 1, par3, par4) == Block.redstoneWire.blockID && par1World.getBlockMetadata(par2 + 1, par3, par4) > 0;

            default:
                return false;
        }
    }

    /**
     * Called upon block activation (left or right click on the block.). The three integers represent x,y,z of the
     * block.
     */
    public boolean blockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        int var6 = par1World.getBlockMetadata(par2, par3, par4);
        int var7 = (var6 & 12) >> 2;
        var7 = var7 + 1 << 2 & 12;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, var7 | var6 & 3);
        return true;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Called when a block is using an item and passed in who placed it. Args: x, y, z, entityLiving
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving)
    {
        int var6 = ((MathHelper.floor_double((double)(par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, var6);
        boolean var7 = this.ignoreTick(par1World, par2, par3, par4, var6);

        if (var7)
        {
            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, 1);
        }
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
        par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
        par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
        par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
        par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
        par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
    }

    /**
     * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
     */
    public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5)
    {
        if (this.isRepeaterPowered)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
        }

        super.onBlockDestroyedByPlayer(par1World, par2, par3, par4, par5);
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
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Item.redstoneRepeater.shiftedIndex;
    }
}
