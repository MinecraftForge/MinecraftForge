/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.model;

import java.util.List;

import net.minecraft.client.resources.model.geometry.BakedQuad;

/**
 * Transformer for {@link BakedQuad baked quads}.
 *
 * @see QuadTransformers
 */
@FunctionalInterface
public interface IQuadTransformer {
    BakedQuad process(BakedQuad quad);

    default List<BakedQuad> process(List<BakedQuad> inputs) {
        return inputs.stream().map(this::process).toList();
    }

    default IQuadTransformer andThen(IQuadTransformer other) {
        return quad -> other.process(process(quad));
    }
}
