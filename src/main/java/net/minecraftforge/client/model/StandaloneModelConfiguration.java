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

package net.minecraftforge.client.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.textures.UnitSprite;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Helper to allow baking models outwise the context of block/item model loading.
 */
public class StandaloneModelConfiguration implements IModelConfiguration
{
    public static final ResourceLocation LOCATION = new ResourceLocation("forge", "standalone");

    public static final StandaloneModelConfiguration INSTANCE = new StandaloneModelConfiguration(LOCATION, Map.of());

    public static StandaloneModelConfiguration create(ResourceLocation modelName)
    {
        return new StandaloneModelConfiguration(modelName, Map.of());
    }

    public static StandaloneModelConfiguration create(Map<String, ResourceLocation> textures)
    {
        return new StandaloneModelConfiguration(LOCATION, textures);
    }

    public static StandaloneModelConfiguration create(ResourceLocation modelName, Map<String, ResourceLocation> textures)
    {
        return new StandaloneModelConfiguration(modelName, textures);
    }

    private final Map<String, ResourceLocation> textures;

    public StandaloneModelConfiguration(ResourceLocation modelName, Map<String, ResourceLocation> textures)
    {

        this.textures = textures;
    }

    @Nullable
    @Override
    public UnbakedModel getOwnerModel()
    {
        return null;
    }

    @Override
    public String getModelName()
    {
        return LOCATION.toString();
    }

    @Override
    public boolean isTexturePresent(String name)
    {
        return textures.containsKey(name);
    }

    @Override
    public Material resolveTexture(String name)
    {
        return new Material(UnitSprite.LOCATION, textures.getOrDefault(name, UnitSprite.LOCATION));
    }

    @Override
    public boolean isShadedInGui()
    {
        return true;
    }

    @Override
    public boolean isSideLit()
    {
        return true;
    }

    @Override
    public boolean useSmoothLighting()
    {
        return true;
    }

    @Override
    public ItemTransforms getCameraTransforms()
    {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ModelState getCombinedTransform()
    {
        return BlockModelRotation.X0_Y0;
    }
}
