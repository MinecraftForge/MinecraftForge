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

package net.minecraftforge.items.filter;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class FluidFilter implements IStackFilter
{
    private final FluidStack fluidStackToTest;
    private final MatchingStrategy matchingStrategy;

    public FluidFilter(FluidStack fluidStackToTest, MatchingStrategy matchingStrategy)
    {
        this.fluidStackToTest = fluidStackToTest;
        this.matchingStrategy = matchingStrategy;
    }

    public FluidFilter(FluidStack fluidStackToTest)
    {
        this.fluidStackToTest = fluidStackToTest;
        this.matchingStrategy = MatchingStrategy.EXCEED;
    }

    @Override
    public boolean test(ItemStack stack)
    {
        FluidStack fluidStack = FluidUtil.getFluidContained(stack);
        if (fluidStack != null)
        {
            switch (this.matchingStrategy)
            {
                case EXCEED:
                    return fluidStack.isFluidEqual(fluidStackToTest) && fluidStack.amount <= fluidStackToTest.amount;

                case EXACT:
                    return fluidStack.isFluidStackIdentical(fluidStackToTest);

                default: // Should be impossible
                    return false;
            }
        }
        return false;
    }

    public enum MatchingStrategy
    {
        /**
         * EXACT matching indicates that it will try matching exact amount of fluid.
         * Example: if the FluidIngredient asks for 500 mB water, a vanilla water bucket
         * will not be matched, as it holds 1000 mB water, 1000 != 500.
         */
        EXACT,

        /**
         * EXCEEDED matching is the default matching strategy, when not specified.
         * It will try matching those containers that can drain the specified amount of
         * fluid out. Example: if we ask for 500 mB water, a vanilla bucket
         * will be matched.
         */
        EXCEED,
    }
}
