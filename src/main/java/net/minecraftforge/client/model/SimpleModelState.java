/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;

/**
 * Simple implementation of {@link ModelState}.
 */
public record SimpleModelState(Transformation getRotation, boolean isUvLocked) implements ModelState {
    public SimpleModelState(Transformation transformation) {
        this(transformation, false);
    }
}
