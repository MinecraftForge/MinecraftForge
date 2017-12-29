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

package net.minecraftforge.items.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;

import javax.annotation.Nonnull;
import java.util.OptionalInt;

public class HopperWrapper extends InvWrapper
{
    private final TileEntityHopper hopper;

    public HopperWrapper(TileEntityHopper hopper)
    {
        super(hopper);
        this.hopper = hopper;
    }

    @Nonnull
    @Override
    public ItemStack insert(OptionalInt slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (simulate){
            return super.insert(slot, stack, simulate);
        }

        boolean wasEmpty = getInventory().isEmpty();

        int originalStackSize = stack.getCount();
        ItemStack remainder = super.insert(slot, stack, false);

        if (wasEmpty && originalStackSize > remainder.getCount())
        {
            if (!hopper.mayTransfer())
            {
                // This cooldown is always set to 8 in vanilla with one exception:
                // Hopper -> Hopper transfer sets this cooldown to 7 when this hopper
                // has not been updated as recently as the one pushing items into it.
                // This vanilla behavior is preserved by VanillaInventoryCodeHooks#insertStack,
                // the cooldown is set properly by the hopper that is pushing items into this one.
                hopper.setTransferCooldown(8);
            }
        }
        return remainder;


    }
}
