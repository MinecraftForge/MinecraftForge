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

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

/**
 * Handler for the the coloring of ItemStacks' effect overlays.
 * */
public interface IStackOverlayColor
{

    /**
     * Vanilla's default Item effect color value.
     * */
    int defaultOverlayColor = 0xFF8040CC;

    IStackOverlayColor VANILLA = new Vanilla();

    /**
     * Used for coloring the first effect pass.
     *
     * @param stack the ItemStack
     *
     * @return a hex int (ARGB) used for coloring the effect
     * */
    int getFirstPassColor(ItemStack stack);

    /**
     * Used for coloring the second effect pass.
     *
     * @param stack the ItemStack
     *
     * @return a hex int (ARGB) used for coloring the effect
     * */
    int getSecondPassColor(ItemStack stack);

    /**
     * Checks for the first applicable handler when multiple are subscribed to an item.
     *
     * @return true to override vanilla/other subscribers' handlers
     * */
    boolean useForStack(ItemStack stack);

    class Vanilla implements IStackOverlayColor
    {

        @Override
        public int getFirstPassColor(ItemStack stack)
        {
            return defaultOverlayColor;
        }

        @Override
        public int getSecondPassColor(ItemStack stack)
        {
            return defaultOverlayColor;
        }

        @Override
        public boolean useForStack(ItemStack stack)
        {
            return true;
        }
    }

    final class SubscriptionWrapper implements IStackOverlayColor
    {
        IStackOverlayColor[] subscribers;

        IStackOverlayColor cachedPointer;

        public SubscriptionWrapper(IStackOverlayColor toSubscribe)
        {
            subscribers = new IStackOverlayColor[] {toSubscribe};
        }

        public void addSubscription(IStackOverlayColor toSubscribe)
        {
            if(Loader.instance().hasReachedState(LoaderState.AVAILABLE))
            {
                FMLLog.bigWarning("Skipping subscription of {} to StackOverlayColors because it's too late in the load cycle..", toSubscribe.getClass());
                return; //Trump bad behavior
            }
            IStackOverlayColor[] grow = new IStackOverlayColor[subscribers.length + 1];
            for(int i = 0; i < subscribers.length; i++)
                grow[i] = subscribers[i];
            grow[subscribers.length] = toSubscribe;
            subscribers = grow;
        }

        @Override
        public int getFirstPassColor(ItemStack stack)
        {
            for(int i = 0; i < subscribers.length; i++)
                if((cachedPointer = subscribers[i]).useForStack(stack))
                    return cachedPointer.getFirstPassColor(stack);
            cachedPointer = VANILLA;
            return cachedPointer.getFirstPassColor(stack);
        }

        @Override
        public int getSecondPassColor(ItemStack stack)
        {
            return cachedPointer.getSecondPassColor(stack);
        }

        @Override
        public boolean useForStack(ItemStack stack)
        {
            return true;
        }
    }
}
