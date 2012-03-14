package net.minecraft.src;

import java.util.Random;

public class BlockChest extends BlockContainer
{
    private Random random = new Random();

    protected BlockChest(int par1)
    {
        super(par1, Material.wood);
        this.blockIndexInTexture = 26;
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
        return 22;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);
        this.unifyAdjacentChests(par1World, par2, par3, par4);
        int var5 = par1World.getBlockId(par2, par3, par4 - 1);
        int var6 = par1World.getBlockId(par2, par3, par4 + 1);
        int var7 = par1World.getBlockId(par2 - 1, par3, par4);
        int var8 = par1World.getBlockId(par2 + 1, par3, par4);

        if (var5 == this.blockID)
        {
            this.unifyAdjacentChests(par1World, par2, par3, par4 - 1);
        }

        if (var6 == this.blockID)
        {
            this.unifyAdjacentChests(par1World, par2, par3, par4 + 1);
        }

        if (var7 == this.blockID)
        {
            this.unifyAdjacentChests(par1World, par2 - 1, par3, par4);
        }

        if (var8 == this.blockID)
        {
            this.unifyAdjacentChests(par1World, par2 + 1, par3, par4);
        }
    }

    /**
     * Called when a block is placed by using an ItemStack from inventory and passed in who placed it. Args:
     * x,y,z,entityliving
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving)
    {
        int var6 = par1World.getBlockId(par2, par3, par4 - 1);
        int var7 = par1World.getBlockId(par2, par3, par4 + 1);
        int var8 = par1World.getBlockId(par2 - 1, par3, par4);
        int var9 = par1World.getBlockId(par2 + 1, par3, par4);
        byte var10 = 0;
        int var11 = MathHelper.floor_double((double)(par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (var11 == 0)
        {
            var10 = 2;
        }

        if (var11 == 1)
        {
            var10 = 5;
        }

        if (var11 == 2)
        {
            var10 = 3;
        }

        if (var11 == 3)
        {
            var10 = 4;
        }

        if (var6 != this.blockID && var7 != this.blockID && var8 != this.blockID && var9 != this.blockID)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var10);
        }
        else
        {
            if ((var6 == this.blockID || var7 == this.blockID) && (var10 == 4 || var10 == 5))
            {
                if (var6 == this.blockID)
                {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4 - 1, var10);
                }
                else
                {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4 + 1, var10);
                }

                par1World.setBlockMetadataWithNotify(par2, par3, par4, var10);
            }

            if ((var8 == this.blockID || var9 == this.blockID) && (var10 == 2 || var10 == 3))
            {
                if (var8 == this.blockID)
                {
                    par1World.setBlockMetadataWithNotify(par2 - 1, par3, par4, var10);
                }
                else
                {
                    par1World.setBlockMetadataWithNotify(par2 + 1, par3, par4, var10);
                }

                par1World.setBlockMetadataWithNotify(par2, par3, par4, var10);
            }
        }
    }

    public void unifyAdjacentChests(World par1World, int par2, int par3, int par4)
    {
        if (!par1World.isRemote)
        {
            int var5 = par1World.getBlockId(par2, par3, par4 - 1);
            int var6 = par1World.getBlockId(par2, par3, par4 + 1);
            int var7 = par1World.getBlockId(par2 - 1, par3, par4);
            int var8 = par1World.getBlockId(par2 + 1, par3, par4);
            boolean var9 = true;
            int var10;
            int var11;
            boolean var12;
            byte var13;
            int var14;

            if (var5 != this.blockID && var6 != this.blockID)
            {
                if (var7 != this.blockID && var8 != this.blockID)
                {
                    var13 = 3;

                    if (Block.opaqueCubeLookup[var5] && !Block.opaqueCubeLookup[var6])
                    {
                        var13 = 3;
                    }

                    if (Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var5])
                    {
                        var13 = 2;
                    }

                    if (Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var8])
                    {
                        var13 = 5;
                    }

                    if (Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var7])
                    {
                        var13 = 4;
                    }
                }
                else
                {
                    var10 = par1World.getBlockId(var7 == this.blockID ? par2 - 1 : par2 + 1, par3, par4 - 1);
                    var11 = par1World.getBlockId(var7 == this.blockID ? par2 - 1 : par2 + 1, par3, par4 + 1);
                    var13 = 3;
                    var12 = true;

                    if (var7 == this.blockID)
                    {
                        var14 = par1World.getBlockMetadata(par2 - 1, par3, par4);
                    }
                    else
                    {
                        var14 = par1World.getBlockMetadata(par2 + 1, par3, par4);
                    }

                    if (var14 == 2)
                    {
                        var13 = 2;
                    }

                    if ((Block.opaqueCubeLookup[var5] || Block.opaqueCubeLookup[var10]) && !Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var11])
                    {
                        var13 = 3;
                    }

                    if ((Block.opaqueCubeLookup[var6] || Block.opaqueCubeLookup[var11]) && !Block.opaqueCubeLookup[var5] && !Block.opaqueCubeLookup[var10])
                    {
                        var13 = 2;
                    }
                }
            }
            else
            {
                var10 = par1World.getBlockId(par2 - 1, par3, var5 == this.blockID ? par4 - 1 : par4 + 1);
                var11 = par1World.getBlockId(par2 + 1, par3, var5 == this.blockID ? par4 - 1 : par4 + 1);
                var13 = 5;
                var12 = true;

                if (var5 == this.blockID)
                {
                    var14 = par1World.getBlockMetadata(par2, par3, par4 - 1);
                }
                else
                {
                    var14 = par1World.getBlockMetadata(par2, par3, par4 + 1);
                }

                if (var14 == 4)
                {
                    var13 = 4;
                }

                if ((Block.opaqueCubeLookup[var7] || Block.opaqueCubeLookup[var10]) && !Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var11])
                {
                    var13 = 5;
                }

                if ((Block.opaqueCubeLookup[var8] || Block.opaqueCubeLookup[var11]) && !Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var10])
                {
                    var13 = 4;
                }
            }

            par1World.setBlockMetadataWithNotify(par2, par3, par4, var13);
        }
    }

    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public int getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        if (par5 == 1)
        {
            return this.blockIndexInTexture - 1;
        }
        else if (par5 == 0)
        {
            return this.blockIndexInTexture - 1;
        }
        else
        {
            int var6 = par1IBlockAccess.getBlockId(par2, par3, par4 - 1);
            int var7 = par1IBlockAccess.getBlockId(par2, par3, par4 + 1);
            int var8 = par1IBlockAccess.getBlockId(par2 - 1, par3, par4);
            int var9 = par1IBlockAccess.getBlockId(par2 + 1, par3, par4);
            int var10;
            int var11;
            int var12;
            byte var13;

            if (var6 != this.blockID && var7 != this.blockID)
            {
                if (var8 != this.blockID && var9 != this.blockID)
                {
                    byte var14 = 3;

                    if (Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var7])
                    {
                        var14 = 3;
                    }

                    if (Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var6])
                    {
                        var14 = 2;
                    }

                    if (Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var9])
                    {
                        var14 = 5;
                    }

                    if (Block.opaqueCubeLookup[var9] && !Block.opaqueCubeLookup[var8])
                    {
                        var14 = 4;
                    }

                    return par5 == var14 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture;
                }
                else if (par5 != 4 && par5 != 5)
                {
                    var10 = 0;

                    if (var8 == this.blockID)
                    {
                        var10 = -1;
                    }

                    var11 = par1IBlockAccess.getBlockId(var8 == this.blockID ? par2 - 1 : par2 + 1, par3, par4 - 1);
                    var12 = par1IBlockAccess.getBlockId(var8 == this.blockID ? par2 - 1 : par2 + 1, par3, par4 + 1);

                    if (par5 == 3)
                    {
                        var10 = -1 - var10;
                    }

                    var13 = 3;

                    if ((Block.opaqueCubeLookup[var6] || Block.opaqueCubeLookup[var11]) && !Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var12])
                    {
                        var13 = 3;
                    }

                    if ((Block.opaqueCubeLookup[var7] || Block.opaqueCubeLookup[var12]) && !Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var11])
                    {
                        var13 = 2;
                    }

                    return (par5 == var13 ? this.blockIndexInTexture + 16 : this.blockIndexInTexture + 32) + var10;
                }
                else
                {
                    return this.blockIndexInTexture;
                }
            }
            else if (par5 != 2 && par5 != 3)
            {
                var10 = 0;

                if (var6 == this.blockID)
                {
                    var10 = -1;
                }

                var11 = par1IBlockAccess.getBlockId(par2 - 1, par3, var6 == this.blockID ? par4 - 1 : par4 + 1);
                var12 = par1IBlockAccess.getBlockId(par2 + 1, par3, var6 == this.blockID ? par4 - 1 : par4 + 1);

                if (par5 == 4)
                {
                    var10 = -1 - var10;
                }

                var13 = 5;

                if ((Block.opaqueCubeLookup[var8] || Block.opaqueCubeLookup[var11]) && !Block.opaqueCubeLookup[var9] && !Block.opaqueCubeLookup[var12])
                {
                    var13 = 5;
                }

                if ((Block.opaqueCubeLookup[var9] || Block.opaqueCubeLookup[var12]) && !Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var11])
                {
                    var13 = 4;
                }

                return (par5 == var13 ? this.blockIndexInTexture + 16 : this.blockIndexInTexture + 32) + var10;
            }
            else
            {
                return this.blockIndexInTexture;
            }
        }
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int getBlockTextureFromSide(int par1)
    {
        return par1 == 1 ? this.blockIndexInTexture - 1 : (par1 == 0 ? this.blockIndexInTexture - 1 : (par1 == 3 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture));
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        int var5 = 0;

        if (par1World.getBlockId(par2 - 1, par3, par4) == this.blockID)
        {
            ++var5;
        }

        if (par1World.getBlockId(par2 + 1, par3, par4) == this.blockID)
        {
            ++var5;
        }

        if (par1World.getBlockId(par2, par3, par4 - 1) == this.blockID)
        {
            ++var5;
        }

        if (par1World.getBlockId(par2, par3, par4 + 1) == this.blockID)
        {
            ++var5;
        }

        return var5 > 1 ? false : (this.isThereANeighborChest(par1World, par2 - 1, par3, par4) ? false : (this.isThereANeighborChest(par1World, par2 + 1, par3, par4) ? false : (this.isThereANeighborChest(par1World, par2, par3, par4 - 1) ? false : !this.isThereANeighborChest(par1World, par2, par3, par4 + 1))));
    }

    /**
     * Checks the neighbor blocks to see if there is a chest there. Args: world, x, y, z
     */
    private boolean isThereANeighborChest(World par1World, int par2, int par3, int par4)
    {
        return par1World.getBlockId(par2, par3, par4) != this.blockID ? false : (par1World.getBlockId(par2 - 1, par3, par4) == this.blockID ? true : (par1World.getBlockId(par2 + 1, par3, par4) == this.blockID ? true : (par1World.getBlockId(par2, par3, par4 - 1) == this.blockID ? true : par1World.getBlockId(par2, par3, par4 + 1) == this.blockID)));
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        TileEntityChest var6 = (TileEntityChest)par1World.getBlockTileEntity(par2, par3, par4);

        if (var6 != null)
        {
            var6.updateContainingBlockInfo();
        }
    }

    /**
     * Called whenever the block is removed.
     */
    public void onBlockRemoval(World par1World, int par2, int par3, int par4)
    {
        TileEntityChest var5 = (TileEntityChest)par1World.getBlockTileEntity(par2, par3, par4);

        if (var5 != null)
        {
            for (int var6 = 0; var6 < var5.getSizeInventory(); ++var6)
            {
                ItemStack var7 = var5.getStackInSlot(var6);

                if (var7 != null)
                {
                    float var8 = this.random.nextFloat() * 0.8F + 0.1F;
                    float var9 = this.random.nextFloat() * 0.8F + 0.1F;
                    EntityItem var12;

                    for (float var10 = this.random.nextFloat() * 0.8F + 0.1F; var7.stackSize > 0; par1World.spawnEntityInWorld(var12))
                    {
                        int var11 = this.random.nextInt(21) + 10;

                        if (var11 > var7.stackSize)
                        {
                            var11 = var7.stackSize;
                        }

                        var7.stackSize -= var11;
                        var12 = new EntityItem(par1World, (double)((float)par2 + var8), (double)((float)par3 + var9), (double)((float)par4 + var10), new ItemStack(var7.itemID, var11, var7.getItemDamage()));
                        float var13 = 0.05F;
                        var12.motionX = (double)((float)this.random.nextGaussian() * var13);
                        var12.motionY = (double)((float)this.random.nextGaussian() * var13 + 0.2F);
                        var12.motionZ = (double)((float)this.random.nextGaussian() * var13);

                        if (var7.hasTagCompound())
                        {
                            var12.item.setTagCompound((NBTTagCompound)var7.getTagCompound().copy());
                        }
                    }
                }
            }
        }

        super.onBlockRemoval(par1World, par2, par3, par4);
    }

    /**
     * Called upon block activation (left or right click on the block.). The three integers represent x,y,z of the
     * block.
     */
    public boolean blockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        Object var6 = (TileEntityChest)par1World.getBlockTileEntity(par2, par3, par4);

        if (var6 == null)
        {
            return true;
        }
        else if (par1World.isBlockSolidOnSide(par2, par3 + 1, par4, 0))
        {
            return true;
        }
        else if (par1World.getBlockId(par2 - 1, par3, par4) == this.blockID && par1World.isBlockSolidOnSide(par2 - 1, par3 + 1, par4, 0))
        {
            return true;
        }
        else if (par1World.getBlockId(par2 + 1, par3, par4) == this.blockID && par1World.isBlockSolidOnSide(par2 + 1, par3 + 1, par4, 0))
        {
            return true;
        }
        else if (par1World.getBlockId(par2, par3, par4 - 1) == this.blockID && par1World.isBlockSolidOnSide(par2, par3 + 1, par4 - 1, 0))
        {
            return true;
        }
        else if (par1World.getBlockId(par2, par3, par4 + 1) == this.blockID && par1World.isBlockSolidOnSide(par2, par3 + 1, par4 + 1, 0))
        {
            return true;
        }
        else
        {
            if (par1World.getBlockId(par2 - 1, par3, par4) == this.blockID)
            {
                var6 = new InventoryLargeChest("Large chest", (TileEntityChest)par1World.getBlockTileEntity(par2 - 1, par3, par4), (IInventory)var6);
            }

            if (par1World.getBlockId(par2 + 1, par3, par4) == this.blockID)
            {
                var6 = new InventoryLargeChest("Large chest", (IInventory)var6, (TileEntityChest)par1World.getBlockTileEntity(par2 + 1, par3, par4));
            }

            if (par1World.getBlockId(par2, par3, par4 - 1) == this.blockID)
            {
                var6 = new InventoryLargeChest("Large chest", (TileEntityChest)par1World.getBlockTileEntity(par2, par3, par4 - 1), (IInventory)var6);
            }

            if (par1World.getBlockId(par2, par3, par4 + 1) == this.blockID)
            {
                var6 = new InventoryLargeChest("Large chest", (IInventory)var6, (TileEntityChest)par1World.getBlockTileEntity(par2, par3, par4 + 1));
            }

            if (par1World.isRemote)
            {
                return true;
            }
            else
            {
                par5EntityPlayer.displayGUIChest((IInventory)var6);
                return true;
            }
        }
    }

    /**
     * Returns the TileEntity used by this block.
     */
    public TileEntity getBlockEntity()
    {
        return new TileEntityChest();
    }
}
