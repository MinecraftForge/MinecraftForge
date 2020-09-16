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

import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;

import javax.annotation.Nullable;

/*
 * Interface that provides generic access to the data within BlockModel,
 * while allowing non-blockmodel usage of models
 */
public interface IModelConfiguration {

    /**
     * If available, gets the owning model (usually BlockModel) of this configuration
     */
    @Nullable
    IUnbakedModel getOwnerModel();

    /**
     * @return The name of the model being baked, for logging and cache purposes.
     */
    String getModelName();

    /**
     * Checks if a texture is present in the model.
     * @param name The name of a texture channel.
     */
    boolean isTexturePresent(String name);

    /**
     * Resolves the final texture name, taking into account texture aliases and replacements.
     * @param name The name of a texture channel.
     * @return The location of the texture, or the missing texture if not found.
     */
    RenderMaterial resolveTexture(String name);

    /**
     * @return True if the item is a 3D model, false if it's a generated item model.
     * TODO: Rename.
     * This value has nothing to do with shading anymore, and this name is misleading.
     * It's actual purpose seems to be relegated to translating the model during rendering, so that it's centered.
     */
    boolean isShadedInGui();

    /**
     * @return True if the item is lit from the side
     */
    boolean isSideLit();

    /**
     * @return True if the item requires per-vertex lighting.
     */
    boolean useSmoothLighting();

    /**
     * Gets the vanilla camera transforms data.
     * Do not use for non-vanilla code. For general usage, prefer getCombinedState.
     */
    ItemCameraTransforms getCameraTransforms();

    /**
     * @return The combined transformation state including vanilla and forge transforms data.
     */
    IModelTransform getCombinedTransform();

    /**
     * Queries the visibility information for the model parts.
     * @param part A part for which to query visibility.
     * @param fallback A boolean specifying the default visibility if an override isn't found in the model data.
     * @return The final computed visibility.
     */
    default boolean getPartVisibility(IModelGeometryPart part, boolean fallback) {
        return fallback;
    }

    /**
     * Queries the visibility information for the model parts. Same as above, but defaulting to visible.
     * @param part A part for which to query visibility.
     * @return The final computed visibility.
     */
    default boolean getPartVisibility(IModelGeometryPart part) {
        return getPartVisibility(part, true);
    }

}
