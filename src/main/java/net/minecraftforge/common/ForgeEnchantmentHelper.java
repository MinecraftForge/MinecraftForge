package net.minecraftforge.common;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEnchantmentLevelEvent;

import javax.annotation.Nullable;

/**
 * This class contains helper functions related to enchantments
 * that should be used instead of those in {@link EnchantmentHelper}.
 */
public class ForgeEnchantmentHelper {

    /**
     * Post an `LivingEnchantmentLevelEvent` to get the effective level of the given enchantment.
     * @param entityLiving The entity holding the given item..
     * @param enchantment The enchantment that is being tested/
     * @param heldItem The item that is being held by the player and could have the given enchanment.
     * @param level The starting enchantment level derived from the held item by Vanilla logic.
     * @return The effective enchantment level.
     */
    public static int getEnchantmentLevel(EntityLivingBase entityLiving, Enchantment enchantment, ItemStack heldItem, int level)
    {
        LivingEnchantmentLevelEvent e = new LivingEnchantmentLevelEvent(entityLiving, enchantment, heldItem, level);
        MinecraftForge.EVENT_BUS.post(e);
        return e.getLevel();
    }

    /**
     * Copied from {@link EnchantmentHelper#getEnchantmentLevel} to add entity parameter.
     */
    public static int getEnchantmentLevel(Enchantment enchID, ItemStack stack, EntityLivingBase entityLivingBase)
    {
        return getEnchantmentLevel(entityLivingBase, enchID, stack, EnchantmentHelper.getEnchantmentLevel(enchID, stack));
    }

    /**
     * Copied from {@link EnchantmentHelper#applyEnchantmentModifier} to add entity parameter.
     */
    public static void applyEnchantmentModifier(EnchantmentHelper.IModifier modifier, ItemStack stack, @Nullable EntityLivingBase entityLivingBase)
    {
        if (!stack.isEmpty())
        {
            NBTTagList nbttaglist = stack.getEnchantmentTagList();

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                int j = nbttaglist.getCompoundTagAt(i).getShort("id");
                int k = nbttaglist.getCompoundTagAt(i).getShort("lvl");

                if (Enchantment.getEnchantmentByID(j) != null)
                {
                    modifier.calculateModifier(Enchantment.getEnchantmentByID(j), entityLivingBase == null ? k : getEnchantmentLevel(entityLivingBase, Enchantment.getEnchantmentByID(j), stack, k));
                }
            }
        }
    }

    /**
     * Copied from {@link EnchantmentHelper#applyEnchantmentModifierArray} to add entity parameter.
     */
    public static void applyEnchantmentModifierArray(EnchantmentHelper.IModifier modifier, Iterable<ItemStack> stacks, @Nullable EntityLivingBase entityLivingBase)
    {
        for (ItemStack itemstack : stacks)
        {
            applyEnchantmentModifier(modifier, itemstack, entityLivingBase);
        }
    }

    /**
     * Copied from {@link EnchantmentHelper#getEnchantmentModifierDamage} to add entity parameter.
     */
    public static int getEnchantmentModifierDamage(Iterable<ItemStack> stacks, DamageSource source, @Nullable EntityLivingBase entityLivingBase)
    {
        EnchantmentHelper.ENCHANTMENT_MODIFIER_DAMAGE.damageModifier = 0;
        EnchantmentHelper.ENCHANTMENT_MODIFIER_DAMAGE.source = source;
        applyEnchantmentModifierArray(EnchantmentHelper.ENCHANTMENT_MODIFIER_DAMAGE, stacks, entityLivingBase);
        return EnchantmentHelper.ENCHANTMENT_MODIFIER_DAMAGE.damageModifier;
    }

    /**
     * Copied from {@link EnchantmentHelper#getModifierForCreature} to add entity parameter.
     */
    public static float getModifierForCreature(ItemStack stack, EnumCreatureAttribute creatureAttribute, @Nullable EntityLivingBase entityLivingBase)
    {
        EnchantmentHelper.ENCHANTMENT_MODIFIER_LIVING.livingModifier = 0.0F;
        EnchantmentHelper.ENCHANTMENT_MODIFIER_LIVING.entityLiving = creatureAttribute;
        applyEnchantmentModifier(EnchantmentHelper.ENCHANTMENT_MODIFIER_LIVING, stack, entityLivingBase);
        return EnchantmentHelper.ENCHANTMENT_MODIFIER_LIVING.livingModifier;
    }

    /**
     * Copied from {@link EnchantmentHelper#getFishingLuckBonus} to add entity parameter.
     */
    public static int getFishingLuckBonus(ItemStack stack, EntityLivingBase entityLiving)
    {
        return getEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, stack, entityLiving);
    }

    /**
     * Copied from {@link EnchantmentHelper#getFishingSpeedBonus} to add entity parameter.
     */
    public static int getFishingSpeedBonus(ItemStack stack, EntityLivingBase entityLiving)
    {
        return getEnchantmentLevel(Enchantments.LURE, stack, entityLiving);
    }

    /**
     * Copied from {@link EnchantmentHelper#hasBindingCurse} to add entity parameter.
     */
    public static boolean hasBindingCurse(ItemStack stack, EntityLivingBase entityLiving)
    {
        return getEnchantmentLevel(Enchantments.BINDING_CURSE, stack, entityLiving) > 0;
    }

    /**
     * Copied from {@link EnchantmentHelper#hasVanishingCurse} to add entity parameter.
     */
    public static boolean hasVanishingCurse(ItemStack stack, EntityLivingBase entityLiving)
    {
        return getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack, entityLiving) > 0;
    }

}
