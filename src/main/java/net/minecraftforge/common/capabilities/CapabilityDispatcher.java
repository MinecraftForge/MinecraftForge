/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.common.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A high-speed implementation of a capability delegator.
 * This is used to wrap the results of the AttachCapabilitiesEvent.
 * It is HIGHLY recommended that you DO NOT use this approach unless
 * you MUST delegate to multiple providers instead just implement y
 * our handlers using normal if statements.
 *
 * Internally the handlers are baked into arrays for fast iteration.
 * The ResourceLocations will be used for the NBT Key when serializing.
 */
public final class CapabilityDispatcher implements INBTSerializable<NBTTagCompound>, ICapabilityProvider, ICapabilityShareTag<NBTTagCompound>
{
    private ICapabilityProvider[] caps;
    private INBTSerializable<NBTBase>[] writers;
    private String[] writerNames;
    private ICapabilityShareTag<NBTBase>[] shareTags;
    private String[] shareTagNames;

    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list)
    {
        this(list, null);
    }

    @SuppressWarnings("unchecked")
    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, @Nullable ICapabilityProvider parent)
    {
        List<ICapabilityProvider> lstCaps = Lists.newArrayList();
        List<INBTSerializable<NBTBase>> lstWriters = Lists.newArrayList();
        List<String> lstNames = Lists.newArrayList();
        List<ICapabilityShareTag<NBTBase>> lstShareTags = Lists.newArrayList();
        List<String> lstShareTagNames = Lists.newArrayList();

        if (parent != null) // Parents go first!
        {
            lstCaps.add(parent);
            if (parent instanceof INBTSerializable)
            {
                lstWriters.add((INBTSerializable<NBTBase>)parent);
                lstNames.add("Parent");
            }
        }

        for (Map.Entry<ResourceLocation, ICapabilityProvider> entry : list.entrySet())
        {
            ICapabilityProvider prov = entry.getValue();
            lstCaps.add(prov);
            if (prov instanceof INBTSerializable)
            {
                lstWriters.add((INBTSerializable<NBTBase>)prov);
                lstNames.add(entry.getKey().toString());
            }
            if (prov instanceof ICapabilityShareTag)
            {
                lstShareTags.add((ICapabilityShareTag<NBTBase>)prov);
                lstShareTagNames.add(entry.getKey().toString());
            }
        }

        caps = lstCaps.toArray(new ICapabilityProvider[lstCaps.size()]);

        if (lstShareTags.size() > 0)
        {
            writers = lstWriters.toArray(new INBTSerializable[lstWriters.size()]);
            writerNames = lstNames.toArray(new String[lstNames.size()]);
        }

        if (lstShareTags.size() > 0)
        {
            shareTags = lstShareTags.toArray(new ICapabilityShareTag[lstShareTags.size()]);
            shareTagNames = lstShareTagNames.toArray(new String[lstShareTagNames.size()]);
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        for (ICapabilityProvider cap : caps)
        {
            if (cap.hasCapability(capability, facing))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        for (ICapabilityProvider cap : caps)
        {
            T ret = cap.getCapability(capability, facing);
            if (ret != null)
            {
                return ret;
            }
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        if (writers == null)
            return nbt;
        for (int x = 0; x < writers.length; x++)
        {
            nbt.setTag(writerNames[x], writers[x].serializeNBT());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (writers == null)
            return;
        for (int x = 0; x < writers.length; x++)
        {
            if (nbt.hasKey(writerNames[x]))
            {
                writers[x].deserializeNBT(nbt.getTag(writerNames[x]));
            }
        }
    }

    /**
     *  Called from ItemStack to compare equality.
     *  Only compares serializeable caps.
     *  Done this way so we can do some pre-checks before doing the costly NBT serialization and compare
     * @param other The other dispatcher
     * @return True if they are serialization-compatible
     */
    public boolean areCompatible(CapabilityDispatcher other)
    {
        if (other == null) return writers == null || this.writers.length == 0;
        if (writers == null || this.writers.length == 0) return other.writers == null || other.writers.length == 0;
        return this.serializeNBT().equals(other.serializeNBT());
    }

    @Override
    @Nullable
    public NBTTagCompound serializeShareTag()
    {
        if (shareTags == null || shareTags.length == 0)
            return null;
        NBTTagCompound nbt = new NBTTagCompound();
        for (int x = 0; x < shareTags.length; x++)
        {
            nbt.setTag(shareTagNames[x], shareTags[x].serializeShareTag());
        }
        return nbt;
    }

    @Override
    public void deserializeShareTag(NBTTagCompound nbt)
    {
        for (int x = 0; x < shareTags.length; x++)
        {
            if (nbt.hasKey(shareTagNames[x]))
            {
                shareTags[x].deserializeShareTag(nbt.getTag(shareTagNames[x]));
            }
        }
    }
}
