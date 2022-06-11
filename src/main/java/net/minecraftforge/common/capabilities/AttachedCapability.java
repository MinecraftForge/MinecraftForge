/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;

public final class AttachedCapability<T> {

    public static final AttachedCapability<?> EMPTY = new AttachedCapability<Object>(new ResourceLocation("empty"), null, LazyOptional.empty());

    private final ResourceLocation id;
    private final Capability<T> cap;
    private final LazyOptional<T> inst;
    
    private Direction[] sides;
    private boolean serializable;
    private boolean comparable;

    public AttachedCapability(ResourceLocation id, Capability<T> cap, LazyOptional<T> inst)
    {
        this.id = id;
        this.cap = cap;
        this.inst = inst;
    }
    
	public AttachedCapability<T> setSides(Direction[] sides)
	{
		if (sides == null) this.sides = sides;
		return this;
	}

    public AttachedCapability<T> setSerializable()
    {
        this.serializable = true;
        return this;
    }

    public AttachedCapability<T> setComparable()
    {
        this.comparable = true;
        return this;
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    public Capability<T> getCap()
    {
        return this.cap;
    }

    public LazyOptional<T> getInst()
    {
        return this.inst;
    }

    public Direction[] getSides()
    {
        return this.sides;
    }

    public boolean isSerializable()
    {
        return this.serializable;
    }

    public boolean isComparable()
    {
        return this.comparable;
    }

}
