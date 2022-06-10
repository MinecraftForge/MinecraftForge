/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * A high-speed implementation of a capability delegator. This is used to wrap
 * the results of the AttachCapabilitiesEvent. It is HIGHLY recommended that you
 * DO NOT use this approach unless you MUST delegate to multiple providers
 * instead just implement y our handlers using normal if statements.
 *
 * Internally the handlers are baked into arrays for fast iteration. The
 * ResourceLocations will be used for the NBT Key when serializing.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CapabilityDispatcher implements INBTSerializable<CompoundTag>, ICapabilityProvider {

	protected final AttachedCapability<?>[][] caps;
	private final Map<ResourceLocation, AttachedCapability<?>> uniqueCaps;
	private final List<AttachedCapability<?>> serializable;
	private final List<AttachedCapability<?>> comparable;
	private final ICapabilityProvider owner;

	public CapabilityDispatcher(AttachCapabilitiesEvent<?> event, ICapabilityProvider owner) {
		this.caps = event.getCapabilities();
		this.uniqueCaps = new HashMap<>();
		for(int i = 0; i < caps.length; i++) {
			for(int j = 0; j < caps[i].length; j++) {
				if(caps[i][j] != null) this.uniqueCaps.put(caps[i][j].getId(), caps[i][j]);
			}
		}
		serializable = uniqueCaps.values().stream().filter(AttachedCapability::isSerializable).collect(Collectors.toList());
		comparable = uniqueCaps.values().stream().filter(AttachedCapability::isComparable).collect(Collectors.toList());
		this.owner = owner;
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean areCompatible(@Nullable CapabilityDispatcher other)
    {
        if (other == null) return this.comparable.size() == 0;
        if (this.comparable.size() != other.comparable.size()) return false;
        for(AttachedCapability<?> c : this.comparable) {
        	Object ob1 = c.getInst().orElse(null);
        	Object ob2 = other.uniqueCaps.getOrDefault(c.getId(), AttachedCapability.EMPTY).getInst().orElse(null);
        	if(ob1 instanceof Comparable cmp && cmp.compareTo(ob2) != 0) return false;
        }
        return true;
    }

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (!this.owner.isDirectionSensitive())
			side = null;
		int dKey = side == null ? 6 : side.ordinal();
		return caps[cap.getId()][dKey] == null ? LazyOptional.empty() : caps[cap.getId()][dKey].getInst().cast();
	}

	@Override
	public void invalidateCaps() {
		for (AttachedCapability<?> cap : this.uniqueCaps.values()) {
			LazyOptional<?> inst = cap.getInst();
			if (inst.isPresent())
				inst.invalidate();
		}
	}

	@Override
	public void reviveCaps() {
		for (AttachedCapability<?> cap : this.uniqueCaps.values()) {
			cap.getInst().revive();
		}
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		for (AttachedCapability<?> cap : this.serializable) {
			cap.getInst().ifPresent(obj -> {
				if (obj instanceof INBTSerializable<?> serial) {
					tag.put(cap.getId().toString(), serial.serializeNBT());
				}
			});
		}
		return tag;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void deserializeNBT(CompoundTag tag) {
		for (AttachedCapability<?> cap : this.serializable) {
			Tag tg = tag.get(cap.getId().toString());
			if (tg != null) {
				cap.getInst().ifPresent(obj -> {
					if (obj instanceof INBTSerializable serial) {
						serial.deserializeNBT(tg);
					}
				});
			}
		}
	}

}
