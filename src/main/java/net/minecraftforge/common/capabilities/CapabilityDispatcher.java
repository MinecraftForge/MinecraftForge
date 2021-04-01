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

package net.minecraftforge.common.capabilities;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

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
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CapabilityDispatcher implements INBTSerializable<CompoundNBT>, ICapabilityProvider, INetworkCapability
{
    private static final String SYNC_END_MARKER = "END";

    private ICapabilityProvider[] caps;
    private INBTSerializable<INBT>[] writers;
    private String[] writerNames;
    private final List<Runnable> listeners;
    private final Object2ObjectOpenHashMap<String, INetworkCapability> networkCapabilities = new Object2ObjectOpenHashMap<>();

    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, List<Runnable> listeners)
    {
        this(list, listeners, null);
    }

    @SuppressWarnings("unchecked")
    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, List<Runnable> listeners, @Nullable ICapabilityProvider parent)
    {
        List<ICapabilityProvider> lstCaps = Lists.newArrayList();
        List<INBTSerializable<INBT>> lstWriters = Lists.newArrayList();
        List<String> lstNames = Lists.newArrayList();
        this.listeners = listeners;

        if (parent != null) // Parents go first!
        {
            lstCaps.add(parent);
            if (parent instanceof INBTSerializable)
            {
                lstWriters.add((INBTSerializable<INBT>)parent);
                lstNames.add("Parent");
            }
        }

        for (Map.Entry<ResourceLocation, ICapabilityProvider> entry : list.entrySet())
        {
            ICapabilityProvider prov = entry.getValue();
            lstCaps.add(prov);
            if (prov instanceof INBTSerializable)
            {
                lstWriters.add((INBTSerializable<INBT>)prov);
                lstNames.add(entry.getKey().toString());
            }
            if (prov instanceof INetworkCapability)
            {
                this.networkCapabilities.put(entry.getKey().toString(), (INetworkCapability)prov);
            }
        }

        caps = lstCaps.toArray(new ICapabilityProvider[lstCaps.size()]);
        writers = lstWriters.toArray(new INBTSerializable[lstWriters.size()]);
        writerNames = lstNames.toArray(new String[lstNames.size()]);
    }


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        for (ICapabilityProvider c : caps)
        {
            LazyOptional<T> ret = c.getCapability(cap, side);
            //noinspection ConstantConditions
            if (ret == null)
            {
                throw new RuntimeException(
                        String.format(
                                "Provider %s.getCapability() returned null; return LazyOptional.empty() instead!",
                                c.getClass().getTypeName()
                        )
                );
            }
            if (ret.isPresent())
            {
                return ret;
            }
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        for (int x = 0; x < writers.length; x++)
        {
            nbt.put(writerNames[x], writers[x].serializeNBT());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        for (int x = 0; x < writers.length; x++)
        {
            if (nbt.contains(writerNames[x]))
            {
                writers[x].deserializeNBT(nbt.get(writerNames[x]));
            }
        }
    }

    public boolean areCompatible(@Nullable CapabilityDispatcher other) //Called from ItemStack to compare equality.
    {                                                        // Only compares serializeable caps.
        if (other == null) return this.writers.length == 0;  // Done this way so we can do some pre-checks before doing the costly NBT serialization and compare
        if (this.writers.length == 0) return other.writers.length == 0;
        return this.serializeNBT().equals(other.serializeNBT());
    }

    public void invalidate()
    {
        this.listeners.forEach(Runnable::run);
    }

    @Override
    public void encode(PacketBuffer out, boolean writeAll)
    {
        ObjectIterator<Object2ObjectOpenHashMap.Entry<String, INetworkCapability>>  it = networkCapabilities.object2ObjectEntrySet().fastIterator();
        while (it.hasNext())
        {
            Object2ObjectOpenHashMap.Entry<String, INetworkCapability> entry = it.next();
            if (!writeAll && !entry.getValue().requiresSync())
                continue;
            out.writeUtf(entry.getKey(), 0x100);
            PacketBuffer capabilityData = new PacketBuffer(Unpooled.buffer());
            entry.getValue().encode(capabilityData, writeAll);
            out.writeVarInt(capabilityData.readableBytes());
            out.writeBytes(capabilityData);
        }
        out.writeUtf(SYNC_END_MARKER, 0x100);
    }

    @Override
    public void decode(PacketBuffer in)
    {
        String name;
        while (!(name = in.readUtf(0x100)).equals(SYNC_END_MARKER))
        {
            int dataSize = in.readVarInt();
            INetworkCapability cap = networkCapabilities.get(name);
            if (cap == null)
            {
                in.readerIndex(in.readerIndex() + dataSize);
                continue;
            }
            cap.decode(in);
        }
    }

    @Override
    public boolean requiresSync()
    {
        return networkCapabilities.values().stream().anyMatch(INetworkCapability::requiresSync);
    }
}