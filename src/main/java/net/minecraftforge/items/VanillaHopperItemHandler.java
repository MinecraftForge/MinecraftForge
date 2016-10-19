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

public class VanillaHopperItemHandler extends InvWrapper
{
    private final TileEntityHopper hopper;

    public VanillaHopperItemHandler(TileEntityHopper hopper)
    {
        super(hopper);
        this.hopper = hopper;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (stack == null)
            return null;
        if (simulate || !hopper.mayTransfer())
            return super.insertItem(slot, stack, simulate);

        int curStackSize = stack.stackSize;
        ItemStack itemStack = super.insertItem(slot, stack, false);
        if (itemStack == null || curStackSize != itemStack.stackSize)
        {
            hopper.setTransferCooldown(8);
        }
        return itemStack;
    }
}
