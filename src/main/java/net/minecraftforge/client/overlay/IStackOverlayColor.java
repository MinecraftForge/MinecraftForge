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
    }
}
