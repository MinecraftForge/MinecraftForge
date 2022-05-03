/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.common.base.Objects;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.client.renderer.model.IModelTransform;

/**
 * An {@link IModelTransform} that combines the transforms from two child {@link IModelTransform}.
 */
public class ModelTransformComposition implements IModelTransform
{
    private final IModelTransform first;
    private final IModelTransform second;
    private final boolean uvLock;

    public ModelTransformComposition(IModelTransform first, IModelTransform second)
    {
        this(first, second, false);
    }

    public ModelTransformComposition(IModelTransform first, IModelTransform second, boolean uvLock)
    {
        this.first = first;
        this.second = second;
        this.uvLock = uvLock;
    }

    @Override
    public boolean isUvLocked()
    {
        return uvLock;
    }

    @Override
    public TransformationMatrix getRotation()
    {
        return first.getRotation().compose(second.getRotation());
    }

    @Override
    public TransformationMatrix getPartTransformation(Object part)
    {
        return first.getPartTransformation(part).compose(second.getPartTransformation(part));
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
        ModelTransformComposition that = (ModelTransformComposition) o;
        return Objects.equal(first, that.first) && Objects.equal(second, that.second);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(first, second);
    }
}
