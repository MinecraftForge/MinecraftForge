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

package net.minecraftforge.items.holder;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class CapabilityItemHolder
{
    /**
     * this is only intended for ItemStack to get slots its ItemHolder.
     * and is NOT intended for external use bye other mods, for that use the IItemHandler
     */
    @CapabilityInject(IItemHolder.class)
    public static IItemHolder ITEM_HOLDER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IItemHolder.class, new Capability.IStorage<IItemHolder>()
        {

            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IItemHolder> capability, IItemHolder instance, EnumFacing side)
            {
                if (!(instance instanceof INBTSerializable))
                    throw new IllegalArgumentException("the IItemHolder must implement INBTSerializable");
                else return ((INBTSerializable) instance).serializeNBT();
            }

            @Override
            @SuppressWarnings("unchecked")
            public void readNBT(Capability<IItemHolder> capability, IItemHolder instance, EnumFacing side, NBTBase nbt)
            {
                if (!(instance instanceof INBTSerializable))
                    throw new IllegalArgumentException("the IItemHolder must implement INBTSerializable");
                else ((INBTSerializable) instance).deserializeNBT(nbt);
            }
        }, () -> new ItemHolder(1));
    }
}
