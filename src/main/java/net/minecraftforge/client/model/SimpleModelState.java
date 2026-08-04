/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import net.minecraft.client.renderer.texture.ISprite;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Optional;
import com.google.common.collect.ImmutableMap;

/**
 * Simple implementation of IModelState via a map and a default value.
 */
public final class SimpleModelState implements IModelState, ISprite
{
    private final ImmutableMap<? extends IModelPart, TRSRTransformation> map;
    private final Optional<TRSRTransformation> def;

    public SimpleModelState(ImmutableMap<? extends IModelPart, TRSRTransformation> map)
    {
        this(map, Optional.empty());
    }

    public SimpleModelState(ImmutableMap<? extends IModelPart, TRSRTransformation> map, Optional<TRSRTransformation> def)
    {
        this.map = map;
        this.def = def;
    }

    @Override
    public IModelState getState()
    {
        return this;
    }

    @Override
    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        if(!part.isPresent())
        {
            return def;
        }
        if(!map.containsKey(part.get()))
        {
            return Optional.empty();
        }
        return Optional.ofNullable(map.get(part.get()));
    }
}
