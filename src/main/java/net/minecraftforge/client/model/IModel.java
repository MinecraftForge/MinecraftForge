/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;

import com.google.common.base.Function;

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
    Collection<ResourceLocation> getDependencies();

    /*
     * Returns all texture locations that this model depends on.
     * Assume that returned collection is immutable.
     */
    Collection<ResourceLocation> getTextures();

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
    IModelState getDefaultState();
}
