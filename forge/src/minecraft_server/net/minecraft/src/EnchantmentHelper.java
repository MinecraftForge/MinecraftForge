package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EnchantmentHelper
{
    /** Is the random seed of enchantment effects. */
    private static final Random enchantmentRand = new Random();

    /**
     * Used to calculate the extra armor of enchantments on armors equipped on player.
     */
    private static final EnchantmentModifierDamage enchantmentModifierDamage = new EnchantmentModifierDamage((Empty3)null);

    /**
     * Used to calculate the (magic) extra damage done by enchantments on current equipped item of player.
     */
    private static final EnchantmentModifierLiving enchantmentModifierLiving = new EnchantmentModifierLiving((Empty3)null);

    /**
     * Returns the level of enchantment on the ItemStack passed.
     */
    public static int getEnchantmentLevel(int par0, ItemStack par1ItemStack)
    {
        if (par1ItemStack == null)
        {
            return 0;
        }
        else
        {
            NBTTagList var2 = par1ItemStack.getEnchantmentTagList();

            if (var2 == null)
            {
                return 0;
            }
            else
            {
                for (int var3 = 0; var3 < var2.tagCount(); ++var3)
                {
                    short var4 = ((NBTTagCompound)var2.tagAt(var3)).getShort("id");
                    short var5 = ((NBTTagCompound)var2.tagAt(var3)).getShort("lvl");

                    if (var4 == par0)
                    {
                        return var5;
                    }
                }

                return 0;
            }
        }
    }

    private static int getMaxEnchantmentLevel(int par0, ItemStack[] par1ArrayOfItemStack)
    {
        int var2 = 0;
        ItemStack[] var3 = par1ArrayOfItemStack;
        int var4 = par1ArrayOfItemStack.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            ItemStack var6 = var3[var5];
            int var7 = getEnchantmentLevel(par0, var6);

            if (var7 > var2)
            {
                var2 = var7;
            }
        }

        return var2;
    }

    /**
     * Executes the enchantment modifier on the ItemStack passed.
     */
    private static void applyEnchantmentModifier(IEnchantmentModifier par0IEnchantmentModifier, ItemStack par1ItemStack)
    {
        if (par1ItemStack != null)
        {
            NBTTagList var2 = par1ItemStack.getEnchantmentTagList();

            if (var2 != null)
            {
                for (int var3 = 0; var3 < var2.tagCount(); ++var3)
                {
                    short var4 = ((NBTTagCompound)var2.tagAt(var3)).getShort("id");
                    short var5 = ((NBTTagCompound)var2.tagAt(var3)).getShort("lvl");

                    if (Enchantment.enchantmentsList[var4] != null)
                    {
                        par0IEnchantmentModifier.calculateModifier(Enchantment.enchantmentsList[var4], var5);
                    }
                }
            }
        }
    }

    /**
     * Executes the enchantment modifier on the array of ItemStack passed.
     */
    private static void applyEnchantmentModifierArray(IEnchantmentModifier par0IEnchantmentModifier, ItemStack[] par1ArrayOfItemStack)
    {
        ItemStack[] var2 = par1ArrayOfItemStack;
        int var3 = par1ArrayOfItemStack.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            ItemStack var5 = var2[var4];
            applyEnchantmentModifier(par0IEnchantmentModifier, var5);
        }
    }

    /**
     * Returns the modifier of protection enchantments on armors equipped on player.
     */
    public static int getEnchantmentModifierDamage(InventoryPlayer par0InventoryPlayer, DamageSource par1DamageSource)
    {
        enchantmentModifierDamage.damageModifier = 0;
        enchantmentModifierDamage.damageSource = par1DamageSource;
        applyEnchantmentModifierArray(enchantmentModifierDamage, par0InventoryPlayer.armorInventory);

        if (enchantmentModifierDamage.damageModifier > 25)
        {
            enchantmentModifierDamage.damageModifier = 25;
        }

        return (enchantmentModifierDamage.damageModifier + 1 >> 1) + enchantmentRand.nextInt((enchantmentModifierDamage.damageModifier >> 1) + 1);
    }

    /**
     * Return the (magic) extra damage of the enchantments on player equipped item.
     */
    public static int getEnchantmentModifierLiving(InventoryPlayer par0InventoryPlayer, EntityLiving par1EntityLiving)
    {
        enchantmentModifierLiving.livingModifier = 0;
        enchantmentModifierLiving.entityLiving = par1EntityLiving;
        applyEnchantmentModifier(enchantmentModifierLiving, par0InventoryPlayer.getCurrentItem());
        return enchantmentModifierLiving.livingModifier > 0 ? 1 + enchantmentRand.nextInt(enchantmentModifierLiving.livingModifier) : 0;
    }

    /**
     * Returns the knockback value of enchantments on equipped player item.
     */
    public static int getKnockbackModifier(InventoryPlayer par0InventoryPlayer, EntityLiving par1EntityLiving)
    {
        return getEnchantmentLevel(Enchantment.knockback.effectId, par0InventoryPlayer.getCurrentItem());
    }

    /**
     * Return the fire aspect value of enchantments on equipped player item.
     */
    public static int getFireAspectModifier(InventoryPlayer par0InventoryPlayer, EntityLiving par1EntityLiving)
    {
        return getEnchantmentLevel(Enchantment.fireAspect.effectId, par0InventoryPlayer.getCurrentItem());
    }

    /**
     * Returns the 'Water Breathing' modifier of enchantments on player equipped armors.
     */
    public static int getRespiration(InventoryPlayer par0InventoryPlayer)
    {
        return getMaxEnchantmentLevel(Enchantment.respiration.effectId, par0InventoryPlayer.armorInventory);
    }

    /**
     * Return the extra efficiency of tools based on enchantments on equipped player item.
     */
    public static int getEfficiencyModifier(InventoryPlayer par0InventoryPlayer)
    {
        return getEnchantmentLevel(Enchantment.efficiency.effectId, par0InventoryPlayer.getCurrentItem());
    }

    /**
     * Returns the unbreaking enchantment modifier on current equipped item of player.
     */
    public static int getUnbreakingModifier(InventoryPlayer par0InventoryPlayer)
    {
        return getEnchantmentLevel(Enchantment.unbreaking.effectId, par0InventoryPlayer.getCurrentItem());
    }

    /**
     * Returns the silk touch status of enchantments on current equipped item of player.
     */
    public static boolean getSilkTouchModifier(InventoryPlayer par0InventoryPlayer)
    {
        return getEnchantmentLevel(Enchantment.silkTouch.effectId, par0InventoryPlayer.getCurrentItem()) > 0;
    }

    public static int getFortuneModifier(InventoryPlayer par0InventoryPlayer)
    {
        return getEnchantmentLevel(Enchantment.fortune.effectId, par0InventoryPlayer.getCurrentItem());
    }

    /**
     * Returns the looting enchantment modifier of the current equipped item of player.
     */
    public static int getLootingModifier(InventoryPlayer par0InventoryPlayer)
    {
        return getEnchantmentLevel(Enchantment.looting.effectId, par0InventoryPlayer.getCurrentItem());
    }

    /**
     * Returns the aqua affinity status of enchantments on current equipped item of player.
     */
    public static boolean getAquaAffinityModifier(InventoryPlayer par0InventoryPlayer)
    {
        return getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, par0InventoryPlayer.armorInventory) > 0;
    }

    /**
     * Returns the enchantability of itemstack, it's uses a singular formula for each index (2nd parameter: 0, 1 and 2),
     * cutting to the max enchantability power of the table (3rd parameter)
     */
    public static int calcItemStackEnchantability(Random par0Random, int par1, int par2, ItemStack par3ItemStack)
    {
        Item var4 = par3ItemStack.getItem();
        int var5 = var4.getItemEnchantability();

        if (var5 <= 0)
        {
            return 0;
        }
        else
        {
            if (par2 > 30)
            {
                par2 = 30;
            }

            par2 = 1 + (par2 >> 1) + par0Random.nextInt(par2 + 1);
            int var6 = par0Random.nextInt(5) + par2;
            return par1 == 0 ? (var6 >> 1) + 1 : (par1 == 1 ? var6 * 2 / 3 + 1 : var6);
        }
    }

    public static void func_48622_a(Random par0Random, ItemStack par1ItemStack, int par2)
    {
        List var3 = buildEnchantmentList(par0Random, par1ItemStack, par2);

        if (var3 != null)
        {
            Iterator var4 = var3.iterator();

            while (var4.hasNext())
            {
                EnchantmentData var5 = (EnchantmentData)var4.next();
                par1ItemStack.addEnchantment(var5.enchantmentobj, var5.enchantmentLevel);
            }
        }
    }

    /**
     * Create a list of random EnchantmentData (enchantments) that can be added together to the ItemStack, the 3rd
     * parameter is the total enchantability level.
     */
    public static List buildEnchantmentList(Random par0Random, ItemStack par1ItemStack, int par2)
    {
        Item var3 = par1ItemStack.getItem();
        int var4 = var3.getItemEnchantability();

        if (var4 <= 0)
        {
            return null;
        }
        else
        {
            var4 = 1 + par0Random.nextInt((var4 >> 1) + 1) + par0Random.nextInt((var4 >> 1) + 1);
            int var5 = var4 + par2;
            float var6 = (par0Random.nextFloat() + par0Random.nextFloat() - 1.0F) * 0.25F;
            int var7 = (int)((float)var5 * (1.0F + var6) + 0.5F);
            ArrayList var8 = null;
            Map var9 = mapEnchantmentData(var7, par1ItemStack);

            if (var9 != null && !var9.isEmpty())
            {
                EnchantmentData var10 = (EnchantmentData)WeightedRandom.getRandomItem(par0Random, var9.values());

                if (var10 != null)
                {
                    var8 = new ArrayList();
                    var8.add(var10);

                    for (int var11 = var7 >> 1; par0Random.nextInt(50) <= var11; var11 >>= 1)
                    {
                        Iterator var12 = var9.keySet().iterator();

                        while (var12.hasNext())
                        {
                            Integer var13 = (Integer)var12.next();
                            boolean var14 = true;
                            Iterator var15 = var8.iterator();

                            while (true)
                            {
                                if (var15.hasNext())
                                {
                                    EnchantmentData var16 = (EnchantmentData)var15.next();

                                    if (var16.enchantmentobj.canApplyTogether(Enchantment.enchantmentsList[var13.intValue()]))
                                    {
                                        continue;
                                    }

                                    var14 = false;
                                }

                                if (!var14)
                                {
                                    var12.remove();
                                }

                                break;
                            }
                        }

                        if (!var9.isEmpty())
                        {
                            EnchantmentData var17 = (EnchantmentData)WeightedRandom.getRandomItem(par0Random, var9.values());
                            var8.add(var17);
                        }
                    }
                }
            }

            return var8;
        }
    }

    /**
     * Creates a 'Map' of EnchantmentData (enchantments) possible to add on the ItemStack and the enchantability level
     * passed.
     */
    public static Map mapEnchantmentData(int par0, ItemStack par1ItemStack)
    {
        Item var2 = par1ItemStack.getItem();
        HashMap var3 = null;
        Enchantment[] var4 = Enchantment.enchantmentsList;
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6)
        {
            Enchantment var7 = var4[var6];

            if (var7 != null && var7.canEnchantItem(par1ItemStack))
            {
                for (int var8 = var7.getMinLevel(); var8 <= var7.getMaxLevel(); ++var8)
                {
                    if (par0 >= var7.getMinEnchantability(var8) && par0 <= var7.getMaxEnchantability(var8))
                    {
                        if (var3 == null)
                        {
                            var3 = new HashMap();
                        }

                        var3.put(Integer.valueOf(var7.effectId), new EnchantmentData(var7, var8));
                    }
                }
            }
        }

        return var3;
    }
}
