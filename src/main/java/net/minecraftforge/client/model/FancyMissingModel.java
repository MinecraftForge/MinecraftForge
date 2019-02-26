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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ibm.icu.text.Transform;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

final class FancyMissingModel implements IUnbakedModel
{
    private static final ResourceLocation font = new ResourceLocation("minecraft", "textures/font/ascii.png");
    private static final ResourceLocation font2 = new ResourceLocation("minecraft", "font/ascii");
    private static final TRSRTransformation smallTransformation = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(null, null, new Vector3f(.25f, .25f, .25f), null));
    private static final LoadingCache<VertexFormat, FontRenderer> fontCache = CacheBuilder.newBuilder().maximumSize(3).build(new CacheLoader<VertexFormat, FontRenderer>()
    {
        @Override
        public FontRenderer load(VertexFormat format) throws Exception
        {
            Matrix4f m = new Matrix4f();
            m.m20 = 1f / 128f;
            m.m01 = m.m12 = -m.m20;
            m.m33 = 1;
            m.setTranslation(new Vector3f(1, 1 + 1f / 0x100, 0));
            return new FontRenderer(Minecraft.getInstance().getTextureManager(), new TransformedFont(
                Minecraft.getInstance().gameSettings,
                font,
                Minecraft.getInstance().getTextureManager(),
                false,
                m,
                format
            ));
        }
    });

    private final IUnbakedModel missingModel;
    private final String message;

    public FancyMissingModel(IUnbakedModel missingModel, String message)
    {
        this.missingModel = missingModel;
        this.message = message;
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
    {
        return ImmutableList.of(font2);
    }

    @Override
    public Collection<ResourceLocation> getOverrideLocations()
    {
        return Collections.emptyList();
    }

    @Override
    public IBakedModel bake(Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, IModelState state, boolean uvlock, VertexFormat format)
    {
        IBakedModel bigMissing = missingModel.bake(modelGetter, bakedTextureGetter, state, uvlock, format);
        IModelState smallState = new ModelStateComposition(state, smallTransformation);
        IBakedModel smallMissing = missingModel.bake(modelGetter, bakedTextureGetter, smallState, uvlock, format);
        return new BakedModel(bigMissing, smallMissing, fontCache.getUnchecked(format), message, bakedTextureGetter.apply(font2));
    }

    static final class BakedModel implements IBakedModel
    {
        private final FontRenderer fontRenderer;
        private final String message;
        private final TextureAtlasSprite fontTexture;
        private final IBakedModel missingModel;
        private final IBakedModel otherModel;
        private final boolean big;
        private ImmutableList<BakedQuad> quads;

        public BakedModel(IBakedModel bigMissing, IBakedModel smallMissing, FontRenderer fontRenderer, String message, TextureAtlasSprite fontTexture)
        {
            this.missingModel = bigMissing;
            otherModel = new BakedModel(smallMissing, fontRenderer, message, fontTexture, this);
            this.big = true;
            this.fontRenderer = fontRenderer;
            this.message = message;
            this.fontTexture = fontTexture;
        }

        public BakedModel(IBakedModel smallMissing, FontRenderer fontRenderer, String message, TextureAtlasSprite fontTexture, BakedModel big)
        {
            this.missingModel = smallMissing;
            otherModel = big;
            this.big = false;
            this.fontRenderer = fontRenderer;
            this.message = message;
            this.fontTexture = fontTexture;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand)
        {
            if (side == null)
            {
                if (quads == null)
                {
                    TransformedFont font = null;//(TransformedFont) fontRenderer.getFont();
                    font.setSprite(fontTexture);
                    font.setFillBlanks(true);
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
                    builder.addAll(missingModel.getQuads(state, side, rand));
                    builder.addAll(font.build());
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
        public boolean isBuiltInRenderer() { return false; }

        @Override
        public TextureAtlasSprite getParticleTexture() { return fontTexture; }

        @Override
        public ItemOverrideList getOverrides() { return ItemOverrideList.EMPTY; }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
        {
            TRSRTransformation transform = TRSRTransformation.identity();
            boolean big = true;
            switch (cameraTransformType)
            {

                case THIRD_PERSON_LEFT_HAND:
                    break;
                case THIRD_PERSON_RIGHT_HAND:
                    break;
                case FIRST_PERSON_LEFT_HAND:
                    transform = new TRSRTransformation(new Vector3f(-0.62f, 0.5f, -.5f), new Quat4f(1, -1, -1, 1), null, null);
                    big = false;
                    break;
                case FIRST_PERSON_RIGHT_HAND:
                    transform = new TRSRTransformation(new Vector3f(-0.5f, 0.5f, -.5f), new Quat4f(1, 1, 1, 1), null, null);
                    big = false;
                    break;
                case HEAD:
                    break;
                case GUI:
                    if (ForgeMod.zoomInMissingModelTextInGui)
                    {
                        transform = new TRSRTransformation(null, new Quat4f(1, 1, 1, 1), new Vector3f(4, 4, 4), null);
                        big = false;
                    }
                    else
                    {
                        transform = new TRSRTransformation(null, new Quat4f(1, 1, 1, 1), null, null);
                        big = true;
                    }
                    break;
                case FIXED:
                    transform = new TRSRTransformation(null, new Quat4f(-1, -1, 1, 1), null, null);
                    break;
                default:
                    break;
            }
            if (big != this.big)
            {
                return Pair.of(otherModel, transform.getMatrixVec());
            }
            return Pair.of(this, transform.getMatrixVec());
        }
    }
}
