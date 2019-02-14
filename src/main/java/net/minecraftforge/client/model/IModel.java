/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.model.animation.IClip;

/*
 * Interface for models that can be baked
 * (possibly to different vertex formats and with different state).
 */
@SuppressWarnings("unchecked")
public interface IModel<T extends IModel<T>>
{    
    @Nullable
    IBakedModel bake(Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, IModelState state, boolean uvlock, VertexFormat format);

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
    default T process(ImmutableMap<String, String> customData) {
        return (T) this;
    }

    default T smoothLighting(boolean value) {
        return (T) this;
    }

    default T gui3d(boolean value) {
        return (T) this;
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
    default T retexture(ImmutableMap<String, String> textures) {
        return (T) this;
    }
}
