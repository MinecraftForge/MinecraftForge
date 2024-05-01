/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.common.collect.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Transformation;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;

/**
 * Forge reimplementation of vanilla {@link ItemModelGenerator}, i.e. builtin/generated models,
 * with the following changes:
 * - Represented as a true {@link UnbakedModel} so it can be baked as usual instead of using
 *   special-case logic like vanilla does.
 * - Various fixes in the baking logic.
 * - Not limited to 4 layers maximum.
 */
public final class ItemLayerModel implements IModelGeometry<ItemLayerModel>
{
    public static final ItemLayerModel INSTANCE = new ItemLayerModel(ImmutableList.of());

    private static final Direction[] HORIZONTALS = {Direction.UP, Direction.DOWN};
    private static final Direction[] VERTICALS = {Direction.WEST, Direction.EAST};

    private ImmutableList<Material> textures;
    private final ImmutableSet<Integer> fullbrightLayers;

    public ItemLayerModel()
    {
        this(null, ImmutableSet.of());
    }

    public ItemLayerModel(ImmutableList<Material> textures)
    {
        this(textures, ImmutableSet.of());
    }

    public ItemLayerModel(@Nullable ImmutableList<Material> textures, ImmutableSet<Integer> fullbrightLayers)
    {
        this.textures = textures;
        this.fullbrightLayers = fullbrightLayers;
    }

    private static ImmutableList<Material> getTextures(IModelConfiguration model)
    {
        ImmutableList.Builder<Material> builder = ImmutableList.builder();
        for(int i = 0; model.isTexturePresent("layer" + i); i++)
        {
            builder.add(model.resolveTexture("layer" + i));
        }
        return builder.build();
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery,
                            Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
                            ItemOverrides overrides, ResourceLocation modelLocation)
    {
        ImmutableMap<ItemTransforms.TransformType, Transformation> transformMap =
                PerspectiveMapWrapper.getTransforms(new CompositeModelState(owner.getCombinedTransform(), modelTransform));
        Transformation transform = modelTransform.getRotation();
        TextureAtlasSprite particle = spriteGetter.apply(
                owner.isTexturePresent("particle") ? owner.resolveTexture("particle") : textures.get(0)
        );

        ItemMultiLayerBakedModel.Builder builder = ItemMultiLayerBakedModel.builder(owner, particle, overrides, transformMap);
        for(int i = 0; i < textures.size(); i++)
        {
            TextureAtlasSprite tas = spriteGetter.apply(textures.get(i));
            boolean fullbright = fullbrightLayers.contains(i);
            RenderType rt = getLayerRenderType(fullbright);
            builder.addQuads(rt, getQuadsForSprite(i, tas, transform, fullbright));
        }

        return builder.build();
    }

    public static RenderType getLayerRenderType(boolean isFullbright)
    {
        return isFullbright ? ForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get() : ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get();
    }

    public static ImmutableList<BakedQuad> getQuadsForSprites(List<Material> textures, Transformation transform, Function<Material, TextureAtlasSprite> spriteGetter)
    {
        return getQuadsForSprites(textures, transform, spriteGetter, Collections.emptySet());
    }

    public static ImmutableList<BakedQuad> getQuadsForSprites(List<Material> textures, Transformation transform, Function<Material, TextureAtlasSprite> spriteGetter, Set<Integer> fullbrights)
    {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        for(int i = 0; i < textures.size(); i++)
        {
            TextureAtlasSprite tas = spriteGetter.apply(textures.get(i));
            builder.addAll(getQuadsForSprite(i, tas, transform, fullbrights.contains(i)));
        }
        return builder.build();
    }

    public static ImmutableList<BakedQuad> getQuadsForSprite(int tint, TextureAtlasSprite sprite, Transformation transform)
    {
        return getQuadsForSprite(tint, sprite, transform, false);
    }

    public static ImmutableList<BakedQuad> getQuadsForSprite(int tint, TextureAtlasSprite sprite, Transformation transform, boolean fullbright)
    {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        int uMax = sprite.getWidth();
        int vMax = sprite.getHeight();

        FaceData faceData = new FaceData(uMax, vMax);
        boolean translucent = false;

        for(int f = 0; f < sprite.getFrameCount(); f++)
        {
            boolean ptu;
            boolean[] ptv = new boolean[uMax];
            Arrays.fill(ptv, true);
            for(int v = 0; v < vMax; v++)
            {
                ptu = true;
                for(int u = 0; u < uMax; u++)
                {
                    int alpha = sprite.getPixelRGBA(f, u, vMax - v - 1) >> 24 & 0xFF;
                    boolean t = alpha / 255f <= 0.1f;

                    if (!t && alpha < 255)
                    {
                        translucent = true;
                    }

                    if(ptu && !t) // left - transparent, right - opaque
                    {
                        faceData.set(Direction.WEST, u, v);
                    }
                    if(!ptu && t) // left - opaque, right - transparent
                    {
                        faceData.set(Direction.EAST, u-1, v);
                    }
                    if(ptv[u] && !t) // up - transparent, down - opaque
                    {
                        faceData.set(Direction.UP, u, v);
                    }
                    if(!ptv[u] && t) // up - opaque, down - transparent
                    {
                        faceData.set(Direction.DOWN, u, v-1);
                    }

                    ptu = t;
                    ptv[u] = t;
                }
                if(!ptu) // last - opaque
                {
                    faceData.set(Direction.EAST, uMax-1, v);
                }
            }
            // last line
            for(int u = 0; u < uMax; u++)
            {
                if(!ptv[u])
                {
                    faceData.set(Direction.DOWN, u, vMax-1);
                }
            }
        }

        // horizontal quads
        for (Direction facing : HORIZONTALS)
        {
            for (int v = 0; v < vMax; v++)
            {
                int uStart = 0, uEnd = uMax;
                boolean building = false;
                for (int u = 0; u < uMax; u++)
                {
                    boolean face = faceData.get(facing, u, v);
                    if (!translucent)
                    {
                        if (face)
                        {
                            if (!building)
                            {
                                building = true;
                                uStart = u;
                            }
                            uEnd = u + 1;
                        }
                    }
                    else
                    {
                        if (building && !face) // finish current quad
                        {
                            // make quad [uStart, u]
                            int off = facing == Direction.DOWN ? 1 : 0;
                            builder.add(buildSideQuad(transform, facing, tint, sprite, uStart, v+off, u-uStart, fullbright));
                            building = false;
                        }
                        else if (!building && face) // start new quad
                        {
                            building = true;
                            uStart = u;
                        }
                    }
                }
                if (building) // build remaining quad
                {
                    // make quad [uStart, uEnd]
                    int off = facing == Direction.DOWN ? 1 : 0;
                    builder.add(buildSideQuad(transform, facing, tint, sprite, uStart, v+off, uEnd-uStart, fullbright));
                }
            }
        }

        // vertical quads
        for (Direction facing : VERTICALS)
        {
            for (int u = 0; u < uMax; u++)
            {
                int vStart = 0, vEnd = vMax;
                boolean building = false;
                for (int v = 0; v < vMax; v++)
                {
                    boolean face = faceData.get(facing, u, v);
                    if (!translucent)
                    {
                        if (face)
                        {
                            if (!building)
                            {
                                building = true;
                                vStart = v;
                            }
                            vEnd = v + 1;
                        }
                    }
                    else
                    {
                        if (building && !face) // finish current quad
                        {
                            // make quad [vStart, v]
                            int off = facing == Direction.EAST ? 1 : 0;
                            builder.add(buildSideQuad(transform, facing, tint, sprite, u+off, vStart, v-vStart, fullbright));
                            building = false;
                        }
                        else if (!building && face) // start new quad
                        {
                            building = true;
                            vStart = v;
                        }
                    }
                }
                if (building) // build remaining quad
                {
                    // make quad [vStart, vEnd]
                    int off = facing == Direction.EAST ? 1 : 0;
                    builder.add(buildSideQuad(transform, facing, tint, sprite, u+off, vStart, vEnd-vStart, fullbright));
                }
            }
        }

        // front
        builder.add(buildQuad(transform, Direction.NORTH, sprite, tint, fullbright,
            0, 0, 7.5f / 16f, sprite.getU0(), sprite.getV1(),
            0, 1, 7.5f / 16f, sprite.getU0(), sprite.getV0(),
            1, 1, 7.5f / 16f, sprite.getU1(), sprite.getV0(),
            1, 0, 7.5f / 16f, sprite.getU1(), sprite.getV1()
        ));
        // back
        builder.add(buildQuad(transform, Direction.SOUTH, sprite, tint, fullbright,
            0, 0, 8.5f / 16f, sprite.getU0(), sprite.getV1(),
            1, 0, 8.5f / 16f, sprite.getU1(), sprite.getV1(),
            1, 1, 8.5f / 16f, sprite.getU1(), sprite.getV0(),
            0, 1, 8.5f / 16f, sprite.getU0(), sprite.getV0()
        ));

        return builder.build();
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        textures = getTextures(owner);
        return textures;
    }

    private static class FaceData
    {
        private final EnumMap<Direction, BitSet> data = new EnumMap<>(Direction.class);

        private final int vMax;

        FaceData(int uMax, int vMax)
        {
            this.vMax = vMax;

            data.put(Direction.WEST, new BitSet(uMax * vMax));
            data.put(Direction.EAST, new BitSet(uMax * vMax));
            data.put(Direction.UP,   new BitSet(uMax * vMax));
            data.put(Direction.DOWN, new BitSet(uMax * vMax));
        }

        public void set(Direction facing, int u, int v)
        {
            data.get(facing).set(getIndex(u, v));
        }

        public boolean get(Direction facing, int u, int v)
        {
            return data.get(facing).get(getIndex(u, v));
        }

        private int getIndex(int u, int v)
        {
            return v * vMax + u;
        }
    }

    private static BakedQuad buildSideQuad(Transformation transform, Direction side, int tint, TextureAtlasSprite sprite, int u, int v, int size, boolean fullbright)
    {
        final float eps = 1e-2f;

        int width = sprite.getWidth();
        int height = sprite.getHeight();

        float x0 = (float) u / width;
        float y0 = (float) v / height;
        float x1 = x0, y1 = y0;
        float z0 = 7.5f / 16f, z1 = 8.5f / 16f;

        switch(side)
        {
        case WEST:
            z0 = 8.5f / 16f;
            z1 = 7.5f / 16f;
        case EAST:
            y1 = (float) (v + size) / height;
            break;
        case DOWN:
            z0 = 8.5f / 16f;
            z1 = 7.5f / 16f;
        case UP:
            x1 = (float) (u + size) / width;
            break;
        default:
            throw new IllegalArgumentException("can't handle z-oriented side");
        }

        float dx = side.getNormal().getX() * eps / width;
        float dy = side.getNormal().getY() * eps / height;

        float u0 = 16f * (x0 - dx);
        float u1 = 16f * (x1 - dx);
        float v0 = 16f * (1f - y0 - dy);
        float v1 = 16f * (1f - y1 - dy);

        return buildQuad(
            transform, remap(side), sprite, tint, fullbright,
            x0, y0, z0, sprite.getU(u0), sprite.getV(v0),
            x1, y1, z0, sprite.getU(u1), sprite.getV(v1),
            x1, y1, z1, sprite.getU(u1), sprite.getV(v1),
            x0, y0, z1, sprite.getU(u0), sprite.getV(v0)
        );
    }

    private static Direction remap(Direction side)
    {
        // getOpposite is related to the swapping of V direction
        return side.getAxis() == Direction.Axis.Y ? side.getOpposite() : side;
    }

    private static BakedQuad buildQuad(Transformation transform, Direction side, TextureAtlasSprite sprite, int tint, boolean fullbright,
        float x0, float y0, float z0, float u0, float v0,
        float x1, float y1, float z1, float u1, float v1,
        float x2, float y2, float z2, float u2, float v2,
        float x3, float y3, float z3, float u3, float v3)
    {
        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);

        builder.setQuadTint(tint);
        builder.setQuadOrientation(side);
        builder.setApplyDiffuseLighting(false);

        boolean hasTransform = !transform.isIdentity();
        IVertexConsumer consumer = hasTransform ? new TRSRTransformer(builder, transform) : builder;

        int uLight, vLight;
        uLight = vLight = fullbright ? 15 : 0;

        putVertex(consumer, side, x0, y0, z0, u0, v0, uLight, vLight);
        putVertex(consumer, side, x1, y1, z1, u1, v1, uLight, vLight);
        putVertex(consumer, side, x2, y2, z2, u2, v2, uLight, vLight);
        putVertex(consumer, side, x3, y3, z3, u3, v3, uLight, vLight);

        return builder.build();
    }

    private static void putVertex(IVertexConsumer consumer, Direction side, float x, float y, float z, float u, float v, int uLight, int vLight)
    {
        VertexFormat format = consumer.getVertexFormat();
        for(int e = 0; e < format.getElements().size(); e++)
        {
            VertexFormatElement element = format.getElements().get(e);
            outer:switch(element.getUsage())
            {
            case POSITION:
                consumer.put(e, x, y, z, 1f);
                break;
            case COLOR:
                consumer.put(e, 1f, 1f, 1f, 1f);
                break;
            case NORMAL:
                float offX = (float) side.getStepX();
                float offY = (float) side.getStepY();
                float offZ = (float) side.getStepZ();
                consumer.put(e, offX, offY, offZ, 0f);
                break;
            case UV:
                switch(element.getIndex())
                {
                    case 0:
                        consumer.put(e, u, v, 0f, 1f);
                        break outer;
                    case 2:
                        consumer.put(e, (uLight<<4)/32768.0f, (vLight<<4)/32768.0f, 0, 1);
                        break outer;
                }
                // else fallthrough to default
            default:
                consumer.put(e);
                break;
            }
        }
    }

    public static class Loader implements IModelLoader<ItemLayerModel>
    {
        public static final Loader INSTANCE = new Loader();

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager)
        {
            // nothing to do
        }

        @Override
        public ItemLayerModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
            ImmutableSet.Builder<Integer> fullbrightLayers = ImmutableSet.builder();
            if (modelContents.has("fullbright_layers"))
            {
                JsonArray arr = GsonHelper.getAsJsonArray(modelContents, "fullbright_layers");
                for(int i=0;i<arr.size();i++)
                {
                    fullbrightLayers.add(arr.get(i).getAsInt());
                }
            }
            return new ItemLayerModel(null, fullbrightLayers.build());
        }
    }
}
