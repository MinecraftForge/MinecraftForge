/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
