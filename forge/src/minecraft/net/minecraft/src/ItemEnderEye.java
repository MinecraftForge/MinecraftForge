package net.minecraft.src;

public class ItemEnderEye extends Item
{
    public ItemEnderEye(int par1)
    {
        super(par1);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS !
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7)
    {
        int var8 = par3World.getBlockId(par4, par5, par6);
        int var9 = par3World.getBlockMetadata(par4, par5, par6);

        if (par2EntityPlayer.canPlayerEdit(par4, par5, par6) && var8 == Block.endPortalFrame.blockID && !BlockEndPortalFrame.isEnderEyeInserted(var9))
        {
            if (par3World.isRemote)
            {
                return true;
            }
            else
            {
                par3World.setBlockMetadataWithNotify(par4, par5, par6, var9 + 4);
                --par1ItemStack.stackSize;
                int var10;

                for (var10 = 0; var10 < 16; ++var10)
                {
                    double var11 = (double)((float)par4 + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F);
                    double var13 = (double)((float)par5 + 0.8125F);
                    double var15 = (double)((float)par6 + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F);
                    double var17 = 0.0D;
                    double var19 = 0.0D;
                    double var21 = 0.0D;
                    par3World.spawnParticle("smoke", var11, var13, var15, var17, var19, var21);
                }

                var10 = var9 & 3;
                int var23 = 0;
                int var12 = 0;
                boolean var24 = false;
                boolean var14 = true;
                int var25 = Direction.enderEyeMetaToDirection[var10];
                int var16;
                int var18;
                int var20;
                int var27;
                int var26;

                for (var16 = -2; var16 <= 2; ++var16)
                {
                    var26 = par4 + Direction.offsetX[var25] * var16;
                    var18 = par6 + Direction.offsetZ[var25] * var16;
                    var27 = par3World.getBlockId(var26, par5, var18);

                    if (var27 == Block.endPortalFrame.blockID)
                    {
                        var20 = par3World.getBlockMetadata(var26, par5, var18);

                        if (!BlockEndPortalFrame.isEnderEyeInserted(var20))
                        {
                            var14 = false;
                            break;
                        }

                        if (!var24)
                        {
                            var23 = var16;
                            var12 = var16;
                            var24 = true;
                        }
                        else
                        {
                            var12 = var16;
                        }
                    }
                }

                if (var14 && var12 == var23 + 2)
                {
                    for (var16 = var23; var16 <= var12; ++var16)
                    {
                        var26 = par4 + Direction.offsetX[var25] * var16;
                        var18 = par6 + Direction.offsetZ[var25] * var16;
                        var26 += Direction.offsetX[var10] * 4;
                        var18 += Direction.offsetZ[var10] * 4;
                        var27 = par3World.getBlockId(var26, par5, var18);
                        var20 = par3World.getBlockMetadata(var26, par5, var18);

                        if (var27 != Block.endPortalFrame.blockID || !BlockEndPortalFrame.isEnderEyeInserted(var20))
                        {
                            var14 = false;
                            break;
                        }
                    }

                    for (var16 = var23 - 1; var16 <= var12 + 1; var16 += 4)
                    {
                        for (var26 = 1; var26 <= 3; ++var26)
                        {
                            var18 = par4 + Direction.offsetX[var25] * var16;
                            var27 = par6 + Direction.offsetZ[var25] * var16;
                            var18 += Direction.offsetX[var10] * var26;
                            var27 += Direction.offsetZ[var10] * var26;
                            var20 = par3World.getBlockId(var18, par5, var27);
                            int var28 = par3World.getBlockMetadata(var18, par5, var27);

                            if (var20 != Block.endPortalFrame.blockID || !BlockEndPortalFrame.isEnderEyeInserted(var28))
                            {
                                var14 = false;
                                break;
                            }
                        }
                    }

                    if (var14)
                    {
                        for (var16 = var23; var16 <= var12; ++var16)
                        {
                            for (var26 = 1; var26 <= 3; ++var26)
                            {
                                var18 = par4 + Direction.offsetX[var25] * var16;
                                var27 = par6 + Direction.offsetZ[var25] * var16;
                                var18 += Direction.offsetX[var10] * var26;
                                var27 += Direction.offsetZ[var10] * var26;
                                par3World.setBlockWithNotify(var18, par5, var27, Block.endPortal.blockID);
                            }
                        }
                    }
                }

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        MovingObjectPosition var4 = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, false);

        if (var4 != null && var4.typeOfHit == EnumMovingObjectType.TILE)
        {
            int var5 = par2World.getBlockId(var4.blockX, var4.blockY, var4.blockZ);

            if (var5 == Block.endPortalFrame.blockID)
            {
                return par1ItemStack;
            }
        }

        if (!par2World.isRemote)
        {
            ChunkPosition var7 = par2World.findClosestStructure("Stronghold", (int)par3EntityPlayer.posX, (int)par3EntityPlayer.posY, (int)par3EntityPlayer.posZ);

            if (var7 != null)
            {
                EntityEnderEye var6 = new EntityEnderEye(par2World, par3EntityPlayer.posX, par3EntityPlayer.posY + 1.62D - (double)par3EntityPlayer.yOffset, par3EntityPlayer.posZ);
                var6.func_40090_a((double)var7.x, var7.y, (double)var7.z);
                par2World.spawnEntityInWorld(var6);
                par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
                par2World.playAuxSFXAtEntity((EntityPlayer)null, 1002, (int)par3EntityPlayer.posX, (int)par3EntityPlayer.posY, (int)par3EntityPlayer.posZ, 0);

                if (!par3EntityPlayer.capabilities.depleteBuckets)
                {
                    --par1ItemStack.stackSize;
                }
            }
        }

        return par1ItemStack;
    }
}
