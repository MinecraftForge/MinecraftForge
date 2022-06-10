/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;


import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.Event;

/**
 * 
 */
public abstract class AttachCapabilitiesEvent<T extends ICapabilityProvider> extends Event {

	protected final T obj;
	protected final AttachedCapability<?>[][] caps = new AttachedCapability<?>[CapabilityManager.getNumCaps()][7];
	protected int size = 0;

	protected AttachCapabilitiesEvent(T obj) {
		this.obj = obj;
		System.out.println("Initialized " + obj.toString() + " hash " + obj.hashCode());
	}

	/**
	 * Retrieves the object that is being created, Not much state is set.
	 */
	public final T getObject() {
		return this.obj;
	}

	public final int getSize() {
		return this.size;
	}
	
	/**
	 * Adds a capability to be attached to this object. Keys MUST be unique, it is
	 * suggested that you set the domain to your mod ID. If the capability is an
	 * instance of INBTSerializable, this key will be used when serializing this
	 * capability.
	 *
	 * @param key The name of owner of this capability provider.
	 * @param cap The capability provider
	 */
	public <C> AttachedCapability<C> addCapability(ResourceLocation id, Capability<C> cap, LazyOptional<C> inst,
			Direction... sides) {
		if (!this.obj.isDirectionSensitive())
			sides = new Direction[] { null };
		AttachedCapability<C> attached = new AttachedCapability<>(id, cap, inst, sides);
		for (Direction side : sides) {
			int dKey = side == null ? 6 : side.ordinal();
			if (caps[cap.getId()][dKey] == null) {
				caps[cap.getId()][dKey] = attached;
				this.size++;
			} else {
				throw new RuntimeException("Duplicated capabilities!"); // TODO: Change to a better message.
			}
		}
		return attached;
	}

	/**
	 * A unmodifiable view of the capabilities that will be attached to this object.
	 */
	public AttachedCapability<?>[][] getCapabilities() {
		return this.caps;
	}

	public static final class Items extends AttachCapabilitiesEvent<ItemStack> {

		public Items(ItemStack obj) {
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
