package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWire extends Block
{
    private static final String __OBFID = "CL_00000328";

    public BlockTripWire()
    {
        super(Material.field_151594_q);
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
        this.func_149675_a(true);
    }

    public int func_149738_a(World p_149738_1_)
    {
        return 10;
    }

    public AxisAlignedBB func_149668_a(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

    public boolean func_149662_c()
    {
        return false;
    }

    public boolean func_149686_d()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public int func_149701_w()
    {
        return 1;
    }

    public int func_149645_b()
    {
        return 30;
    }

    public Item func_149650_a(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Items.string;
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        int l = p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_);
        boolean flag = (l & 2) == 2;
        boolean flag1 = !World.func_147466_a(p_149695_1_, p_149695_2_, p_149695_3_ - 1, p_149695_4_);

        if (flag != flag1)
        {
            this.func_149697_b(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, l, 0);
            p_149695_1_.func_147468_f(p_149695_2_, p_149695_3_, p_149695_4_);
        }
    }

    public void func_149719_a(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
    {
        int l = p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_);
        boolean flag = (l & 4) == 4;
        boolean flag1 = (l & 2) == 2;

        if (!flag1)
        {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
        }
        else if (!flag)
        {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
        else
        {
            this.func_149676_a(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
        }
    }

    @SideOnly(Side.CLIENT)
    public Item func_149694_d(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Items.string;
    }

    public void func_149726_b(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        int l = World.func_147466_a(p_149726_1_, p_149726_2_, p_149726_3_ - 1, p_149726_4_) ? 0 : 2;
        p_149726_1_.setBlockMetadataWithNotify(p_149726_2_, p_149726_3_, p_149726_4_, l, 3);
        this.func_150138_a(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_, l);
    }

    public void func_149749_a(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        this.func_150138_a(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_6_ | 1);
    }

    public void func_149681_a(World p_149681_1_, int p_149681_2_, int p_149681_3_, int p_149681_4_, int p_149681_5_, EntityPlayer p_149681_6_)
    {
        if (!p_149681_1_.isRemote)
        {
            if (p_149681_6_.getCurrentEquippedItem() != null && p_149681_6_.getCurrentEquippedItem().getItem() == Items.shears)
            {
                p_149681_1_.setBlockMetadataWithNotify(p_149681_2_, p_149681_3_, p_149681_4_, p_149681_5_ | 8, 4);
            }
        }
    }

    private void func_150138_a(World p_150138_1_, int p_150138_2_, int p_150138_3_, int p_150138_4_, int p_150138_5_)
    {
        int i1 = 0;

        while (i1 < 2)
        {
            int j1 = 1;

            while (true)
            {
                if (j1 < 42)
                {
                    int k1 = p_150138_2_ + Direction.offsetX[i1] * j1;
                    int l1 = p_150138_4_ + Direction.offsetZ[i1] * j1;
                    Block block = p_150138_1_.func_147439_a(k1, p_150138_3_, l1);

                    if (block == Blocks.tripwire_hook)
                    {
                        int i2 = p_150138_1_.getBlockMetadata(k1, p_150138_3_, l1) & 3;

                        if (i2 == Direction.rotateOpposite[i1])
                        {
                            Blocks.tripwire_hook.func_150136_a(p_150138_1_, k1, p_150138_3_, l1, false, p_150138_1_.getBlockMetadata(k1, p_150138_3_, l1), true, j1, p_150138_5_);
                        }
                    }
                    else if (block == Blocks.tripwire)
                    {
                        ++j1;
                        continue;
                    }
                }

                ++i1;
                break;
            }
        }
    }

    public void func_149670_a(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
    {
        if (!p_149670_1_.isRemote)
        {
            if ((p_149670_1_.getBlockMetadata(p_149670_2_, p_149670_3_, p_149670_4_) & 1) != 1)
            {
                this.func_150140_e(p_149670_1_, p_149670_2_, p_149670_3_, p_149670_4_);
            }
        }
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        if (!p_149674_1_.isRemote)
        {
            if ((p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_) & 1) == 1)
            {
                this.func_150140_e(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_);
            }
        }
    }

    private void func_150140_e(World p_150140_1_, int p_150140_2_, int p_150140_3_, int p_150140_4_)
    {
        int l = p_150140_1_.getBlockMetadata(p_150140_2_, p_150140_3_, p_150140_4_);
        boolean flag = (l & 1) == 1;
        boolean flag1 = false;
        List list = p_150140_1_.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.getAABBPool().getAABB((double)p_150140_2_ + this.field_149759_B, (double)p_150140_3_ + this.field_149760_C, (double)p_150140_4_ + this.field_149754_D, (double)p_150140_2_ + this.field_149755_E, (double)p_150140_3_ + this.field_149756_F, (double)p_150140_4_ + this.field_149757_G));

        if (!list.isEmpty())
        {
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                Entity entity = (Entity)iterator.next();

                if (!entity.func_145773_az())
                {
                    flag1 = true;
                    break;
                }
            }
        }

        if (flag1 && !flag)
        {
            l |= 1;
        }

        if (!flag1 && flag)
        {
            l &= -2;
        }

        if (flag1 != flag)
        {
            p_150140_1_.setBlockMetadataWithNotify(p_150140_2_, p_150140_3_, p_150140_4_, l, 3);
            this.func_150138_a(p_150140_1_, p_150140_2_, p_150140_3_, p_150140_4_, l);
        }

        if (flag1)
        {
            p_150140_1_.func_147464_a(p_150140_2_, p_150140_3_, p_150140_4_, this, this.func_149738_a(p_150140_1_));
        }
    }

    @SideOnly(Side.CLIENT)
    public static boolean func_150139_a(IBlockAccess p_150139_0_, int p_150139_1_, int p_150139_2_, int p_150139_3_, int p_150139_4_, int p_150139_5_)
    {
        int j1 = p_150139_1_ + Direction.offsetX[p_150139_5_];
        int k1 = p_150139_3_ + Direction.offsetZ[p_150139_5_];
        Block block = p_150139_0_.func_147439_a(j1, p_150139_2_, k1);
        boolean flag = (p_150139_4_ & 2) == 2;
        int l1;

        if (block == Blocks.tripwire_hook)
        {
            l1 = p_150139_0_.getBlockMetadata(j1, p_150139_2_, k1);
            int i2 = l1 & 3;
            return i2 == Direction.rotateOpposite[p_150139_5_];
        }
        else if (block == Blocks.tripwire)
        {
            l1 = p_150139_0_.getBlockMetadata(j1, p_150139_2_, k1);
            boolean flag1 = (l1 & 2) == 2;
            return flag == flag1;
        }
        else
        {
            return false;
        }
    }
}