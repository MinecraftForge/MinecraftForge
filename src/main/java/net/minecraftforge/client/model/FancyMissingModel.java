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

import java.util.function.Function;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

final class FancyMissingModel implements IUnbakedModel
{
    private static final ResourceLocation font = new ResourceLocation("minecraft", "textures/font/ascii.png");
    private static final Material font2 = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("minecraft", "font/ascii"));
    private static final TransformationMatrix smallTransformation = new TransformationMatrix(null, null, new Vector3f(.25f, .25f, .25f), null)
            .blockCenterToCorner();
    private static final SimpleModelFontRenderer fontRenderer = Util.make(() -> {
        float [] mv = new float[16];
        mv[2*4+0] = 1f / 128f;
        mv[0*4+1] =mv[1*4+2] = -mv[2*4+0];
        mv[3*4+3] = 1;
        mv[0*4+3] = 1;
        mv[0*4+3] = 1 + 1f / 0x100;
        mv[0*4+3] = 0;
        Matrix4f m = new Matrix4f(mv);
        return new SimpleModelFontRenderer(
            Minecraft.getInstance().gameSettings,
            font,
            Minecraft.getInstance().getTextureManager(),
            false,
            m
        ) {/* TODO Implement once SimpleModelFontRenderer is fixed
            @Override
            protected float renderUnicodeChar(char c, boolean italic)
            {
                return super.renderDefaultChar(126, italic);
            }
      */};
    });

    private final IUnbakedModel missingModel;
    private final String message;

    public FancyMissingModel(IUnbakedModel missingModel, String message)
    {
        this.missingModel = missingModel;
        this.message = message;
    }

    @Override
    public Collection<Material> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> missingTextureErrors)
    {
        return ImmutableList.of(font2);
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public IBakedModel bakeModel(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
    {
        IBakedModel bigMissing = missingModel.bakeModel(bakery, spriteGetter, modelTransform, modelLocation);
        ModelTransformComposition smallState = new ModelTransformComposition(modelTransform, new SimpleModelTransform(smallTransformation));
        IBakedModel smallMissing = missingModel.bakeModel(bakery, spriteGetter, smallState, modelLocation);
        return new BakedModel(bigMissing, smallMissing, fontRenderer, message, spriteGetter.apply(font2));
    }

    static final class BakedModel implements IBakedModel
    {
        private final SimpleModelFontRenderer fontRenderer;
        private final String message;
        private final TextureAtlasSprite fontTexture;
        private final IBakedModel missingModel;
        private final IBakedModel otherModel;
        private final boolean big;
        private ImmutableList<BakedQuad> quads;

        public BakedModel(IBakedModel bigMissing, IBakedModel smallMissing, SimpleModelFontRenderer fontRenderer, String message, TextureAtlasSprite fontTexture)
        {
            this.missingModel = bigMissing;
            otherModel = new BakedModel(smallMissing, fontRenderer, message, fontTexture, this);
            this.big = true;
            this.fontRenderer = fontRenderer;
            this.message = message;
            this.fontTexture = fontTexture;
        }

        public BakedModel(IBakedModel smallMissing, SimpleModelFontRenderer fontRenderer, String message, TextureAtlasSprite fontTexture, BakedModel big)
        {
            this.missingModel = smallMissing;
            otherModel = big;
            this.big = false;
            this.fontRenderer = fontRenderer;
            this.message = message;
            this.fontTexture = fontTexture;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
        {
            if (side == null)
            {
                if (quads == null)
                {
                    fontRenderer.setSprite(fontTexture);
                    fontRenderer.setFillBlanks(true);
                    String[] lines = message.split("\\r?\\n");
                    List<String> splitLines = Lists.newArrayList();
                    for (int y = 0; y < lines.length; y++)
                    {
                        splitLines.addAll(fontRenderer.listFormattedStringToWidth(lines[y], 0x80));
                    }
                    for (int y = 0; y < splitLines.size(); y++)
                    {
                        fontRenderer.drawString(splitLines.get(y), 0, ((y - splitLines.size() / 2f) * fontRenderer.FONT_HEIGHT) + 0x40, 0xFF00FFFF);
                    }
                    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                    builder.addAll(missingModel.getQuads (state, side, rand));
                    builder.addAll(fontRenderer.build());
                    quads = builder.build();
                }
                return quads;
            }
            return missingModel.getQuads (state, side, rand);
        }

        @Override
        public boolean isAmbientOcclusion() { return true; }

        @Override
        public boolean isGui3d() { return false; }

        @Override
        public boolean func_230044_c_() { return false; }

        @Override
        public boolean isBuiltInRenderer() { return false; }

        @Override
        public TextureAtlasSprite getParticleTexture() { return fontTexture; }

        @Override
        public ItemOverrideList getOverrides() { return ItemOverrideList.EMPTY; }

        @Override
        public boolean doesHandlePerspectives()
        {
            return true;
        }

        @Override
        public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
        {
            TransformationMatrix transform = TransformationMatrix.identity();
            boolean big = true;
            switch (cameraTransformType)
            {

                case THIRD_PERSON_LEFT_HAND:
                    break;
                case THIRD_PERSON_RIGHT_HAND:
                    break;
                case FIRST_PERSON_LEFT_HAND:
                    transform = new TransformationMatrix(new Vector3f(-0.62f, 0.5f, -.5f), new Quaternion(1, -1, -1, 1), null, null);
                    big = false;
                    break;
                case FIRST_PERSON_RIGHT_HAND:
                    transform = new TransformationMatrix(new Vector3f(-0.5f, 0.5f, -.5f), new Quaternion(1, 1, 1, 1), null, null);
                    big = false;
                    break;
                case HEAD:
                    break;
                case GUI:
                    if (ForgeConfig.CLIENT.zoomInMissingModelTextInGui.get())
                    {
                        transform = new TransformationMatrix(null, new Quaternion(1, 1, 1, 1), new Vector3f(4, 4, 4), null);
                        big = false;
                    }
                    else
                    {
                        transform = new TransformationMatrix(null, new Quaternion(1, 1, 1, 1), null, null);
                        big = true;
                    }
                    break;
                case FIXED:
                    transform = new TransformationMatrix(null, new Quaternion(-1, -1, 1, 1), null, null);
                    break;
                default:
                    break;
            }
            mat.getLast().getMatrix().mul(transform.getMatrix());
            if (big != this.big)
            {
                return otherModel;
            }
            return this;
        }
    }
}
