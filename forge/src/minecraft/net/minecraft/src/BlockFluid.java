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

    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return this.blockMaterial != Material.lava;
    }

    public int getBlockColor()
    {
        return 16777215;
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        if (this.blockMaterial != Material.water)
        {
            return 16777215;
        }
        else
        {
            int var5 = 0;
            int var6 = 0;
            int var7 = 0;

            for (int var8 = -1; var8 <= 1; ++var8)
            {
                for (int var9 = -1; var9 <= 1; ++var9)
                {
                    int var10 = par1IBlockAccess.func_48454_a(par2 + var9, par4 + var8).waterColorMultiplier;
                    var5 += (var10 & 16711680) >> 16;
                    var6 += (var10 & 65280) >> 8;
                    var7 += var10 & 255;
                }
            }

            return (var5 / 9 & 255) << 16 | (var6 / 9 & 255) << 8 | var7 / 9 & 255;
        }
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

    /**
     * Returns the amount of fluid decay at the coordinates, or -1 if the block at the coordinates is not the same
     * material as the fluid.
     */
    protected int getFlowDecay(World par1World, int par2, int par3, int par4)
    {
        return par1World.getBlockMaterial(par2, par3, par4) != this.blockMaterial ? -1 : par1World.getBlockMetadata(par2, par3, par4);
    }

    /**
     * Returns the flow decay but converts values indicating falling liquid (values >=8) to their effective source block
     * value of zero.
     */
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
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        Material var6 = par1IBlockAccess.getBlockMaterial(par2, par3, par4);
        return var6 == this.blockMaterial ? false : (par5 == 1 ? true : (var6 == Material.ice ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5)));
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

    /**
     * Returns a vector indicating the direction and intensity of fluid flow.
     */
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
     * 'Goes straight to getLightBrightnessForSkyBlocks for Blocks, does some fancy computing for Fluids'
     */
    public int getMixedBrightnessForBlock(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int var5 = par1IBlockAccess.getLightBrightnessForSkyBlocks(par2, par3, par4, 0);
        int var6 = par1IBlockAccess.getLightBrightnessForSkyBlocks(par2, par3 + 1, par4, 0);
        int var7 = var5 & 255;
        int var8 = var6 & 255;
        int var9 = var5 >> 16 & 255;
        int var10 = var6 >> 16 & 255;
        return (var7 > var8 ? var7 : var8) | (var9 > var10 ? var9 : var10) << 16;
    }

    /**
     * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
     */
    public float getBlockBrightness(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        float var5 = par1IBlockAccess.getLightBrightness(par2, par3, par4);
        float var6 = par1IBlockAccess.getLightBrightness(par2, par3 + 1, par4);
        return var5 > var6 ? var5 : var6;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        super.updateTick(par1World, par2, par3, par4, par5Random);
    }

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    public int getRenderBlockPass()
    {
        return this.blockMaterial == Material.water ? 1 : 0;
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        int var6;

        if (this.blockMaterial == Material.water)
        {
            if (par5Random.nextInt(10) == 0)
            {
                var6 = par1World.getBlockMetadata(par2, par3, par4);

                if (var6 <= 0 || var6 >= 8)
                {
                    par1World.spawnParticle("suspended", (double)((float)par2 + par5Random.nextFloat()), (double)((float)par3 + par5Random.nextFloat()), (double)((float)par4 + par5Random.nextFloat()), 0.0D, 0.0D, 0.0D);
                }
            }

            for (var6 = 0; var6 < 0; ++var6)
            {
                int var7 = par5Random.nextInt(4);
                int var8 = par2;
                int var9 = par4;

                if (var7 == 0)
                {
                    var8 = par2 - 1;
                }

                if (var7 == 1)
                {
                    ++var8;
                }

                if (var7 == 2)
                {
                    var9 = par4 - 1;
                }

                if (var7 == 3)
                {
                    ++var9;
                }

                if (par1World.getBlockMaterial(var8, par3, var9) == Material.air && (par1World.getBlockMaterial(var8, par3 - 1, var9).blocksMovement() || par1World.getBlockMaterial(var8, par3 - 1, var9).isLiquid()))
                {
                    float var10 = 0.0625F;
                    double var11 = (double)((float)par2 + par5Random.nextFloat());
                    double var13 = (double)((float)par3 + par5Random.nextFloat());
                    double var15 = (double)((float)par4 + par5Random.nextFloat());

                    if (var7 == 0)
                    {
                        var11 = (double)((float)par2 - var10);
                    }

                    if (var7 == 1)
                    {
                        var11 = (double)((float)(par2 + 1) + var10);
                    }

                    if (var7 == 2)
                    {
                        var15 = (double)((float)par4 - var10);
                    }

                    if (var7 == 3)
                    {
                        var15 = (double)((float)(par4 + 1) + var10);
                    }

                    double var17 = 0.0D;
                    double var19 = 0.0D;

                    if (var7 == 0)
                    {
                        var17 = (double)(-var10);
                    }

                    if (var7 == 1)
                    {
                        var17 = (double)var10;
                    }

                    if (var7 == 2)
                    {
                        var19 = (double)(-var10);
                    }

                    if (var7 == 3)
                    {
                        var19 = (double)var10;
                    }

                    par1World.spawnParticle("splash", var11, var13, var15, var17, 0.0D, var19);
                }
            }
        }

        if (this.blockMaterial == Material.water && par5Random.nextInt(64) == 0)
        {
            var6 = par1World.getBlockMetadata(par2, par3, par4);

            if (var6 > 0 && var6 < 8)
            {
                par1World.playSoundEffect((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), "liquid.water", par5Random.nextFloat() * 0.25F + 0.75F, par5Random.nextFloat() * 1.0F + 0.5F);
            }
        }

        double var21;
        double var23;
        double var22;

        if (this.blockMaterial == Material.lava && par1World.getBlockMaterial(par2, par3 + 1, par4) == Material.air && !par1World.isBlockOpaqueCube(par2, par3 + 1, par4))
        {
            if (par5Random.nextInt(100) == 0)
            {
                var21 = (double)((float)par2 + par5Random.nextFloat());
                var22 = (double)par3 + this.maxY;
                var23 = (double)((float)par4 + par5Random.nextFloat());
                par1World.spawnParticle("lava", var21, var22, var23, 0.0D, 0.0D, 0.0D);
                par1World.playSoundEffect(var21, var22, var23, "liquid.lavapop", 0.2F + par5Random.nextFloat() * 0.2F, 0.9F + par5Random.nextFloat() * 0.15F);
            }

            if (par5Random.nextInt(200) == 0)
            {
                par1World.playSoundEffect((double)par2, (double)par3, (double)par4, "liquid.lava", 0.2F + par5Random.nextFloat() * 0.2F, 0.9F + par5Random.nextFloat() * 0.15F);
            }
        }

        if (par5Random.nextInt(10) == 0 && par1World.isBlockNormalCube(par2, par3 - 1, par4) && !par1World.getBlockMaterial(par2, par3 - 2, par4).blocksMovement())
        {
            var21 = (double)((float)par2 + par5Random.nextFloat());
            var22 = (double)par3 - 1.05D;
            var23 = (double)((float)par4 + par5Random.nextFloat());

            if (this.blockMaterial == Material.water)
            {
                par1World.spawnParticle("dripWater", var21, var22, var23, 0.0D, 0.0D, 0.0D);
            }
            else
            {
                par1World.spawnParticle("dripLava", var21, var22, var23, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public static double func_293_a(IBlockAccess par0IBlockAccess, int par1, int par2, int par3, Material par4Material)
    {
        Vec3D var5 = null;

        if (par4Material == Material.water)
        {
            var5 = ((BlockFluid)Block.waterMoving).getFlowVector(par0IBlockAccess, par1, par2, par3);
        }

        if (par4Material == Material.lava)
        {
            var5 = ((BlockFluid)Block.lavaMoving).getFlowVector(par0IBlockAccess, par1, par2, par3);
        }

        return var5.xCoord == 0.0D && var5.zCoord == 0.0D ? -1000.0D : Math.atan2(var5.zCoord, var5.xCoord) - (Math.PI / 2D);
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
     * their own) Args: x, y, z, neighbor blockID
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

    /**
     * Creates fizzing sound and smoke. Used when lava flows over block or mixes with water.
     */
    protected void triggerLavaMixEffects(World par1World, int par2, int par3, int par4)
    {
        par1World.playSoundEffect((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

        for (int var5 = 0; var5 < 8; ++var5)
        {
            par1World.spawnParticle("largesmoke", (double)par2 + Math.random(), (double)par3 + 1.2D, (double)par4 + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }
}
