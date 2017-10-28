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

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.BakeItemLayerModelEvent;
import net.minecraftforge.client.event.ClientAttachCapabilitiesEvent;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class ItemLayerModel implements IModel, ICapabilityProvider
{
    public static final ItemLayerModel INSTANCE = new ItemLayerModel(ImmutableList.of());

    private static final EnumFacing[] HORIZONTALS = {EnumFacing.UP, EnumFacing.DOWN};
    private static final EnumFacing[] VERTICALS = {EnumFacing.WEST, EnumFacing.EAST};

    private final ImmutableList<ResourceLocation> textures;
    private final ItemOverrideList overrides;
    private final CapabilityDispatcher capabilities;

    public ItemLayerModel(ImmutableList<ResourceLocation> textures, NBTTagCompound capabilities)
    {
        this(textures, ItemOverrideList.NONE, capabilities);
    }

    public ItemLayerModel(ImmutableList<ResourceLocation> textures)
    {
        this(textures, (NBTTagCompound) null);
    }

    public ItemLayerModel(ImmutableList<ResourceLocation> textures, ItemOverrideList overrides, NBTTagCompound capabilities)
    {
        this.textures = textures;
        this.overrides = overrides;
        this.capabilities = net.minecraftforge.event.ForgeEventFactory.gatherCapabilitiesNotNull(new ClientAttachCapabilitiesEvent.ItemLayerModel(this));
        if(capabilities != null) this.capabilities.deserializeNBT(capabilities);
    }

    public ItemLayerModel(ImmutableList<ResourceLocation> textures, ItemOverrideList overrides)
    {
        this(textures, overrides, null);
    }

    public ItemLayerModel(ModelBlock model, NBTTagCompound capabilities)
    {
        this(getTextures(model), model.createOverrides(), capabilities);
    }

    public ItemLayerModel(ModelBlock model)
    {
        this(model, null);
    }

    private static ImmutableList<ResourceLocation> getTextures(ModelBlock model)
    {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        for(int i = 0; model.isTexturePresent("layer" + i); i++)
        {
            builder.add(new ResourceLocation(model.resolveTextureName("layer" + i)));
        }
        return builder.build();
    }

    public Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.of();
    }

    public ItemOverrideList getOverrides()
    {
        return overrides;
    }

    public CapabilityDispatcher getCapabilities()
    {
        return capabilities;
    }

    public Collection<ResourceLocation> getTextures()
    {
        return textures;
    }

    public ItemLayerModel retexture(ImmutableMap<String, String> textures)
    {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        for(int i = 0; i < textures.size() + this.textures.size(); i++)
        {
            if(textures.containsKey("layer" + i))
            {
                builder.add(new ResourceLocation(textures.get("layer" + i)));
            }
            else if(i < this.textures.size())
            {
                builder.add(this.textures.get(i));
            }
        }
        return new ItemLayerModel(builder.build(), overrides, capabilities.serializeNBT());
    }

    @Override
    public IBakedModel bake(IModelState state, final VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        Optional<TRSRTransformation> transform = state.apply(Optional.empty());
        BakeItemLayerModelEvent event = new BakeItemLayerModelEvent.Pre(this, state, format, bakedTextureGetter, builder, transform);
        MinecraftForge.EVENT_BUS.post(event);
        builder = event.getBuilder();
        transform = event.getTransform();
        for(int i = 0; i < textures.size(); i++)
        {
            TextureAtlasSprite sprite = bakedTextureGetter.apply(textures.get(i));
            builder.addAll(getQuadsForSprite(i, sprite, format, transform));
        }
        TextureAtlasSprite particle = bakedTextureGetter.apply(textures.isEmpty() ? new ResourceLocation("missingno") : textures.get(0));
        ImmutableMap<TransformType, TRSRTransformation> map = PerspectiveMapWrapper.getTransforms(state);
        BakeItemLayerModelEvent.Post pevent = new BakeItemLayerModelEvent.Post(this, state, format, bakedTextureGetter, builder, transform, particle, map);
        MinecraftForge.EVENT_BUS.post(pevent);
        return new BakedItemModel(pevent.getBuilder().build(), pevent.getParticle(), pevent.getMap(), overrides, null);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (getCapability(capability, facing) != null)
            return true;
        return capabilities == null ? false : capabilities.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capabilities == null ? null : capabilities.getCapability(capability, facing);
    }

    private static final class BakedItemModel implements IBakedModel
    {
        private final ImmutableList<BakedQuad> quads;
        private final TextureAtlasSprite particle;
        private final ImmutableMap<TransformType, TRSRTransformation> transforms;
        private final IBakedModel otherModel;
        private final boolean isCulled;
        private final ItemOverrideList overrides;

        public BakedItemModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, ImmutableMap<TransformType, TRSRTransformation> transforms, ItemOverrideList overrides, @Nullable IBakedModel otherModel)
        {
            this.quads = quads;
            this.particle = particle;
            this.transforms = transforms;
            this.overrides = overrides;
            if(otherModel != null)
            {
                this.otherModel = otherModel;
                this.isCulled = true;
            }
            else
            {
                ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                for(BakedQuad quad : quads)
                {
                    if(quad.getFace() == EnumFacing.SOUTH)
                    {
                        builder.add(quad);
                    }
                }
                this.otherModel = new BakedItemModel(builder.build(), particle, transforms, overrides, this);
                isCulled = false;
            }
        }

        public boolean isAmbientOcclusion() { return true; }
        public boolean isGui3d() { return false; }
        public boolean isBuiltInRenderer() { return false; }
        public TextureAtlasSprite getParticleTexture() { return particle; }
        public ItemOverrideList getOverrides() { return overrides; }
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
        {
            if(side == null) return quads;
            return ImmutableList.of();
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type)
        {
            Pair<? extends IBakedModel, Matrix4f> pair = PerspectiveMapWrapper.handlePerspective(this, transforms, type);
            if(type == TransformType.GUI && !isCulled && pair.getRight() == null)
            {
                return Pair.of(otherModel, null);
            }
            else if(type != TransformType.GUI && isCulled)
            {
                return Pair.of(otherModel, pair.getRight());
            }
            return pair;
        }
    }

    public static ImmutableList<BakedQuad> getQuadsForSprite(int tint, TextureAtlasSprite sprite, VertexFormat format, Optional<TRSRTransformation> transform)
    {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        int uMax = sprite.getIconWidth();
        int vMax = sprite.getIconHeight();

        FaceData faceData = new FaceData(uMax, vMax);

        for(int f = 0; f < sprite.getFrameCount(); f++)
        {
            int[] pixels = sprite.getFrameTextureData(f)[0];
            boolean ptu;
            boolean[] ptv = new boolean[uMax];
            Arrays.fill(ptv, true);
            for(int v = 0; v < vMax; v++)
            {
                ptu = true;
                for(int u = 0; u < uMax; u++)
                {
                    boolean t = isTransparent(pixels, uMax, vMax, u, v);
                    if(ptu && !t) // left - transparent, right - opaque
                    {
                        faceData.set(EnumFacing.WEST, u, v);
                    }
                    if(!ptu && t) // left - opaque, right - transparent
                    {
                        faceData.set(EnumFacing.EAST, u-1, v);
                    }
                    if(ptv[u] && !t) // up - transparent, down - opaque
                    {
                        faceData.set(EnumFacing.UP, u, v);
                    }
                    if(!ptv[u] && t) // up - opaque, down - transparent
                    {
                        faceData.set(EnumFacing.DOWN, u, v-1);
                    }
                    ptu = t;
                    ptv[u] = t;
                }
                if(!ptu) // last - opaque
                {
                    faceData.set(EnumFacing.EAST, uMax-1, v);
                }
            }
            // last line
            for(int u = 0; u < uMax; u++)
            {
                if(!ptv[u])
                {
                    faceData.set(EnumFacing.DOWN, u, vMax-1);
                }
            }
        }

        // horizontal quads
        for (EnumFacing facing : HORIZONTALS)
        {
            for (int v = 0; v < vMax; v++)
            {
                int uStart = 0;
                boolean building = false;
                for (int u = 0; u < uMax; u++)
                {
                    boolean face = faceData.get(facing, u, v);
                    if (building && !face) // finish current quad
                    {
                        // make quad [uStart, u]
                        int off = facing == EnumFacing.DOWN ? 1 : 0;
                        builder.add(buildSideQuad(format, transform, facing, tint, sprite, uStart, v+off, u-uStart));
                        building = false;
                    }
                    else if (!building && face) // start new quad
                    {
                        building = true;
                        uStart = u;
                    }
                }
                if (building) // quad extends to far edge
                {
                    // make quad [uStart, uMax]
                    int off = facing == EnumFacing.DOWN ? 1 : 0;
                    builder.add(buildSideQuad(format, transform, facing, tint, sprite, uStart, v+off, uMax-uStart));
                }
            }
        }

        // vertical quads
        for (EnumFacing facing : VERTICALS)
        {
            for (int u = 0; u < uMax; u++)
            {
                int vStart = 0;
                boolean building = false;
                for (int v = 0; v < vMax; v++)
                {
                    boolean face = faceData.get(facing, u, v);
                    if (building && !face) // finish current quad
                    {
                        // make quad [vStart, v]
                        int off = facing == EnumFacing.EAST ? 1 : 0;
                        builder.add(buildSideQuad(format, transform, facing, tint, sprite, u+off, vStart, v-vStart));
                        building = false;
                    }
                    else if (!building && face) // start new quad
                    {
                        building = true;
                        vStart = v;
                    }
                }
                if (building) // quad extends to far edge
                {
                    // make quad [vStart, vMax]
                    int off = facing == EnumFacing.EAST ? 1 : 0;
                    builder.add(buildSideQuad(format, transform, facing, tint, sprite, u+off, vStart, vMax-vStart));
                }
            }
        }

        // front
        builder.add(buildQuad(format, transform, EnumFacing.NORTH, sprite, tint,
            0, 0, 7.5f / 16f, sprite.getMinU(), sprite.getMaxV(),
            0, 1, 7.5f / 16f, sprite.getMinU(), sprite.getMinV(),
            1, 1, 7.5f / 16f, sprite.getMaxU(), sprite.getMinV(),
            1, 0, 7.5f / 16f, sprite.getMaxU(), sprite.getMaxV()
        ));
        // back
        builder.add(buildQuad(format, transform, EnumFacing.SOUTH, sprite, tint,
            0, 0, 8.5f / 16f, sprite.getMinU(), sprite.getMaxV(),
            1, 0, 8.5f / 16f, sprite.getMaxU(), sprite.getMaxV(),
            1, 1, 8.5f / 16f, sprite.getMaxU(), sprite.getMinV(),
            0, 1, 8.5f / 16f, sprite.getMinU(), sprite.getMinV()
        ));

        return builder.build();
    }

    private static class FaceData
    {
        private final EnumMap<EnumFacing, BitSet> data = new EnumMap<>(EnumFacing.class);

        private final int vMax;

        FaceData(int uMax, int vMax)
        {
            this.vMax = vMax;

            data.put(EnumFacing.WEST, new BitSet(uMax * vMax));
            data.put(EnumFacing.EAST, new BitSet(uMax * vMax));
            data.put(EnumFacing.UP,   new BitSet(uMax * vMax));
            data.put(EnumFacing.DOWN, new BitSet(uMax * vMax));
        }

        public void set(EnumFacing facing, int u, int v)
        {
            data.get(facing).set(getIndex(u, v));
        }

        public boolean get(EnumFacing facing, int u, int v)
        {
            return data.get(facing).get(getIndex(u, v));
        }

        private int getIndex(int u, int v)
        {
            return v * vMax + u;
        }
    }

    private static boolean isTransparent(int[] pixels, int uMax, int vMax, int u, int v)
    {
        return (pixels[u + (vMax - 1 - v) * uMax] >> 24 & 0xFF) == 0;
    }

    private static BakedQuad buildSideQuad(VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, int tint, TextureAtlasSprite sprite, int u, int v, int size)
    {
        final float eps0 = 30e-5f;
        final float eps1 = 45e-5f;
        final float eps2 = .5f;
        final float eps3 = .5f;
        float x0 = (float)u / sprite.getIconWidth();
        float y0 = (float)v / sprite.getIconHeight();
        float x1 = x0, y1 = y0;
        float z1 = 7.5f / 16f - eps1, z2 = 8.5f / 16f + eps1;
        switch(side)
        {
        case WEST:
            z1 = 8.5f / 16f + eps1;
            z2 = 7.5f / 16f - eps1;
        case EAST:
            y1 = (float) (v + size) / sprite.getIconHeight();
            break;
        case DOWN:
            z1 = 8.5f / 16f + eps1;
            z2 = 7.5f / 16f - eps1;
        case UP:
            x1 = (float) (u + size) / sprite.getIconWidth();
            break;
        default:
            throw new IllegalArgumentException("can't handle z-oriented side");
        }
        float u0 = 16f * (x0 - side.getDirectionVec().getX() * eps3 / sprite.getIconWidth());
        float u1 = 16f * (x1 - side.getDirectionVec().getX() * eps3 / sprite.getIconWidth());
        float v0 = 16f * (1f - y0 - side.getDirectionVec().getY() * eps3 / sprite.getIconHeight());
        float v1 = 16f * (1f - y1 - side.getDirectionVec().getY() * eps3 / sprite.getIconHeight());
        switch(side)
        {
        case WEST:
        case EAST:
            y0 -= eps1;
            y1 += eps1;
            v0 -= eps2 / sprite.getIconHeight();
            v1 += eps2 / sprite.getIconHeight();
            break;
        case DOWN:
        case UP:
            x0 -= eps1;
            x1 += eps1;
            u0 += eps2 / sprite.getIconWidth();
            u1 -= eps2 / sprite.getIconWidth();
            break;
        default:
            throw new IllegalArgumentException("can't handle z-oriented side");
        }
        switch(side)
        {
        case WEST:
            x0 += eps0;
            x1 += eps0;
            break;
        case EAST:
            x0 -= eps0;
            x1 -= eps0;
            break;
        case DOWN:
            y0 -= eps0;
            y1 -= eps0;
            break;
        case UP:
            y0 += eps0;
            y1 += eps0;
            break;
        default:
            throw new IllegalArgumentException("can't handle z-oriented side");
        }
        return buildQuad(
            format, transform, side.getOpposite(), sprite, tint, // getOpposite is related either to the swapping of V direction, or something else
            x0, y0, z1, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0),
            x1, y1, z1, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1),
            x1, y1, z2, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1),
            x0, y0, z2, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0)
        );
    }

    private static final BakedQuad buildQuad(
        VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, TextureAtlasSprite sprite, int tint,
        float x0, float y0, float z0, float u0, float v0,
        float x1, float y1, float z1, float u1, float v1,
        float x2, float y2, float z2, float u2, float v2,
        float x3, float y3, float z3, float u3, float v3)
    {
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setQuadTint(tint);
        builder.setQuadOrientation(side);
        builder.setTexture(sprite);
        putVertex(builder, format, transform, side, x0, y0, z0, u0, v0);
        putVertex(builder, format, transform, side, x1, y1, z1, u1, v1);
        putVertex(builder, format, transform, side, x2, y2, z2, u2, v2);
        putVertex(builder, format, transform, side, x3, y3, z3, u3, v3);
        return builder.build();
    }

    private static void putVertex(UnpackedBakedQuad.Builder builder, VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, float x, float y, float z, float u, float v)
    {
        Vector4f vec = new Vector4f();
        for(int e = 0; e < format.getElementCount(); e++)
        {
            switch(format.getElement(e).getUsage())
            {
            case POSITION:
                if(transform.isPresent())
                {
                    vec.x = x;
                    vec.y = y;
                    vec.z = z;
                    vec.w = 1;
                    transform.get().getMatrix().transform(vec);
                    builder.put(e, vec.x, vec.y, vec.z, vec.w);
                }
                else
                {
                    builder.put(e, x, y, z, 1);
                }
                break;
            case COLOR:
                builder.put(e, 1f, 1f, 1f, 1f);
                break;
            case UV: if(format.getElement(e).getIndex() == 0)
            {
                builder.put(e, u, v, 0f, 1f);
                break;
            }
            case NORMAL:
                builder.put(e, (float)side.getFrontOffsetX(), (float)side.getFrontOffsetY(), (float)side.getFrontOffsetZ(), 0f);
                break;
            default:
                builder.put(e);
                break;
            }
        }
    }

    public static enum Loader implements ICustomModelLoader
    {
        INSTANCE;

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {}

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals(ForgeVersion.MOD_ID) && (
                modelLocation.getResourcePath().equals("item-layer") ||
                modelLocation.getResourcePath().equals("models/block/item-layer") ||
                modelLocation.getResourcePath().equals("models/item/item-layer"));
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation)
        {
            return ItemLayerModel.INSTANCE;
        }
    }
}
