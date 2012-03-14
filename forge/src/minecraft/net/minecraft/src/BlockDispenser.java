package net.minecraft.src;

import java.util.Random;

public class BlockDispenser extends BlockContainer
{
    private Random random = new Random();

    protected BlockDispenser(int par1)
    {
        super(par1, Material.rock);
        this.blockIndexInTexture = 45;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate()
    {
        return 4;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Block.dispenser.blockID;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);
        this.setDispenserDefaultDirection(par1World, par2, par3, par4);
    }

    /**
     * sets Dispenser block direction so that the front faces an non-opaque block; chooses west to be direction if all
     * surrounding blocks are opaque.
     */
    private void setDispenserDefaultDirection(World par1World, int par2, int par3, int par4)
    {
        if (!par1World.isRemote)
        {
            int var5 = par1World.getBlockId(par2, par3, par4 - 1);
            int var6 = par1World.getBlockId(par2, par3, par4 + 1);
            int var7 = par1World.getBlockId(par2 - 1, par3, par4);
            int var8 = par1World.getBlockId(par2 + 1, par3, par4);
            byte var9 = 3;

            if (Block.opaqueCubeLookup[var5] && !Block.opaqueCubeLookup[var6])
            {
                var9 = 3;
            }

            if (Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var5])
            {
                var9 = 2;
            }

            if (Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var8])
            {
                var9 = 5;
            }

            if (Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var7])
            {
                var9 = 4;
            }

            par1World.setBlockMetadataWithNotify(par2, par3, par4, var9);
        }
    }

    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public int getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        if (par5 == 1)
        {
            return this.blockIndexInTexture + 17;
        }
        else if (par5 == 0)
        {
            return this.blockIndexInTexture + 17;
        }
        else
        {
            int var6 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
            return par5 != var6 ? this.blockIndexInTexture : this.blockIndexInTexture + 1;
        }
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int getBlockTextureFromSide(int par1)
    {
        return par1 == 1 ? this.blockIndexInTexture + 17 : (par1 == 0 ? this.blockIndexInTexture + 17 : (par1 == 3 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture));
    }

    /**
     * Called upon block activation (left or right click on the block.). The three integers represent x,y,z of the
     * block.
     */
    public boolean blockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        if (par1World.isRemote)
        {
            return true;
        }
        else
        {
            TileEntityDispenser var6 = (TileEntityDispenser)par1World.getBlockTileEntity(par2, par3, par4);

            if (var6 != null)
            {
                par5EntityPlayer.displayGUIDispenser(var6);
            }

            return true;
        }
    }

    /**
     * dispenses an item from a randomly selected item stack from the blocks inventory into the game world.
     */
    private void dispenseItem(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        int var6 = par1World.getBlockMetadata(par2, par3, par4);
        byte var7 = 0;
        byte var8 = 0;

        if (var6 == 3)
        {
            var8 = 1;
        }
        else if (var6 == 2)
        {
            var8 = -1;
        }
        else if (var6 == 5)
        {
            var7 = 1;
        }
        else
        {
            var7 = -1;
        }

        TileEntityDispenser var9 = (TileEntityDispenser)par1World.getBlockTileEntity(par2, par3, par4);

        if (var9 != null)
        {
            ItemStack var10 = var9.getRandomStackFromInventory();
            double var11 = (double)par2 + (double)var7 * 0.6D + 0.5D;
            double var13 = (double)par3 + 0.5D;
            double var15 = (double)par4 + (double)var8 * 0.6D + 0.5D;

            if (var10 == null)
            {
                par1World.playAuxSFX(1001, par2, par3, par4, 0);
            }
            else
            {
                boolean var17 = ModLoader.dispenseEntity(par1World, var11, var13, var15, var7, var8, var10);

                if (!var17)
                {
                    if (var10.itemID == Item.arrow.shiftedIndex)
                    {
                        EntityArrow var18 = new EntityArrow(par1World, var11, var13, var15);
                        var18.setArrowHeading((double)var7, 0.10000000149011612D, (double)var8, 1.1F, 6.0F);
                        var18.doesArrowBelongToPlayer = true;
                        par1World.spawnEntityInWorld(var18);
                        par1World.playAuxSFX(1002, par2, par3, par4, 0);
                    }
                    else if (var10.itemID == Item.egg.shiftedIndex)
                    {
                        EntityEgg var24 = new EntityEgg(par1World, var11, var13, var15);
                        var24.setThrowableHeading((double)var7, 0.10000000149011612D, (double)var8, 1.1F, 6.0F);
                        par1World.spawnEntityInWorld(var24);
                        par1World.playAuxSFX(1002, par2, par3, par4, 0);
                    }
                    else if (var10.itemID == Item.snowball.shiftedIndex)
                    {
                        EntitySnowball var23 = new EntitySnowball(par1World, var11, var13, var15);
                        var23.setThrowableHeading((double)var7, 0.10000000149011612D, (double)var8, 1.1F, 6.0F);
                        par1World.spawnEntityInWorld(var23);
                        par1World.playAuxSFX(1002, par2, par3, par4, 0);
                    }
                    else if (var10.itemID == Item.potion.shiftedIndex && ItemPotion.isSplash(var10.getItemDamage()))
                    {
                        EntityPotion var25 = new EntityPotion(par1World, var11, var13, var15, var10.getItemDamage());
                        var25.setThrowableHeading((double)var7, 0.10000000149011612D, (double)var8, 1.375F, 3.0F);
                        par1World.spawnEntityInWorld(var25);
                        par1World.playAuxSFX(1002, par2, par3, par4, 0);
                    }
                    else if (var10.itemID == Item.field_48438_bD.shiftedIndex)
                    {
                        EntityExpBottle var22 = new EntityExpBottle(par1World, var11, var13, var15);
                        var22.setThrowableHeading((double)var7, 0.10000000149011612D, (double)var8, 1.375F, 3.0F);
                        par1World.spawnEntityInWorld(var22);
                        par1World.playAuxSFX(1002, par2, par3, par4, 0);
                    }
                    else if (var10.itemID == Item.monsterPlacer.shiftedIndex)
                    {
                        ItemMonsterPlacer.func_48440_a(par1World, var10.getItemDamage(), var11 + (double)var7 * 0.3D, var13 - 0.3D, var15 + (double)var8 * 0.3D);
                        par1World.playAuxSFX(1002, par2, par3, par4, 0);
                    }
                    else if (var10.itemID == Item.field_48439_bE.shiftedIndex)
                    {
                        EntitySmallFireball var21 = new EntitySmallFireball(par1World, var11 + (double)var7 * 0.3D, var13, var15 + (double)var8 * 0.3D, (double)var7 + par5Random.nextGaussian() * 0.05D, par5Random.nextGaussian() * 0.05D, (double)var8 + par5Random.nextGaussian() * 0.05D);
                        par1World.spawnEntityInWorld(var21);
                        par1World.playAuxSFX(1009, par2, par3, par4, 0);
                    }
                    else
                    {
                        EntityItem var26 = new EntityItem(par1World, var11, var13 - 0.3D, var15, var10);
                        double var19 = par5Random.nextDouble() * 0.1D + 0.2D;
                        var26.motionX = (double)var7 * var19;
                        var26.motionY = 0.20000000298023224D;
                        var26.motionZ = (double)var8 * var19;
                        var26.motionX += par5Random.nextGaussian() * 0.007499999832361937D * 6.0D;
                        var26.motionY += par5Random.nextGaussian() * 0.007499999832361937D * 6.0D;
                        var26.motionZ += par5Random.nextGaussian() * 0.007499999832361937D * 6.0D;
                        par1World.spawnEntityInWorld(var26);
                        par1World.playAuxSFX(1000, par2, par3, par4, 0);
                    }
                }

                par1World.playAuxSFX(2000, par2, par3, par4, var7 + 1 + (var8 + 1) * 3);
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        if (par5 > 0 && Block.blocksList[par5].canProvidePower())
        {
            boolean var6 = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4) || par1World.isBlockIndirectlyGettingPowered(par2, par3 + 1, par4);

            if (var6)
            {
                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote && (par1World.isBlockIndirectlyGettingPowered(par2, par3, par4) || par1World.isBlockIndirectlyGettingPowered(par2, par3 + 1, par4)))
        {
            this.dispenseItem(par1World, par2, par3, par4, par5Random);
        }
    }

    /**
     * Returns the TileEntity used by this block.
     */
    public TileEntity getBlockEntity()
    {
        return new TileEntityDispenser();
    }

    /**
     * Called when a block is placed by using an ItemStack from inventory and passed in who placed it. Args:
     * x,y,z,entityliving
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving)
    {
        int var6 = MathHelper.floor_double((double)(par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (var6 == 0)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 2);
        }

        if (var6 == 1)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 5);
        }

        if (var6 == 2)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 3);
        }

        if (var6 == 3)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 4);
        }
    }

    /**
     * Called whenever the block is removed.
     */
    public void onBlockRemoval(World par1World, int par2, int par3, int par4)
    {
        TileEntityDispenser var5 = (TileEntityDispenser)par1World.getBlockTileEntity(par2, par3, par4);

        if (var5 != null)
        {
            for (int var6 = 0; var6 < var5.getSizeInventory(); ++var6)
            {
                ItemStack var7 = var5.getStackInSlot(var6);

                if (var7 != null)
                {
                    float var8 = this.random.nextFloat() * 0.8F + 0.1F;
                    float var9 = this.random.nextFloat() * 0.8F + 0.1F;
                    float var10 = this.random.nextFloat() * 0.8F + 0.1F;

                    while (var7.stackSize > 0)
                    {
                        int var11 = this.random.nextInt(21) + 10;

                        if (var11 > var7.stackSize)
                        {
                            var11 = var7.stackSize;
                        }

                        var7.stackSize -= var11;
                        EntityItem var12 = new EntityItem(par1World, (double)((float)par2 + var8), (double)((float)par3 + var9), (double)((float)par4 + var10), new ItemStack(var7.itemID, var11, var7.getItemDamage()));
                        float var13 = 0.05F;
                        var12.motionX = (double)((float)this.random.nextGaussian() * var13);
                        var12.motionY = (double)((float)this.random.nextGaussian() * var13 + 0.2F);
                        var12.motionZ = (double)((float)this.random.nextGaussian() * var13);
                        par1World.spawnEntityInWorld(var12);
                    }
                }
            }
        }

        super.onBlockRemoval(par1World, par2, par3, par4);
    }
}
