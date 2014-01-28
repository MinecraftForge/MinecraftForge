package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockBreakable
{
    public static final int[][] field_150001_a = new int[][] {new int[0], {3, 1}, {2, 0}};
    private static final String __OBFID = "CL_00000284";

    public BlockPortal()
    {
        super("portal", Material.field_151567_E, false);
        this.func_149675_a(true);
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        super.func_149674_a(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, p_149674_5_);

        if (p_149674_1_.provider.isSurfaceWorld() && p_149674_1_.getGameRules().getGameRuleBooleanValue("doMobSpawning") && p_149674_5_.nextInt(2000) < p_149674_1_.difficultySetting.func_151525_a())
        {
            int l;

            for (l = p_149674_3_; !World.func_147466_a(p_149674_1_, p_149674_2_, l, p_149674_4_) && l > 0; --l)
            {
                ;
            }

            if (l > 0 && !p_149674_1_.func_147439_a(p_149674_2_, l + 1, p_149674_4_).func_149721_r())
            {
                Entity entity = ItemMonsterPlacer.spawnCreature(p_149674_1_, 57, (double)p_149674_2_ + 0.5D, (double)l + 1.1D, (double)p_149674_4_ + 0.5D);

                if (entity != null)
                {
                    entity.timeUntilPortal = entity.getPortalCooldown();
                }
            }
        }
    }

    public AxisAlignedBB func_149668_a(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

    public void func_149719_a(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
    {
        int l = func_149999_b(p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_));

        if (l == 0)
        {
            if (p_149719_1_.func_147439_a(p_149719_2_ - 1, p_149719_3_, p_149719_4_) != this && p_149719_1_.func_147439_a(p_149719_2_ + 1, p_149719_3_, p_149719_4_) != this)
            {
                l = 2;
            }
            else
            {
                l = 1;
            }

            if (p_149719_1_ instanceof World && !((World)p_149719_1_).isRemote)
            {
                ((World)p_149719_1_).setBlockMetadataWithNotify(p_149719_2_, p_149719_3_, p_149719_4_, l, 2);
            }
        }

        float f = 0.125F;
        float f1 = 0.125F;

        if (l == 1)
        {
            f = 0.5F;
        }

        if (l == 2)
        {
            f1 = 0.5F;
        }

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f1, 0.5F + f, 1.0F, 0.5F + f1);
    }

    public boolean func_149686_d()
    {
        return false;
    }

    public boolean func_150000_e(World p_150000_1_, int p_150000_2_, int p_150000_3_, int p_150000_4_)
    {
        BlockPortal.Size size = new BlockPortal.Size(p_150000_1_, p_150000_2_, p_150000_3_, p_150000_4_, 1);
        BlockPortal.Size size1 = new BlockPortal.Size(p_150000_1_, p_150000_2_, p_150000_3_, p_150000_4_, 2);

        if (size.func_150860_b() && size.field_150864_e == 0)
        {
            size.func_150859_c();
            return true;
        }
        else if (size1.func_150860_b() && size1.field_150864_e == 0)
        {
            size1.func_150859_c();
            return true;
        }
        else
        {
            return false;
        }
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        int l = func_149999_b(p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_));
        BlockPortal.Size size = new BlockPortal.Size(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, 1);
        BlockPortal.Size size1 = new BlockPortal.Size(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, 2);

        if (l == 1 && (!size.func_150860_b() || size.field_150864_e < size.field_150868_h * size.field_150862_g))
        {
            p_149695_1_.func_147449_b(p_149695_2_, p_149695_3_, p_149695_4_, Blocks.air);
        }
        else if (l == 2 && (!size1.func_150860_b() || size1.field_150864_e < size1.field_150868_h * size1.field_150862_g))
        {
            p_149695_1_.func_147449_b(p_149695_2_, p_149695_3_, p_149695_4_, Blocks.air);
        }
        else if (l == 0 && !size.func_150860_b() && !size1.func_150860_b())
        {
            p_149695_1_.func_147449_b(p_149695_2_, p_149695_3_, p_149695_4_, Blocks.air);
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149646_a(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
    {
        int i1 = 0;

        if (p_149646_1_.func_147439_a(p_149646_2_, p_149646_3_, p_149646_4_) == this)
        {
            i1 = func_149999_b(p_149646_1_.getBlockMetadata(p_149646_2_, p_149646_3_, p_149646_4_));

            if (i1 == 0)
            {
                return false;
            }

            if (i1 == 2 && p_149646_5_ != 5 && p_149646_5_ != 4)
            {
                return false;
            }

            if (i1 == 1 && p_149646_5_ != 3 && p_149646_5_ != 2)
            {
                return false;
            }
        }

        boolean flag = p_149646_1_.func_147439_a(p_149646_2_ - 1, p_149646_3_, p_149646_4_) == this && p_149646_1_.func_147439_a(p_149646_2_ - 2, p_149646_3_, p_149646_4_) != this;
        boolean flag1 = p_149646_1_.func_147439_a(p_149646_2_ + 1, p_149646_3_, p_149646_4_) == this && p_149646_1_.func_147439_a(p_149646_2_ + 2, p_149646_3_, p_149646_4_) != this;
        boolean flag2 = p_149646_1_.func_147439_a(p_149646_2_, p_149646_3_, p_149646_4_ - 1) == this && p_149646_1_.func_147439_a(p_149646_2_, p_149646_3_, p_149646_4_ - 2) != this;
        boolean flag3 = p_149646_1_.func_147439_a(p_149646_2_, p_149646_3_, p_149646_4_ + 1) == this && p_149646_1_.func_147439_a(p_149646_2_, p_149646_3_, p_149646_4_ + 2) != this;
        boolean flag4 = flag || flag1 || i1 == 1;
        boolean flag5 = flag2 || flag3 || i1 == 2;
        return flag4 && p_149646_5_ == 4 ? true : (flag4 && p_149646_5_ == 5 ? true : (flag5 && p_149646_5_ == 2 ? true : flag5 && p_149646_5_ == 3));
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return 0;
    }

    public void func_149670_a(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
    {
        if (p_149670_5_.ridingEntity == null && p_149670_5_.riddenByEntity == null)
        {
            p_149670_5_.setInPortal();
        }
    }

    @SideOnly(Side.CLIENT)
    public int func_149701_w()
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public void func_149734_b(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_)
    {
        if (p_149734_5_.nextInt(100) == 0)
        {
            p_149734_1_.playSound((double)p_149734_2_ + 0.5D, (double)p_149734_3_ + 0.5D, (double)p_149734_4_ + 0.5D, "portal.portal", 0.5F, p_149734_5_.nextFloat() * 0.4F + 0.8F, false);
        }

        for (int l = 0; l < 4; ++l)
        {
            double d0 = (double)((float)p_149734_2_ + p_149734_5_.nextFloat());
            double d1 = (double)((float)p_149734_3_ + p_149734_5_.nextFloat());
            double d2 = (double)((float)p_149734_4_ + p_149734_5_.nextFloat());
            double d3 = 0.0D;
            double d4 = 0.0D;
            double d5 = 0.0D;
            int i1 = p_149734_5_.nextInt(2) * 2 - 1;
            d3 = ((double)p_149734_5_.nextFloat() - 0.5D) * 0.5D;
            d4 = ((double)p_149734_5_.nextFloat() - 0.5D) * 0.5D;
            d5 = ((double)p_149734_5_.nextFloat() - 0.5D) * 0.5D;

            if (p_149734_1_.func_147439_a(p_149734_2_ - 1, p_149734_3_, p_149734_4_) != this && p_149734_1_.func_147439_a(p_149734_2_ + 1, p_149734_3_, p_149734_4_) != this)
            {
                d0 = (double)p_149734_2_ + 0.5D + 0.25D * (double)i1;
                d3 = (double)(p_149734_5_.nextFloat() * 2.0F * (float)i1);
            }
            else
            {
                d2 = (double)p_149734_4_ + 0.5D + 0.25D * (double)i1;
                d5 = (double)(p_149734_5_.nextFloat() * 2.0F * (float)i1);
            }

            p_149734_1_.spawnParticle("portal", d0, d1, d2, d3, d4, d5);
        }
    }

    public static int func_149999_b(int p_149999_0_)
    {
        return p_149999_0_ & 3;
    }

    @SideOnly(Side.CLIENT)
    public Item func_149694_d(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Item.func_150899_d(0);
    }

    public static class Size
        {
            private final World field_150867_a;
            private final int field_150865_b;
            private final int field_150866_c;
            private final int field_150863_d;
            private int field_150864_e = 0;
            private ChunkCoordinates field_150861_f;
            private int field_150862_g;
            private int field_150868_h;
            private static final String __OBFID = "CL_00000285";

            public Size(World p_i45415_1_, int p_i45415_2_, int p_i45415_3_, int p_i45415_4_, int p_i45415_5_)
            {
                this.field_150867_a = p_i45415_1_;
                this.field_150865_b = p_i45415_5_;
                this.field_150863_d = BlockPortal.field_150001_a[p_i45415_5_][0];
                this.field_150866_c = BlockPortal.field_150001_a[p_i45415_5_][1];

                for (int i1 = p_i45415_3_; p_i45415_3_ > i1 - 21 && p_i45415_3_ > 0 && this.func_150857_a(p_i45415_1_.func_147439_a(p_i45415_2_, p_i45415_3_ - 1, p_i45415_4_)); --p_i45415_3_)
                {
                    ;
                }

                int j1 = this.func_150853_a(p_i45415_2_, p_i45415_3_, p_i45415_4_, this.field_150863_d) - 1;

                if (j1 >= 0)
                {
                    this.field_150861_f = new ChunkCoordinates(p_i45415_2_ + j1 * Direction.offsetX[this.field_150863_d], p_i45415_3_, p_i45415_4_ + j1 * Direction.offsetZ[this.field_150863_d]);
                    this.field_150868_h = this.func_150853_a(this.field_150861_f.posX, this.field_150861_f.posY, this.field_150861_f.posZ, this.field_150866_c);

                    if (this.field_150868_h < 2 || this.field_150868_h > 21)
                    {
                        this.field_150861_f = null;
                        this.field_150868_h = 0;
                    }
                }

                if (this.field_150861_f != null)
                {
                    this.field_150862_g = this.func_150858_a();
                }
            }

            protected int func_150853_a(int p_150853_1_, int p_150853_2_, int p_150853_3_, int p_150853_4_)
            {
                int j1 = Direction.offsetX[p_150853_4_];
                int k1 = Direction.offsetZ[p_150853_4_];
                int i1;
                Block block;

                for (i1 = 0; i1 < 22; ++i1)
                {
                    block = this.field_150867_a.func_147439_a(p_150853_1_ + j1 * i1, p_150853_2_, p_150853_3_ + k1 * i1);

                    if (!this.func_150857_a(block))
                    {
                        break;
                    }

                    Block block1 = this.field_150867_a.func_147439_a(p_150853_1_ + j1 * i1, p_150853_2_ - 1, p_150853_3_ + k1 * i1);

                    if (block1 != Blocks.obsidian)
                    {
                        break;
                    }
                }

                block = this.field_150867_a.func_147439_a(p_150853_1_ + j1 * i1, p_150853_2_, p_150853_3_ + k1 * i1);
                return block == Blocks.obsidian ? i1 : 0;
            }

            protected int func_150858_a()
            {
                int i;
                int j;
                int k;
                int l;
                label56:

                for (this.field_150862_g = 0; this.field_150862_g < 21; ++this.field_150862_g)
                {
                    i = this.field_150861_f.posY + this.field_150862_g;

                    for (j = 0; j < this.field_150868_h; ++j)
                    {
                        k = this.field_150861_f.posX + j * Direction.offsetX[BlockPortal.field_150001_a[this.field_150865_b][1]];
                        l = this.field_150861_f.posZ + j * Direction.offsetZ[BlockPortal.field_150001_a[this.field_150865_b][1]];
                        Block block = this.field_150867_a.func_147439_a(k, i, l);

                        if (!this.func_150857_a(block))
                        {
                            break label56;
                        }

                        if (block == Blocks.portal)
                        {
                            ++this.field_150864_e;
                        }

                        if (j == 0)
                        {
                            block = this.field_150867_a.func_147439_a(k + Direction.offsetX[BlockPortal.field_150001_a[this.field_150865_b][0]], i, l + Direction.offsetZ[BlockPortal.field_150001_a[this.field_150865_b][0]]);

                            if (block != Blocks.obsidian)
                            {
                                break label56;
                            }
                        }
                        else if (j == this.field_150868_h - 1)
                        {
                            block = this.field_150867_a.func_147439_a(k + Direction.offsetX[BlockPortal.field_150001_a[this.field_150865_b][1]], i, l + Direction.offsetZ[BlockPortal.field_150001_a[this.field_150865_b][1]]);

                            if (block != Blocks.obsidian)
                            {
                                break label56;
                            }
                        }
                    }
                }

                for (i = 0; i < this.field_150868_h; ++i)
                {
                    j = this.field_150861_f.posX + i * Direction.offsetX[BlockPortal.field_150001_a[this.field_150865_b][1]];
                    k = this.field_150861_f.posY + this.field_150862_g;
                    l = this.field_150861_f.posZ + i * Direction.offsetZ[BlockPortal.field_150001_a[this.field_150865_b][1]];

                    if (this.field_150867_a.func_147439_a(j, k, l) != Blocks.obsidian)
                    {
                        this.field_150862_g = 0;
                        break;
                    }
                }

                if (this.field_150862_g <= 21 && this.field_150862_g >= 3)
                {
                    return this.field_150862_g;
                }
                else
                {
                    this.field_150861_f = null;
                    this.field_150868_h = 0;
                    this.field_150862_g = 0;
                    return 0;
                }
            }

            protected boolean func_150857_a(Block p_150857_1_)
            {
                return p_150857_1_.field_149764_J == Material.field_151579_a || p_150857_1_ == Blocks.fire || p_150857_1_ == Blocks.portal;
            }

            public boolean func_150860_b()
            {
                return this.field_150861_f != null && this.field_150868_h >= 2 && this.field_150868_h <= 21 && this.field_150862_g >= 3 && this.field_150862_g <= 21;
            }

            public void func_150859_c()
            {
                for (int i = 0; i < this.field_150868_h; ++i)
                {
                    int j = this.field_150861_f.posX + Direction.offsetX[this.field_150866_c] * i;
                    int k = this.field_150861_f.posZ + Direction.offsetZ[this.field_150866_c] * i;

                    for (int l = 0; l < this.field_150862_g; ++l)
                    {
                        int i1 = this.field_150861_f.posY + l;
                        this.field_150867_a.func_147465_d(j, i1, k, Blocks.portal, this.field_150865_b, 2);
                    }
                }
            }
        }
}