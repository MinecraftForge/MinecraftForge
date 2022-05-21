/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.renderable;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Matrix4f;

import javax.annotation.Nullable;

/**
 * A context value that provides {@link Matrix4f} transforms for certain parts of the model.
 */
public class MultipartTransforms implements IMultipartRenderValues<Matrix4f>
{
    /**
     * A default instance that has no transforms specified.
     */
    public static final MultipartTransforms EMPTY = new MultipartTransforms(ImmutableMap.of());

    /**
     * Builds a MultipartTransforms object with the given mapping.
     */
    public static MultipartTransforms of(ImmutableMap<String, Matrix4f> parts)
    {
        return new MultipartTransforms(parts);
    }

    private final ImmutableMap<String, Matrix4f> parts;

    private MultipartTransforms(ImmutableMap<String, Matrix4f> parts)
    {
        this.parts = parts;
    }

    @Nullable
    @Override
    public Matrix4f getPartValues(String part)
    {
        return parts.get(part);
    }
}
