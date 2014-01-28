package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonBase extends Block
{
    private final boolean field_150082_a;
    @SideOnly(Side.CLIENT)
    private IIcon field_150081_b;
    @SideOnly(Side.CLIENT)
    private IIcon field_150083_M;
    @SideOnly(Side.CLIENT)
    private IIcon field_150084_N;
    private static final String __OBFID = "CL_00000366";

    public BlockPistonBase(boolean p_i45443_1_)
    {
        super(Material.piston);
        this.field_150082_a = p_i45443_1_;
        this.func_149672_a(field_149780_i);
        this.func_149711_c(0.5F);
        this.func_149647_a(CreativeTabs.tabRedstone);
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_150073_e()
    {
        return this.field_150084_N;
    }

    @SideOnly(Side.CLIENT)
    public void func_150070_b(float p_150070_1_, float p_150070_2_, float p_150070_3_, float p_150070_4_, float p_150070_5_, float p_150070_6_)
    {
        this.func_149676_a(p_150070_1_, p_150070_2_, p_150070_3_, p_150070_4_, p_150070_5_, p_150070_6_);
    }

    public int func_149645_b()
    {
        return 16;
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        int k = func_150076_b(p_149691_2_);
        return k > 5 ? this.field_150084_N : (p_149691_1_ == k ? (!func_150075_c(p_149691_2_) && this.field_149759_B <= 0.0D && this.field_149760_C <= 0.0D && this.field_149754_D <= 0.0D && this.field_149755_E >= 1.0D && this.field_149756_F >= 1.0D && this.field_149757_G >= 1.0D ? this.field_150084_N : this.field_150081_b) : (p_149691_1_ == Facing.oppositeSide[k] ? this.field_150083_M : this.field_149761_L));
    }

    @SideOnly(Side.CLIENT)
    public static IIcon func_150074_e(String p_150074_0_)
    {
        return p_150074_0_ == "piston_side" ? Blocks.piston.field_149761_L : (p_150074_0_ == "piston_top_normal" ? Blocks.piston.field_150084_N : (p_150074_0_ == "piston_top_sticky" ? Blocks.sticky_piston.field_150084_N : (p_150074_0_ == "piston_inner" ? Blocks.piston.field_150081_b : null)));
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_149761_L = p_149651_1_.registerIcon("piston_side");
        this.field_150084_N = p_149651_1_.registerIcon(this.field_150082_a ? "piston_top_sticky" : "piston_top_normal");
        this.field_150081_b = p_149651_1_.registerIcon("piston_inner");
        this.field_150083_M = p_149651_1_.registerIcon("piston_bottom");
    }

    public boolean func_149662_c()
    {
        return false;
    }

    public boolean func_149727_a(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        return false;
    }

    public void func_149689_a(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_)
    {
        int l = func_150071_a(p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_);
        p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, l, 2);

        if (!p_149689_1_.isRemote)
        {
            this.func_150078_e(p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_);
        }
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        if (!p_149695_1_.isRemote)
        {
            this.func_150078_e(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_);
        }
    }

    public void func_149726_b(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        if (!p_149726_1_.isRemote && p_149726_1_.func_147438_o(p_149726_2_, p_149726_3_, p_149726_4_) == null)
        {
            this.func_150078_e(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
        }
    }

    private void func_150078_e(World p_150078_1_, int p_150078_2_, int p_150078_3_, int p_150078_4_)
    {
        int l = p_150078_1_.getBlockMetadata(p_150078_2_, p_150078_3_, p_150078_4_);
        int i1 = func_150076_b(l);

        if (i1 != 7)
        {
            boolean flag = this.func_150072_a(p_150078_1_, p_150078_2_, p_150078_3_, p_150078_4_, i1);

            if (flag && !func_150075_c(l))
            {
                if (func_150077_h(p_150078_1_, p_150078_2_, p_150078_3_, p_150078_4_, i1))
                {
                    p_150078_1_.func_147452_c(p_150078_2_, p_150078_3_, p_150078_4_, this, 0, i1);
                }
            }
            else if (!flag && func_150075_c(l))
            {
                p_150078_1_.setBlockMetadataWithNotify(p_150078_2_, p_150078_3_, p_150078_4_, i1, 2);
                p_150078_1_.func_147452_c(p_150078_2_, p_150078_3_, p_150078_4_, this, 1, i1);
            }
        }
    }

    private boolean func_150072_a(World p_150072_1_, int p_150072_2_, int p_150072_3_, int p_150072_4_, int p_150072_5_)
    {
        return p_150072_5_ != 0 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ - 1, p_150072_4_, 0) ? true : (p_150072_5_ != 1 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 1, p_150072_4_, 1) ? true : (p_150072_5_ != 2 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_, p_150072_4_ - 1, 2) ? true : (p_150072_5_ != 3 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_, p_150072_4_ + 1, 3) ? true : (p_150072_5_ != 5 && p_150072_1_.getIndirectPowerOutput(p_150072_2_ + 1, p_150072_3_, p_150072_4_, 5) ? true : (p_150072_5_ != 4 && p_150072_1_.getIndirectPowerOutput(p_150072_2_ - 1, p_150072_3_, p_150072_4_, 4) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_, p_150072_4_, 0) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 2, p_150072_4_, 1) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 1, p_150072_4_ - 1, 2) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 1, p_150072_4_ + 1, 3) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_ - 1, p_150072_3_ + 1, p_150072_4_, 4) ? true : p_150072_1_.getIndirectPowerOutput(p_150072_2_ + 1, p_150072_3_ + 1, p_150072_4_, 5)))))))))));
    }

    public boolean func_149696_a(World p_149696_1_, int p_149696_2_, int p_149696_3_, int p_149696_4_, int p_149696_5_, int p_149696_6_)
    {
        if (!p_149696_1_.isRemote)
        {
            boolean flag = this.func_150072_a(p_149696_1_, p_149696_2_, p_149696_3_, p_149696_4_, p_149696_6_);

            if (flag && p_149696_5_ == 1)
            {
                p_149696_1_.setBlockMetadataWithNotify(p_149696_2_, p_149696_3_, p_149696_4_, p_149696_6_ | 8, 2);
                return false;
            }

            if (!flag && p_149696_5_ == 0)
            {
                return false;
            }
        }

        if (p_149696_5_ == 0)
        {
            if (!this.func_150079_i(p_149696_1_, p_149696_2_, p_149696_3_, p_149696_4_, p_149696_6_))
            {
                return false;
            }

            p_149696_1_.setBlockMetadataWithNotify(p_149696_2_, p_149696_3_, p_149696_4_, p_149696_6_ | 8, 2);
            p_149696_1_.playSoundEffect((double)p_149696_2_ + 0.5D, (double)p_149696_3_ + 0.5D, (double)p_149696_4_ + 0.5D, "tile.piston.out", 0.5F, p_149696_1_.rand.nextFloat() * 0.25F + 0.6F);
        }
        else if (p_149696_5_ == 1)
        {
            TileEntity tileentity1 = p_149696_1_.func_147438_o(p_149696_2_ + Facing.offsetsXForSide[p_149696_6_], p_149696_3_ + Facing.offsetsYForSide[p_149696_6_], p_149696_4_ + Facing.offsetsZForSide[p_149696_6_]);

            if (tileentity1 instanceof TileEntityPiston)
            {
                ((TileEntityPiston)tileentity1).func_145866_f();
            }

            p_149696_1_.func_147465_d(p_149696_2_, p_149696_3_, p_149696_4_, Blocks.piston_extension, p_149696_6_, 3);
            p_149696_1_.func_147455_a(p_149696_2_, p_149696_3_, p_149696_4_, BlockPistonMoving.func_149962_a(this, p_149696_6_, p_149696_6_, false, true));

            if (this.field_150082_a)
            {
                int j1 = p_149696_2_ + Facing.offsetsXForSide[p_149696_6_] * 2;
                int k1 = p_149696_3_ + Facing.offsetsYForSide[p_149696_6_] * 2;
                int l1 = p_149696_4_ + Facing.offsetsZForSide[p_149696_6_] * 2;
                Block block = p_149696_1_.func_147439_a(j1, k1, l1);
                int i2 = p_149696_1_.getBlockMetadata(j1, k1, l1);
                boolean flag1 = false;

                if (block == Blocks.piston_extension)
                {
                    TileEntity tileentity = p_149696_1_.func_147438_o(j1, k1, l1);

                    if (tileentity instanceof TileEntityPiston)
                    {
                        TileEntityPiston tileentitypiston = (TileEntityPiston)tileentity;

                        if (tileentitypiston.func_145864_c() == p_149696_6_ && tileentitypiston.func_145868_b())
                        {
                            tileentitypiston.func_145866_f();
                            block = tileentitypiston.func_145861_a();
                            i2 = tileentitypiston.func_145832_p();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1 && block.func_149688_o() != Material.field_151579_a && func_150080_a(block, p_149696_1_, j1, k1, l1, false) && (block.func_149656_h() == 0 || block == Blocks.piston || block == Blocks.sticky_piston))
                {
                    p_149696_2_ += Facing.offsetsXForSide[p_149696_6_];
                    p_149696_3_ += Facing.offsetsYForSide[p_149696_6_];
                    p_149696_4_ += Facing.offsetsZForSide[p_149696_6_];
                    p_149696_1_.func_147465_d(p_149696_2_, p_149696_3_, p_149696_4_, Blocks.piston_extension, i2, 3);
                    p_149696_1_.func_147455_a(p_149696_2_, p_149696_3_, p_149696_4_, BlockPistonMoving.func_149962_a(block, i2, p_149696_6_, false, false));
                    p_149696_1_.func_147468_f(j1, k1, l1);
                }
                else if (!flag1)
                {
                    p_149696_1_.func_147468_f(p_149696_2_ + Facing.offsetsXForSide[p_149696_6_], p_149696_3_ + Facing.offsetsYForSide[p_149696_6_], p_149696_4_ + Facing.offsetsZForSide[p_149696_6_]);
                }
            }
            else
            {
                p_149696_1_.func_147468_f(p_149696_2_ + Facing.offsetsXForSide[p_149696_6_], p_149696_3_ + Facing.offsetsYForSide[p_149696_6_], p_149696_4_ + Facing.offsetsZForSide[p_149696_6_]);
            }

            p_149696_1_.playSoundEffect((double)p_149696_2_ + 0.5D, (double)p_149696_3_ + 0.5D, (double)p_149696_4_ + 0.5D, "tile.piston.in", 0.5F, p_149696_1_.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    public void func_149719_a(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
    {
        int l = p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_);

        if (func_150075_c(l))
        {
            float f = 0.25F;

            switch (func_150076_b(l))
            {
                case 0:
                    this.func_149676_a(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case 1:
                    this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                    break;
                case 2:
                    this.func_149676_a(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                    break;
                case 3:
                    this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                    break;
                case 4:
                    this.func_149676_a(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case 5:
                    this.func_149676_a(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
            }
        }
        else
        {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public void func_149683_g()
    {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void func_149743_a(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_)
    {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.func_149743_a(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
    }

    public AxisAlignedBB func_149668_a(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        this.func_149719_a(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
        return super.func_149668_a(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
    }

    public boolean func_149686_d()
    {
        return false;
    }

    public static int func_150076_b(int p_150076_0_)
    {
        return p_150076_0_ & 7;
    }

    public static boolean func_150075_c(int p_150075_0_)
    {
        return (p_150075_0_ & 8) != 0;
    }

    public static int func_150071_a(World p_150071_0_, int p_150071_1_, int p_150071_2_, int p_150071_3_, EntityLivingBase p_150071_4_)
    {
        if (MathHelper.abs((float)p_150071_4_.posX - (float)p_150071_1_) < 2.0F && MathHelper.abs((float)p_150071_4_.posZ - (float)p_150071_3_) < 2.0F)
        {
            double d0 = p_150071_4_.posY + 1.82D - (double)p_150071_4_.yOffset;

            if (d0 - (double)p_150071_2_ > 2.0D)
            {
                return 1;
            }

            if ((double)p_150071_2_ - d0 > 0.0D)
            {
                return 0;
            }
        }

        int l = MathHelper.floor_double((double)(p_150071_4_.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }

    private static boolean func_150080_a(Block p_150080_0_, World p_150080_1_, int p_150080_2_, int p_150080_3_, int p_150080_4_, boolean p_150080_5_)
    {
        if (p_150080_0_ == Blocks.obsidian)
        {
            return false;
        }
        else
        {
            if (p_150080_0_ != Blocks.piston && p_150080_0_ != Blocks.sticky_piston)
            {
                if (p_150080_0_.func_149712_f(p_150080_1_, p_150080_2_, p_150080_3_, p_150080_4_) == -1.0F)
                {
                    return false;
                }

                if (p_150080_0_.func_149656_h() == 2)
                {
                    return false;
                }

                if (p_150080_0_.func_149656_h() == 1)
                {
                    if (!p_150080_5_)
                    {
                        return false;
                    }

                    return true;
                }
            }
            else if (func_150075_c(p_150080_1_.getBlockMetadata(p_150080_2_, p_150080_3_, p_150080_4_)))
            {
                return false;
            }

            return !(p_150080_1_.func_147439_a(p_150080_2_, p_150080_3_, p_150080_4_).hasTileEntity(p_150080_1_.getBlockMetadata(p_150080_2_, p_150080_3_, p_150080_4_)));
            
        }
    }

    private static boolean func_150077_h(World p_150077_0_, int p_150077_1_, int p_150077_2_, int p_150077_3_, int p_150077_4_)
    {
        int i1 = p_150077_1_ + Facing.offsetsXForSide[p_150077_4_];
        int j1 = p_150077_2_ + Facing.offsetsYForSide[p_150077_4_];
        int k1 = p_150077_3_ + Facing.offsetsZForSide[p_150077_4_];
        int l1 = 0;

        while (true)
        {
            if (l1 < 13)
            {
                if (j1 <= 0 || j1 >= p_150077_0_.getHeight())
                {
                    return false;
                }

                Block block = p_150077_0_.func_147439_a(i1, j1, k1);

                if (!block.isAir(p_150077_0_, i1, j1, k1))
                {
                    if (!func_150080_a(block, p_150077_0_, i1, j1, k1, true))
                    {
                        return false;
                    }

                    if (block.func_149656_h() != 1)
                    {
                        if (l1 == 12)
                        {
                            return false;
                        }

                        i1 += Facing.offsetsXForSide[p_150077_4_];
                        j1 += Facing.offsetsYForSide[p_150077_4_];
                        k1 += Facing.offsetsZForSide[p_150077_4_];
                        ++l1;
                        continue;
                    }
                }
            }

            return true;
        }
    }

    private boolean func_150079_i(World p_150079_1_, int p_150079_2_, int p_150079_3_, int p_150079_4_, int p_150079_5_)
    {
        int i1 = p_150079_2_ + Facing.offsetsXForSide[p_150079_5_];
        int j1 = p_150079_3_ + Facing.offsetsYForSide[p_150079_5_];
        int k1 = p_150079_4_ + Facing.offsetsZForSide[p_150079_5_];
        int l1 = 0;

        while (true)
        {
            if (l1 < 13)
            {
                if (j1 <= 0 || j1 >= p_150079_1_.getHeight())
                {
                    return false;
                }

                Block block = p_150079_1_.func_147439_a(i1, j1, k1);

                if (!block.isAir(p_150079_1_, i1, j1, k1))
                {
                    if (!func_150080_a(block, p_150079_1_, i1, j1, k1, true))
                    {
                        return false;
                    }

                    if (block.func_149656_h() != 1)
                    {
                        if (l1 == 12)
                        {
                            return false;
                        }

                        i1 += Facing.offsetsXForSide[p_150079_5_];
                        j1 += Facing.offsetsYForSide[p_150079_5_];
                        k1 += Facing.offsetsZForSide[p_150079_5_];
                        ++l1;
                        continue;
                    }

                    //With our change to how snowballs are dropped this needs to disallow to mimic vanilla behavior.
                    float chance = block instanceof BlockSnow ? -1.0f : 1.0f;
                    block.func_149690_a(p_150079_1_, i1, j1, k1, p_150079_1_.getBlockMetadata(i1, j1, k1), chance, 0);
                    p_150079_1_.func_147468_f(i1, j1, k1);
                }
            }

            l1 = i1;
            int k3 = j1;
            int i2 = k1;
            int j2 = 0;
            Block[] ablock;
            int k2;
            int l2;
            int i3;

            for (ablock = new Block[13]; i1 != p_150079_2_ || j1 != p_150079_3_ || k1 != p_150079_4_; k1 = i3)
            {
                k2 = i1 - Facing.offsetsXForSide[p_150079_5_];
                l2 = j1 - Facing.offsetsYForSide[p_150079_5_];
                i3 = k1 - Facing.offsetsZForSide[p_150079_5_];
                Block block1 = p_150079_1_.func_147439_a(k2, l2, i3);
                int j3 = p_150079_1_.getBlockMetadata(k2, l2, i3);

                if (block1 == this && k2 == p_150079_2_ && l2 == p_150079_3_ && i3 == p_150079_4_)
                {
                    p_150079_1_.func_147465_d(i1, j1, k1, Blocks.piston_extension, p_150079_5_ | (this.field_150082_a ? 8 : 0), 4);
                    p_150079_1_.func_147455_a(i1, j1, k1, BlockPistonMoving.func_149962_a(Blocks.piston_head, p_150079_5_ | (this.field_150082_a ? 8 : 0), p_150079_5_, true, false));
                }
                else
                {
                    p_150079_1_.func_147465_d(i1, j1, k1, Blocks.piston_extension, j3, 4);
                    p_150079_1_.func_147455_a(i1, j1, k1, BlockPistonMoving.func_149962_a(block1, j3, p_150079_5_, true, false));
                }

                ablock[j2++] = block1;
                i1 = k2;
                j1 = l2;
            }

            i1 = l1;
            j1 = k3;
            k1 = i2;

            for (j2 = 0; i1 != p_150079_2_ || j1 != p_150079_3_ || k1 != p_150079_4_; k1 = i3)
            {
                k2 = i1 - Facing.offsetsXForSide[p_150079_5_];
                l2 = j1 - Facing.offsetsYForSide[p_150079_5_];
                i3 = k1 - Facing.offsetsZForSide[p_150079_5_];
                p_150079_1_.func_147459_d(k2, l2, i3, ablock[j2++]);
                i1 = k2;
                j1 = l2;
            }

            return true;
        }
    }
}