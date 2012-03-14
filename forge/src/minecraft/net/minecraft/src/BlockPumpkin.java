package net.minecraft.src;

public class BlockPumpkin extends BlockDirectional
{
    /** Boolean used to seperate different states of blocks */
    private boolean blockType;

    protected BlockPumpkin(int par1, int par2, boolean par3)
    {
        super(par1, Material.pumpkin);
        this.blockIndexInTexture = par2;
        this.setTickRandomly(true);
        this.blockType = par3;
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        if (par1 == 1)
        {
            return this.blockIndexInTexture;
        }
        else if (par1 == 0)
        {
            return this.blockIndexInTexture;
        }
        else
        {
            int var3 = this.blockIndexInTexture + 1 + 16;

            if (this.blockType)
            {
                ++var3;
            }

            return par2 == 2 && par1 == 2 ? var3 : (par2 == 3 && par1 == 5 ? var3 : (par2 == 0 && par1 == 3 ? var3 : (par2 == 1 && par1 == 4 ? var3 : this.blockIndexInTexture + 16)));
        }
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int getBlockTextureFromSide(int par1)
    {
        return par1 == 1 ? this.blockIndexInTexture : (par1 == 0 ? this.blockIndexInTexture : (par1 == 3 ? this.blockIndexInTexture + 1 + 16 : this.blockIndexInTexture + 16));
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);

        if (par1World.getBlockId(par2, par3 - 1, par4) == Block.blockSnow.blockID && par1World.getBlockId(par2, par3 - 2, par4) == Block.blockSnow.blockID)
        {
            if (!par1World.isRemote)
            {
                par1World.setBlock(par2, par3, par4, 0);
                par1World.setBlock(par2, par3 - 1, par4, 0);
                par1World.setBlock(par2, par3 - 2, par4, 0);
                EntitySnowman var9 = new EntitySnowman(par1World);
                var9.setLocationAndAngles((double)par2 + 0.5D, (double)par3 - 1.95D, (double)par4 + 0.5D, 0.0F, 0.0F);
                par1World.spawnEntityInWorld(var9);
                par1World.notifyBlockChange(par2, par3, par4, 0);
                par1World.notifyBlockChange(par2, par3 - 1, par4, 0);
                par1World.notifyBlockChange(par2, par3 - 2, par4, 0);
            }

            for (int var10 = 0; var10 < 120; ++var10)
            {
                par1World.spawnParticle("snowshovel", (double)par2 + par1World.rand.nextDouble(), (double)(par3 - 2) + par1World.rand.nextDouble() * 2.5D, (double)par4 + par1World.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
        else if (par1World.getBlockId(par2, par3 - 1, par4) == Block.blockSteel.blockID && par1World.getBlockId(par2, par3 - 2, par4) == Block.blockSteel.blockID)
        {
            boolean var5 = par1World.getBlockId(par2 - 1, par3 - 1, par4) == Block.blockSteel.blockID && par1World.getBlockId(par2 + 1, par3 - 1, par4) == Block.blockSteel.blockID;
            boolean var6 = par1World.getBlockId(par2, par3 - 1, par4 - 1) == Block.blockSteel.blockID && par1World.getBlockId(par2, par3 - 1, par4 + 1) == Block.blockSteel.blockID;

            if (var5 || var6)
            {
                par1World.setBlock(par2, par3, par4, 0);
                par1World.setBlock(par2, par3 - 1, par4, 0);
                par1World.setBlock(par2, par3 - 2, par4, 0);

                if (var5)
                {
                    par1World.setBlock(par2 - 1, par3 - 1, par4, 0);
                    par1World.setBlock(par2 + 1, par3 - 1, par4, 0);
                }
                else
                {
                    par1World.setBlock(par2, par3 - 1, par4 - 1, 0);
                    par1World.setBlock(par2, par3 - 1, par4 + 1, 0);
                }

                EntityIronGolem var7 = new EntityIronGolem(par1World);
                var7.func_48115_b(true);
                var7.setLocationAndAngles((double)par2 + 0.5D, (double)par3 - 1.95D, (double)par4 + 0.5D, 0.0F, 0.0F);
                par1World.spawnEntityInWorld(var7);

                for (int var8 = 0; var8 < 120; ++var8)
                {
                    par1World.spawnParticle("snowballpoof", (double)par2 + par1World.rand.nextDouble(), (double)(par3 - 2) + par1World.rand.nextDouble() * 3.9D, (double)par4 + par1World.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
                }

                par1World.notifyBlockChange(par2, par3, par4, 0);
                par1World.notifyBlockChange(par2, par3 - 1, par4, 0);
                par1World.notifyBlockChange(par2, par3 - 2, par4, 0);

                if (var5)
                {
                    par1World.notifyBlockChange(par2 - 1, par3 - 1, par4, 0);
                    par1World.notifyBlockChange(par2 + 1, par3 - 1, par4, 0);
                }
                else
                {
                    par1World.notifyBlockChange(par2, par3 - 1, par4 - 1, 0);
                    par1World.notifyBlockChange(par2, par3 - 1, par4 + 1, 0);
                }
            }
        }
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        int var5 = par1World.getBlockId(par2, par3, par4);
        return (var5 == 0 || Block.blocksList[var5].blockMaterial.isGroundCover()) && par1World.isBlockNormalCube(par2, par3 - 1, par4);
    }

    /**
     * Called when a block is placed by using an ItemStack from inventory and passed in who placed it. Args:
     * x,y,z,entityliving
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving)
    {
        int var6 = MathHelper.floor_double((double)(par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, var6);
    }
}
