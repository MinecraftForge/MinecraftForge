/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.IAttachedCapabilityProvider.IItemStackCapabilityProvider;
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

    /*********************************
          ItemStack-Specific Code
           Warning: Generic Hell
     *********************************/
    
    /**
     * Internal copy constructor.
     * @see {@link #copy(ICapabilityProvider)}
     * @see {@link IItemStackCapabilityProvider#copy(ICapabilityProvider)}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private CapabilityDispatcher(CapabilityDispatcher<T> other, ItemStack newOwner)
    {
        Map<CapabilityType<?>, IAttachedCapabilityProvider<?, T>> caps = new HashMap<>(other.caps.size(), 1);
        Map<ResourceLocation, IAttachedCapabilityProvider<?, T>> byName = new HashMap<>(other.byName.size(), 1);
        for(Map.Entry<ResourceLocation, IAttachedCapabilityProvider<?, T>> entry : other.byName.entrySet())
        {
            IAttachedCapabilityProvider<?, T> copy = ((IItemStackCapabilityProvider) entry.getValue()).copy(newOwner);
            // Ideally we would ensure the type and key don't change here, but it's an expensive check that we don't "need" to do.
            caps.put(copy.getType(), copy);
            byName.put(copy.getId(), copy);
        }
        this.caps = Collections.unmodifiableMap(caps);
        this.byName = Collections.unmodifiableMap(byName);
        this.owner = (T) newOwner;
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
     * @see {@link IItemStackCapabilityProvider#isEquivalentTo(IAttachedCapabilityProvider)}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean isEquivalentTo(@Nullable CapabilityDispatcher<ItemStack> other)
    {
        if(other == null) {
            for(IItemStackCapabilityProvider<?> prov : (Collection<IItemStackCapabilityProvider>) (Collection) this.byName.values()) 
            {
                if(!prov.isEquivalentTo(null)) return false;    // Would anyone ever want this behavior? / Do mods have caps that are ignored upon stacking?
            }
            return true;
        }
        else
        {
            for(ResourceLocation id : this.byName.keySet())
            {
                var ourProv = (IItemStackCapabilityProvider) this.byName.get(id);
                var theirProv = other == null ? null : (IItemStackCapabilityProvider) other.byName.get(id);        
                if(ourProv == null && !theirProv.isEquivalentTo(null)) return false;
                else if(theirProv == null && !ourProv.isEquivalentTo(null)) return false;
                else if(!ourProv.isEquivalentTo((IItemStackCapabilityProvider) theirProv)) return false;
            }
            if(other != null)
            {
                for(ResourceLocation id : other.byName.keySet())
                {
                    if(this.byName.containsKey(id)) continue; // Skip any found in our provider, as we already checked above.
                    var theirProv = (IItemStackCapabilityProvider) other.byName.get(id);        
                    if(!theirProv.isEquivalentTo(null)) return false;
                }
            }
            return true;
        }
    }

    public CapabilityDispatcher<T> copy(ItemStack newOwner)
    {
        return new CapabilityDispatcher<>(this, newOwner);
    }

    /*********************************
        End ItemStack-Specific Code
     *********************************/

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
        
        for(String s : tag.getAllKeys())
        {
            ResourceLocation id = new ResourceLocation(s);
            var prov = this.byName.get(id);
            if(prov != null) prov.deserializeNBT(tag.getCompound(s));
        }
    }

}