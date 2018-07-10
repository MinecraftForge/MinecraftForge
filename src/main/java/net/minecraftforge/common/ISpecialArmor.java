/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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
    //TODO: Change 'int slot' to EnumArmorType
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
    ArmorProperties getProperties(EntityLivingBase player, @Nonnull ItemStack armor, DamageSource source, double damage, int slot);

    /**
     * Get the displayed effective armor.
     *
     * @param player The player wearing the armor.
     * @param armor The ItemStack of the armor item itself.
     * @param slot The armor slot the item is in.
     * @return The number of armor points for display, 2 per shield.
     */
    int getArmorDisplay(EntityPlayer player, @Nonnull ItemStack armor, int slot);

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
    void damageArmor(EntityLivingBase entity, @Nonnull ItemStack stack, DamageSource source, int damage, int slot);

    /**
    * Simple check to see if the armor should interact with "Unblockable" damage
    * sources. A fair number of vanilla damage sources have this tag, such as
    * Anvils, Falling, Fire, and Magic.
    *
    * Returning true here means that the armor is able to meaningfully respond
    * to this damage source. Otherwise, no interaction is allowed.
    */
    default boolean handleUnblockableDamage(EntityLivingBase entity, @Nonnull ItemStack armor, DamageSource source, double damage, int slot)
    {
        return false;
    }

    public static class ArmorProperties implements Comparable<ArmorProperties>
    {
        public int    Priority          = 0;
        public int    AbsorbMax         = Integer.MAX_VALUE;
        public double AbsorbRatio       = 0;
        public double Armor             = 0;        //Additional armor, separate from the armor added by vanilla attributes.
        public double Toughness         = 0;        //Additional toughness, separate from the armor added by vanilla attributes.
        public int Slot                 = 0;
        private static final boolean DEBUG = false; //Only enable this if you wish to be spammed with debugging information.
                                                    //Left it in because I figured it'd be useful for modders developing custom armor.

        
        public ArmorProperties(int priority, double ratio, int max)
        {
            Priority    = priority;
            AbsorbRatio = ratio;
            Armor       = 0;
            Toughness   = 0;
            AbsorbMax   = max;
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

            if (source.isUnblockable())
            {
                totalArmor = 0;
                totalToughness = 0;
            }

            ArrayList<ArmorProperties> dmgVals = new ArrayList<ArmorProperties>();
            for (int slot = 0; slot < inventory.size(); slot++)
            {
                ItemStack stack = inventory.get(slot);
                
                if (stack.isEmpty())
                {
                    continue;
                }
                
                ArmorProperties prop = null;
                if (stack.getItem() instanceof ISpecialArmor)
                {
                    if (!source.isUnblockable() || ((ISpecialArmor) stack.getItem()).handleUnblockableDamage(entity, stack, source, damage, slot)) {
                        ISpecialArmor armor = (ISpecialArmor)stack.getItem();
                        prop = armor.getProperties(entity, stack, source, damage, slot).copy();
                        totalArmor += prop.Armor;
                        totalToughness += prop.Toughness;
                    }
                }
                else if (stack.getItem() instanceof ItemArmor && !source.isUnblockable())
                {
                    ItemArmor armor = (ItemArmor)stack.getItem();
                    prop = new ArmorProperties(0, 0, Integer.MAX_VALUE);
                    prop.Armor = armor.damageReduceAmount;
                    prop.Toughness = armor.toughness;
                }
                if (prop != null)
                {
                    prop.Slot = slot;
                    dmgVals.add(prop);
                }
            }
            if (dmgVals.size() > 0)
            {
                ArmorProperties[] props = dmgVals.toArray(new ArmorProperties[dmgVals.size()]);
                StandardizeList(props, damage);
                int level = props[0].Priority;
                double ratio = 0;
                for (ArmorProperties prop : props)
                {
                    if (level != prop.Priority)
                    {
                        damage -= (damage * ratio);
                        ratio = 0;
                        level = prop.Priority;
                    }
                    ratio += prop.AbsorbRatio;

                    double absorb = damage * prop.AbsorbRatio;
                    if (absorb > 0)
                    {
                        ItemStack stack = inventory.get(prop.Slot);
                        int itemDamage = (int)Math.max(1, absorb);
                        if (stack.getItem() instanceof ISpecialArmor)
                        {
                            ((ISpecialArmor)stack.getItem()).damageArmor(entity, stack, source, itemDamage, prop.Slot);
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
                            /*if (entity instanceof EntityPlayer)
                            {
                                stack.onItemDestroyedByUse((EntityPlayer)entity);
                            }*/
                            inventory.set(prop.Slot, ItemStack.EMPTY);
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
        private static void StandardizeList(ArmorProperties[] armor, double damage)
        {
            Arrays.sort(armor);

            int     start     = 0;
            double  total     = 0;
            int     priority  = armor[0].Priority;
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
                total += armor[x].AbsorbRatio;
                if (x == armor.length - 1 || armor[x].Priority != priority)
                {
                    if (armor[x].Priority != priority)
                    {
                        total -= armor[x].AbsorbRatio;
                        x--;
                        pChange = true;
                    }
                    if (total > 1)
                    {
                        for (int y = start; y <= x; y++)
                        {
                            double newRatio = armor[y].AbsorbRatio / total;
                            if (newRatio * damage > armor[y].AbsorbMax)
                            {
                                armor[y].AbsorbRatio = (double)armor[y].AbsorbMax / damage;
                                total = 0;
                                for (int z = pStart; z <= y; z++)
                                {
                                    total += armor[z].AbsorbRatio;
                                }
                                start = y + 1;
                                x = y;
                                break;
                            }
                            else
                            {
                                armor[y].AbsorbRatio = newRatio;
                                pFinished = true;
                            }
                        }
                        if (pChange && pFinished)
                        {
                            damage -= (damage * total);
                            total = 0;
                            start = x + 1;
                            priority = armor[start].Priority;
                            pStart = start;
                            pChange = false;
                            pFinished = false;
                            if (damage <= 0)
                            {
                                for (int y = x + 1; y < armor.length; y++)
                                {
                                    armor[y].AbsorbRatio = 0;
                                }
                                break;
                            }
                        }
                    }
                    else
                    {
                        for (int y = start; y <= x; y++)
                        {
                            total -= armor[y].AbsorbRatio;
                            if (damage * armor[y].AbsorbRatio > armor[y].AbsorbMax)
                            {
                                armor[y].AbsorbRatio = (double)armor[y].AbsorbMax / damage;
                            }
                            total += armor[y].AbsorbRatio;
                        }
                        damage -= (damage * total);
                        total = 0;
                        if (x != armor.length - 1)
                        {
                            start = x + 1;
                            priority = armor[start].Priority;
                            pStart = start;
                            pChange = false;
                            if (damage <= 0)
                            {
                                for (int y = x + 1; y < armor.length; y++)
                                {
                                    armor[y].AbsorbRatio = 0;
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
            if (o.Priority != Priority)
            {
                return o.Priority - Priority;
            }
            double left =  (  AbsorbRatio == 0 ? 0 :   AbsorbMax * 100.0D /   AbsorbRatio);
            double right = (o.AbsorbRatio == 0 ? 0 : o.AbsorbMax * 100.0D / o.AbsorbRatio);
            return (int)(left - right);
        }

        @Override
        public String toString()
        {
            return String.format("%d, %d, %f, %d", Priority, AbsorbMax, AbsorbRatio, (AbsorbRatio == 0 ? 0 : (int)(AbsorbMax * 100.0D / AbsorbRatio)));
        }

        public ArmorProperties copy()
        {
            ArmorProperties copy = new ArmorProperties(Priority, AbsorbRatio, AbsorbMax);
            copy.Armor = Armor;
            copy.Toughness = Toughness;
            return copy;
        }
    }
}
