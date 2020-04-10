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

import java.util.Collection;

import java.util.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;

import java.util.function.Function;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.model.animation.IClip;

/*
 * Interface for models that can be baked
 * (possibly to different vertex formats and with different state).
 */
public interface IModel
{
    /*
     * Returns all model locations that this model depends on.
     * Assume that returned collection is immutable.
     * See ModelLoaderRegistry.getModel for dependency loading.
     */
    default Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of();
    }

    /*
     * Returns all texture locations that this model depends on.
     * Assume that returned collection is immutable.
     */
    default Collection<ResourceLocation> getTextures() {
        return ImmutableList.of();
    }

    /*
     * All model texture coordinates should be resolved at this method.
     * Returned model should be in the simplest form possible, for performance
     * reasons (if it's not ISmartBlock/ItemModel - then it should be
     * represented by List<BakedQuad> internally).
     * Returned model's getFormat() can me less specific than the passed
     * format argument (some attributes can be replaced with padding),
     * if there's no such info in this model.
     */
    IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter);

    /*
     * Default state this model will be baked with.
     * See IModelState.
     */
    default IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }

    default Optional<? extends IClip> getClip(String name) {
        return Optional.empty();
    }

    /**
     * Allows the model to process custom data from the variant definition.
     * If unknown data is encountered it should be skipped.
     * @return a new model, with data applied.
     */
    default IModel process(ImmutableMap<String, String> customData) {
        return this;
    }

    default IModel smoothLighting(boolean value) {
        return this;
    }

    default IModel gui3d(boolean value) {
        return this;
    }

    default IModel uvlock(boolean value) {
        return this;
    }

    /**
     * Applies new textures to the model.
     * The returned model should be independent of the accessed one,
     * as a model should be able to be retextured multiple times producing
     * a separate model each time.
     *
     * The input map MAY map to an empty string "" which should be used
     * to indicate the texture was removed. Handling of that is up to
     * the model itself. Such as using default, missing texture, or
     * removing vertices.
     *
     * The input should be considered a DIFF of the old textures, not a
     * replacement as it may not contain everything.
     *
     * @param textures New
     * @return Model with textures applied.
     */
    default IModel retexture(ImmutableMap<String, String> textures) {
        return this;
    }

    default Optional<ModelBlock> asVanillaModel() {
        return Optional.empty();
    }
}
