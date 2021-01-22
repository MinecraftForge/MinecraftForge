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

package net.minecraftforge.fluids.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

/**
 * ItemStacks handled by an {@link IFluidHandler} may change, so this class allows
 * users of the fluid handler to get the container after it has been used.
 */
public interface IFluidHandlerItem extends IFluidHandler
{
    /**
     * Get the container currently acted on by this fluid handler.
     * The ItemStack may be different from its initial state, in the case of fluid containers that have different items
     * for their filled and empty states.
     * May be an empty item if the container was drained and is consumable.
     */
    @Nonnull
    ItemStack getContainer();
}
