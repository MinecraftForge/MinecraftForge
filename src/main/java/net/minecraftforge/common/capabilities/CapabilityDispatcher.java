/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A Capability Dispatcher is a manager object that contains the result of the
 * {@link AttachCapabilitiesEvent} and makes the attached capabilities visible on
 * the parent object.
 * 
 * @param <T> The type of the parent object.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CapabilityDispatcher<T extends ICapabilityProvider> implements INBTSerializable<CompoundTag>, ICapabilityProvider
{

    private final T owner;
    private final Map<CapabilityType<?>, IAttachedCapabilityProvider<?, T>> caps;
    private final Map<ResourceLocation, IAttachedCapabilityProvider<?, T>> byName;
    private boolean isValid = true;

    public CapabilityDispatcher(AttachCapabilitiesEvent<T> event, T owner)
    {
        this.caps = event.getCapabilities();
        this.byName = event.getCapabilitiesByName();
        this.owner = owner;
    }

    /**
     * Internal copy constructor.
     * @see {@link #copy(ICapabilityProvider)}
     * @see {@link IAttachedCapabilityProvider#copy(ICapabilityProvider)}
     */
    private CapabilityDispatcher(CapabilityDispatcher<T> other, T newOwner)
    {
        this.caps = other.caps.values().stream().map(old -> old.copy(newOwner)).collect(Collectors.toMap(IAttachedCapabilityProvider::getType, t -> t));
        this.byName = this.caps.values().stream().collect(Collectors.toMap(IAttachedCapabilityProvider::getId, t -> t));
        this.owner = newOwner;
    }

    /**
     * Checks if another capability dispatcher is equivalent to this one.<br>
     * Equivalence in this case means either dispatcher could replace the other one.<br>
     * This is used in itemstack merging.<br>
     * 
     * Equivalence is checked by asking all attached providers to compare against their
     * presence on the other dispatcher.  Equivalence must be unanimous.
     * 
     * @param other The other dispatcher being checked against.
     * @return If this dispatcher is equivalent to the other dispatcher.
     * 
     * @see {@link IAttachedCapabilityProvider#isEquivalentTo(IAttachedCapabilityProvider)}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean isEquivalentTo(@Nullable CapabilityDispatcher<T> other)
    {
        Set<ResourceLocation> keys = new HashSet<>();
        keys.addAll(byName.keySet());
        if(other != null) keys.addAll(other.byName.keySet());
        for(ResourceLocation id : keys) {
            var ourProv = this.byName.get(id);
            var theirProv = other == null ? null : other.byName.get(id);        
            if(ourProv == null && !theirProv.isEquivalentTo(null)) return false;
            else if(theirProv == null && !ourProv.isEquivalentTo(null)) return false;
            else if(!ourProv.isEquivalentTo((IAttachedCapabilityProvider) theirProv)) return false;
        }
        return true;
    }

    public CapabilityDispatcher<T> copy(T newOwner)
    {
        return new CapabilityDispatcher<>(this, newOwner);
    }

    @Override
    public <C> Capability<C> getCapability(CapabilityType<C> cap, @Nullable Direction side)
    {
    	if(!isValid) return Capability.empty();
        IAttachedCapabilityProvider<?, T> provider = caps.get(cap);
        if(provider == null) return Capability.empty();
        return provider.getCapability(this.owner.isDirectionSensitive() ? side : null).cast();
    }

    @Override
    public void invalidateCaps()
    {
    	this.isValid = false;
        caps.values().forEach(IAttachedCapabilityProvider::invalidateCaps);
    }

    @Override
    public void reviveCaps()
    {
    	this.isValid = true;
        caps.values().forEach(IAttachedCapabilityProvider::reviveCaps);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        
        if(!isValid) return tag;
        
        this.caps.values().forEach(prov -> {
            CompoundTag provTag = prov.serializeNBT();
            if(provTag != null) tag.put(prov.getId().toString(), provTag);
        });
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag)
    {
    	if(!isValid) return;
    	
        for(String s : tag.getAllKeys()) {
            ResourceLocation id = new ResourceLocation(s);
            var prov = this.byName.get(id);
            if(prov != null) prov.deserializeNBT(tag.getCompound(s));
        }
    }

}