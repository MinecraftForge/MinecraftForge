package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider
{
    private static final String __OBFID = "CL_00000220";

    public BlockRedstoneComparator(boolean p_i45399_1_)
    {
        super(p_i45399_1_);
        this.field_149758_A = true;
    }

    public Item func_149650_a(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Items.comparator;
    }

    @SideOnly(Side.CLIENT)
    public Item func_149694_d(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Items.comparator;
    }

    protected int func_149901_b(int p_149901_1_)
    {
        return 2;
    }

    protected BlockRedstoneDiode func_149906_e()
    {
        return Blocks.powered_comparator;
    }

    protected BlockRedstoneDiode func_149898_i()
    {
        return Blocks.unpowered_comparator;
    }

    public int func_149645_b()
    {
        return 37;
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        boolean flag = this.field_149914_a || (p_149691_2_ & 8) != 0;
        return p_149691_1_ == 0 ? (flag ? Blocks.redstone_torch.func_149733_h(p_149691_1_) : Blocks.unlit_redstone_torch.func_149733_h(p_149691_1_)) : (p_149691_1_ == 1 ? (flag ? Blocks.powered_comparator.field_149761_L : this.field_149761_L) : Blocks.double_stone_slab.func_149733_h(1));
    }

    protected boolean func_149905_c(int p_149905_1_)
    {
        return this.field_149914_a || (p_149905_1_ & 8) != 0;
    }

    protected int func_149904_f(IBlockAccess p_149904_1_, int p_149904_2_, int p_149904_3_, int p_149904_4_, int p_149904_5_)
    {
        return this.func_149971_e(p_149904_1_, p_149904_2_, p_149904_3_, p_149904_4_).func_145996_a();
    }

    private int func_149970_j(World p_149970_1_, int p_149970_2_, int p_149970_3_, int p_149970_4_, int p_149970_5_)
    {
        return !this.func_149969_d(p_149970_5_) ? this.func_149903_h(p_149970_1_, p_149970_2_, p_149970_3_, p_149970_4_, p_149970_5_) : Math.max(this.func_149903_h(p_149970_1_, p_149970_2_, p_149970_3_, p_149970_4_, p_149970_5_) - this.func_149902_h(p_149970_1_, p_149970_2_, p_149970_3_, p_149970_4_, p_149970_5_), 0);
    }

    public boolean func_149969_d(int p_149969_1_)
    {
        return (p_149969_1_ & 4) == 4;
    }

    protected boolean func_149900_a(World p_149900_1_, int p_149900_2_, int p_149900_3_, int p_149900_4_, int p_149900_5_)
    {
        int i1 = this.func_149903_h(p_149900_1_, p_149900_2_, p_149900_3_, p_149900_4_, p_149900_5_);

        if (i1 >= 15)
        {
            return true;
        }
        else if (i1 == 0)
        {
            return false;
        }
        else
        {
            int j1 = this.func_149902_h(p_149900_1_, p_149900_2_, p_149900_3_, p_149900_4_, p_149900_5_);
            return j1 == 0 ? true : i1 >= j1;
        }
    }

    protected int func_149903_h(World p_149903_1_, int p_149903_2_, int p_149903_3_, int p_149903_4_, int p_149903_5_)
    {
        int i1 = super.func_149903_h(p_149903_1_, p_149903_2_, p_149903_3_, p_149903_4_, p_149903_5_);
        int j1 = func_149895_l(p_149903_5_);
        int k1 = p_149903_2_ + Direction.offsetX[j1];
        int l1 = p_149903_4_ + Direction.offsetZ[j1];
        Block block = p_149903_1_.func_147439_a(k1, p_149903_3_, l1);

        if (block.func_149740_M())
        {
            i1 = block.func_149736_g(p_149903_1_, k1, p_149903_3_, l1, Direction.rotateOpposite[j1]);
        }
        else if (i1 < 15 && block.func_149721_r())
        {
            k1 += Direction.offsetX[j1];
            l1 += Direction.offsetZ[j1];
            block = p_149903_1_.func_147439_a(k1, p_149903_3_, l1);

            if (block.func_149740_M())
            {
                i1 = block.func_149736_g(p_149903_1_, k1, p_149903_3_, l1, Direction.rotateOpposite[j1]);
            }
        }

        return i1;
    }

    public TileEntityComparator func_149971_e(IBlockAccess p_149971_1_, int p_149971_2_, int p_149971_3_, int p_149971_4_)
    {
        return (TileEntityComparator)p_149971_1_.func_147438_o(p_149971_2_, p_149971_3_, p_149971_4_);
    }

    public boolean func_149727_a(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        int i1 = p_149727_1_.getBlockMetadata(p_149727_2_, p_149727_3_, p_149727_4_);
        boolean flag = this.field_149914_a | (i1 & 8) != 0;
        boolean flag1 = !this.func_149969_d(i1);
        int j1 = flag1 ? 4 : 0;
        j1 |= flag ? 8 : 0;
        p_149727_1_.playSoundEffect((double)p_149727_2_ + 0.5D, (double)p_149727_3_ + 0.5D, (double)p_149727_4_ + 0.5D, "random.click", 0.3F, flag1 ? 0.55F : 0.5F);
        p_149727_1_.setBlockMetadataWithNotify(p_149727_2_, p_149727_3_, p_149727_4_, j1 | i1 & 3, 2);
        this.func_149972_c(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_, p_149727_1_.rand);
        return true;
    }

    protected void func_149897_b(World p_149897_1_, int p_149897_2_, int p_149897_3_, int p_149897_4_, Block p_149897_5_)
    {
        if (!p_149897_1_.func_147477_a(p_149897_2_, p_149897_3_, p_149897_4_, this))
        {
            int l = p_149897_1_.getBlockMetadata(p_149897_2_, p_149897_3_, p_149897_4_);
            int i1 = this.func_149970_j(p_149897_1_, p_149897_2_, p_149897_3_, p_149897_4_, l);
            int j1 = this.func_149971_e(p_149897_1_, p_149897_2_, p_149897_3_, p_149897_4_).func_145996_a();

            if (i1 != j1 || this.func_149905_c(l) != this.func_149900_a(p_149897_1_, p_149897_2_, p_149897_3_, p_149897_4_, l))
            {
                if (this.func_149912_i(p_149897_1_, p_149897_2_, p_149897_3_, p_149897_4_, l))
                {
                    p_149897_1_.func_147454_a(p_149897_2_, p_149897_3_, p_149897_4_, this, this.func_149901_b(0), -1);
                }
                else
                {
                    p_149897_1_.func_147454_a(p_149897_2_, p_149897_3_, p_149897_4_, this, this.func_149901_b(0), 0);
                }
            }
        }
    }

    private void func_149972_c(World p_149972_1_, int p_149972_2_, int p_149972_3_, int p_149972_4_, Random p_149972_5_)
    {
        int l = p_149972_1_.getBlockMetadata(p_149972_2_, p_149972_3_, p_149972_4_);
        int i1 = this.func_149970_j(p_149972_1_, p_149972_2_, p_149972_3_, p_149972_4_, l);
        int j1 = this.func_149971_e(p_149972_1_, p_149972_2_, p_149972_3_, p_149972_4_).func_145996_a();
        this.func_149971_e(p_149972_1_, p_149972_2_, p_149972_3_, p_149972_4_).func_145995_a(i1);

        if (j1 != i1 || !this.func_149969_d(l))
        {
            boolean flag = this.func_149900_a(p_149972_1_, p_149972_2_, p_149972_3_, p_149972_4_, l);
            boolean flag1 = this.field_149914_a || (l & 8) != 0;

            if (flag1 && !flag)
            {
                p_149972_1_.setBlockMetadataWithNotify(p_149972_2_, p_149972_3_, p_149972_4_, l & -9, 2);
            }
            else if (!flag1 && flag)
            {
                p_149972_1_.setBlockMetadataWithNotify(p_149972_2_, p_149972_3_, p_149972_4_, l | 8, 2);
            }

            this.func_149911_e(p_149972_1_, p_149972_2_, p_149972_3_, p_149972_4_);
        }
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        if (this.field_149914_a)
        {
            int l = p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_);
            p_149674_1_.func_147465_d(p_149674_2_, p_149674_3_, p_149674_4_, this.func_149898_i(), l | 8, 4);
        }

        this.func_149972_c(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, p_149674_5_);
    }

    public void func_149726_b(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        super.func_149726_b(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
        p_149726_1_.func_147455_a(p_149726_2_, p_149726_3_, p_149726_4_, this.func_149915_a(p_149726_1_, 0));
    }

    public void func_149749_a(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        super.func_149749_a(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
        p_149749_1_.func_147475_p(p_149749_2_, p_149749_3_, p_149749_4_);
        this.func_149911_e(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_);
    }

    public boolean func_149696_a(World p_149696_1_, int p_149696_2_, int p_149696_3_, int p_149696_4_, int p_149696_5_, int p_149696_6_)
    {
        super.func_149696_a(p_149696_1_, p_149696_2_, p_149696_3_, p_149696_4_, p_149696_5_, p_149696_6_);
        TileEntity tileentity = p_149696_1_.func_147438_o(p_149696_2_, p_149696_3_, p_149696_4_);
        return tileentity != null ? tileentity.func_145842_c(p_149696_5_, p_149696_6_) : false;
    }

    public TileEntity func_149915_a(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityComparator();
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
    {
        if (y == tileY && world instanceof World)
        {
            func_149695_a((World)world, x, y, z, world.func_147439_a(tileX, tileY, tileZ));
        }   
    }
    
    @Override
    public boolean getWeakChanges(IBlockAccess world, int x, int y, int z)
    {
        return true;
    }
}