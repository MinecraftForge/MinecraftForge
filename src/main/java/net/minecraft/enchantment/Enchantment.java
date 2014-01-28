package net.minecraft.enchantment;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

public abstract class Enchantment
{
    public static final Enchantment[] enchantmentsList = new Enchantment[256];
    // JAVADOC FIELD $$ field_92090_c
    public static final Enchantment[] enchantmentsBookList;
    // JAVADOC FIELD $$ field_77332_c
    public static final Enchantment protection = new EnchantmentProtection(0, 10, 0);
    // JAVADOC FIELD $$ field_77329_d
    public static final Enchantment fireProtection = new EnchantmentProtection(1, 5, 1);
    // JAVADOC FIELD $$ field_77330_e
    public static final Enchantment featherFalling = new EnchantmentProtection(2, 5, 2);
    // JAVADOC FIELD $$ field_77327_f
    public static final Enchantment blastProtection = new EnchantmentProtection(3, 2, 3);
    // JAVADOC FIELD $$ field_77328_g
    public static final Enchantment projectileProtection = new EnchantmentProtection(4, 5, 4);
    // JAVADOC FIELD $$ field_77340_h
    public static final Enchantment respiration = new EnchantmentOxygen(5, 2);
    // JAVADOC FIELD $$ field_77341_i
    public static final Enchantment aquaAffinity = new EnchantmentWaterWorker(6, 2);
    public static final Enchantment thorns = new EnchantmentThorns(7, 1);
    // JAVADOC FIELD $$ field_77338_j
    public static final Enchantment sharpness = new EnchantmentDamage(16, 10, 0);
    // JAVADOC FIELD $$ field_77339_k
    public static final Enchantment smite = new EnchantmentDamage(17, 5, 1);
    // JAVADOC FIELD $$ field_77336_l
    public static final Enchantment baneOfArthropods = new EnchantmentDamage(18, 5, 2);
    // JAVADOC FIELD $$ field_77337_m
    public static final Enchantment knockback = new EnchantmentKnockback(19, 5);
    // JAVADOC FIELD $$ field_77334_n
    public static final Enchantment fireAspect = new EnchantmentFireAspect(20, 2);
    // JAVADOC FIELD $$ field_77335_o
    public static final Enchantment looting = new EnchantmentLootBonus(21, 2, EnumEnchantmentType.weapon);
    // JAVADOC FIELD $$ field_77349_p
    public static final Enchantment efficiency = new EnchantmentDigging(32, 10);
    // JAVADOC FIELD $$ field_77348_q
    public static final Enchantment silkTouch = new EnchantmentUntouching(33, 1);
    // JAVADOC FIELD $$ field_77347_r
    public static final Enchantment unbreaking = new EnchantmentDurability(34, 5);
    // JAVADOC FIELD $$ field_77346_s
    public static final Enchantment fortune = new EnchantmentLootBonus(35, 2, EnumEnchantmentType.digger);
    // JAVADOC FIELD $$ field_77345_t
    public static final Enchantment power = new EnchantmentArrowDamage(48, 10);
    // JAVADOC FIELD $$ field_77344_u
    public static final Enchantment punch = new EnchantmentArrowKnockback(49, 2);
    // JAVADOC FIELD $$ field_77343_v
    public static final Enchantment flame = new EnchantmentArrowFire(50, 2);
    // JAVADOC FIELD $$ field_77342_w
    public static final Enchantment infinity = new EnchantmentArrowInfinite(51, 1);
    public static final Enchantment field_151370_z = new EnchantmentLootBonus(61, 2, EnumEnchantmentType.fishing_rod);
    public static final Enchantment field_151369_A = new EnchantmentFishingSpeed(62, 2, EnumEnchantmentType.fishing_rod);
    public final int effectId;
    private final int weight;
    // JAVADOC FIELD $$ field_77351_y
    public EnumEnchantmentType type;
    // JAVADOC FIELD $$ field_77350_z
    protected String name;
    private static final String __OBFID = "CL_00000105";

    protected Enchantment(int par1, int par2, EnumEnchantmentType par3EnumEnchantmentType)
    {
        this.effectId = par1;
        this.weight = par2;
        this.type = par3EnumEnchantmentType;

        if (enchantmentsList[par1] != null)
        {
            throw new IllegalArgumentException("Duplicate enchantment id!");
        }
        else
        {
            enchantmentsList[par1] = this;
        }
    }

    public int getWeight()
    {
        return this.weight;
    }

    // JAVADOC METHOD $$ func_77319_d
    public int getMinLevel()
    {
        return 1;
    }

    // JAVADOC METHOD $$ func_77325_b
    public int getMaxLevel()
    {
        return 1;
    }

    // JAVADOC METHOD $$ func_77321_a
    public int getMinEnchantability(int par1)
    {
        return 1 + par1 * 10;
    }

    // JAVADOC METHOD $$ func_77317_b
    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + 5;
    }

    // JAVADOC METHOD $$ func_77318_a
    public int calcModifierDamage(int par1, DamageSource par2DamageSource)
    {
        return 0;
    }

    // JAVADOC METHOD $$ func_77323_a
    public float calcModifierLiving(int par1, EntityLivingBase par2EntityLivingBase)
    {
        return 0.0F;
    }

    // JAVADOC METHOD $$ func_77326_a
    public boolean canApplyTogether(Enchantment par1Enchantment)
    {
        return this != par1Enchantment;
    }

    // JAVADOC METHOD $$ func_77322_b
    public Enchantment setName(String par1Str)
    {
        this.name = par1Str;
        return this;
    }

    // JAVADOC METHOD $$ func_77320_a
    public String getName()
    {
        return "enchantment." + this.name;
    }

    // JAVADOC METHOD $$ func_77316_c
    public String getTranslatedName(int par1)
    {
        String s = StatCollector.translateToLocal(this.getName());
        return s + " " + StatCollector.translateToLocal("enchantment.level." + par1);
    }

    public boolean canApply(ItemStack par1ItemStack)
    {
        return this.type.canEnchantItem(par1ItemStack.getItem());
    }

    public void func_151368_a(EntityLivingBase p_151368_1_, Entity p_151368_2_, int p_151368_3_) {}

    public void func_151367_b(EntityLivingBase p_151367_1_, Entity p_151367_2_, int p_151367_3_) {}

    /**
     * This applies specifically to applying at the enchanting table. The other method {@link #canApply(ItemStack)}
     * applies for <i>all possible</i> enchantments.
     * @param stack
     * @return
     */
    public boolean canApplyAtEnchantingTable(ItemStack stack)
    {
        return canApply(stack);
    }

    /**
     * Add to the list of enchantments applicable by the anvil from a book
     *
     * @param enchantment
     */
    public static void addToBookList(Enchantment enchantment)
    {
        com.google.common.collect.ObjectArrays.concat(enchantmentsBookList, enchantment);
    }

    /**
     * Is this enchantment allowed to be enchanted on books via Enchantment Table
     * @return false to disable the vanilla feature
     */
    public boolean isAllowedOnBooks()
    {
        return true;
    }

    static
    {
        ArrayList var0 = new ArrayList();
        Enchantment[] var1 = enchantmentsList;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3)
        {
            Enchantment var4 = var1[var3];

            if (var4 != null)
            {
                var0.add(var4);
            }
        }

        enchantmentsBookList = (Enchantment[])var0.toArray(new Enchantment[0]);
    }
}