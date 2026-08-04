/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.common.base.Objects;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Optional;

/**
 * An {@link IModelState} that combines the transforms from two child {@link IModelState}.
 */
public class ModelStateComposition implements IModelState, ISprite
{
    private final IModelState first;
    private final IModelState second;
    private final boolean uvLock;

    public ModelStateComposition(IModelState first, IModelState second)
    {
        this(first, second, false);
    }

    public ModelStateComposition(IModelState first, IModelState second, boolean uvLock)
    {
        this.first = first;
        this.second = second;
        this.uvLock = uvLock;
    }

    @Override
    public IModelState getState()
    {
        return this;
    }

    @Override
    public boolean isUvLock()
    {
        return uvLock;
    }

    @Override
    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        Optional<TRSRTransformation> f = first.apply(part), s = second.apply(part);
        if(f.isPresent() && s.isPresent())
        {
            return Optional.of(f.get().compose(s.get()));
        }
        if (f.isPresent()) {
            return f;
        }
        return s;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        ModelStateComposition that = (ModelStateComposition) o;
        return Objects.equal(first, that.first) && Objects.equal(second, that.second);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(first, second);
    }
}
