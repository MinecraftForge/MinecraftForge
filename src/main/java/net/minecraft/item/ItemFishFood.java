package net.minecraft.item;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood
{
    private final boolean field_150907_b;
    private static final String __OBFID = "CL_00000032";

    public ItemFishFood(boolean p_i45338_1_)
    {
        super(0, 0.0F, false);
        this.field_150907_b = p_i45338_1_;
    }

    public int func_150905_g(ItemStack p_150905_1_)
    {
        ItemFishFood.FishType fishtype = ItemFishFood.FishType.func_150978_a(p_150905_1_);
        return this.field_150907_b && fishtype.func_150973_i() ? fishtype.func_150970_e() : fishtype.func_150975_c();
    }

    public float func_150906_h(ItemStack p_150906_1_)
    {
        ItemFishFood.FishType fishtype = ItemFishFood.FishType.func_150978_a(p_150906_1_);
        return this.field_150907_b && fishtype.func_150973_i() ? fishtype.func_150977_f() : fishtype.func_150967_d();
    }

    public String func_150896_i(ItemStack p_150896_1_)
    {
        return ItemFishFood.FishType.func_150978_a(p_150896_1_) == ItemFishFood.FishType.PUFFERFISH ? PotionHelper.field_151423_m : null;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        ItemFishFood.FishType[] afishtype = ItemFishFood.FishType.values();
        int i = afishtype.length;

        for (int j = 0; j < i; ++j)
        {
            ItemFishFood.FishType fishtype = afishtype[j];
            fishtype.func_150968_a(par1IconRegister);
        }
    }

    protected void onFoodEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        ItemFishFood.FishType fishtype = ItemFishFood.FishType.func_150978_a(par1ItemStack);

        if (fishtype == ItemFishFood.FishType.PUFFERFISH)
        {
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.poison.id, 1200, 3));
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.hunger.id, 300, 2));
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.confusion.id, 300, 1));
        }

        super.onFoodEaten(par1ItemStack, par2World, par3EntityPlayer);
    }

    // JAVADOC METHOD $$ func_77617_a
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1)
    {
        ItemFishFood.FishType fishtype = ItemFishFood.FishType.func_150974_a(par1);
        return this.field_150907_b && fishtype.func_150973_i() ? fishtype.func_150979_h() : fishtype.func_150971_g();
    }

    @SideOnly(Side.CLIENT)
    public void func_150895_a(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_)
    {
        ItemFishFood.FishType[] afishtype = ItemFishFood.FishType.values();
        int i = afishtype.length;

        for (int j = 0; j < i; ++j)
        {
            ItemFishFood.FishType fishtype = afishtype[j];

            if (!this.field_150907_b || fishtype.func_150973_i())
            {
                p_150895_3_.add(new ItemStack(this, 1, fishtype.func_150976_a()));
            }
        }
    }

    // JAVADOC METHOD $$ func_77667_c
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        ItemFishFood.FishType fishtype = ItemFishFood.FishType.func_150978_a(par1ItemStack);
        return this.getUnlocalizedName() + "." + fishtype.func_150972_b() + "." + (this.field_150907_b && fishtype.func_150973_i() ? "cooked" : "raw");
    }

    public static enum FishType
    {
        COD(0, "cod", 2, 0.1F, 5, 0.6F),
        SALMON(1, "salmon", 2, 0.1F, 6, 0.8F),
        CLOWNFISH(2, "clownfish", 1, 0.1F),
        PUFFERFISH(3, "pufferfish", 1, 0.1F);
        private static final Map field_150983_e = Maps.newHashMap();
        private final int field_150980_f;
        private final String field_150981_g;
        @SideOnly(Side.CLIENT)
        private IIcon field_150993_h;
        @SideOnly(Side.CLIENT)
        private IIcon field_150994_i;
        private final int field_150991_j;
        private final float field_150992_k;
        private final int field_150989_l;
        private final float field_150990_m;
        private boolean field_150987_n = false;

        private static final String __OBFID = "CL_00000033";

        private FishType(int p_i45336_3_, String p_i45336_4_, int p_i45336_5_, float p_i45336_6_, int p_i45336_7_, float p_i45336_8_)
        {
            this.field_150980_f = p_i45336_3_;
            this.field_150981_g = p_i45336_4_;
            this.field_150991_j = p_i45336_5_;
            this.field_150992_k = p_i45336_6_;
            this.field_150989_l = p_i45336_7_;
            this.field_150990_m = p_i45336_8_;
            this.field_150987_n = true;
        }

        private FishType(int p_i45337_3_, String p_i45337_4_, int p_i45337_5_, float p_i45337_6_)
        {
            this.field_150980_f = p_i45337_3_;
            this.field_150981_g = p_i45337_4_;
            this.field_150991_j = p_i45337_5_;
            this.field_150992_k = p_i45337_6_;
            this.field_150989_l = 0;
            this.field_150990_m = 0.0F;
            this.field_150987_n = false;
        }

        public int func_150976_a()
        {
            return this.field_150980_f;
        }

        public String func_150972_b()
        {
            return this.field_150981_g;
        }

        public int func_150975_c()
        {
            return this.field_150991_j;
        }

        public float func_150967_d()
        {
            return this.field_150992_k;
        }

        public int func_150970_e()
        {
            return this.field_150989_l;
        }

        public float func_150977_f()
        {
            return this.field_150990_m;
        }

        @SideOnly(Side.CLIENT)
        public void func_150968_a(IIconRegister p_150968_1_)
        {
            this.field_150993_h = p_150968_1_.registerIcon("fish_" + this.field_150981_g + "_raw");

            if (this.field_150987_n)
            {
                this.field_150994_i = p_150968_1_.registerIcon("fish_" + this.field_150981_g + "_cooked");
            }
        }

        @SideOnly(Side.CLIENT)
        public IIcon func_150971_g()
        {
            return this.field_150993_h;
        }

        @SideOnly(Side.CLIENT)
        public IIcon func_150979_h()
        {
            return this.field_150994_i;
        }

        public boolean func_150973_i()
        {
            return this.field_150987_n;
        }

        public static ItemFishFood.FishType func_150974_a(int p_150974_0_)
        {
            ItemFishFood.FishType fishtype = (ItemFishFood.FishType)field_150983_e.get(Integer.valueOf(p_150974_0_));
            return fishtype == null ? COD : fishtype;
        }

        public static ItemFishFood.FishType func_150978_a(ItemStack p_150978_0_)
        {
            return p_150978_0_.getItem() instanceof ItemFishFood ? func_150974_a(p_150978_0_.getItemDamage()) : COD;
        }

        static
        {
            ItemFishFood.FishType[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
                ItemFishFood.FishType var3 = var0[var2];
                field_150983_e.put(Integer.valueOf(var3.func_150976_a()), var3);
            }
        }
    }
}