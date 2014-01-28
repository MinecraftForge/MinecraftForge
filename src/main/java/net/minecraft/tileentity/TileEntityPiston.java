package net.minecraft.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;

public class TileEntityPiston extends TileEntity
{
    private Block field_145869_a;
    private int field_145876_i;
    private int field_145874_j;
    private boolean field_145875_k;
    private boolean field_145872_l;
    private float field_145873_m;
    private float field_145870_n;
    private List field_145871_o = new ArrayList();
    private static final String __OBFID = "CL_00000369";

    public TileEntityPiston() {}

    public TileEntityPiston(Block p_i45444_1_, int p_i45444_2_, int p_i45444_3_, boolean p_i45444_4_, boolean p_i45444_5_)
    {
        this.field_145869_a = p_i45444_1_;
        this.field_145876_i = p_i45444_2_;
        this.field_145874_j = p_i45444_3_;
        this.field_145875_k = p_i45444_4_;
        this.field_145872_l = p_i45444_5_;
    }

    public Block func_145861_a()
    {
        return this.field_145869_a;
    }

    public int func_145832_p()
    {
        return this.field_145876_i;
    }

    public boolean func_145868_b()
    {
        return this.field_145875_k;
    }

    public int func_145864_c()
    {
        return this.field_145874_j;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_145867_d()
    {
        return this.field_145872_l;
    }

    public float func_145860_a(float p_145860_1_)
    {
        if (p_145860_1_ > 1.0F)
        {
            p_145860_1_ = 1.0F;
        }

        return this.field_145870_n + (this.field_145873_m - this.field_145870_n) * p_145860_1_;
    }

    private void func_145863_a(float p_145863_1_, float p_145863_2_)
    {
        if (this.field_145875_k)
        {
            p_145863_1_ = 1.0F - p_145863_1_;
        }
        else
        {
            --p_145863_1_;
        }

        AxisAlignedBB axisalignedbb = Blocks.piston_extension.func_149964_a(this.field_145850_b, this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145869_a, p_145863_1_, this.field_145874_j);

        if (axisalignedbb != null)
        {
            List list = this.field_145850_b.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);

            if (!list.isEmpty())
            {
                this.field_145871_o.addAll(list);
                Iterator iterator = this.field_145871_o.iterator();

                while (iterator.hasNext())
                {
                    Entity entity = (Entity)iterator.next();
                    entity.moveEntity((double)(p_145863_2_ * (float)Facing.offsetsXForSide[this.field_145874_j]), (double)(p_145863_2_ * (float)Facing.offsetsYForSide[this.field_145874_j]), (double)(p_145863_2_ * (float)Facing.offsetsZForSide[this.field_145874_j]));
                }

                this.field_145871_o.clear();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public float func_145865_b(float p_145865_1_)
    {
        return this.field_145875_k ? (this.func_145860_a(p_145865_1_) - 1.0F) * (float)Facing.offsetsXForSide[this.field_145874_j] : (1.0F - this.func_145860_a(p_145865_1_)) * (float)Facing.offsetsXForSide[this.field_145874_j];
    }

    @SideOnly(Side.CLIENT)
    public float func_145862_c(float p_145862_1_)
    {
        return this.field_145875_k ? (this.func_145860_a(p_145862_1_) - 1.0F) * (float)Facing.offsetsYForSide[this.field_145874_j] : (1.0F - this.func_145860_a(p_145862_1_)) * (float)Facing.offsetsYForSide[this.field_145874_j];
    }

    @SideOnly(Side.CLIENT)
    public float func_145859_d(float p_145859_1_)
    {
        return this.field_145875_k ? (this.func_145860_a(p_145859_1_) - 1.0F) * (float)Facing.offsetsZForSide[this.field_145874_j] : (1.0F - this.func_145860_a(p_145859_1_)) * (float)Facing.offsetsZForSide[this.field_145874_j];
    }

    public void func_145866_f()
    {
        if (this.field_145870_n < 1.0F && this.field_145850_b != null)
        {
            this.field_145870_n = this.field_145873_m = 1.0F;
            this.field_145850_b.func_147475_p(this.field_145851_c, this.field_145848_d, this.field_145849_e);
            this.func_145843_s();

            if (this.field_145850_b.func_147439_a(this.field_145851_c, this.field_145848_d, this.field_145849_e) == Blocks.piston_extension)
            {
                this.field_145850_b.func_147465_d(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145869_a, this.field_145876_i, 3);
                this.field_145850_b.func_147460_e(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145869_a);
            }
        }
    }

    public void func_145845_h()
    {
        this.field_145870_n = this.field_145873_m;

        if (this.field_145870_n >= 1.0F)
        {
            this.func_145863_a(1.0F, 0.25F);
            this.field_145850_b.func_147475_p(this.field_145851_c, this.field_145848_d, this.field_145849_e);
            this.func_145843_s();

            if (this.field_145850_b.func_147439_a(this.field_145851_c, this.field_145848_d, this.field_145849_e) == Blocks.piston_extension)
            {
                this.field_145850_b.func_147465_d(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145869_a, this.field_145876_i, 3);
                this.field_145850_b.func_147460_e(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145869_a);
            }
        }
        else
        {
            this.field_145873_m += 0.5F;

            if (this.field_145873_m >= 1.0F)
            {
                this.field_145873_m = 1.0F;
            }

            if (this.field_145875_k)
            {
                this.func_145863_a(this.field_145873_m, this.field_145873_m - this.field_145870_n + 0.0625F);
            }
        }
    }

    public void func_145839_a(NBTTagCompound p_145839_1_)
    {
        super.func_145839_a(p_145839_1_);
        this.field_145869_a = Block.func_149729_e(p_145839_1_.getInteger("blockId"));
        this.field_145876_i = p_145839_1_.getInteger("blockData");
        this.field_145874_j = p_145839_1_.getInteger("facing");
        this.field_145870_n = this.field_145873_m = p_145839_1_.getFloat("progress");
        this.field_145875_k = p_145839_1_.getBoolean("extending");
    }

    public void func_145841_b(NBTTagCompound p_145841_1_)
    {
        super.func_145841_b(p_145841_1_);
        p_145841_1_.setInteger("blockId", Block.func_149682_b(this.field_145869_a));
        p_145841_1_.setInteger("blockData", this.field_145876_i);
        p_145841_1_.setInteger("facing", this.field_145874_j);
        p_145841_1_.setFloat("progress", this.field_145870_n);
        p_145841_1_.setBoolean("extending", this.field_145875_k);
    }
}