package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    private IIcon field_149927_a;
    private static final String __OBFID = "CL_00000260";

    protected BlockJukebox()
    {
        super(Material.field_151575_d);
        this.func_149647_a(CreativeTabs.tabDecorations);
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        return p_149691_1_ == 1 ? this.field_149927_a : this.field_149761_L;
    }

    public boolean func_149727_a(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if (p_149727_1_.getBlockMetadata(p_149727_2_, p_149727_3_, p_149727_4_) == 0)
        {
            return false;
        }
        else
        {
            this.func_149925_e(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_);
            return true;
        }
    }

    public void func_149926_b(World p_149926_1_, int p_149926_2_, int p_149926_3_, int p_149926_4_, ItemStack p_149926_5_)
    {
        if (!p_149926_1_.isRemote)
        {
            BlockJukebox.TileEntityJukebox tileentityjukebox = (BlockJukebox.TileEntityJukebox)p_149926_1_.func_147438_o(p_149926_2_, p_149926_3_, p_149926_4_);

            if (tileentityjukebox != null)
            {
                tileentityjukebox.func_145857_a(p_149926_5_.copy());
                p_149926_1_.setBlockMetadataWithNotify(p_149926_2_, p_149926_3_, p_149926_4_, 1, 2);
            }
        }
    }

    public void func_149925_e(World p_149925_1_, int p_149925_2_, int p_149925_3_, int p_149925_4_)
    {
        if (!p_149925_1_.isRemote)
        {
            BlockJukebox.TileEntityJukebox tileentityjukebox = (BlockJukebox.TileEntityJukebox)p_149925_1_.func_147438_o(p_149925_2_, p_149925_3_, p_149925_4_);

            if (tileentityjukebox != null)
            {
                ItemStack itemstack = tileentityjukebox.func_145856_a();

                if (itemstack != null)
                {
                    p_149925_1_.playAuxSFX(1005, p_149925_2_, p_149925_3_, p_149925_4_, 0);
                    p_149925_1_.playRecord((String)null, p_149925_2_, p_149925_3_, p_149925_4_);
                    tileentityjukebox.func_145857_a((ItemStack)null);
                    p_149925_1_.setBlockMetadataWithNotify(p_149925_2_, p_149925_3_, p_149925_4_, 0, 2);
                    float f = 0.7F;
                    double d0 = (double)(p_149925_1_.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d1 = (double)(p_149925_1_.rand.nextFloat() * f) + (double)(1.0F - f) * 0.2D + 0.6D;
                    double d2 = (double)(p_149925_1_.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    ItemStack itemstack1 = itemstack.copy();
                    EntityItem entityitem = new EntityItem(p_149925_1_, (double)p_149925_2_ + d0, (double)p_149925_3_ + d1, (double)p_149925_4_ + d2, itemstack1);
                    entityitem.field_145804_b = 10;
                    p_149925_1_.spawnEntityInWorld(entityitem);
                }
            }
        }
    }

    public void func_149749_a(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        this.func_149925_e(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_);
        super.func_149749_a(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
    }

    public void func_149690_a(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_, int p_149690_5_, float p_149690_6_, int p_149690_7_)
    {
        if (!p_149690_1_.isRemote)
        {
            super.func_149690_a(p_149690_1_, p_149690_2_, p_149690_3_, p_149690_4_, p_149690_5_, p_149690_6_, 0);
        }
    }

    public TileEntity func_149915_a(World p_149915_1_, int p_149915_2_)
    {
        return new BlockJukebox.TileEntityJukebox();
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_149761_L = p_149651_1_.registerIcon(this.func_149641_N() + "_side");
        this.field_149927_a = p_149651_1_.registerIcon(this.func_149641_N() + "_top");
    }

    public boolean func_149740_M()
    {
        return true;
    }

    public int func_149736_g(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_)
    {
        ItemStack itemstack = ((BlockJukebox.TileEntityJukebox)p_149736_1_.func_147438_o(p_149736_2_, p_149736_3_, p_149736_4_)).func_145856_a();
        return itemstack == null ? 0 : Item.func_150891_b(itemstack.getItem()) + 1 - Item.func_150891_b(Items.record_13);
    }

    public static class TileEntityJukebox extends TileEntity
        {
            private ItemStack field_145858_a;
            private static final String __OBFID = "CL_00000261";

            public void func_145839_a(NBTTagCompound p_145839_1_)
            {
                super.func_145839_a(p_145839_1_);

                if (p_145839_1_.func_150297_b("RecordItem", 10))
                {
                    this.func_145857_a(ItemStack.loadItemStackFromNBT(p_145839_1_.getCompoundTag("RecordItem")));
                }
                else if (p_145839_1_.getInteger("Record") > 0)
                {
                    this.func_145857_a(new ItemStack(Item.func_150899_d(p_145839_1_.getInteger("Record")), 1, 0));
                }
            }

            public void func_145841_b(NBTTagCompound p_145841_1_)
            {
                super.func_145841_b(p_145841_1_);

                if (this.func_145856_a() != null)
                {
                    p_145841_1_.setTag("RecordItem", this.func_145856_a().writeToNBT(new NBTTagCompound()));
                    p_145841_1_.setInteger("Record", Item.func_150891_b(this.func_145856_a().getItem()));
                }
            }

            public ItemStack func_145856_a()
            {
                return this.field_145858_a;
            }

            public void func_145857_a(ItemStack p_145857_1_)
            {
                this.field_145858_a = p_145857_1_;
                this.onInventoryChanged();
            }
        }
}