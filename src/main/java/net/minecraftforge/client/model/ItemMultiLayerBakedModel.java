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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ItemMultiLayerBakedModel implements IBakedModel
{
    private final boolean smoothLighting;
    private final boolean shadedInGui;
    private final boolean sideLit;
    private final TextureAtlasSprite particle;
    private final ItemOverrideList overrides;
    private final ImmutableList<Pair<IBakedModel, RenderType>> layerModels;
    private final ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> cameraTransforms;

    public ItemMultiLayerBakedModel(boolean smoothLighting, boolean shadedInGui, boolean sideLit,
                                    TextureAtlasSprite particle, ItemOverrideList overrides,
                                    ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> cameraTransforms,
                                    ImmutableList<Pair<IBakedModel, RenderType>> layerModels)
    {
        this.smoothLighting = smoothLighting;
        this.shadedInGui = shadedInGui;
        this.sideLit = sideLit;
        this.particle = particle;
        this.overrides = overrides;
        this.layerModels = layerModels;
        this.cameraTransforms = cameraTransforms;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        List<BakedQuad> quads = Lists.newArrayList();
        layerModels.forEach(lm -> quads.addAll(lm.getFirst().getQuads(state, side, rand, EmptyModelData.INSTANCE)));
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return smoothLighting;
    }

    @Override
    public boolean isGui3d()
    {
        return shadedInGui;
    }

    @Override
    public boolean func_230044_c_()
    {
        return sideLit;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return particle;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return overrides;
    }

    @Override
    public boolean doesHandlePerspectives()
    {
        return true;
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
    {
        return PerspectiveMapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType, mat);
    }

    @Override
    public boolean isLayered()
    {
        return true;
    }

    @Override
    public List<Pair<IBakedModel, RenderType>> getLayerModels(ItemStack itemStack)
    {
        return layerModels;
    }
}
