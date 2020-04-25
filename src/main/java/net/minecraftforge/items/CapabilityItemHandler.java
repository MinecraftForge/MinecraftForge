/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.concurrent.Callable;

public class CapabilityItemHandler
{
    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IItemHandler.class, new Capability.IStorage<IItemHandler>()
        {
            @Override
            public INBT writeNBT(Capability<IItemHandler> capability, IItemHandler instance, Direction side)
            {
                ListNBT nbtTagList = new ListNBT();
                int size = instance.getSlots();
                for (int i = 0; i < size; i++)
                {
                    ItemStack stack = instance.getStackInSlot(i);
                    if (!stack.isEmpty())
                    {
                        CompoundNBT itemTag = new CompoundNBT();
                        itemTag.putInt("Slot", i);
                        stack.write(itemTag);
                        nbtTagList.add(itemTag);
                    }
                }
                return nbtTagList;
            }

            @Override
            public void readNBT(Capability<IItemHandler> capability, IItemHandler instance, Direction side, INBT base)
            {
                if (!(instance instanceof IItemHandlerModifiable))
                    throw new RuntimeException("IItemHandler instance does not implement IItemHandlerModifiable");
                IItemHandlerModifiable itemHandlerModifiable = (IItemHandlerModifiable) instance;
                ListNBT tagList = (ListNBT) base;
                for (int i = 0; i < tagList.size(); i++)
                {
                    CompoundNBT itemTags = tagList.getCompound(i);
                    int j = itemTags.getInt("Slot");

                    if (j >= 0 && j < instance.getSlots())
                    {
                        itemHandlerModifiable.setStackInSlot(j, ItemStack.read(itemTags));
                    }
                }
            }
        }, ItemStackHandler::new);
    }

}
