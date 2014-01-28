package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class ItemEnderEye extends Item
{
    private static final String __OBFID = "CL_00000026";

    public ItemEnderEye()
    {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    // JAVADOC METHOD $$ func_77648_a
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        Block block = par3World.func_147439_a(par4, par5, par6);
        int i1 = par3World.getBlockMetadata(par4, par5, par6);

        if (par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && block == Blocks.end_portal_frame && !BlockEndPortalFrame.func_150020_b(i1))
        {
            if (par3World.isRemote)
            {
                return true;
            }
            else
            {
                par3World.setBlockMetadataWithNotify(par4, par5, par6, i1 + 4, 2);
                par3World.func_147453_f(par4, par5, par6, Blocks.end_portal_frame);
                --par1ItemStack.stackSize;
                int j1;

                for (j1 = 0; j1 < 16; ++j1)
                {
                    double d0 = (double)((float)par4 + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F);
                    double d1 = (double)((float)par5 + 0.8125F);
                    double d2 = (double)((float)par6 + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F);
                    double d3 = 0.0D;
                    double d4 = 0.0D;
                    double d5 = 0.0D;
                    par3World.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
                }

                j1 = i1 & 3;
                int j2 = 0;
                int k1 = 0;
                boolean flag1 = false;
                boolean flag = true;
                int k2 = Direction.rotateRight[j1];
                int l1;
                int i2;
                int l2;

                for (l1 = -2; l1 <= 2; ++l1)
                {
                    l2 = par4 + Direction.offsetX[k2] * l1;
                    i2 = par6 + Direction.offsetZ[k2] * l1;

                    if (par3World.func_147439_a(l2, par5, i2) == Blocks.end_portal_frame)
                    {
                        if (!BlockEndPortalFrame.func_150020_b(par3World.getBlockMetadata(l2, par5, i2)))
                        {
                            flag = false;
                            break;
                        }

                        k1 = l1;

                        if (!flag1)
                        {
                            j2 = l1;
                            flag1 = true;
                        }
                    }
                }

                if (flag && k1 == j2 + 2)
                {
                    for (l1 = j2; l1 <= k1; ++l1)
                    {
                        l2 = par4 + Direction.offsetX[k2] * l1;
                        i2 = par6 + Direction.offsetZ[k2] * l1;
                        l2 += Direction.offsetX[j1] * 4;
                        i2 += Direction.offsetZ[j1] * 4;

                        if (par3World.func_147439_a(l2, par5, i2) != Blocks.end_portal_frame || !BlockEndPortalFrame.func_150020_b(par3World.getBlockMetadata(l2, par5, i2)))
                        {
                            flag = false;
                            break;
                        }
                    }

                    int i3;

                    for (l1 = j2 - 1; l1 <= k1 + 1; l1 += 4)
                    {
                        for (l2 = 1; l2 <= 3; ++l2)
                        {
                            i2 = par4 + Direction.offsetX[k2] * l1;
                            i3 = par6 + Direction.offsetZ[k2] * l1;
                            i2 += Direction.offsetX[j1] * l2;
                            i3 += Direction.offsetZ[j1] * l2;

                            if (par3World.func_147439_a(i2, par5, i3) != Blocks.end_portal_frame || !BlockEndPortalFrame.func_150020_b(par3World.getBlockMetadata(i2, par5, i3)))
                            {
                                flag = false;
                                break;
                            }
                        }
                    }

                    if (flag)
                    {
                        for (l1 = j2; l1 <= k1; ++l1)
                        {
                            for (l2 = 1; l2 <= 3; ++l2)
                            {
                                i2 = par4 + Direction.offsetX[k2] * l1;
                                i3 = par6 + Direction.offsetZ[k2] * l1;
                                i2 += Direction.offsetX[j1] * l2;
                                i3 += Direction.offsetZ[j1] * l2;
                                par3World.func_147465_d(i2, par5, i3, Blocks.end_portal, 0, 2);
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

    // JAVADOC METHOD $$ func_77659_a
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, false);

        if (movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && par2World.func_147439_a(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ) == Blocks.end_portal_frame)
        {
            return par1ItemStack;
        }
        else
        {
            if (!par2World.isRemote)
            {
                ChunkPosition chunkposition = par2World.func_147440_b("Stronghold", (int)par3EntityPlayer.posX, (int)par3EntityPlayer.posY, (int)par3EntityPlayer.posZ);

                if (chunkposition != null)
                {
                    EntityEnderEye entityendereye = new EntityEnderEye(par2World, par3EntityPlayer.posX, par3EntityPlayer.posY + 1.62D - (double)par3EntityPlayer.yOffset, par3EntityPlayer.posZ);
                    entityendereye.moveTowards((double)chunkposition.field_151329_a, chunkposition.field_151327_b, (double)chunkposition.field_151328_c);
                    par2World.spawnEntityInWorld(entityendereye);
                    par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
                    par2World.playAuxSFXAtEntity((EntityPlayer)null, 1002, (int)par3EntityPlayer.posX, (int)par3EntityPlayer.posY, (int)par3EntityPlayer.posZ, 0);

                    if (!par3EntityPlayer.capabilities.isCreativeMode)
                    {
                        --par1ItemStack.stackSize;
                    }
                }
            }

            return par1ItemStack;
        }
    }
}