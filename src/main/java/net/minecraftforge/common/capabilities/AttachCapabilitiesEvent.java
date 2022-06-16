/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.eventbus.api.Event;

/**
 * The AttachCapabilitiesEvent is used to attach arbitrary capability instances to arbitrary providers.<br>
 * The firing of this event is determined by the provider object, and is not consistent across usages.<br>
 * For most cases, this is fired whenever the {@link CapabilityDispatcher} is required for functionality.<br>
 * This means that typically, this is not fired until capabilities are attempted to be accessed for a particular object.
 */
public abstract class AttachCapabilitiesEvent<T extends ICapabilityProvider> extends Event {

	protected final T obj;
	protected final Map<CapabilityType<?>, IAttachedCapabilityProvider<?, T>> providers = new HashMap<>();
	protected final Map<CapabilityType<?>, IAttachedCapabilityProvider<?, T>> view = Collections.unmodifiableMap(providers);
	protected final Map<ResourceLocation, IAttachedCapabilityProvider<?, T>> byName = new HashMap<>();
	protected final Map<ResourceLocation, IAttachedCapabilityProvider<?, T>> byNameView = Collections.unmodifiableMap(byName);

	protected AttachCapabilitiesEvent(T obj) {
		this.obj = obj;
	}

	/**
	 * Returns the object that is having capabilities attached.
	 */
	public final T getObject() {
		return this.obj;
	}

	/**
	 * Returns the number of successful attachments.
	 */
	public final int getSize() {
		return this.providers.size();
	}
	
	/**
	 * Adds a capability to this object. Utilized the returned {@link AttachedCapability} to mark 
	 * it as serializable or comparable.<br> 
	 * The ID of this capability instance should be unique (but consistent) and under your domain.
	 * @param id The unique ID representing this capability.  Only needs to be unique to this object, not all objects.
	 * @param cap The type of capability being attached.
	 * @param inst A (lazy) instance of the capability object.
	 * @param sides The sides to attach the object to.  If the object is not {@link ICapabilityProvider#isDirectionSensitive()}, this will be replaced with { null }.
	 * @return An {@link AttachedCapability} representing the newly attached cap, or null, if the cap could not be attached to any requested sides.
	 * @see {@link AttachedCapability#setSerializable()}
	 * @see {@link AttachedCapability#setComparable()}
	 */
	 public boolean addCapability(IAttachedCapabilityProvider<?, T> provider) {
		CapabilityType<?> type = provider.getType();
		// Review Note : Potentially throw on duplicate name?  Would indicate major issues.
		if(this.providers.containsKey(type) || this.byName.containsKey(provider.getId())) return false;
		this.providers.put(type, provider);
		this.byName.put(provider.getId(), provider);
		return true;
	}

	/**
	 * The currently attached capabilities.  Do not modify this array.
	 */
	public Map<CapabilityType<?>, IAttachedCapabilityProvider<?, T>> getCapabilities() {
		return this.view;
	}

	/**
	 * The currently attached capabilities.  Do not modify this array.
	 */
	public Map<ResourceLocation, IAttachedCapabilityProvider<?, T>> getCapabilitiesByName() {
		return this.byNameView;
	}

	public static final class ItemStacks extends AttachCapabilitiesEvent<ItemStack> {

		public ItemStacks(ItemStack obj) {
			super(obj);
		}

	}

	public static final class BlockEntities extends AttachCapabilitiesEvent<BlockEntity> {

		public BlockEntities(BlockEntity obj) {
			super(obj);
		}

	}
	
	public static final class Chunks extends AttachCapabilitiesEvent<LevelChunk> {

		public Chunks(LevelChunk obj) {
			super(obj);
		}

	}
	
	public static final class Entities extends AttachCapabilitiesEvent<Entity> {

		public Entities(Entity obj) {
			super(obj);
		}

	}
	
	public static final class Levels extends AttachCapabilitiesEvent<Level> {

		public Levels(Level obj) {
			super(obj);
		}

	}
}