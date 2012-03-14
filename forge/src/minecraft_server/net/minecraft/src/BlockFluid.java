package net.minecraft.src;

import java.util.Random;

public abstract class BlockFluid extends Block
{
    protected BlockFluid(int par1, Material par2Material)
    {
        super(par1, (par2Material == Material.lava ? 14 : 12) * 16 + 13, par2Material);
        float var3 = 0.0F;
        float var4 = 0.0F;
        this.setBlockBounds(0.0F + var4, 0.0F + var3, 0.0F + var4, 1.0F + var4, 1.0F + var3, 1.0F + var4);
        this.setTickRandomly(true);
    }

    public boolean func_48127_b(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return this.blockMaterial != Material.lava;
    }

    /**
     * Returns the percentage of the fluid block that is air, based on the given flow decay of the fluid.
     */
    public static float getFluidHeightPercent(int par0)
    {
        if (par0 >= 8)
        {
            par0 = 0;
        }

        float var1 = (float)(par0 + 1) / 9.0F;
        return var1;
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int getBlockTextureFromSide(int par1)
    {
        return par1 != 0 && par1 != 1 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture;
    }

    protected int getFlowDecay(World par1World, int par2, int par3, int par4)
    {
        return par1World.getBlockMaterial(par2, par3, par4) != this.blockMaterial ? -1 : par1World.getBlockMetadata(par2, par3, par4);
    }

    protected int getEffectiveFlowDecay(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        if (par1IBlockAccess.getBlockMaterial(par2, par3, par4) != this.blockMaterial)
        {
            return -1;
        }
        else
        {
            int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);

            if (var5 >= 8)
            {
                var5 = 0;
            }

            return var5;
        }
    }

    /**
     * If this block doesn't render as an ordinary block it will return false (examples: signs, buttons, stairs, etc)
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
     * Returns whether this block is collideable based on the arguments passed in Args: blockMetaData, unknownFlag
     */
    public boolean canCollideCheck(int par1, boolean par2)
    {
        return par2 && par1 == 0;
    }

    /**
     * Returns Returns true if the given side of this block type should be rendered (if it's solid or not), if the
     * adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
     */
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        Material var6 = par1IBlockAccess.getBlockMaterial(par2, par3, par4);
        return var6 == this.blockMaterial ? false : (par5 == 1 ? true : (var6 == Material.ice ? false : super.isBlockSolid(par1IBlockAccess, par2, par3, par4, par5)));
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
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 4;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return 0;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    private Vec3D getFlowVector(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        Vec3D var5 = Vec3D.createVector(0.0D, 0.0D, 0.0D);
        int var6 = this.getEffectiveFlowDecay(par1IBlockAccess, par2, par3, par4);

        for (int var7 = 0; var7 < 4; ++var7)
        {
            int var8 = par2;
            int var10 = par4;

            if (var7 == 0)
            {
                var8 = par2 - 1;
            }

            if (var7 == 1)
            {
                var10 = par4 - 1;
            }

            if (var7 == 2)
            {
                ++var8;
            }

            if (var7 == 3)
            {
                ++var10;
            }

            int var11 = this.getEffectiveFlowDecay(par1IBlockAccess, var8, par3, var10);
            int var12;

            if (var11 < 0)
            {
                if (!par1IBlockAccess.getBlockMaterial(var8, par3, var10).blocksMovement())
                {
                    var11 = this.getEffectiveFlowDecay(par1IBlockAccess, var8, par3 - 1, var10);

                    if (var11 >= 0)
                    {
                        var12 = var11 - (var6 - 8);
                        var5 = var5.addVector((double)((var8 - par2) * var12), (double)((par3 - par3) * var12), (double)((var10 - par4) * var12));
                    }
                }
            }
            else if (var11 >= 0)
            {
                var12 = var11 - var6;
                var5 = var5.addVector((double)((var8 - par2) * var12), (double)((par3 - par3) * var12), (double)((var10 - par4) * var12));
            }
        }

        if (par1IBlockAccess.getBlockMetadata(par2, par3, par4) >= 8)
        {
            boolean var13 = false;

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2, par3, par4 - 1, 2))
            {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2, par3, par4 + 1, 3))
            {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2 - 1, par3, par4, 4))
            {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2 + 1, par3, par4, 5))
            {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2, par3 + 1, par4 - 1, 2))
            {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2, par3 + 1, par4 + 1, 3))
            {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2 - 1, par3 + 1, par4, 4))
            {
                var13 = true;
            }

            if (var13 || this.isBlockSolid(par1IBlockAccess, par2 + 1, par3 + 1, par4, 5))
            {
                var13 = true;
            }

            if (var13)
            {
                var5 = var5.normalize().addVector(0.0D, -6.0D, 0.0D);
            }
        }

        var5 = var5.normalize();
        return var5;
    }

    /**
     * Can add to the passed in vector for a movement vector to be applied to the entity. Args: x, y, z, entity, vec3d
     */
    public void velocityToAddToEntity(World par1World, int par2, int par3, int par4, Entity par5Entity, Vec3D par6Vec3D)
    {
        Vec3D var7 = this.getFlowVector(par1World, par2, par3, par4);
        par6Vec3D.xCoord += var7.xCoord;
        par6Vec3D.yCoord += var7.yCoord;
        par6Vec3D.zCoord += var7.zCoord;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate()
    {
        return this.blockMaterial == Material.water ? 5 : (this.blockMaterial == Material.lava ? 30 : 0);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        super.updateTick(par1World, par2, par3, par4, par5Random);
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        this.checkForHarden(par1World, par2, par3, par4);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        this.checkForHarden(par1World, par2, par3, par4);
    }

    /**
     * Forces lava to check to see if it is colliding with water, and then decide what it should harden to.
     */
    private void checkForHarden(World par1World, int par2, int par3, int par4)
    {
        if (par1World.getBlockId(par2, par3, par4) == this.blockID)
        {
            if (this.blockMaterial == Material.lava)
            {
                boolean var5 = false;

                if (var5 || par1World.getBlockMaterial(par2, par3, par4 - 1) == Material.water)
                {
                    var5 = true;
                }

                if (var5 || par1World.getBlockMaterial(par2, par3, par4 + 1) == Material.water)
                {
                    var5 = true;
                }

                if (var5 || par1World.getBlockMaterial(par2 - 1, par3, par4) == Material.water)
                {
                    var5 = true;
                }

                if (var5 || par1World.getBlockMaterial(par2 + 1, par3, par4) == Material.water)
                {
                    var5 = true;
                }

                if (var5 || par1World.getBlockMaterial(par2, par3 + 1, par4) == Material.water)
                {
                    var5 = true;
                }

                if (var5)
                {
                    int var6 = par1World.getBlockMetadata(par2, par3, par4);

                    if (var6 == 0)
                    {
                        par1World.setBlockWithNotify(par2, par3, par4, Block.obsidian.blockID);
                    }
                    else if (var6 <= 4)
                    {
                        par1World.setBlockWithNotify(par2, par3, par4, Block.cobblestone.blockID);
                    }

                    this.triggerLavaMixEffects(par1World, par2, par3, par4);
                }
            }
        }
    }

    protected void triggerLavaMixEffects(World par1World, int par2, int par3, int par4)
    {
        par1World.playSoundEffect((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

        for (int var5 = 0; var5 < 8; ++var5)
        {
            par1World.spawnParticle("largesmoke", (double)par2 + Math.random(), (double)par3 + 1.2D, (double)par4 + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }
}
