/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;

/**
 * Extension interface for {@link ModelState}. An {@code ModelState} is a function from model part to a
 * transformation that should be applied when that part is baked, thus representing the current "state" of the model
 * and its parts.
 */
public interface IForgeModelState
{
    private ModelState self()
    {
        return (ModelState) this;
    }

    /**
     * {@return A transformation to apply to the part} This may be an {@linkplain Transformation#isIdentity() identity
     * transformation} if there is no transformation to be applied. The coordinate system of the transform is determined
     * by the part type.
     *
     * @param part part of the model we are wanting to transform. An empty optional means
     *             we want a transform for the entire model.
     */
    default Transformation getPartTransformation(Object part)
    {
        return Transformation.identity();
    }
}
