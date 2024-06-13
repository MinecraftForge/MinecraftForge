/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

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
@SuppressWarnings("deprecation")
public final class CapabilityDispatcher implements INBTSerializable<CompoundTag>, ICapabilityProvider {
    private final ICapabilityProvider[] caps;
    private final INBTSerializable<Tag>[] writers;
    private final String[] names;
    private final List<Runnable> listeners;

    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, List<Runnable> listeners) {
        this(list, listeners, null);
    }

    @SuppressWarnings("unchecked")
    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, List<Runnable> listeners, @Nullable ICapabilityProvider parent) {
        List<ICapabilityProvider> lstCaps = new ArrayList<>();
        List<INBTSerializable<Tag>> lstWriters = new ArrayList<>();
        List<String> lstNames = new ArrayList<>();
        this.listeners = listeners;

        if (parent != null) { // Parents go first!
            lstCaps.add(parent);
            if (parent instanceof INBTSerializable) {
                lstWriters.add((INBTSerializable<Tag>)parent);
                lstNames.add("Parent");
            }
        }

        for (var entry : list.entrySet()) {
            ICapabilityProvider prov = entry.getValue();
            lstCaps.add(prov);
            if (prov instanceof INBTSerializable) {
                lstWriters.add((INBTSerializable<Tag>)prov);
                lstNames.add(entry.getKey().toString());
            }
        }

        caps = lstCaps.toArray(new ICapabilityProvider[lstCaps.size()]);
        writers = lstWriters.toArray(new INBTSerializable[lstWriters.size()]);
        names = lstNames.toArray(new String[lstNames.size()]);
    }


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        for (ICapabilityProvider c : caps) {
            LazyOptional<T> ret = c.getCapability(cap, side);
            //noinspection ConstantConditions
            if (ret == null) {
                throw new RuntimeException(
                        String.format(
                                Locale.ENGLISH,
                                "Provider %s.getCapability() returned null; return LazyOptional.empty() instead!",
                                c.getClass().getTypeName()
                        )
                );
            }

            if (ret.isPresent())
                return ret;
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
        CompoundTag nbt = new CompoundTag();
        for (int x = 0; x < writers.length; x++)
            nbt.put(names[x], writers[x].serializeNBT(registryAccess));
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
        for (int x = 0; x < writers.length; x++) {
            if (nbt.contains(names[x]))
                writers[x].deserializeNBT(registryAccess, nbt.get(names[x]));
        }
    }

    public void invalidate() {
        this.listeners.forEach(Runnable::run);
    }
}
