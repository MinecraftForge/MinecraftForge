/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

