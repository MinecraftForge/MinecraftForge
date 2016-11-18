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

package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class VanillaHopperItemHandler extends InvWrapper
{
    private final TileEntityHopper hopper;

    public VanillaHopperItemHandler(TileEntityHopper hopper)
    {
        super(hopper);
        this.hopper = hopper;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (stack.func_190926_b())
            return null;
//        if (simulate || !hopper.mayTransfer())
//            return super.insertItem(slot, stack, simulate);

        int curStackSize = stack.func_190916_E();
        ItemStack itemStack = super.insertItem(slot, stack, false);
//        if (itemStack == null || curStackSize != itemStack.func_190916_E())
//        {
//            hopper.setTransferCooldown(8);
//        }
        return itemStack;
    }
}
