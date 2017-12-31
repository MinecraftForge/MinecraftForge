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

package net.minecraftforge.items.itemhandlerobserver;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityItemHandlerObserver
{
    @CapabilityInject(IItemHandlerObservable.class)
    public static Capability<IItemHandlerObservable> ITEMHANDLER_OBSERVER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IItemHandlerObservable.class, new Capability.IStorage<IItemHandlerObservable>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IItemHandlerObservable> capability, IItemHandlerObservable instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IItemHandlerObservable> capability, IItemHandlerObservable instance, EnumFacing side, NBTBase nbt)
            {

            }
        }, DefaultObservable::new);
    }
}
