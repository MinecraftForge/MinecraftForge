/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.Objects;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;

public class AttachedCapability<T> {

	public static final AttachedCapability<?> EMPTY = new AttachedCapability<Object>(new ResourceLocation("empty"), null, LazyOptional.empty(), new Direction[0]);

	private final ResourceLocation id;
	private final Capability<T> cap;
	private final LazyOptional<T> inst;
	private final Direction[] sides;
	private final int hash;

	private boolean serializable;
	private boolean comparable;

	public AttachedCapability(ResourceLocation id, Capability<T> cap, LazyOptional<T> inst, Direction... sides) {
		this.id = id;
		this.cap = cap;
		this.inst = inst;
		this.sides = sides;
		this.hash = Objects.hash(id, cap, sides);
	}

	public AttachedCapability<T> setSerializable() {
		this.serializable = true;
		return this;
	}

	public AttachedCapability<T> setComparable() {
		this.comparable = true;
		return this;
	}

	public ResourceLocation getId() {
		return this.id;
	}

	public Capability<T> getCap() {
		return this.cap;
	}

	public LazyOptional<T> getInst() {
		return this.inst;
	}

	public Direction[] getSides() {
		return this.sides;
	}

	public boolean isSerializable() {
		return this.serializable;
	}

	public boolean isComparable() {
		return this.comparable;
	}

	@Override
	public int hashCode() {
		return this.hash;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof AttachedCapability && obj.hashCode() == this.hashCode();
	}

}
