package net.minecraft.src;

import net.minecraft.src.forge.MinecraftForge;

public class ItemBucket extends Item
{
    /** field for checking if the bucket has been filled. */
    private int isFull;

    public ItemBucket(int par1, int par2)
    {
        super(par1);
        this.maxStackSize = 1;
        this.isFull = par2;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        float var4 = 1.0F;
        double var5 = par3EntityPlayer.prevPosX + (par3EntityPlayer.posX - par3EntityPlayer.prevPosX) * (double)var4;
        double var7 = par3EntityPlayer.prevPosY + (par3EntityPlayer.posY - par3EntityPlayer.prevPosY) * (double)var4 + 1.62D - (double)par3EntityPlayer.yOffset;
        double var9 = par3EntityPlayer.prevPosZ + (par3EntityPlayer.posZ - par3EntityPlayer.prevPosZ) * (double)var4;
        boolean var11 = this.isFull == 0;
        MovingObjectPosition var12 = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, var11);

        if (var12 == null)
        {
            return par1ItemStack;
        }
        else
        {
            if (var12.typeOfHit == EnumMovingObjectType.TILE)
            {
                int var13 = var12.blockX;
                int var14 = var12.blockY;
                int var15 = var12.blockZ;

                if (!par2World.canMineBlock(par3EntityPlayer, var13, var14, var15))
                {
                    return par1ItemStack;
                }

                if (this.isFull == 0)
                {
                    if (par3EntityPlayer != null && !par3EntityPlayer.canPlayerEdit(var13, var14, var15))
                    {
                        return par1ItemStack;
                    }
                    
                    ItemStack stack = MinecraftForge.fillCustomBucket(par2World, var13, var14, var15);
                    if (stack != null)
                    {
                        return stack;
                    }

                    if (par2World.getBlockMaterial(var13, var14, var15) == Material.water && par2World.getBlockMetadata(var13, var14, var15) == 0)
                    {
                        par2World.setBlockWithNotify(var13, var14, var15, 0);

                        if (par3EntityPlayer.capabilities.depleteBuckets)
                        {
                            return par1ItemStack;
                        }

                        return new ItemStack(Item.bucketWater);
                    }

                    if (par2World.getBlockMaterial(var13, var14, var15) == Material.lava && par2World.getBlockMetadata(var13, var14, var15) == 0)
                    {
                        par2World.setBlockWithNotify(var13, var14, var15, 0);

                        if (par3EntityPlayer.capabilities.depleteBuckets)
                        {
                            return par1ItemStack;
                        }

                        return new ItemStack(Item.bucketLava);
                    }
                }
                else
                {
                    if (this.isFull < 0)
                    {
                        return new ItemStack(Item.bucketEmpty);
                    }

                    if (var12.sideHit == 0)
                    {
                        --var14;
                    }

                    if (var12.sideHit == 1)
                    {
                        ++var14;
                    }

                    if (var12.sideHit == 2)
                    {
                        --var15;
                    }

                    if (var12.sideHit == 3)
                    {
                        ++var15;
                    }

                    if (var12.sideHit == 4)
                    {
                        --var13;
                    }

                    if (var12.sideHit == 5)
                    {
                        ++var13;
                    }

                    if (!par3EntityPlayer.canPlayerEdit(var13, var14, var15))
                    {
                        return par1ItemStack;
                    }

                    if (par2World.isAirBlock(var13, var14, var15) || !par2World.getBlockMaterial(var13, var14, var15).isSolid())
                    {
                        if (par2World.worldProvider.isHellWorld && this.isFull == Block.waterMoving.blockID)
                        {
                            par2World.playSoundEffect(var5 + 0.5D, var7 + 0.5D, var9 + 0.5D, "random.fizz", 0.5F, 2.6F + (par2World.rand.nextFloat() - par2World.rand.nextFloat()) * 0.8F);

                            for (int var16 = 0; var16 < 8; ++var16)
                            {
                                par2World.spawnParticle("largesmoke", (double)var13 + Math.random(), (double)var14 + Math.random(), (double)var15 + Math.random(), 0.0D, 0.0D, 0.0D);
                            }
                        }
                        else
                        {
                            par2World.setBlockAndMetadataWithNotify(var13, var14, var15, this.isFull, 0);
                        }

                        if (par3EntityPlayer.capabilities.depleteBuckets)
                        {
                            return par1ItemStack;
                        }

                        return new ItemStack(Item.bucketEmpty);
                    }
                }
            }
            else if (this.isFull == 0 && var12.entityHit instanceof EntityCow)
            {
                return new ItemStack(Item.bucketMilk);
            }

            return par1ItemStack;
        }
    }
}
