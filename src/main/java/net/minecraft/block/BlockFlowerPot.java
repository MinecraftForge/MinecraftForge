package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.World;

public class BlockFlowerPot extends BlockContainer
{
    private static final String __OBFID = "CL_00000247";

    public BlockFlowerPot()
    {
        super(Material.field_151594_q);
        this.func_149683_g();
    }

    public void func_149683_g()
    {
        float f = 0.375F;
        float f1 = f / 2.0F;
        this.func_149676_a(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, f, 0.5F + f1);
    }

    public boolean func_149662_c()
    {
        return false;
    }

    public int func_149645_b()
    {
        return 33;
    }

    public boolean func_149686_d()
    {
        return false;
    }

    public boolean func_149727_a(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        ItemStack itemstack = p_149727_5_.inventory.getCurrentItem();

        if (itemstack != null && itemstack.getItem() instanceof ItemBlock)
        {
            if (p_149727_1_.getBlockMetadata(p_149727_2_, p_149727_3_, p_149727_4_) != 0)
            {
                return false;
            }
            else
            {
                TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_);

                if (tileentityflowerpot != null)
                {
                    Block block = Block.func_149634_a(itemstack.getItem());

                    if (!this.func_149928_a(block, itemstack.getItemDamage()))
                    {
                        return false;
                    }
                    else
                    {
                        tileentityflowerpot.func_145964_a(itemstack.getItem(), itemstack.getItemDamage());
                        tileentityflowerpot.onInventoryChanged();

                        if (!p_149727_1_.setBlockMetadataWithNotify(p_149727_2_, p_149727_3_, p_149727_4_, itemstack.getItemDamage(), 2))
                        {
                            p_149727_1_.func_147471_g(p_149727_2_, p_149727_3_, p_149727_4_);
                        }

                        if (!p_149727_5_.capabilities.isCreativeMode && --itemstack.stackSize <= 0)
                        {
                            p_149727_5_.inventory.setInventorySlotContents(p_149727_5_.inventory.currentItem, (ItemStack)null);
                        }

                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

    private boolean func_149928_a(Block p_149928_1_, int p_149928_2_)
    {
        return p_149928_1_ != Blocks.yellow_flower && p_149928_1_ != Blocks.red_flower && p_149928_1_ != Blocks.cactus && p_149928_1_ != Blocks.brown_mushroom && p_149928_1_ != Blocks.red_mushroom && p_149928_1_ != Blocks.sapling && p_149928_1_ != Blocks.deadbush ? p_149928_1_ == Blocks.tallgrass && p_149928_2_ == 2 : true;
    }

    @SideOnly(Side.CLIENT)
    public Item func_149694_d(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(p_149694_1_, p_149694_2_, p_149694_3_, p_149694_4_);
        return tileentityflowerpot != null && tileentityflowerpot.func_145965_a() != null ? tileentityflowerpot.func_145965_a() : Items.flower_pot;
    }

    public int func_149643_k(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_)
    {
        TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(p_149643_1_, p_149643_2_, p_149643_3_, p_149643_4_);
        return tileentityflowerpot != null && tileentityflowerpot.func_145965_a() != null ? tileentityflowerpot.func_145966_b() : 0;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149648_K()
    {
        return true;
    }

    public boolean func_149742_c(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return super.func_149742_c(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_) && World.func_147466_a(p_149742_1_, p_149742_2_, p_149742_3_ - 1, p_149742_4_);
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        if (!World.func_147466_a(p_149695_1_, p_149695_2_, p_149695_3_ - 1, p_149695_4_))
        {
            this.func_149697_b(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_), 0);
            p_149695_1_.func_147468_f(p_149695_2_, p_149695_3_, p_149695_4_);
        }
    }

    public void func_149690_a(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_, int p_149690_5_, float p_149690_6_, int p_149690_7_)
    {
        super.func_149690_a(p_149690_1_, p_149690_2_, p_149690_3_, p_149690_4_, p_149690_5_, p_149690_6_, p_149690_7_);
        TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(p_149690_1_, p_149690_2_, p_149690_3_, p_149690_4_);

        if (tileentityflowerpot != null && tileentityflowerpot.func_145965_a() != null)
        {
            this.func_149642_a(p_149690_1_, p_149690_2_, p_149690_3_, p_149690_4_, new ItemStack(tileentityflowerpot.func_145965_a(), 1, tileentityflowerpot.func_145966_b()));
        }
    }

    public void func_149749_a(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_);

        if (tileentityflowerpot != null && tileentityflowerpot.func_145965_a() != null)
        {
            this.func_149642_a(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, new ItemStack(tileentityflowerpot.func_145965_a(), 1, tileentityflowerpot.func_145966_b()));
        }

        super.func_149749_a(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
    }

    public void func_149681_a(World p_149681_1_, int p_149681_2_, int p_149681_3_, int p_149681_4_, int p_149681_5_, EntityPlayer p_149681_6_)
    {
        super.func_149681_a(p_149681_1_, p_149681_2_, p_149681_3_, p_149681_4_, p_149681_5_, p_149681_6_);

        if (p_149681_6_.capabilities.isCreativeMode)
        {
            TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(p_149681_1_, p_149681_2_, p_149681_3_, p_149681_4_);

            if (tileentityflowerpot != null)
            {
                tileentityflowerpot.func_145964_a(Item.func_150899_d(0), 0);
            }
        }
    }

    public Item func_149650_a(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Items.flower_pot;
    }

    private TileEntityFlowerPot func_149929_e(World p_149929_1_, int p_149929_2_, int p_149929_3_, int p_149929_4_)
    {
        TileEntity tileentity = p_149929_1_.func_147438_o(p_149929_2_, p_149929_3_, p_149929_4_);
        return tileentity != null && tileentity instanceof TileEntityFlowerPot ? (TileEntityFlowerPot)tileentity : null;
    }

    public TileEntity func_149915_a(World p_149915_1_, int p_149915_2_)
    {
        Object object = null;
        byte b0 = 0;

        switch (p_149915_2_)
        {
            case 1:
                object = Blocks.red_flower;
                b0 = 0;
                break;
            case 2:
                object = Blocks.yellow_flower;
                break;
            case 3:
                object = Blocks.sapling;
                b0 = 0;
                break;
            case 4:
                object = Blocks.sapling;
                b0 = 1;
                break;
            case 5:
                object = Blocks.sapling;
                b0 = 2;
                break;
            case 6:
                object = Blocks.sapling;
                b0 = 3;
                break;
            case 7:
                object = Blocks.red_mushroom;
                break;
            case 8:
                object = Blocks.brown_mushroom;
                break;
            case 9:
                object = Blocks.cactus;
                break;
            case 10:
                object = Blocks.deadbush;
                break;
            case 11:
                object = Blocks.tallgrass;
                b0 = 2;
                break;
            case 12:
                object = Blocks.sapling;
                b0 = 4;
                break;
            case 13:
                object = Blocks.sapling;
                b0 = 5;
        }

        return new TileEntityFlowerPot(Item.func_150898_a((Block)object), b0);
    }
}