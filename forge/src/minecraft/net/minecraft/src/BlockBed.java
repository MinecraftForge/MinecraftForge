package net.minecraft.src;

import java.util.Iterator;
import java.util.Random;

public class BlockBed extends BlockDirectional
{
    /** Maps the head-of-bed block to the foot-of-bed block. */
    public static final int[][] headBlockToFootBlockMap = new int[][] {{0, 1}, { -1, 0}, {0, -1}, {1, 0}};

    public BlockBed(int par1)
    {
        super(par1, 134, Material.cloth);
        this.setBounds();
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
            int var6 = par1World.getBlockMetadata(par2, par3, par4);

            if (!isBlockFootOfBed(var6))
            {
                int var7 = func_48216_a(var6);
                par2 += headBlockToFootBlockMap[var7][0];
                par4 += headBlockToFootBlockMap[var7][1];

                if (par1World.getBlockId(par2, par3, par4) != this.blockID)
                {
                    return true;
                }

                var6 = par1World.getBlockMetadata(par2, par3, par4);
            }

            if (!par1World.worldProvider.canRespawnHere())
            {
                double var16 = (double)par2 + 0.5D;
                double var17 = (double)par3 + 0.5D;
                double var11 = (double)par4 + 0.5D;
                par1World.setBlockWithNotify(par2, par3, par4, 0);
                int var13 = func_48216_a(var6);
                par2 += headBlockToFootBlockMap[var13][0];
                par4 += headBlockToFootBlockMap[var13][1];

                if (par1World.getBlockId(par2, par3, par4) == this.blockID)
                {
                    par1World.setBlockWithNotify(par2, par3, par4, 0);
                    var16 = (var16 + (double)par2 + 0.5D) / 2.0D;
                    var17 = (var17 + (double)par3 + 0.5D) / 2.0D;
                    var11 = (var11 + (double)par4 + 0.5D) / 2.0D;
                }

                par1World.newExplosion((Entity)null, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), 5.0F, true);
                return true;
            }
            else
            {
                if (isBedOccupied(var6))
                {
                    EntityPlayer var14 = null;
                    Iterator var8 = par1World.playerEntities.iterator();

                    while (var8.hasNext())
                    {
                        EntityPlayer var9 = (EntityPlayer)var8.next();

                        if (var9.isPlayerSleeping())
                        {
                            ChunkCoordinates var10 = var9.playerLocation;

                            if (var10.posX == par2 && var10.posY == par3 && var10.posZ == par4)
                            {
                                var14 = var9;
                            }
                        }
                    }

                    if (var14 != null)
                    {
                        par5EntityPlayer.addChatMessage("tile.bed.occupied");
                        return true;
                    }

                    setBedOccupied(par1World, par2, par3, par4, false);
                }

                EnumStatus var15 = par5EntityPlayer.sleepInBedAt(par2, par3, par4);

                if (var15 == EnumStatus.OK)
                {
                    setBedOccupied(par1World, par2, par3, par4, true);
                    return true;
                }
                else
                {
                    if (var15 == EnumStatus.NOT_POSSIBLE_NOW)
                    {
                        par5EntityPlayer.addChatMessage("tile.bed.noSleep");
                    }
                    else if (var15 == EnumStatus.NOT_SAFE)
                    {
                        par5EntityPlayer.addChatMessage("tile.bed.notSafe");
                    }

                    return true;
                }
            }
        }
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        if (par1 == 0)
        {
            return Block.planks.blockIndexInTexture;
        }
        else
        {
            int var3 = func_48216_a(par2);
            int var4 = Direction.bedDirection[var3][par1];
            return isBlockFootOfBed(par2) ? (var4 == 2 ? this.blockIndexInTexture + 2 + 16 : (var4 != 5 && var4 != 4 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture + 1 + 16)) : (var4 == 3 ? this.blockIndexInTexture - 1 + 16 : (var4 != 5 && var4 != 4 ? this.blockIndexInTexture : this.blockIndexInTexture + 16));
        }
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 14;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
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
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        this.setBounds();
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        int var6 = par1World.getBlockMetadata(par2, par3, par4);
        int var7 = func_48216_a(var6);

        if (isBlockFootOfBed(var6))
        {
            if (par1World.getBlockId(par2 - headBlockToFootBlockMap[var7][0], par3, par4 - headBlockToFootBlockMap[var7][1]) != this.blockID)
            {
                par1World.setBlockWithNotify(par2, par3, par4, 0);
            }
        }
        else if (par1World.getBlockId(par2 + headBlockToFootBlockMap[var7][0], par3, par4 + headBlockToFootBlockMap[var7][1]) != this.blockID)
        {
            par1World.setBlockWithNotify(par2, par3, par4, 0);

            if (!par1World.isRemote)
            {
                this.dropBlockAsItem(par1World, par2, par3, par4, var6, 0);
            }
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return isBlockFootOfBed(par1) ? 0 : Item.bed.shiftedIndex;
    }

    /**
     * Set the bounds of the bed block.
     */
    private void setBounds()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
    }

    /**
     * Returns whether or not this bed block is the foot of the bed.
     */
    public static boolean isBlockFootOfBed(int par0)
    {
        return (par0 & 8) != 0;
    }

    /**
     * Return whether or not the bed is occupied.
     */
    public static boolean isBedOccupied(int par0)
    {
        return (par0 & 4) != 0;
    }

    /**
     * Sets whether or not the bed is occupied.
     */
    public static void setBedOccupied(World par0World, int par1, int par2, int par3, boolean par4)
    {
        int var5 = par0World.getBlockMetadata(par1, par2, par3);

        if (par4)
        {
            var5 |= 4;
        }
        else
        {
            var5 &= -5;
        }

        par0World.setBlockMetadataWithNotify(par1, par2, par3, var5);
    }

    /**
     * Gets the nearest empty chunk coordinates for the player to wake up from a bed into.
     */
    public static ChunkCoordinates getNearestEmptyChunkCoordinates(World par0World, int par1, int par2, int par3, int par4)
    {
        int var5 = par0World.getBlockMetadata(par1, par2, par3);
        int var6 = BlockDirectional.func_48216_a(var5);

        for (int var7 = 0; var7 <= 1; ++var7)
        {
            int var8 = par1 - headBlockToFootBlockMap[var6][0] * var7 - 1;
            int var9 = par3 - headBlockToFootBlockMap[var6][1] * var7 - 1;
            int var10 = var8 + 2;
            int var11 = var9 + 2;

            for (int var12 = var8; var12 <= var10; ++var12)
            {
                for (int var13 = var9; var13 <= var11; ++var13)
                {
                    if (par0World.isBlockNormalCube(var12, par2 - 1, var13) && par0World.isAirBlock(var12, par2, var13) && par0World.isAirBlock(var12, par2 + 1, var13))
                    {
                        if (par4 <= 0)
                        {
                            return new ChunkCoordinates(var12, par2, var13);
                        }

                        --par4;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
    {
        if (!isBlockFootOfBed(par5))
        {
            super.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, par6, 0);
        }
    }

    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
     * and stop pistons
     */
    public int getMobilityFlag()
    {
        return 1;
    }
}
