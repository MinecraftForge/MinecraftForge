/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

public interface IModelBuilder<T extends IModelBuilder<T>>
{
    static IModelBuilder<?> of(IModelConfiguration owner, ItemOverrideList overrides, TextureAtlasSprite particle)
    {
        return new Simple(new SimpleBakedModel.Builder(owner, overrides).setTexture(particle));
    }

    T addFaceQuad(Direction facing, BakedQuad quad);
    T addGeneralQuad(BakedQuad quad);

    IBakedModel build();

    class Simple implements IModelBuilder<Simple> {
        final SimpleBakedModel.Builder builder;

        Simple(SimpleBakedModel.Builder builder)
        {
            this.builder = builder;
        }

        @Override
        public Simple addFaceQuad(Direction facing, BakedQuad quad)
        {
            builder.addFaceQuad(facing, quad);
            return this;
        }

        @Override
        public Simple addGeneralQuad(BakedQuad quad)
        {
            builder.addGeneralQuad(quad);
            return this;
        }

        @Override
        public IBakedModel build()
        {
            return builder.build();
        }
    }
}

