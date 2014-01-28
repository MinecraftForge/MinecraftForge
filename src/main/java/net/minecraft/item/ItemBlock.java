package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBlock extends Item
{
    protected final Block field_150939_a;
    @SideOnly(Side.CLIENT)
    private IIcon field_150938_b;
    private static final String __OBFID = "CL_00001772";

    public ItemBlock(Block p_i45328_1_)
    {
        this.field_150939_a = p_i45328_1_;
    }

    // JAVADOC METHOD $$ func_77655_b
    public ItemBlock setUnlocalizedName(String p_150937_1_)
    {
        super.setUnlocalizedName(p_150937_1_);
        return this;
    }

    // JAVADOC METHOD $$ func_94901_k
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber()
    {
        return this.field_150939_a.func_149702_O() != null ? 1 : 0;
    }

    // JAVADOC METHOD $$ func_77617_a
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1)
    {
        return this.field_150938_b != null ? this.field_150938_b : this.field_150939_a.func_149733_h(1);
    }

    // JAVADOC METHOD $$ func_77648_a
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        Block block = par3World.func_147439_a(par4, par5, par6);

        if (block == Blocks.snow_layer && (par3World.getBlockMetadata(par4, par5, par6) & 7) < 1)
        {
            par7 = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(par3World, par4, par5, par6))
        {
            if (par7 == 0)
            {
                --par5;
            }

            if (par7 == 1)
            {
                ++par5;
            }

            if (par7 == 2)
            {
                --par6;
            }

            if (par7 == 3)
            {
                ++par6;
            }

            if (par7 == 4)
            {
                --par4;
            }

            if (par7 == 5)
            {
                ++par4;
            }
        }

        if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
        {
            return false;
        }
        else if (par5 == 255 && this.field_150939_a.func_149688_o().isSolid())
        {
            return false;
        }
        else if (par3World.func_147472_a(this.field_150939_a, par4, par5, par6, false, par7, par2EntityPlayer, par1ItemStack))
        {
            int i1 = this.getMetadata(par1ItemStack.getItemDamage());
            int j1 = this.field_150939_a.func_149660_a(par3World, par4, par5, par6, par7, par8, par9, par10, i1);

            if (placeBlockAt(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10, j1))
            {
                par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), this.field_150939_a.field_149762_H.func_150496_b(), (this.field_150939_a.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_150939_a.field_149762_H.func_150494_d() * 0.8F);
                --par1ItemStack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean func_150936_a(World p_150936_1_, int p_150936_2_, int p_150936_3_, int p_150936_4_, int p_150936_5_, EntityPlayer p_150936_6_, ItemStack p_150936_7_)
    {
        Block block = p_150936_1_.func_147439_a(p_150936_2_, p_150936_3_, p_150936_4_);

        if (block == Blocks.snow_layer)
        {
            p_150936_5_ = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(p_150936_1_, p_150936_2_, p_150936_3_, p_150936_4_))
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
        }

        return p_150936_1_.func_147472_a(this.field_150939_a, p_150936_2_, p_150936_3_, p_150936_4_, false, p_150936_5_, (Entity)null, p_150936_7_);
    }

    // JAVADOC METHOD $$ func_77667_c
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return this.field_150939_a.func_149739_a();
    }

    // JAVADOC METHOD $$ func_77658_a
    public String getUnlocalizedName()
    {
        return this.field_150939_a.func_149739_a();
    }

    // JAVADOC METHOD $$ func_77640_w
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab()
    {
        return this.field_150939_a.func_149708_J();
    }

    @SideOnly(Side.CLIENT)
    public void func_150895_a(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_)
    {
        this.field_150939_a.func_149666_a(p_150895_1_, p_150895_2_, p_150895_3_);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        String s = this.field_150939_a.func_149702_O();

        if (s != null)
        {
            this.field_150938_b = par1IconRegister.registerIcon(s);
        }
    }

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {

       if (!world.func_147465_d(x, y, z, field_150939_a, metadata, 3))
       {
           return false;
       }

       if (world.func_147439_a(x, y, z) == field_150939_a)
       {
           field_150939_a.func_149689_a(world, x, y, z, player, stack);
           field_150939_a.func_149714_e(world, x, y, z, metadata);
       }

       return true;
    }
}