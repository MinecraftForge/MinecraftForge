/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fluids;

import javax.annotation.Nonnull;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FluidUtil
{
    private FluidUtil()
    {
    }

    /**
     * @param fluidStack contents used to fill the bucket.
     *                   FluidStack is used instead of Fluid to preserve fluid NBT, the amount is ignored.
     * @return a filled vanilla bucket or filled universal bucket.
     *         Returns empty itemStack if none of the enabled buckets can hold the fluid.
     */
    @Nonnull
    public static ItemStack getFilledBucket(@Nonnull FluidStack fluidStack)
    {
        Fluid fluid = fluidStack.getFluid();

        if (!fluidStack.hasTag() || fluidStack.getTag().isEmpty())
        {
            if (fluid == Fluids.WATER)
            {
                return new ItemStack(Items.WATER_BUCKET);
            }
            else if (fluid == Fluids.LAVA)
            {
                return new ItemStack(Items.LAVA_BUCKET);
            }
        }

        return fluid.getAttributes().getBucket(fluidStack);
    }
}
