/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemArmor;
import net.minecraft.src.ItemStack;

public class ArmorProperties implements Comparable<ArmorProperties>
{
    public int    Priority    = 0;
    public int    AbsorbMax   = Integer.MAX_VALUE;
    public double AbsorbRatio = 0;
    public int    Slot        = 0;
    private static final boolean DEBUG = false; //Only enable this if you wish to be spamed with debugging information.
                                                //Left it in because I figured it'd be useful for modders developing custom armor.

    public ArmorProperties(int priority, double ratio, int max)
    {
        Priority    = priority;
        AbsorbRatio = ratio;
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
    public static int ApplyArmor(EntityLiving entity, ItemStack[] inventory, DamageSource source, double damage)
    {
        if (DEBUG)
        {
            System.out.println("Start: " + damage + " " + (damage * 25));
        }
        damage *= 25;
        ArrayList<ArmorProperties> dmgVals = new ArrayList<ArmorProperties>();
        for (int x = 0; x < inventory.length; x++)
        {
            ItemStack stack = inventory[x];
            if (stack == null)
            {
                continue;
            }
            ArmorProperties prop = null;
            if (stack.getItem() instanceof ISpecialArmor)
            {
                ISpecialArmor armor = (ISpecialArmor)stack.getItem();
                prop = armor.getProperties(entity, stack, source, damage / 25D, x).copy();
            }
            else if (stack.getItem() instanceof ItemArmor && !source.isUnblockable())
            {
                ItemArmor armor = (ItemArmor)stack.getItem();
                prop = new ArmorProperties(0, armor.damageReduceAmount / 25D, armor.getMaxDamage() + 1 - stack.getItemDamage());
            }
            if (prop != null)
            {
                prop.Slot = x;
                dmgVals.add(prop);
            }
        }
        if (dmgVals.size() > 0)
        {
            ArmorProperties[] props = dmgVals.toArray(new ArmorProperties[0]);
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
                    ItemStack stack = inventory[prop.Slot];
                    int itemDamage = (int)(absorb / 25D < 1 ? 1 : absorb / 25D);
                    if (stack.getItem() instanceof ISpecialArmor)
                    {
                        ((ISpecialArmor)stack.getItem()).damageArmor(entity, stack, source, itemDamage, prop.Slot);
                    }
                    else
                    {
                        if (DEBUG)
                        {
                            System.out.println("Item: " + stack.toString() + " Absorbed: " + (absorb / 25D) + " Damaged: " + itemDamage);
                        }
                        stack.damageItem(itemDamage, entity);
                    }
                    if (stack.stackSize <= 0)
                    {
                        if (entity instanceof EntityPlayer)
                        {
                            stack.onItemDestroyedByUse((EntityPlayer)entity);
                        }
                        inventory[prop.Slot] = null;
                    }
                }
            }
            damage -= (damage * ratio);
        }
        damage += entity.carryoverDamage;
        if (DEBUG)
        {
            System.out.println("Return: " + (int)(damage / 25D) + " " + damage);
        }
        entity.carryoverDamage = (int)damage % 25;
        return (int)(damage / 25D);
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
                            armor[y].AbsorbRatio = (double)armor[y].AbsorbMax / (double)damage;
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

    public String toString()
    {
        return String.format("%d, %d, %f, %d", Priority, AbsorbMax, AbsorbRatio, (AbsorbRatio == 0 ? 0 : (int)(AbsorbMax * 100.0D / AbsorbRatio)));
    }

    public ArmorProperties copy()
    {
        return new ArmorProperties(Priority, AbsorbRatio, AbsorbMax);
    }
}
