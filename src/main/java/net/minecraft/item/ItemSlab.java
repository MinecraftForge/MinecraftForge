package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock
{
    private final boolean field_150948_b;
    private final BlockSlab field_150949_c;
    private final BlockSlab field_150947_d;
    private static final String __OBFID = "CL_00000071";

    public ItemSlab(Block p_i45355_1_, BlockSlab p_i45355_2_, BlockSlab p_i45355_3_, boolean p_i45355_4_)
    {
        super(p_i45355_1_);
        this.field_150949_c = p_i45355_2_;
        this.field_150947_d = p_i45355_3_;
        this.field_150948_b = p_i45355_4_;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    // JAVADOC METHOD $$ func_77617_a
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1)
    {
        return Block.func_149634_a(this).func_149691_a(2, par1);
    }

    // JAVADOC METHOD $$ func_77647_b
    public int getMetadata(int par1)
    {
        return par1;
    }

    // JAVADOC METHOD $$ func_77667_c
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return this.field_150949_c.func_150002_b(par1ItemStack.getItemDamage());
    }

    // JAVADOC METHOD $$ func_77648_a
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (this.field_150948_b)
        {
            return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
        }
        else if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
        {
            return false;
        }
        else
        {
            Block block = par3World.func_147439_a(par4, par5, par6);
            int i1 = par3World.getBlockMetadata(par4, par5, par6);
            int j1 = i1 & 7;
            boolean flag = (i1 & 8) != 0;

            if ((par7 == 1 && !flag || par7 == 0 && flag) && block == this.field_150949_c && j1 == par1ItemStack.getItemDamage())
            {
                if (par3World.checkNoEntityCollision(this.field_150947_d.func_149668_a(par3World, par4, par5, par6)) && par3World.func_147465_d(par4, par5, par6, this.field_150947_d, j1, 3))
                {
                    par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), this.field_150947_d.field_149762_H.func_150496_b(), (this.field_150947_d.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_150947_d.field_149762_H.func_150494_d() * 0.8F);
                    --par1ItemStack.stackSize;
                }

                return true;
            }
            else
            {
                return this.func_150946_a(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7) ? true : super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean func_150936_a(World p_150936_1_, int p_150936_2_, int p_150936_3_, int p_150936_4_, int p_150936_5_, EntityPlayer p_150936_6_, ItemStack p_150936_7_)
    {
        int i1 = p_150936_2_;
        int j1 = p_150936_3_;
        int k1 = p_150936_4_;
        Block block = p_150936_1_.func_147439_a(p_150936_2_, p_150936_3_, p_150936_4_);
        int l1 = p_150936_1_.getBlockMetadata(p_150936_2_, p_150936_3_, p_150936_4_);
        int i2 = l1 & 7;
        boolean flag = (l1 & 8) != 0;

        if ((p_150936_5_ == 1 && !flag || p_150936_5_ == 0 && flag) && block == this.field_150949_c && i2 == p_150936_7_.getItemDamage())
        {
            return true;
        }
        else
        {
            if (p_150936_5_ == 0)
            {
                --p_150936_3_;
            }

            if (p_150936_5_ == 1)
            {
                ++p_150936_3_;
            }

            if (p_150936_5_ == 2)
            {
                --p_150936_4_;
            }

            if (p_150936_5_ == 3)
            {
                ++p_150936_4_;
            }

            if (p_150936_5_ == 4)
            {
                --p_150936_2_;
            }

            if (p_150936_5_ == 5)
            {
                ++p_150936_2_;
            }

            Block block1 = p_150936_1_.func_147439_a(p_150936_2_, p_150936_3_, p_150936_4_);
            int j2 = p_150936_1_.getBlockMetadata(p_150936_2_, p_150936_3_, p_150936_4_);
            i2 = j2 & 7;
            return block1 == this.field_150949_c && i2 == p_150936_7_.getItemDamage() ? true : super.func_150936_a(p_150936_1_, i1, j1, k1, p_150936_5_, p_150936_6_, p_150936_7_);
        }
    }

    private boolean func_150946_a(ItemStack p_150946_1_, EntityPlayer p_150946_2_, World p_150946_3_, int p_150946_4_, int p_150946_5_, int p_150946_6_, int p_150946_7_)
    {
        if (p_150946_7_ == 0)
        {
            --p_150946_5_;
        }

        if (p_150946_7_ == 1)
        {
            ++p_150946_5_;
        }

        if (p_150946_7_ == 2)
        {
            --p_150946_6_;
        }

        if (p_150946_7_ == 3)
        {
            ++p_150946_6_;
        }

        if (p_150946_7_ == 4)
        {
            --p_150946_4_;
        }

        if (p_150946_7_ == 5)
        {
            ++p_150946_4_;
        }

        Block block = p_150946_3_.func_147439_a(p_150946_4_, p_150946_5_, p_150946_6_);
        int i1 = p_150946_3_.getBlockMetadata(p_150946_4_, p_150946_5_, p_150946_6_);
        int j1 = i1 & 7;

        if (block == this.field_150949_c && j1 == p_150946_1_.getItemDamage())
        {
            if (p_150946_3_.checkNoEntityCollision(this.field_150947_d.func_149668_a(p_150946_3_, p_150946_4_, p_150946_5_, p_150946_6_)) && p_150946_3_.func_147465_d(p_150946_4_, p_150946_5_, p_150946_6_, this.field_150947_d, j1, 3))
            {
                p_150946_3_.playSoundEffect((double)((float)p_150946_4_ + 0.5F), (double)((float)p_150946_5_ + 0.5F), (double)((float)p_150946_6_ + 0.5F), this.field_150947_d.field_149762_H.func_150496_b(), (this.field_150947_d.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_150947_d.field_149762_H.func_150494_d() * 0.8F);
                --p_150946_1_.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}