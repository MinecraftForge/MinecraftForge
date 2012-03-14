package net.minecraft.src;

import java.util.Random;

public class BlockFire extends Block
{
    /** The chance this block will encourage nearby blocks to catch on fire */
    private int[] chanceToEncourageFire = Block.blockFireSpreadSpeed;

    /**
     * This is an array indexed by block ID the larger the number in the array the more likely a block type will catch
     * fires
     */
    private int[] abilityToCatchFire = Block.blockFlammability;

    protected BlockFire(int par1, int par2)
    {
        super(par1, par2, Material.fire);
        this.setTickRandomly(true);
    }

    /**
     * This method is called on a block after all other blocks gets already created. You can use it to reference and
     * configure something on the block that needs the others ones.
     */
    public void initializeBlock()
    {
        this.setBurnRate(Block.planks.blockID, 5, 20);
        this.setBurnRate(Block.fence.blockID, 5, 20);
        this.setBurnRate(Block.stairCompactPlanks.blockID, 5, 20);
        this.setBurnRate(Block.wood.blockID, 5, 5);
        this.setBurnRate(Block.leaves.blockID, 30, 60);
        this.setBurnRate(Block.bookShelf.blockID, 30, 20);
        this.setBurnRate(Block.tnt.blockID, 15, 100);
        this.setBurnRate(Block.tallGrass.blockID, 60, 100);
        this.setBurnRate(Block.cloth.blockID, 30, 60);
        this.setBurnRate(Block.vine.blockID, 15, 100);
    }

    /**
     * Sets the burn rate for a block. The larger abilityToCatchFire the more easily it will catch. The larger
     * chanceToEncourageFire the faster it will burn and spread to other blocks. Args: blockID, chanceToEncourageFire,
     * abilityToCatchFire
     */
    private void setBurnRate(int par1, int par2, int par3)
    {
        Block.setBurnProperties(par1,  par2, par3);
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
        return 3;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate()
    {
        return 30;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        Block base = Block.blocksList[par1World.getBlockId(par2, par3 - 1, par4)];
        boolean var6 = (base != null && base.isFireSource(par1World, par2, par3 - 1, par4, par1World.getBlockMetadata(par2, par3 - 1, par4), 0));
        
        if (par1World.worldProvider instanceof WorldProviderEnd && par1World.getBlockId(par2, par3 - 1, par4) == Block.bedrock.blockID)
        {
            var6 = true;
        }

        if (!this.canPlaceBlockAt(par1World, par2, par3, par4))
        {
            par1World.setBlockWithNotify(par2, par3, par4, 0);
        }

        if (!var6 && par1World.isRaining() && (par1World.canLightningStrikeAt(par2, par3, par4) || par1World.canLightningStrikeAt(par2 - 1, par3, par4) || par1World.canLightningStrikeAt(par2 + 1, par3, par4) || par1World.canLightningStrikeAt(par2, par3, par4 - 1) || par1World.canLightningStrikeAt(par2, par3, par4 + 1)))
        {
            par1World.setBlockWithNotify(par2, par3, par4, 0);
        }
        else
        {
            int var7 = par1World.getBlockMetadata(par2, par3, par4);

            if (var7 < 15)
            {
                par1World.setBlockMetadata(par2, par3, par4, var7 + par5Random.nextInt(3) / 2);
            }

            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate() + par5Random.nextInt(10));

            if (!var6 && !this.canNeighborBurn(par1World, par2, par3, par4))
            {
                if (!par1World.isBlockSolidOnSide(par2, par3 - 1, par4, 1) || var7 > 3)
                {
                    par1World.setBlockWithNotify(par2, par3, par4, 0);
                }
            }
            else if (!var6 && !this.canBlockCatchFire(par1World, par2, par3 - 1, par4, 1) && var7 == 15 && par5Random.nextInt(4) == 0)
            {
                par1World.setBlockWithNotify(par2, par3, par4, 0);
            }
            else
            {
                boolean var8 = par1World.func_48455_z(par2, par3, par4);
                byte var9 = 0;

                if (var8)
                {
                    var9 = -50;
                }

                this.tryToCatchBlockOnFire(par1World, par2 + 1, par3, par4, 300 + var9, par5Random, var7, 4);
                this.tryToCatchBlockOnFire(par1World, par2 - 1, par3, par4, 300 + var9, par5Random, var7, 5);
                this.tryToCatchBlockOnFire(par1World, par2, par3 - 1, par4, 250 + var9, par5Random, var7, 1);
                this.tryToCatchBlockOnFire(par1World, par2, par3 + 1, par4, 250 + var9, par5Random, var7, 0);
                this.tryToCatchBlockOnFire(par1World, par2, par3, par4 - 1, 300 + var9, par5Random, var7, 3);
                this.tryToCatchBlockOnFire(par1World, par2, par3, par4 + 1, 300 + var9, par5Random, var7, 2);

                for (int var10 = par2 - 1; var10 <= par2 + 1; ++var10)
                {
                    for (int var11 = par4 - 1; var11 <= par4 + 1; ++var11)
                    {
                        for (int var12 = par3 - 1; var12 <= par3 + 4; ++var12)
                        {
                            if (var10 != par2 || var12 != par3 || var11 != par4)
                            {
                                int var13 = 100;

                                if (var12 > par3 + 1)
                                {
                                    var13 += (var12 - (par3 + 1)) * 100;
                                }

                                int var14 = this.getChanceOfNeighborsEncouragingFire(par1World, var10, var12, var11);

                                if (var14 > 0)
                                {
                                    int var15 = (var14 + 40) / (var7 + 30);

                                    if (var8)
                                    {
                                        var15 /= 2;
                                    }

                                    if (var15 > 0 && par5Random.nextInt(var13) <= var15 && (!par1World.isRaining() || !par1World.canLightningStrikeAt(var10, var12, var11)) && !par1World.canLightningStrikeAt(var10 - 1, var12, par4) && !par1World.canLightningStrikeAt(var10 + 1, var12, var11) && !par1World.canLightningStrikeAt(var10, var12, var11 - 1) && !par1World.canLightningStrikeAt(var10, var12, var11 + 1))
                                    {
                                        int var16 = var7 + par5Random.nextInt(5) / 4;

                                        if (var16 > 15)
                                        {
                                            var16 = 15;
                                        }

                                        par1World.setBlockAndMetadataWithNotify(var10, var12, var11, this.blockID, var16);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /** Deprecated for a side-sensitive version */
    @Deprecated
    private void func_48211_a(World par1World, int par2, int par3, int par4, int par5, Random par6Random, int par7)
    {
        tryToCatchBlockOnFire(par1World, par2, par3, par4, par5, par6Random, par7, 0);
    }

    private void tryToCatchBlockOnFire(World par1World, int par2, int par3, int par4, int par5, Random par6Random, int par7, int face)
    {
        int var8 = 0;
        Block block = Block.blocksList[par1World.getBlockId(par2, par3, par4)];
        if (block != null)
        {
            var8 = block.getFlammability(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), face);
        }

        if (par6Random.nextInt(par5) < var8)
        {
            boolean var9 = par1World.getBlockId(par2, par3, par4) == Block.tnt.blockID;

            if (par6Random.nextInt(par7 + 10) < 5 && !par1World.canLightningStrikeAt(par2, par3, par4))
            {
                int var10 = par7 + par6Random.nextInt(5) / 4;

                if (var10 > 15)
                {
                    var10 = 15;
                }

                par1World.setBlockAndMetadataWithNotify(par2, par3, par4, this.blockID, var10);
            }
            else
            {
                par1World.setBlockWithNotify(par2, par3, par4, 0);
            }

            if (var9)
            {
                Block.tnt.onBlockDestroyedByPlayer(par1World, par2, par3, par4, 1);
            }
        }
    }

    /**
     * Returns true if at least one block next to this one can burn.
     */
    private boolean canNeighborBurn(World par1World, int par2, int par3, int par4)
    {
        return canBlockCatchFire(par1World, par2 + 1, par3, par4, 4) ||
               canBlockCatchFire(par1World, par2 - 1, par3, par4, 5) ||
               canBlockCatchFire(par1World, par2, par3 - 1, par4, 1) ||
               canBlockCatchFire(par1World, par2, par3 + 1, par4, 0) ||
               canBlockCatchFire(par1World, par2, par3, par4 - 1, 3) ||
               canBlockCatchFire(par1World, par2, par3, par4 + 1, 2);
    }

    /**
     * Gets the highest chance of a neighbor block encouraging this block to catch fire
     */
    private int getChanceOfNeighborsEncouragingFire(World par1World, int par2, int par3, int par4)
    {
        byte var5 = 0;

        if (!par1World.isAirBlock(par2, par3, par4))
        {
            return 0;
        }
        else
        {
            int var6 = this.getChanceToEncourageFire(par1World, par2 + 1, par3, par4, var5, 4);
            var6 = this.getChanceToEncourageFire(par1World, par2 - 1, par3, par4, var6, 5);
            var6 = this.getChanceToEncourageFire(par1World, par2, par3 - 1, par4, var6, 1);
            var6 = this.getChanceToEncourageFire(par1World, par2, par3 + 1, par4, var6, 0);
            var6 = this.getChanceToEncourageFire(par1World, par2, par3, par4 - 1, var6, 3);
            var6 = this.getChanceToEncourageFire(par1World, par2, par3, par4 + 1, var6, 2);
            return var6;
        }
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    public boolean isCollidable()
    {
        return false;
    }

    /**
     * Checks the specified block coordinate to see if it can catch fire.  Args: blockAccess, x, y, z
     * Deprecated for a side-sensitive version
     */
    @Deprecated
    public boolean canBlockCatchFire(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return canBlockCatchFire(par1IBlockAccess, par2, par3, par4, 0);
    }

    /**
     * Retrieves a specified block's chance to encourage their neighbors to burn and if the number is greater than the
     * current number passed in it will return its number instead of the passed in one.  Args: world, x, y, z,
     * curChanceToEncourageFire
     * Deprecated for a side-sensitive version
     */
    @Deprecated
    public int getChanceToEncourageFire(World par1World, int par2, int par3, int par4, int par5)
    {
        return getChanceToEncourageFire(par1World, par2, par3, par4, par5, 0);
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return par1World.isBlockSolidOnSide(par2, par3 - 1, par4, 1) || this.canNeighborBurn(par1World, par2, par3, par4);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!par1World.isBlockSolidOnSide(par2, par3 - 1, par4, 1) && !this.canNeighborBurn(par1World, par2, par3, par4))
        {
            par1World.setBlockWithNotify(par2, par3, par4, 0);
        }
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        if (par1World.worldProvider.worldType > 0 || par1World.getBlockId(par2, par3 - 1, par4) != Block.obsidian.blockID || !Block.portal.tryToCreatePortal(par1World, par2, par3, par4))
        {
            if (!par1World.isBlockSolidOnSide(par2, par3 - 1, par4, 1) && !this.canNeighborBurn(par1World, par2, par3, par4))
            {
                par1World.setBlockWithNotify(par2, par3, par4, 0);
            }
            else
            {
                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate() + par1World.rand.nextInt(10));
            }
        }
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (par5Random.nextInt(24) == 0)
        {
            par1World.playSoundEffect((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), "fire.fire", 1.0F + par5Random.nextFloat(), par5Random.nextFloat() * 0.7F + 0.3F);
        }

        int var6;
        float var7;
        float var8;
        float var9;

        if (!par1World.isBlockSolidOnSide(par2, par3 - 1, par4, 1) && !Block.fire.canBlockCatchFire(par1World, par2, par3 - 1, par4, 1))
        {
            if (Block.fire.canBlockCatchFire(par1World, par2 - 1, par3, par4, 5))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float)par2 + par5Random.nextFloat() * 0.1F;
                    var8 = (float)par3 + par5Random.nextFloat();
                    var9 = (float)par4 + par5Random.nextFloat();
                    par1World.spawnParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Block.fire.canBlockCatchFire(par1World, par2 + 1, par3, par4, 4))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float)(par2 + 1) - par5Random.nextFloat() * 0.1F;
                    var8 = (float)par3 + par5Random.nextFloat();
                    var9 = (float)par4 + par5Random.nextFloat();
                    par1World.spawnParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Block.fire.canBlockCatchFire(par1World, par2, par3, par4 - 1, 3))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float)par2 + par5Random.nextFloat();
                    var8 = (float)par3 + par5Random.nextFloat();
                    var9 = (float)par4 + par5Random.nextFloat() * 0.1F;
                    par1World.spawnParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Block.fire.canBlockCatchFire(par1World, par2, par3, par4 + 1, 2))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float)par2 + par5Random.nextFloat();
                    var8 = (float)par3 + par5Random.nextFloat();
                    var9 = (float)(par4 + 1) - par5Random.nextFloat() * 0.1F;
                    par1World.spawnParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Block.fire.canBlockCatchFire(par1World, par2, par3 + 1, par4, 0))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float)par2 + par5Random.nextFloat();
                    var8 = (float)(par3 + 1) - par5Random.nextFloat() * 0.1F;
                    var9 = (float)par4 + par5Random.nextFloat();
                    par1World.spawnParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        else
        {
            for (var6 = 0; var6 < 3; ++var6)
            {
                var7 = (float)par2 + par5Random.nextFloat();
                var8 = (float)par3 + par5Random.nextFloat() * 0.5F + 0.5F;
                var9 = (float)par4 + par5Random.nextFloat();
                par1World.spawnParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
            }
        }
    }
    
    /**
     * Side sensitive version that calls the block function.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param face The side the fire is coming from
     * @return True if the face can catch fire.
     */
    public boolean canBlockCatchFire(IBlockAccess world, int x, int y, int z, int face)
    {
    	Block block = Block.blocksList[world.getBlockId(x, y, z)];
    	if (block != null)
    	{
    		return block.isFlammable(world, x, y, z, world.getBlockMetadata(x, y, z), face);
    	}
    	return false;
    }
    
    /**
     * Side sensitive version that calls the block function.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param oldChance The previous maximum chance.
     * @param face The side the fire is coming from
     * @return The chance of the block catching fire, or oldChance if it is higher
     */
    public int getChanceToEncourageFire(World world, int x, int y, int z, int oldChance, int face)
    {
    	int newChance = 0;
    	Block block = Block.blocksList[world.getBlockId(x, y, z)];
    	if (block != null)
    	{
    		newChance = block.getFireSpreadSpeed(world, x, y, z, world.getBlockMetadata(x, y, z), face);
    	}
        return (newChance > oldChance ? newChance : oldChance);
    }    
}
