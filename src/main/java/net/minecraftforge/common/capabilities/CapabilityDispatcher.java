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
 * A high-speed implementation of a capability delegator. This is used to wrap
 * the results of the AttachCapabilitiesEvent.<br>
 * The caps are kept in a 2-D array for fast accesses, and are serialized and compared as needed.<br>
 * Only capabilities marked as serializable or comparable will be used for such purposes.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CapabilityDispatcher<T extends ICapabilityProvider> implements INBTSerializable<CompoundTag>, ICapabilityProvider {

	private final T owner;
	private final Map<CapabilityType<?>, IAttachedCapabilityProvider<?, T>> caps;
	private final Map<ResourceLocation, IAttachedCapabilityProvider<?, T>> byName;

	public CapabilityDispatcher(AttachCapabilitiesEvent<T> event, T owner) {
		this.caps = event.getCapabilities();
		this.byName = event.getCapabilitiesByName();
		this.owner = owner;
	}
	
	private CapabilityDispatcher(CapabilityDispatcher<T> other, T newOwner) {
		this.caps = other.caps.values().stream().map(old -> old.copy(newOwner)).collect(Collectors.toMap(IAttachedCapabilityProvider::getType, t -> t));
		this.byName = this.caps.values().stream().collect(Collectors.toMap(IAttachedCapabilityProvider::getId, t -> t));
		this.owner = newOwner;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean areCompatible(@Nullable CapabilityDispatcher<T> other)
    {
    	Set<ResourceLocation> keys = new HashSet<>();
    	keys.addAll(byName.keySet());
    	keys.addAll(other.byName.keySet());
    	for(ResourceLocation id : keys) {
    		var ourProv = this.byName.get(id);
    		var theirProv = other.byName.get(id);		
    		if(ourProv == null && theirProv.compareTo(null) != 0) return false;
    		else if(theirProv == null && ourProv.compareTo(null) != 0) return false;
    		else if(ourProv.compareTo((IAttachedCapabilityProvider) theirProv) != 0) return false;
    	}
    	return true;
    }
    
    public CapabilityDispatcher<T> copy(T newOwner){
    	return new CapabilityDispatcher<>(this, newOwner);
    }
	
	@Override
	public <C> Capability<C> getCapability(CapabilityType<C> cap, @Nullable Direction side) {
		IAttachedCapabilityProvider<?, T> provider = caps.get(cap);
		if(provider == null) return Capability.empty();
		return provider.getCapability(this.owner.isDirectionSensitive() ? side : null).cast();
	}

	@Override
	public void invalidateCaps() {
		caps.values().forEach(IAttachedCapabilityProvider::invalidateCaps);
	}

	@Override
	public void reviveCaps() {
		caps.values().forEach(IAttachedCapabilityProvider::reviveCaps);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		this.caps.values().forEach(prov -> {
			CompoundTag provTag = prov.serializeNBT();
			if(provTag != null) tag.put(prov.getId().toString(), provTag);
		});
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		for(String s : tag.getAllKeys()) {
			ResourceLocation id = new ResourceLocation(s);
			var prov = this.byName.get(id);
			if(prov != null) prov.deserializeNBT(tag.getCompound(s));
		}
	}

}