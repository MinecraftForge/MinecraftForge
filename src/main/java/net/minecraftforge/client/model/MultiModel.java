package net.minecraftforge.client.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

// TODO: Switch to vanilla class, or to something similar
@Deprecated
public class MultiModel implements IModel
{
    public static class Baked implements IPerspectiveAwareModel
    {
        protected final IBakedModel base;
        protected final ImmutableMap<String, IBakedModel> parts;

        protected final IBakedModel internalBase;
        protected ImmutableMap<Optional<EnumFacing>, ImmutableList<BakedQuad>> quads;
        protected final ImmutableMap<TransformType, Pair<Baked, TRSRTransformation>> transforms;

        public Baked(IBakedModel base, ImmutableMap<String, IBakedModel> parts)
        {
            this(null, false, base, parts);
        }

        public Baked(ResourceLocation location, boolean perspective, IBakedModel base, ImmutableMap<String, IBakedModel> parts)
        {
            this.base = base;
            this.parts = parts;

            if (base != null)
                internalBase = base;
            else
            {
                Iterator<IBakedModel> iter = parts.values().iterator();
                if (iter.hasNext())
                    internalBase = iter.next();
                else
                    throw new IllegalArgumentException("No base model or submodel provided for MultiModel.Baked " + location + ".");
            }

            // Only changes the base model based on perspective, may recurse for parts in the future.
            if(perspective && base instanceof IPerspectiveAwareModel)
            {
                IPerspectiveAwareModel perBase = (IPerspectiveAwareModel)base;
                ImmutableMap.Builder<TransformType, Pair<Baked, TRSRTransformation>> builder = ImmutableMap.builder();
                for(TransformType type : TransformType.values())
                {
                    Pair<? extends IBakedModel, Matrix4f> p = perBase.handlePerspective(type);
                    IBakedModel newBase = p.getLeft();
                    builder.put(type, Pair.of(new Baked(location, false, newBase, parts), new TRSRTransformation(p.getRight())));
                }
                transforms = builder.build();
            }
            else
            {
                transforms = ImmutableMap.of();
            }
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return internalBase.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d()
        {
            return internalBase.isGui3d();
        }

        @Override
        public boolean func_188618_c()
        {
            return internalBase.func_188618_c();
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return internalBase.getParticleTexture();
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return internalBase.getItemCameraTransforms();
        }

        @Override
        public List<BakedQuad> func_188616_a(IBlockState state, EnumFacing side, long rand)
        {
            if(quads == null)
            {
                ImmutableMap.Builder<Optional<EnumFacing>, ImmutableList<BakedQuad>> builder = ImmutableMap.builder();

                for (EnumFacing face : EnumFacing.values())
                {
                    ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
                    if (base != null)
                    {
                        quads.addAll(base.func_188616_a(null, face, 0));
                    }
                    for (IBakedModel bakedPart : parts.values())
                    {
                        quads.addAll(bakedPart.func_188616_a(null, face, 0));
                    }
                    builder.put(Optional.of(face), quads.build());
                }
                ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
                if (base != null)
                {
                    quads.addAll(base.func_188616_a(null, null, 0));
                }
                for (IBakedModel bakedPart : parts.values())
                {
                    quads.addAll(bakedPart.func_188616_a(null, null, 0));
                }
                builder.put(Optional.absent(), quads.build());
                this.quads = builder.build();
            }
            return quads.get(Optional.fromNullable(side));
        }

        public IBakedModel getBaseModel()
        {
            return base;
        }

        public Map<String, IBakedModel> getParts()
        {
            return parts;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            if(transforms.isEmpty()) return Pair.of(this, null);
            Pair<Baked, TRSRTransformation> p = transforms.get(cameraTransformType);
            return Pair.of(p.getLeft(), p.getRight().getMatrix());
        }

        @Override
        public ItemOverrideList func_188617_f()
        {
            // TODO Auto-generated method stub
            return null;
        }
    }

    protected final ResourceLocation location;
    protected final IModel base;
    protected final IModelState baseState;
    protected final Map<String, Pair<IModel, IModelState>> parts;

    public MultiModel(ResourceLocation location, IModel base, IModelState baseState, ImmutableMap<String, Pair<IModel, IModelState>> parts)
    {
        this.location = location;
        this.base = base;
        this.baseState = baseState;
        this.parts = parts;
    }

    public MultiModel(IModel base, IModelState baseState, ImmutableMap<String, Pair<IModel, IModelState>> parts)
    {
        this(null, base, baseState, parts);
    }

    public MultiModel(IModel base, IModelState baseState, Map<String, Pair<IModel, IModelState>> parts)
    {
        this(null, base, baseState, ImmutableMap.copyOf(parts));
    }

    public MultiModel(ResourceLocation location, IModel base, IModelState baseState, Map<String, Pair<IModel, IModelState>> parts)
    {
        this(location, base, baseState, ImmutableMap.copyOf(parts));
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        Set<ResourceLocation> deps = Sets.newHashSet();

        if (base != null)
            deps.addAll(base.getDependencies());

        for (Pair<IModel, IModelState> pair : parts.values())
            deps.addAll(pair.getLeft().getDependencies());

        return deps;
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        Set<ResourceLocation> deps = Sets.newHashSet();

        if (base != null)
            deps.addAll(base.getTextures());

        for (Pair<IModel, IModelState> pair : parts.values())
            deps.addAll(pair.getLeft().getTextures());

        return deps;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        IBakedModel bakedBase = null;

        if (base != null)
            bakedBase = base.bake(state, format, bakedTextureGetter);

        ImmutableMap.Builder<String, IBakedModel> mapBuilder = ImmutableMap.builder();

        for (Entry<String, Pair<IModel, IModelState>> entry : parts.entrySet())
        {
            Pair<IModel, IModelState> pair = entry.getValue();
            mapBuilder.put(entry.getKey(), pair.getLeft().bake(pair.getRight(), format, bakedTextureGetter));
        }

        if(bakedBase == null && parts.isEmpty())
        {
            FMLLog.log(Level.ERROR, "MultiModel %s is empty (no base model or parts were provided/resolved)", location);
            IModel missing = ModelLoaderRegistry.getMissingModel();
            return missing.bake(missing.getDefaultState(), format, bakedTextureGetter);
        }
        return new Baked(location, true, bakedBase, mapBuilder.build());
    }

    /**
     * @return The base model of this MultiModel. May be null.
     */
    public IModel getBaseModel()
    {
        return base;
    }

    /**
     * @return A map of the submodel name to its IModel and IModelState.
     */
    public Map<String, Pair<IModel, IModelState>> getParts()
    {
        return parts;
    }

    @Override
    public IModelState getDefaultState()
    {
        return baseState;
    }
}
