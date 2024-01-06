/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public interface IModelBuilder<T extends IModelBuilder<T>>
{
    static IModelBuilder<?> of(IModelConfiguration owner, ItemOverrides overrides, TextureAtlasSprite particle)
    {
        return new Simple(new SimpleBakedModel.Builder(owner, overrides).particle(particle));
    }

    T addFaceQuad(Direction facing, BakedQuad quad);
    T addGeneralQuad(BakedQuad quad);

    BakedModel build();

    class Simple implements IModelBuilder<Simple> {
        final SimpleBakedModel.Builder builder;

        Simple(SimpleBakedModel.Builder builder)
        {
            this.builder = builder;
        }

        @Override
        public Simple addFaceQuad(Direction facing, BakedQuad quad)
        {
            builder.addCulledFace(facing, quad);
            return this;
        }

        @Override
        public Simple addGeneralQuad(BakedQuad quad)
        {
            builder.addUnculledFace(quad);
            return this;
        }

        @Override
        public BakedModel build()
        {
            return builder.build();
        }
    }
}

