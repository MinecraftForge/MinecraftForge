/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.IAttachedCapabilityProvider.ICopyableCapabilityProvider;
import net.minecraftforge.common.capabilities.IAttachedCapabilityProvider.IComparableCapabilityProvider;
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

    private final Map<CapabilityType<?>, IAttachedCapabilityProvider<T>> caps;
    private final Map<ResourceLocation, IAttachedCapabilityProvider<T>> byName;
    private boolean isValid = true;

    public CapabilityDispatcher(AttachCapabilitiesEvent<T> event)
    {
        this.caps = event.getCapabilities();
        this.byName = event.getCapabilitiesByName();
    }

    /*********************************
             Compare/Copy Code
           Warning: Generic Hell
     *********************************/

    /**
     * Internal copy constructor.
     * @see {@link #copy(ICapabilityProvider)}
     * @see {@link IItemStackCapabilityProvider#copy(ICapabilityProvider)}
     */
	private CapabilityDispatcher(CapabilityDispatcher<T> other, T newOwner)
    {
        Map<CapabilityType<?>, IAttachedCapabilityProvider<T>> caps = new IdentityHashMap<>(other.caps.size());
        Map<ResourceLocation, IAttachedCapabilityProvider<T>> byName = new HashMap<>(other.byName.size(), 1);
        other.caps.forEach((type, prov) ->
        {
            IAttachedCapabilityProvider<T> copy = byName.computeIfAbsent(prov.getId(), key -> ((ICopyableCapabilityProvider<T>) prov).copy(newOwner));
            // Ideally we would ensure the type and key don't change here, but it's an expensive check that we don't "need" to do.
            if(copy != null) caps.put(type, copy);
        });
        this.caps = Collections.unmodifiableMap(caps);
        this.byName = Collections.unmodifiableMap(byName);
    }

    /**
     * <b>Do not call unless you are CERTAIN that both dispatchers are comparable!</b>
     * <p>
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
     * @see {@link IComparableCapabilityProvider#isEquivalentTo(IAttachedCapabilityProvider)}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean isEquivalentTo(@Nullable CapabilityDispatcher<T> other)
    {
        if(other == null) {
            for(IComparableCapabilityProvider<T> prov : (Collection<IComparableCapabilityProvider>) (Collection) this.byName.values()) 
            {
                if(!prov.isEquivalentTo(null)) return false;    // Would anyone ever want this behavior? / Do mods have caps that are ignored upon stacking?
            }
            return true;
        }
        else
        {
            for(ResourceLocation id : this.byName.keySet())
            {
                var ourProv = (IComparableCapabilityProvider) this.byName.get(id);
                var theirProv = other == null ? null : (IComparableCapabilityProvider) other.byName.get(id);        
                if(theirProv == null && !ourProv.isEquivalentTo(null)) return false;
                else if(!ourProv.isEquivalentTo(theirProv)) return false;
            }
            if(other != null)
            {
                for(ResourceLocation id : other.byName.keySet())
                {
                    if(this.byName.containsKey(id)) continue; // Skip any found in our provider, as we already checked above.
                    var theirProv = (IComparableCapabilityProvider) other.byName.get(id);        
                    if(!theirProv.isEquivalentTo(null)) return false;
                }
            }
            return true;
        }
    }

    /**
     * <b>Do not call unless you are CERTAIN that both dispatchers are comparable!</b>
     * <p>
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
     * @see {@link IItemStackCapabilityProvider#isEquivalentTo(IAttachedCapabilityProvider)}
     */
    public CapabilityDispatcher<T> copy(T newOwner)
    {
        return new CapabilityDispatcher<>(this, newOwner);
    }

    /*********************************
           End Compare/Copy Code
     *********************************/

    @Override
    public <C> Capability<C> getCapability(CapabilityType<C> type, @Nullable Direction side)
    {
        if(!isValid) return Capability.empty();
        IAttachedCapabilityProvider<T> provider = caps.get(type);
        if(provider == null) return Capability.empty();
        return provider.getCapability(type, side).cast();
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
        this.caps.values().forEach(prov -> {
            CompoundTag provTag = prov.serializeNBT();
            if(provTag != null) tag.put(prov.getId().toString(), provTag);
        });
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag)
    {
        for(String s : tag.getAllKeys())
        {
            ResourceLocation id = new ResourceLocation(s);
            var prov = this.byName.get(id);
            if(prov != null) prov.deserializeNBT(tag.getCompound(s));
        }
    }

}