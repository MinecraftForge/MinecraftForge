/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import com.mojang.math.Transformation;

/**
 * An {@code IModelState} is a function from model part to a transformation that should be applied
 * when that part is baked, thus representing the current "state" of the model and its parts.
 */
public interface IForgeModelState
{
    /**
     * @param part Part of the model we are wanting to transform. An empty optional means
     *             we want a transform for the entire model.
     * @return A transformation to apply to the part, if any. The coordinate system of the transform
     *         is determined by the part type.
     */
    default Transformation getPartTransformation(Object part)
    {
        return Transformation.identity();
    }
}
