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

package net.minecraftforge.client.overlay;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

/**
 * Handler for the the coloring of armor overlay.
 * */
public interface IArmorOverlayColor
{

    /**
     * Wrapper for vanilla. Always returns {@value defaultOverlayColor}
     * */
    IArmorOverlayColor VANILLA = new Vanilla();

    /**
     * Vanilla's default armor glint color value. Uses half the alpha value than the Item color.
     * */
    int defaultOverlayColor = 0xAA8040CC;

    /**
     * Used for coloring the first overlay pass.
     *
     * @param stack the ItemStack
     * @param wearer the Entity in question
     * @param slot the slot the armor is in
     *
     * @return a hex int (ARGB) used for coloring the effect
     * */
    int getFirstPassColor(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot);

    /**
     * Used for coloring the second overlay pass.
     *
     * @param stack the ItemStack
     * @param wearer the Entity in question
     * @param slot the slot the armor is in
     *
     * @return a hex int (ARGB) used for coloring the effect
     * */
    int getSecondPassColor(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot);

    /**
     * Checks for the first applicable handler when multiple are subscribed to an item.
     *
     * @return true to override vanilla/other subscribers' handlers
     * */
    boolean useForStack(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot);

    final class Vanilla implements IArmorOverlayColor
    {

        @Override
        public int getFirstPassColor(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            return defaultOverlayColor;
        }

        @Override
        public int getSecondPassColor(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            return defaultOverlayColor;
        }

        @Override
        public boolean useForStack(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            return true;
        }

    }

    final class SubscriptionWrapper implements IArmorOverlayColor
    {
        IArmorOverlayColor[] subscribers;

        IArmorOverlayColor cached;

        public SubscriptionWrapper(IArmorOverlayColor toSubscribe)
        {
            subscribers = new IArmorOverlayColor[] {toSubscribe};
        }

        public void addSubscription(IArmorOverlayColor toSubscribe)
        {
            if(Loader.instance().hasReachedState(LoaderState.AVAILABLE))
            {
                FMLLog.bigWarning("Skipping subscription of {} to ArmorOverlayColors because it's too late in the load cycle..", toSubscribe.getClass());
                return; //Trump bad behavior
            }
            IArmorOverlayColor[] grow = new IArmorOverlayColor[subscribers.length + 1];
            for(int i = 0; i < subscribers.length; i++)
                grow[i] = subscribers[i];
            grow[subscribers.length] = toSubscribe;
            subscribers = grow;
        }

        @Override
        public int getFirstPassColor(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            for(int i = 0; i < subscribers.length; i++)
                if((cached = subscribers[i]).useForStack(stack, wearer, slot))
                    return cached.getFirstPassColor(stack, wearer, slot);
            return (cached = VANILLA).getFirstPassColor(stack, wearer, slot);
        }

        @Override
        public int getSecondPassColor(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            return cached.getSecondPassColor(stack, wearer, slot);
        }

        @Override
        public boolean useForStack(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            return true;
        }
    }
}
