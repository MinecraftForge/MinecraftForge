package net.minecraftforge.client.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class ItemLayerModel implements IRetexturableModel {

    public static final ItemLayerModel instance = new ItemLayerModel(ImmutableList.<ResourceLocation>of());

    private final ImmutableList<ResourceLocation> textures;

    public ItemLayerModel(ImmutableList<ResourceLocation> textures)
    {
        this.textures = textures;
    }

    public ItemLayerModel(ModelBlock model)
    {
        this(getTextures(model));
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

    public Collection<ResourceLocation> getTextures()
    {
        return textures;
    }

    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }

    public IModel retexture(ImmutableMap<String, String> textures)
    {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        for(int i = 0; i < textures.size(); i++)
        {
            if(textures.containsKey("layer" + i))
            {
                builder.add(new ResourceLocation(textures.get("layer" + i)));
            }
        }
        return new ItemLayerModel(builder.build());
    }

    public IFlexibleBakedModel bake(IModelState state, final VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        final TRSRTransformation transform = state.apply(this);
        for(int i = 0; i < textures.size(); i++)
        {
            TextureAtlasSprite sprite = bakedTextureGetter.apply(textures.get(i));
            builder.addAll(getQuadsForSprite(i, sprite, format, transform));
        }
        TextureAtlasSprite particle = bakedTextureGetter.apply(textures.isEmpty() ? new ResourceLocation("missingno") : textures.get(0));
        if(state instanceof IPerspectiveState)
        {
            IPerspectiveState ps = (IPerspectiveState)state;
            ImmutableMap<TransformType, TRSRTransformation> map = IPerspectiveAwareModel.MapWrapper.getTransforms(ps, this);
            return new BakedModel(builder.build(), particle, format, Maps.immutableEnumMap(map));
        }
        return new BakedModel(builder.build(), particle, format);
    }

    public static class BakedModel implements IFlexibleBakedModel, IPerspectiveAwareModel
    {
        private final ImmutableList<BakedQuad> quads;
        private final TextureAtlasSprite particle;
        private final VertexFormat format;
        private final ImmutableMap<TransformType, TRSRTransformation> transforms;

        public BakedModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format)
        {
            this(quads, particle, format, ImmutableMap.<TransformType, TRSRTransformation>of());
        }

        public BakedModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format, ImmutableMap<TransformType, TRSRTransformation> transforms)
        {
            this.quads = quads;
            this.particle = particle;
            this.format = format;
            this.transforms = transforms;
        }

        public boolean isAmbientOcclusion() { return true; }
        public boolean isGui3d() { return false; }
        public boolean isBuiltInRenderer() { return false; }
        public TextureAtlasSprite getTexture() { return particle; }
        public ItemCameraTransforms getItemCameraTransforms() { return ItemCameraTransforms.DEFAULT; }
        public List<BakedQuad> getFaceQuads(EnumFacing side) { return ImmutableList.of(); }
        public List<BakedQuad> getGeneralQuads() { return quads; }
        public VertexFormat getFormat() { return format; }

        @Override
        public Pair<IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            TRSRTransformation tr = transforms.get(cameraTransformType);
            Matrix4f mat = null;
            if(tr != null && tr != TRSRTransformation.identity()) mat = TRSRTransformation.blockCornerToCenter(tr).getMatrix();
            return Pair.of((IBakedModel)this, mat);
        }
    }

    public ImmutableList<BakedQuad> getQuadsForSprite(int tint, TextureAtlasSprite sprite, VertexFormat format, TRSRTransformation transform)
    {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        int uMax = sprite.getIconWidth();
        int vMax = sprite.getIconHeight();

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
                        builder.add(buildSideQuad(format, transform, EnumFacing.WEST, tint, sprite, u, v));
                    }
                    if(!ptu && t) // left - opaque, right - transparent
                    {
                        builder.add(buildSideQuad(format, transform, EnumFacing.EAST, tint, sprite, u, v));
                    }
                    if(ptv[u] && !t) // up - transparent, down - opaque
                    {
                        builder.add(buildSideQuad(format, transform, EnumFacing.UP, tint, sprite, u, v));
                    }
                    if(!ptv[u] && t) // up - opaque, down - transparent
                    {
                        builder.add(buildSideQuad(format, transform, EnumFacing.DOWN, tint, sprite, u, v));
                    }
                    ptu = t;
                    ptv[u] = t;
                }
                if(!ptu) // last - opaque
                {
                    builder.add(buildSideQuad(format, transform, EnumFacing.EAST, tint, sprite, uMax, v));
                }
            }
            // last line
            for(int u = 0; u < uMax; u++)
            {
                if(!ptv[u])
                {
                    builder.add(buildSideQuad(format, transform, EnumFacing.DOWN, tint, sprite, u, vMax));
                }
            }
        }
        // front
        builder.add(buildQuad(format, transform, EnumFacing.SOUTH, tint,
            0, 0, 7.5f / 16f, sprite.getMinU(), sprite.getMaxV(),
            0, 1, 7.5f / 16f, sprite.getMinU(), sprite.getMinV(),
            1, 1, 7.5f / 16f, sprite.getMaxU(), sprite.getMinV(),
            1, 0, 7.5f / 16f, sprite.getMaxU(), sprite.getMaxV()
        ));
        // back
        builder.add(buildQuad(format, transform, EnumFacing.NORTH, tint,
            0, 0, 8.5f / 16f, sprite.getMinU(), sprite.getMaxV(),
            1, 0, 8.5f / 16f, sprite.getMaxU(), sprite.getMaxV(),
            1, 1, 8.5f / 16f, sprite.getMaxU(), sprite.getMinV(),
            0, 1, 8.5f / 16f, sprite.getMinU(), sprite.getMinV()
        ));
        return builder.build();
    }

    protected boolean isTransparent(int[] pixels, int uMax, int vMax, int u, int v)
    {
        return (pixels[u + (vMax - 1 - v) * uMax] >> 24 & 0xFF) == 0;
    }

    private static BakedQuad buildSideQuad(VertexFormat format, TRSRTransformation transform, EnumFacing side, int tint, TextureAtlasSprite sprite, int u, int v)
    {
        float x0 = (float)u / sprite.getIconWidth();
        float y0 = (float)v / sprite.getIconHeight();
        float x1 = x0, y1 = y0;
        float z1 = 7.5f / 16f, z2 = 8.5f / 16f;
        switch(side)
        {
        case WEST:
            z1 = 8.5f / 16f;
            z2 = 7.5f / 16f;
        case EAST:
            y1 = (v + 1f) / sprite.getIconHeight();
            break;
        case DOWN:
            z1 = 8.5f / 16f;
            z2 = 7.5f / 16f;
        case UP:
            x1 = (u + 1f) / sprite.getIconWidth();
            break;
        default:
            throw new IllegalArgumentException("can't handle z-oriented side");
        }
        float u0 = 16f * (x0 - side.getDirectionVec().getX() * 1e-2f / sprite.getIconWidth());
        float u1 = 16f * (x1 - side.getDirectionVec().getX() * 1e-2f / sprite.getIconWidth());
        float v0 = 16f * (1f - y0 - side.getDirectionVec().getY() * 1e-2f / sprite.getIconHeight());
        float v1 = 16f * (1f - y1 - side.getDirectionVec().getY() * 1e-2f / sprite.getIconHeight());
        return buildQuad(
            format, transform, side.getOpposite(), tint, // getOpposite is related either to the swapping of V direction, or something else
            x0, y0, z1, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0),
            x1, y1, z1, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1),
            x1, y1, z2, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1),
            x0, y0, z2, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0)
        );
    }

    private static final BakedQuad buildQuad(
        VertexFormat format, TRSRTransformation transform, EnumFacing side, int tint,
        float x0, float y0, float z0, float u0, float v0,
        float x1, float y1, float z1, float u1, float v1,
        float x2, float y2, float z2, float u2, float v2,
        float x3, float y3, float z3, float u3, float v3)
    {
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setQuadTint(tint);
        builder.setQuadOrientation(side);
        putVertex(builder, format, transform, side, x0, y0, z0, u0, v0);
        putVertex(builder, format, transform, side, x1, y1, z1, u1, v1);
        putVertex(builder, format, transform, side, x2, y2, z2, u2, v2);
        putVertex(builder, format, transform, side, x3, y3, z3, u3, v3);
        return builder.build();
    }

    private static void putVertex(UnpackedBakedQuad.Builder builder, VertexFormat format, TRSRTransformation transform, EnumFacing side, float x, float y, float z, float u, float v)
    {
        Vector4f vec = new Vector4f();
        for(int e = 0; e < format.getElementCount(); e++)
        {
            switch(format.getElement(e).getUsage())
            {
            case POSITION:
                vec.x = x;
                vec.y = y;
                vec.z = z;
                vec.w = 1;
                transform.getMatrix().transform(vec);
                builder.put(e, vec.x, vec.y, vec.z, vec.w);
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
        instance;

        public void onResourceManagerReload(IResourceManager resourceManager) {}

        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals("forge") && (
                modelLocation.getResourcePath().equals("item-layer") ||
                modelLocation.getResourcePath().equals("models/block/item-layer") ||
                modelLocation.getResourcePath().equals("models/item/item-layer"));
        }

        public IModel loadModel(ResourceLocation modelLocation)
        {
            return ItemLayerModel.instance;
        }
    }
}
