package net.minecraft.enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.WeightedRandom;

public class EnchantmentHelper
{
    // JAVADOC FIELD $$ field_77522_a
    private static final Random enchantmentRand = new Random();
    // JAVADOC FIELD $$ field_77520_b
    private static final EnchantmentHelper.ModifierDamage enchantmentModifierDamage = new EnchantmentHelper.ModifierDamage(null);
    // JAVADOC FIELD $$ field_77521_c
    private static final EnchantmentHelper.ModifierLiving enchantmentModifierLiving = new EnchantmentHelper.ModifierLiving(null);
    private static final EnchantmentHelper.HurtIterator field_151388_d = new EnchantmentHelper.HurtIterator(null);
    private static final EnchantmentHelper.DamageIterator field_151389_e = new EnchantmentHelper.DamageIterator(null);
    private static final String __OBFID = "CL_00000107";

    // JAVADOC METHOD $$ func_77506_a
    public static int getEnchantmentLevel(int par0, ItemStack par1ItemStack)
    {
        if (par1ItemStack == null)
        {
            return 0;
        }
        else
        {
            NBTTagList nbttaglist = par1ItemStack.getEnchantmentTagList();

            if (nbttaglist == null)
            {
                return 0;
            }
            else
            {
                for (int j = 0; j < nbttaglist.tagCount(); ++j)
                {
                    short short1 = nbttaglist.func_150305_b(j).getShort("id");
                    short short2 = nbttaglist.func_150305_b(j).getShort("lvl");

                    if (short1 == par0)
                    {
                        return short2;
                    }
                }

                return 0;
            }
        }
    }

    // JAVADOC METHOD $$ func_82781_a
    public static Map getEnchantments(ItemStack par0ItemStack)
    {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        NBTTagList nbttaglist = par0ItemStack.getItem() == Items.enchanted_book ? Items.enchanted_book.func_92110_g(par0ItemStack) : par0ItemStack.getEnchantmentTagList();

        if (nbttaglist != null)
        {
            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                short short1 = nbttaglist.func_150305_b(i).getShort("id");
                short short2 = nbttaglist.func_150305_b(i).getShort("lvl");
                linkedhashmap.put(Integer.valueOf(short1), Integer.valueOf(short2));
            }
        }

        return linkedhashmap;
    }

    // JAVADOC METHOD $$ func_82782_a
    public static void setEnchantments(Map par0Map, ItemStack par1ItemStack)
    {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = par0Map.keySet().iterator();

        while (iterator.hasNext())
        {
            int i = ((Integer)iterator.next()).intValue();
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setShort("id", (short)i);
            nbttagcompound.setShort("lvl", (short)((Integer)par0Map.get(Integer.valueOf(i))).intValue());
            nbttaglist.appendTag(nbttagcompound);

            if (par1ItemStack.getItem() == Items.enchanted_book)
            {
                Items.enchanted_book.addEnchantment(par1ItemStack, new EnchantmentData(i, ((Integer)par0Map.get(Integer.valueOf(i))).intValue()));
            }
        }

        if (nbttaglist.tagCount() > 0)
        {
            if (par1ItemStack.getItem() != Items.enchanted_book)
            {
                par1ItemStack.setTagInfo("ench", nbttaglist);
            }
        }
        else if (par1ItemStack.hasTagCompound())
        {
            par1ItemStack.getTagCompound().removeTag("ench");
        }
    }

    // JAVADOC METHOD $$ func_77511_a
    public static int getMaxEnchantmentLevel(int par0, ItemStack[] par1ArrayOfItemStack)
    {
        if (par1ArrayOfItemStack == null)
        {
            return 0;
        }
        else
        {
            int j = 0;
            ItemStack[] aitemstack1 = par1ArrayOfItemStack;
            int k = par1ArrayOfItemStack.length;

            for (int l = 0; l < k; ++l)
            {
                ItemStack itemstack = aitemstack1[l];
                int i1 = getEnchantmentLevel(par0, itemstack);

                if (i1 > j)
                {
                    j = i1;
                }
            }

            return j;
        }
    }

    // JAVADOC METHOD $$ func_77518_a
    private static void applyEnchantmentModifier(EnchantmentHelper.IModifier par0IEnchantmentModifier, ItemStack par1ItemStack)
    {
        if (par1ItemStack != null)
        {
            NBTTagList nbttaglist = par1ItemStack.getEnchantmentTagList();

            if (nbttaglist != null)
            {
                for (int i = 0; i < nbttaglist.tagCount(); ++i)
                {
                    short short1 = nbttaglist.func_150305_b(i).getShort("id");
                    short short2 = nbttaglist.func_150305_b(i).getShort("lvl");

                    if (Enchantment.enchantmentsList[short1] != null)
                    {
                        par0IEnchantmentModifier.calculateModifier(Enchantment.enchantmentsList[short1], short2);
                    }
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_77516_a
    private static void applyEnchantmentModifierArray(EnchantmentHelper.IModifier par0IEnchantmentModifier, ItemStack[] par1ArrayOfItemStack)
    {
        ItemStack[] aitemstack1 = par1ArrayOfItemStack;
        int i = par1ArrayOfItemStack.length;

        for (int j = 0; j < i; ++j)
        {
            ItemStack itemstack = aitemstack1[j];
            applyEnchantmentModifier(par0IEnchantmentModifier, itemstack);
        }
    }

    // JAVADOC METHOD $$ func_77508_a
    public static int getEnchantmentModifierDamage(ItemStack[] par0ArrayOfItemStack, DamageSource par1DamageSource)
    {
        enchantmentModifierDamage.damageModifier = 0;
        enchantmentModifierDamage.source = par1DamageSource;
        applyEnchantmentModifierArray(enchantmentModifierDamage, par0ArrayOfItemStack);

        if (enchantmentModifierDamage.damageModifier > 25)
        {
            enchantmentModifierDamage.damageModifier = 25;
        }

        return (enchantmentModifierDamage.damageModifier + 1 >> 1) + enchantmentRand.nextInt((enchantmentModifierDamage.damageModifier >> 1) + 1);
    }

    // JAVADOC METHOD $$ func_77512_a
    public static float getEnchantmentModifierLiving(EntityLivingBase par0EntityLivingBase, EntityLivingBase par1EntityLivingBase)
    {
        enchantmentModifierLiving.livingModifier = 0.0F;
        enchantmentModifierLiving.entityLiving = par1EntityLivingBase;
        applyEnchantmentModifier(enchantmentModifierLiving, par0EntityLivingBase.getHeldItem());
        return enchantmentModifierLiving.livingModifier;
    }

    public static void func_151384_a(EntityLivingBase p_151384_0_, Entity p_151384_1_)
    {
        field_151388_d.field_151363_b = p_151384_1_;
        field_151388_d.field_151364_a = p_151384_0_;
        applyEnchantmentModifierArray(field_151388_d, p_151384_0_.getLastActiveItems());

        if (p_151384_1_ instanceof EntityPlayer)
        {
            applyEnchantmentModifier(field_151388_d, p_151384_0_.getHeldItem());
        }
    }

    public static void func_151385_b(EntityLivingBase p_151385_0_, Entity p_151385_1_)
    {
        field_151389_e.field_151366_a = p_151385_0_;
        field_151389_e.field_151365_b = p_151385_1_;
        applyEnchantmentModifierArray(field_151389_e, p_151385_0_.getLastActiveItems());

        if (p_151385_0_ instanceof EntityPlayer)
        {
            applyEnchantmentModifier(field_151389_e, p_151385_0_.getHeldItem());
        }
    }

    // JAVADOC METHOD $$ func_77507_b
    public static int getKnockbackModifier(EntityLivingBase par0EntityLivingBase, EntityLivingBase par1EntityLivingBase)
    {
        // JAVADOC METHOD $$ func_77506_a
        return getEnchantmentLevel(Enchantment.knockback.effectId, par0EntityLivingBase.getHeldItem());
    }

    public static int getFireAspectModifier(EntityLivingBase par0EntityLivingBase)
    {
        // JAVADOC METHOD $$ func_77506_a
        return getEnchantmentLevel(Enchantment.fireAspect.effectId, par0EntityLivingBase.getHeldItem());
    }

    // JAVADOC METHOD $$ func_77501_a
    public static int getRespiration(EntityLivingBase par0EntityLivingBase)
    {
        // JAVADOC METHOD $$ func_77511_a
        return getMaxEnchantmentLevel(Enchantment.respiration.effectId, par0EntityLivingBase.getLastActiveItems());
    }

    // JAVADOC METHOD $$ func_77509_b
    public static int getEfficiencyModifier(EntityLivingBase par0EntityLivingBase)
    {
        // JAVADOC METHOD $$ func_77506_a
        return getEnchantmentLevel(Enchantment.efficiency.effectId, par0EntityLivingBase.getHeldItem());
    }

    // JAVADOC METHOD $$ func_77502_d
    public static boolean getSilkTouchModifier(EntityLivingBase par0EntityLivingBase)
    {
        // JAVADOC METHOD $$ func_77506_a
        return getEnchantmentLevel(Enchantment.silkTouch.effectId, par0EntityLivingBase.getHeldItem()) > 0;
    }

    // JAVADOC METHOD $$ func_77517_e
    public static int getFortuneModifier(EntityLivingBase par0EntityLivingBase)
    {
        // JAVADOC METHOD $$ func_77506_a
        return getEnchantmentLevel(Enchantment.fortune.effectId, par0EntityLivingBase.getHeldItem());
    }

    public static int func_151386_g(EntityLivingBase p_151386_0_)
    {
        // JAVADOC METHOD $$ func_77506_a
        return getEnchantmentLevel(Enchantment.field_151370_z.effectId, p_151386_0_.getHeldItem());
    }

    public static int func_151387_h(EntityLivingBase p_151387_0_)
    {
        // JAVADOC METHOD $$ func_77506_a
        return getEnchantmentLevel(Enchantment.field_151369_A.effectId, p_151387_0_.getHeldItem());
    }

    // JAVADOC METHOD $$ func_77519_f
    public static int getLootingModifier(EntityLivingBase par0EntityLivingBase)
    {
        // JAVADOC METHOD $$ func_77506_a
        return getEnchantmentLevel(Enchantment.looting.effectId, par0EntityLivingBase.getHeldItem());
    }

    // JAVADOC METHOD $$ func_77510_g
    public static boolean getAquaAffinityModifier(EntityLivingBase par0EntityLivingBase)
    {
        // JAVADOC METHOD $$ func_77511_a
        return getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, par0EntityLivingBase.getLastActiveItems()) > 0;
    }

    public static ItemStack func_92099_a(Enchantment par0Enchantment, EntityLivingBase par1EntityLivingBase)
    {
        ItemStack[] aitemstack = par1EntityLivingBase.getLastActiveItems();
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j)
        {
            ItemStack itemstack = aitemstack[j];

            if (itemstack != null && getEnchantmentLevel(par0Enchantment.effectId, itemstack) > 0)
            {
                return itemstack;
            }
        }

        return null;
    }

    // JAVADOC METHOD $$ func_77514_a
    public static int calcItemStackEnchantability(Random par0Random, int par1, int par2, ItemStack par3ItemStack)
    {
        Item item = par3ItemStack.getItem();
        int k = item.getItemEnchantability();

        if (k <= 0)
        {
            return 0;
        }
        else
        {
            if (par2 > 15)
            {
                par2 = 15;
            }

            int l = par0Random.nextInt(8) + 1 + (par2 >> 1) + par0Random.nextInt(par2 + 1);
            return par1 == 0 ? Math.max(l / 3, 1) : (par1 == 1 ? l * 2 / 3 + 1 : Math.max(l, par2 * 2));
        }
    }

    // JAVADOC METHOD $$ func_77504_a
    public static ItemStack addRandomEnchantment(Random par0Random, ItemStack par1ItemStack, int par2)
    {
        List list = buildEnchantmentList(par0Random, par1ItemStack, par2);
        boolean flag = par1ItemStack.getItem() == Items.book;

        if (flag)
        {
            par1ItemStack.func_150996_a(Items.enchanted_book);
        }

        if (list != null)
        {
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EnchantmentData enchantmentdata = (EnchantmentData)iterator.next();

                if (flag)
                {
                    Items.enchanted_book.addEnchantment(par1ItemStack, enchantmentdata);
                }
                else
                {
                    par1ItemStack.addEnchantment(enchantmentdata.enchantmentobj, enchantmentdata.enchantmentLevel);
                }
            }
        }

        return par1ItemStack;
    }

    // JAVADOC METHOD $$ func_77513_b
    public static List buildEnchantmentList(Random par0Random, ItemStack par1ItemStack, int par2)
    {
        Item item = par1ItemStack.getItem();
        int j = item.getItemEnchantability();

        if (j <= 0)
        {
            return null;
        }
        else
        {
            j /= 2;
            j = 1 + par0Random.nextInt((j >> 1) + 1) + par0Random.nextInt((j >> 1) + 1);
            int k = j + par2;
            float f = (par0Random.nextFloat() + par0Random.nextFloat() - 1.0F) * 0.15F;
            int l = (int)((float)k * (1.0F + f) + 0.5F);

            if (l < 1)
            {
                l = 1;
            }

            ArrayList arraylist = null;
            Map map = mapEnchantmentData(l, par1ItemStack);

            if (map != null && !map.isEmpty())
            {
                EnchantmentData enchantmentdata = (EnchantmentData)WeightedRandom.getRandomItem(par0Random, map.values());

                if (enchantmentdata != null)
                {
                    arraylist = new ArrayList();
                    arraylist.add(enchantmentdata);

                    for (int i1 = l; par0Random.nextInt(50) <= i1; i1 >>= 1)
                    {
                        Iterator iterator = map.keySet().iterator();

                        while (iterator.hasNext())
                        {
                            Integer integer = (Integer)iterator.next();
                            boolean flag = true;
                            Iterator iterator1 = arraylist.iterator();

                            while (true)
                            {
                                if (iterator1.hasNext())
                                {
                                    EnchantmentData enchantmentdata1 = (EnchantmentData)iterator1.next();

                                    if (enchantmentdata1.enchantmentobj.canApplyTogether(Enchantment.enchantmentsList[integer.intValue()]))
                                    {
                                        continue;
                                    }

                                    flag = false;
                                }

                                if (!flag)
                                {
                                    iterator.remove();
                                }

                                break;
                            }
                        }

                        if (!map.isEmpty())
                        {
                            EnchantmentData enchantmentdata2 = (EnchantmentData)WeightedRandom.getRandomItem(par0Random, map.values());
                            arraylist.add(enchantmentdata2);
                        }
                    }
                }
            }

            return arraylist;
        }
    }

    // JAVADOC METHOD $$ func_77505_b
    public static Map mapEnchantmentData(int par0, ItemStack par1ItemStack)
    {
        Item item = par1ItemStack.getItem();
        HashMap hashmap = null;
        boolean flag = par1ItemStack.getItem() == Items.book;
        Enchantment[] aenchantment = Enchantment.enchantmentsList;
        int j = aenchantment.length;

        for (int k = 0; k < j; ++k)
        {
            Enchantment enchantment = aenchantment[k];

            if (enchantment == null) continue;
            if (enchantment.canApplyAtEnchantingTable(par1ItemStack) || ((item == Items.book) && enchantment.isAllowedOnBooks()))
            {
                for (int l = enchantment.getMinLevel(); l <= enchantment.getMaxLevel(); ++l)
                {
                    if (par0 >= enchantment.getMinEnchantability(l) && par0 <= enchantment.getMaxEnchantability(l))
                    {
                        if (hashmap == null)
                        {
                            hashmap = new HashMap();
                        }

                        hashmap.put(Integer.valueOf(enchantment.effectId), new EnchantmentData(enchantment, l));
                    }
                }
            }
        }

        return hashmap;
    }

    static final class ModifierDamage implements EnchantmentHelper.IModifier
        {
            // JAVADOC FIELD $$ field_77497_a
            public int damageModifier;
            // JAVADOC FIELD $$ field_77496_b
            public DamageSource source;
            private static final String __OBFID = "CL_00000114";

            private ModifierDamage() {}

            // JAVADOC METHOD $$ func_77493_a
            public void calculateModifier(Enchantment par1Enchantment, int par2)
            {
                this.damageModifier += par1Enchantment.calcModifierDamage(par2, this.source);
            }

            ModifierDamage(Object par1Empty3)
            {
                this();
            }
        }

    interface IModifier
    {
        // JAVADOC METHOD $$ func_77493_a
        void calculateModifier(Enchantment var1, int var2);
    }

    static final class ModifierLiving implements EnchantmentHelper.IModifier
        {
            // JAVADOC FIELD $$ field_77495_a
            public float livingModifier;
            // JAVADOC FIELD $$ field_77494_b
            public EntityLivingBase entityLiving;
            private static final String __OBFID = "CL_00000112";

            private ModifierLiving() {}

            // JAVADOC METHOD $$ func_77493_a
            public void calculateModifier(Enchantment par1Enchantment, int par2)
            {
                this.livingModifier += par1Enchantment.calcModifierLiving(par2, this.entityLiving);
            }

            ModifierLiving(Object par1Empty3)
            {
                this();
            }
        }

    static final class HurtIterator implements EnchantmentHelper.IModifier
        {
            public EntityLivingBase field_151364_a;
            public Entity field_151363_b;
            private static final String __OBFID = "CL_00000110";

            private HurtIterator() {}

            // JAVADOC METHOD $$ func_77493_a
            public void calculateModifier(Enchantment par1Enchantment, int par2)
            {
                par1Enchantment.func_151367_b(this.field_151364_a, this.field_151363_b, par2);
            }

            HurtIterator(Object p_i45360_1_)
            {
                this();
            }
        }

    static final class DamageIterator implements EnchantmentHelper.IModifier
        {
            public EntityLivingBase field_151366_a;
            public Entity field_151365_b;
            private static final String __OBFID = "CL_00000109";

            private DamageIterator() {}

            // JAVADOC METHOD $$ func_77493_a
            public void calculateModifier(Enchantment par1Enchantment, int par2)
            {
                par1Enchantment.func_151368_a(this.field_151366_a, this.field_151365_b, par2);
            }

            DamageIterator(Object p_i45359_1_)
            {
                this();
            }
        }
}