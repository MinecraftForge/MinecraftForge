/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

/**
 * This interface is to be implemented by ItemArmor classes. It will allow to
 * modify computation of damage and health loss. Computation will be called
 * before the actual armor computation, which can then be cancelled.
 *
 * @see ItemArmor
 */
public interface ISpecialArmor
{
    /**
     * Retrieves the modifiers to be used when calculating armor damage.
     *
     * Armor will higher priority will have damage applied to them before
     * lower priority ones. If there are multiple pieces of armor with the
     * same priority, damage will be distributed between them based on there
     * absorption ratio.
     *
     * @param player The entity wearing the armor.
     * @param armor The ItemStack of the armor item itself.
     * @param source The source of the damage, which can be used to alter armor
     *     properties based on the type or source of damage.
     * @param damage The total damage being applied to the entity
     * @param slot The armor slot the item is in.
     * @return A ArmorProperties instance holding information about how the armor effects damage.
     */
    public ArmorProperties getProperties(EntityLivingBase player, @Nonnull ItemStack armor, DamageSource source, double damage, EntityEquipmentSlot slot);

    /**
     * Get the displayed effective armor.
     *
     * @param player The player wearing the armor.
     * @param armor The ItemStack of the armor item itself.
     * @param slot The armor slot the item is in.
     * @return The number of armor points for display, 2 per shield.
     */
    public abstract int getArmorDisplay(EntityPlayer player, @Nonnull ItemStack armor, EntityEquipmentSlot slot);

    /**
     * Applies damage to the ItemStack. The mod is responsible for reducing the
     * item durability and stack size. If the stack is depleted it will be cleaned
     * up automatically.
     *
     * @param entity The entity wearing the armor
     * @param stack The ItemStack of the armor item itself.
     * @param source The source of the damage, which can be used to alter armor
     *     properties based on the type or source of damage.
     * @param damage The amount of damage being applied to the armor
     * @param slot The armor slot the item is in.
     */
    public abstract void damageArmor(EntityLivingBase entity, @Nonnull ItemStack stack, DamageSource source, int damage, EntityEquipmentSlot slot);

    public static class ArmorProperties implements Comparable<ArmorProperties>
    {
        public int    priority          = 0;
        public int    absorbMax         = Integer.MAX_VALUE;
        public double absorbRatio       = 0;
        public double armor             = 0;        //Additional armor, separate from the armor added by vanilla attributes.
        public double toughness         = 0;        //Additional toughness, separate from the armor added by vanilla attributes.
        public EntityEquipmentSlot slot = EntityEquipmentSlot.CHEST;
        private static final boolean DEBUG = false; //Only enable this if you wish to be spammed with debugging information.
                                                    //Left it in because I figured it'd be useful for modders developing custom armor.

        public ArmorProperties(int priority, double ratio, int max)
        {
            this(priority, ratio, 0, 0, max);
        }
        
        public ArmorProperties(int priority, double ratio, double armor, double toughness, int max)
        {
            this.priority    = priority;
            this.absorbRatio = ratio;
            this.armor       = armor;
            this.toughness   = toughness;
            this.absorbMax   = max;
        }

        /**
         * Gathers and applies armor reduction to damage being dealt to a entity.
         *
         * @param entity The Entity being damage
         * @param inventory An array of armor items
         * @param source The damage source type
         * @param damage The total damage being done
         * @return The left over damage that has not been absorbed by the armor
         */
        public static float applyArmor(EntityLivingBase entity, NonNullList<ItemStack> inventory, DamageSource source, double damage)
        {
            if (DEBUG)
            {
                System.out.println("Start: " + damage);
            }
            
            double totalArmor = entity.getTotalArmorValue();
            double totalToughness = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();

            ArrayList<ArmorProperties> dmgVals = new ArrayList<ArmorProperties>();
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
            {
                if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR)
                {
                    continue;
                }
                
                ItemStack stack = inventory.get(slot.getIndex());
                ArmorProperties prop = null;
                if (stack.getItem() instanceof ISpecialArmor)
                {
                    ISpecialArmor armor = (ISpecialArmor)stack.getItem();
                    prop = armor.getProperties(entity, stack, source, damage, slot).copy();
                    totalArmor += prop.armor;
                    totalToughness += prop.toughness;
                }
                else if (stack.getItem() instanceof ItemArmor && !source.isUnblockable())
                {
                    ItemArmor armor = (ItemArmor)stack.getItem();
                    prop = new ArmorProperties(0, 0, armor.damageReduceAmount, armor.toughness, Integer.MAX_VALUE);
                }
                if (prop != null)
                {
                    prop.slot = slot;
                    dmgVals.add(prop);
                }
            }
            if (dmgVals.size() > 0)
            {
                ArmorProperties[] props = dmgVals.toArray(new ArmorProperties[dmgVals.size()]);
                standardizeList(props, damage);
                int level = props[0].priority;
                double ratio = 0;
                for (ArmorProperties prop : props)
                {
                    if (level != prop.priority)
                    {
                        damage -= (damage * ratio);
                        ratio = 0;
                        level = prop.priority;
                    }
                    ratio += prop.absorbRatio;

                    double absorb = damage * prop.absorbRatio;
                    if (absorb > 0)
                    {
                        ItemStack stack = inventory.get(prop.slot.getIndex());
                        int itemDamage = (int)Math.max(1, absorb);
                        if (stack.getItem() instanceof ISpecialArmor)
                        {
                            ((ISpecialArmor)stack.getItem()).damageArmor(entity, stack, source, itemDamage, prop.slot);
                        }
                        else
                        {
                            if (DEBUG)
                            {
                                System.out.println("Item: " + stack.toString() + " Absorbed: " + absorb + " Damaged: " + itemDamage);
                            }
                            stack.damageItem(itemDamage, entity);
                        }
                        if (stack.isEmpty())
                        {
                            inventory.set(prop.slot.getIndex(), ItemStack.EMPTY);
                        }
                    }
                }
                damage -= (damage * ratio);
            }
            if (damage > 0 && (totalArmor > 0 || totalToughness > 0))
            {
                double armorDamage = Math.max(1.0F, damage / 4.0F);
                
                for (int i = 0; i < inventory.size(); i++)
                {
                    if (inventory.get(i).getItem() instanceof ItemArmor)
                    {
                        inventory.get(i).damageItem((int)armorDamage, entity);
                        
                        if (inventory.get(i).getCount() == 0)
                        {
                            inventory.set(i, ItemStack.EMPTY);
                        }
                    }
                }
                damage = CombatRules.getDamageAfterAbsorb((float)damage, (float)totalArmor, (float)totalToughness);
            }
            if (DEBUG)
            {
                System.out.println("Return: " + (int)(damage) + " " + damage);
            }
            return (float)(damage);
        }

        /**
         * Sorts and standardizes the distribution of damage over armor.
         *
         * @param armor The armor information
         * @param damage The total damage being received
         */
        private static void standardizeList(ArmorProperties[] armor, double damage)
        {
            Arrays.sort(armor);

            int     start     = 0;
            double  total     = 0;
            int     priority  = armor[0].priority;
            int     pStart    = 0;
            boolean pChange   = false;
            boolean pFinished = false;

            if (DEBUG)
            {
                for (ArmorProperties prop : armor)
                {
                    System.out.println(prop);
                }
                System.out.println("========================");
            }

            for (int x = 0; x < armor.length; x++)
            {
                total += armor[x].absorbRatio;
                if (x == armor.length - 1 || armor[x].priority != priority)
                {
                    if (armor[x].priority != priority)
                    {
                        total -= armor[x].absorbRatio;
                        x--;
                        pChange = true;
                    }
                    if (total > 1)
                    {
                        for (int y = start; y <= x; y++)
                        {
                            double newRatio = armor[y].absorbRatio / total;
                            if (newRatio * damage > armor[y].absorbMax)
                            {
                                armor[y].absorbRatio = (double)armor[y].absorbMax / damage;
                                total = 0;
                                for (int z = pStart; z <= y; z++)
                                {
                                    total += armor[z].absorbRatio;
                                }
                                start = y + 1;
                                x = y;
                                break;
                            }
                            else
                            {
                                armor[y].absorbRatio = newRatio;
                                pFinished = true;
                            }
                        }
                        if (pChange && pFinished)
                        {
                            damage -= (damage * total);
                            total = 0;
                            start = x + 1;
                            priority = armor[start].priority;
                            pStart = start;
                            pChange = false;
                            pFinished = false;
                            if (damage <= 0)
                            {
                                for (int y = x + 1; y < armor.length; y++)
                                {
                                    armor[y].absorbRatio = 0;
                                }
                                break;
                            }
                        }
                    }
                    else
                    {
                        for (int y = start; y <= x; y++)
                        {
                            total -= armor[y].absorbRatio;
                            if (damage * armor[y].absorbRatio > armor[y].absorbMax)
                            {
                                armor[y].absorbRatio = (double)armor[y].absorbMax / damage;
                            }
                            total += armor[y].absorbRatio;
                        }
                        damage -= (damage * total);
                        total = 0;
                        if (x != armor.length - 1)
                        {
                            start = x + 1;
                            priority = armor[start].priority;
                            pStart = start;
                            pChange = false;
                            if (damage <= 0)
                            {
                                for (int y = x + 1; y < armor.length; y++)
                                {
                                    armor[y].absorbRatio = 0;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (DEBUG)
            {
                for (ArmorProperties prop : armor)
                {
                    System.out.println(prop);
                }
            }
        }

        @Override
        public int compareTo(ArmorProperties o)
        {
            if (o.priority != priority)
            {
                return o.priority - priority;
            }
            double left =  (  absorbRatio == 0 ? 0 :   absorbMax * 100.0D /   absorbRatio);
            double right = (o.absorbRatio == 0 ? 0 : o.absorbMax * 100.0D / o.absorbRatio);
            return (int)(left - right);
        }

        @Override
        public String toString()
        {
            return String.format("%d, %d, %f, %d, %f, %f", priority, absorbMax, absorbRatio, (absorbRatio == 0 ? 0 : (int)(absorbMax * 100.0D / absorbRatio)), armor, toughness);
        }

        public ArmorProperties copy()
        {
            return new ArmorProperties(priority, absorbRatio, armor, toughness, absorbMax);
        }
    }
}
