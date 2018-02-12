/*
 * Minecraft Forge
 * Copyright (c) 2017.
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

package net.minecraftforge.items.customslots;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

/**
 * To be exposed by items
 */
public class CapabilityExtensionSlotItem
{
    // Special slot IDs
    public static final ResourceLocation ANY_SLOT = new ResourceLocation("forge:any");
    public static final ImmutableSet<ResourceLocation> ANY_SLOT_SET = ImmutableSet.of(ANY_SLOT);

    // The Capability
    @CapabilityInject(IExtensionSlotItem.class)
    public static Capability<IExtensionSlotItem> INSTANCE = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IExtensionSlotItem.class, new Storage(), DefaultImplementation::new);
    }

    // Dummy storage -- there is nothing to save for this capability
    static class Storage implements Capability.IStorage<IExtensionSlotItem>
    {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IExtensionSlotItem> capability, IExtensionSlotItem instance, EnumFacing side)
        {
            return null;
        }

        @Override
        public void readNBT(Capability<IExtensionSlotItem> capability, IExtensionSlotItem instance, EnumFacing side, NBTBase nbt)
        {

        }
    }

    // Dummy default instance -- the interface provides all the boilerplate as default methods
    private static class DefaultImplementation implements IExtensionSlotItem
    {
    }
}

