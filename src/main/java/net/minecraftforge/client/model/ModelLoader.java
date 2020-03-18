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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.BuiltInModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemModelGenerator;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockModelDefinition;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.model.Variant;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.model.WeightedBakedModel;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.AnimationItemOverrideList;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.model.animation.IClip;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.logging.ModelLoaderErrorMessage;

import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.Random;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static net.minecraftforge.fml.Logging.MODELLOADING;

public final class ModelLoader extends ModelBakery
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<ModelResourceLocation, IUnbakedModel> stateModels = Maps.newHashMap();
    private final Map<ModelResourceLocation, BlockModelDefinition> multipartDefinitions = Maps.newHashMap();
    private final Map<BlockModelDefinition, IUnbakedModel> multipartModels = Maps.newHashMap();
    private final Map<ResourceLocation, Exception> loadingExceptions = Maps.newHashMap();
    private IUnbakedModel missingModel = null;

    private boolean isLoading = false;
    public boolean isLoading()
    {
        return isLoading;
    }

    public ModelLoader(IResourceManager manager, AtlasTexture map, BlockColors colours, IProfiler profiler)
    {
        super(manager, map, colours, false);
        VanillaLoader.INSTANCE.setLoader(this);
        VariantLoader.INSTANCE.setLoader(this);
        ModelLoaderRegistry.clearModelCache(manager);
        processLoading(profiler);
    }

    private static Set<ResourceLocation> specialModels = new HashSet<>();

    /**
     * Indicate to vanilla that it should load and bake the given model, even if no blocks or
     * items use it. This is useful if e.g. you have baked models only for entity renderers.
     * Call during {@link net.minecraftforge.client.event.ModelRegistryEvent}
     * @param rl The model, either {@link ModelResourceLocation} to point to a blockstate variant,
     *           or plain {@link ResourceLocation} to point directly to a json in the models folder.
     */
    public static void addSpecialModel(ResourceLocation rl) {
        specialModels.add(rl);
    }

    @Override
    public Set<ResourceLocation> getSpecialModels() {
        return specialModels;
    }

    /**
     * Hooked from ModelBakery, allows using MRLs that don't end with "inventory" for items.
     */
    public static ModelResourceLocation getInventoryVariant(String s)
    {
        if(s.contains("#"))
        {
            return new ModelResourceLocation(s);
        }
        return new ModelResourceLocation(s, "inventory");
    }

    protected ResourceLocation getModelLocation(ResourceLocation model)
    {
        return new ResourceLocation(model.getNamespace(), model.getPath() + ".json");
    }

    private final class VanillaModelWrapper implements IUnbakedModel
    {
        private final ResourceLocation location;
        private final BlockModel model;
        private final boolean uvlock;
        private final ModelBlockAnimation animation;

        public VanillaModelWrapper(ResourceLocation location, BlockModel model, boolean uvlock, ModelBlockAnimation animation)
        {
            this.location = location;
            this.model = model;
            this.uvlock = uvlock;
            this.animation = animation;
        }

        @Override
        public Collection<ResourceLocation> getDependencies()
        {
            Set<ResourceLocation> set = Sets.newHashSet();
            for(ResourceLocation dep : model.getDependencies())
            {
                if(!location.equals(dep))
                {
                    set.add(dep);
                    // TODO: check if this can go somewhere else, random access to global things is bad
                    stateModels.put(getInventoryVariant(dep.toString()), ModelLoaderRegistry.getModelOrLogError(dep, "Could not load override model " + dep + " for model " + location));
                }
            }
            if(model.getParentLocation() != null && !model.getParentLocation().getPath().startsWith("builtin/"))
            {
                set.add(model.getParentLocation());
            }
            return ImmutableSet.copyOf(set);
        }

        @Override
        public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
        {
            // setting parent here to make textures resolve properly
            if(model.getParentLocation() != null)
            {
                if(model.getParentLocation().getPath().equals("builtin/generated"))
                {
                    model.parent = MODEL_GENERATED;
                }
                else
                {
                    IUnbakedModel parent = ModelLoaderRegistry.getModelOrLogError(model.getParentLocation(), "Could not load vanilla model parent '" + model.getParentLocation() + "' for '" + model);
                    if(parent instanceof VanillaModelWrapper)
                    {
                        model.parent = ((VanillaModelWrapper) parent).model;
                    }
                    else
                    {
                        throw new IllegalStateException("vanilla model '" + model + "' can't have non-vanilla parent");
                    }
                }
            }

            ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

            if(model == ModelBakery.MODEL_GENERATED)
            {
                for(String s : ItemModelGenerator.LAYERS)
                {
                    String r = model.resolveTextureName(s);
                    ResourceLocation loc = new ResourceLocation(r);
                    if(!r.equals(s))
                    {
                        builder.add(loc);
                    }
                }
            }
            for(String s : model.textures.values())
            {
                if(!s.startsWith("#"))
                {
                    builder.add(new ResourceLocation(s));
                }
            }

            if (model.parent != null)
                builder.addAll(model.parent.getTextures(modelGetter, missingTextureErrors));

            return builder.build();
        }

        @Nullable
        @Override
        public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format)
        {
            return VanillaLoader.INSTANCE.modelCache.getUnchecked(new BakedModelCacheKey(this, bakery, spriteGetter, sprite.getState(), uvlock, format));
        }

        public IBakedModel bakeImpl(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, IModelState state, boolean uvlock, VertexFormat format)
        {
            BlockModel model = this.model;
            if(model == null) return getMissingModel().bake(bakery, bakedTextureGetter, new BasicState(getMissingModel().getDefaultState(), uvlock), format);

            List<TRSRTransformation> newTransforms = Lists.newArrayList();
            IModelGeometry<?> customGeometry = model.customData.getCustomGeometry();
            if (customGeometry != null)
            {
                Collection<? extends IModelGeometryPart> parts = customGeometry.getParts();
                for (int i = 0; i < parts.size(); i++)
                {
                    newTransforms.add(animation.getPartTransform(state, i));
                }
            }
            else
            {
                List<BlockPart> elements = model.getElements();
                for (int i = 0; i < elements.size(); i++)
                {
                    BlockPart part = elements.get(i);
                    newTransforms.add(animation.getPartTransform(state, part, i));
                }
            }

            ItemCameraTransforms transforms = model.getAllTransforms();
            IModelState perState = model.customData.getCustomModelState();
            if (perState == null)
            {
                perState = new SimpleModelState(PerspectiveMapWrapper.getTransformsWithFallback(state, transforms), state.apply(Optional.empty()));
            }

            if(model == ModelBakery.MODEL_GENERATED)
            {
                return new ItemLayerModel(bakery, model, format).bake(bakery, bakedTextureGetter, new BasicState(perState, uvlock), format);
            }
            TextureAtlasSprite textureatlassprite = bakedTextureGetter.apply(new ResourceLocation(model.resolveTextureName("particle")));
            if(model == ModelBakery.MODEL_ENTITY) return new BuiltInModel(transforms, model.getOverrides(bakery, model, bakedTextureGetter, format), textureatlassprite);
            if(model.customData.hasCustomGeometry())
                return bakeNew(bakery, model, perState, state, newTransforms, format, bakedTextureGetter, uvlock);
            else
                return bakeNormal(bakery, model, perState, state, newTransforms, format, bakedTextureGetter, uvlock);
        }

        private IBakedModel bakeNew(ModelBakery bakery, BlockModel model, IModelState perState, final IModelState modelState, List<TRSRTransformation> newTransforms, final VertexFormat format, final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked)
        {
            final TRSRTransformation baseState = modelState.apply(Optional.empty()).orElse(TRSRTransformation.identity());

            IBakedModel baked = model.customData.bake(bakery, bakedTextureGetter, new BasicState(baseState, uvLocked), format, model.getOverrides(bakery, model, bakedTextureGetter, format));

            return new PerspectiveMapWrapper(baked, perState)
            {
                private final ItemOverrideList overrides = new AnimationItemOverrideList(bakery, VanillaModelWrapper.this, modelState, format, bakedTextureGetter, super.getOverrides());

                @Override
                public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData modelData)
                {
                    IModelState newState = modelData.getData(Properties.AnimationProperty);
                    if(newState != null)
                    {
                        return VanillaModelWrapper.this.bake(bakery, bakedTextureGetter, new ModelStateComposition(modelState, newState, uvlock), format).getQuads(state, side, rand, modelData);
                    }
                    return super.getQuads(state, side, rand, modelData);
                }

                @Override
                public ItemOverrideList getOverrides()
                {
                    return overrides;
                }
            };
        }

        private IBakedModel bakeNormal(ModelBakery bakery, BlockModel model, IModelState perState, final IModelState modelState, List<TRSRTransformation> newTransforms, final VertexFormat format, final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked)
        {
            final TRSRTransformation baseState = modelState.apply(Optional.empty()).orElse(TRSRTransformation.identity());
            TextureAtlasSprite particle = bakedTextureGetter.apply(new ResourceLocation(model.resolveTextureName("particle")));
            SimpleBakedModel.Builder builder = (new SimpleBakedModel.Builder(model, model.getOverrides(bakery, model, bakedTextureGetter, format))).setTexture(particle);
            List<BlockPart> elements = model.getElements();
            for(int i = 0; i < elements.size(); i++)
            {
                if(modelState.apply(Optional.of(Models.getHiddenModelPart(ImmutableList.of(Integer.toString(i))))).isPresent())
                {
                    continue;
                }
                BlockPart part = elements.get(i);
                TRSRTransformation transformation = baseState;
                if(newTransforms.get(i) != null)
                {
                    transformation = transformation.compose(newTransforms.get(i));
                    BlockPartRotation rot = part.partRotation;
                    if(rot == null) rot = new BlockPartRotation(new Vector3f(), Direction.Axis.Y, 0, false);
                    part = new BlockPart(part.positionFrom, part.positionTo, part.mapFaces, rot, part.shade);
                }
                for(Map.Entry<Direction, BlockPartFace> e : part.mapFaces.entrySet())
                {
                    TextureAtlasSprite textureatlassprite1 = bakedTextureGetter.apply(new ResourceLocation(model.resolveTextureName(e.getValue().texture)));

                    if (e.getValue().cullFace == null || !TRSRTransformation.isInteger(transformation.getMatrixVec()))
                    {
                        builder.addGeneralQuad(BlockModel.makeBakedQuad(part, e.getValue(), textureatlassprite1, e.getKey(), new BasicState(transformation, uvLocked)));
                    }
                    else
                    {
                        builder.addFaceQuad(baseState.rotateTransform(e.getValue().cullFace), BlockModel.makeBakedQuad(part, e.getValue(), textureatlassprite1, e.getKey(), new BasicState(transformation, uvLocked)));
                    }
                }
            }

            return new PerspectiveMapWrapper(builder.build(), perState)
            {
                private final ItemOverrideList overrides = new AnimationItemOverrideList(bakery, VanillaModelWrapper.this, modelState, format, bakedTextureGetter, super.getOverrides());

                @Override
                public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData modelData)
                {
                    IModelState newState = modelData.getData(Properties.AnimationProperty);
                    if(newState != null)
                    {
                        return VanillaModelWrapper.this.bake(bakery, bakedTextureGetter, new ModelStateComposition(modelState, newState, uvlock), format).getQuads(state, side, rand, modelData);
                    }
                    return super.getQuads(state, side, rand, modelData);
                }

                @Override
                public ItemOverrideList getOverrides()
                {
                    return overrides;
                }
            };
        }

        @Override
        public VanillaModelWrapper retexture(ImmutableMap<String, String> textures)
        {
            if (textures.isEmpty())
                return this;

            List<BlockPart> elements = Lists.newArrayList(); //We have to duplicate this so we can edit it below.
            if (!model.customData.hasCustomGeometry())
            {
                for (BlockPart part : this.model.getElements())
                {
                    elements.add(new BlockPart(part.positionFrom, part.positionTo, Maps.newHashMap(part.mapFaces), part.partRotation, part.shade));
                }
            }
            BlockModel newModel = new BlockModel(this.model.getParentLocation(), elements,
                Maps.newHashMap(this.model.textures), this.model.isAmbientOcclusion(), this.model.isGui3d(), //New Textures man VERY IMPORTANT
                model.getAllTransforms(), Lists.newArrayList(model.getOverrides()));
            newModel.name = this.model.name;
            newModel.parent = this.model.parent;
            newModel.customData.copyFrom(this.model.customData);

            Set<String> removed = Sets.newHashSet();

            for (Entry<String, String> e : textures.entrySet())
            {
                if ("".equals(e.getValue()))
                {
                    removed.add(e.getKey());
                    newModel.textures.remove(e.getKey());
                }
                else
                    newModel.textures.put(e.getKey(), e.getValue());
            }

            // Map the model's texture references as if it was the parent of a model with the retexture map as its textures.
            Map<String, String> remapped = Maps.newHashMap();

            for (Entry<String, String> e : newModel.textures.entrySet())
            {
                if (e.getValue().startsWith("#"))
                {
                    String key = e.getValue().substring(1);
                    if (newModel.textures.containsKey(key))
                        remapped.put(e.getKey(), newModel.textures.get(key));
                }
            }

            newModel.textures.putAll(remapped);

            //Remove any faces that use a null texture, this is for performance reasons, also allows some cool layering stuff.
            if (!newModel.customData.hasCustomGeometry())
            {
                for (BlockPart part : newModel.getElements())
                {
                    part.mapFaces.entrySet().removeIf(entry -> removed.contains(entry.getValue().texture));
                }
            }

            return new VanillaModelWrapper(location, newModel, uvlock, animation);
        }

        @Override
        public Optional<? extends IClip> getClip(String name)
        {
            if(animation.getClips().containsKey(name))
            {
                return Optional.ofNullable(animation.getClips().get(name));
            }
            return Optional.empty();
        }

        @Override
        public VanillaModelWrapper smoothLighting(boolean value)
        {
            if(model.ambientOcclusion == value)
            {
                return this;
            }
            BlockModel newModel = new BlockModel(model.getParentLocation(), model.getElements(), model.textures, value, model.isGui3d(), model.getAllTransforms(), Lists.newArrayList(model.getOverrides()));
            newModel.parent = model.parent;
            newModel.name = model.name;
            newModel.customData.copyFrom(model.customData);
            return new VanillaModelWrapper(location, newModel, uvlock, animation);
        }

        @Override
        public VanillaModelWrapper gui3d(boolean value)
        {
            if(model.isGui3d() == value)
            {
                return this;
            }
            BlockModel newModel = new BlockModel(model.getParentLocation(), model.getElements(), model.textures, model.ambientOcclusion, value, model.getAllTransforms(), Lists.newArrayList(model.getOverrides()));
            newModel.parent = model.parent;
            newModel.name = model.name;
            newModel.customData.copyFrom(model.customData);
            return new VanillaModelWrapper(location, newModel, uvlock, animation);
        }
    }

    private static final class WeightedRandomModel implements IUnbakedModel
    {
        private final List<Variant> variants;
        private final List<ResourceLocation> locations;
        private final Set<ResourceLocation> textures;
        private final List<IUnbakedModel> models;
        private final IModelState defaultState;

        public WeightedRandomModel(ResourceLocation parent, VariantList variants) throws Exception
        {
            this.variants = variants.getVariantList();
            this.locations = new ArrayList<>();
            this.textures = Sets.newHashSet();
            this.models = new ArrayList<>();
            ImmutableList.Builder<Pair<IUnbakedModel, IModelState>> builder = ImmutableList.builder();
            for (Variant v : this.variants)
            {
                ResourceLocation loc = v.getModelLocation();
                locations.add(loc);

                /*
                 * Vanilla eats this, which makes it only show variants that have models.
                 * But that doesn't help debugging, so throw the exception
                 */
                IUnbakedModel model;
                if(loc.equals(MODEL_MISSING))
                {
                    // explicit missing location, happens if blockstate has "model"=null
                    model = ModelLoaderRegistry.getMissingModel();
                }
                else
                {
                    model = ModelLoaderRegistry.getModel(loc);
                }

                for(ResourceLocation location : model.getDependencies())
                {
                    ModelLoaderRegistry.getModelOrMissing(location);
                }
                //FMLLog.getLogger().error("Exception resolving indirect dependencies for model" + loc, e);
                textures.addAll(model.getTextures(ModelLoader.defaultModelGetter(), new HashSet<>())); // Kick this, just in case.

                models.add(model);

                IModelState modelDefaultState = model.getDefaultState();
                Preconditions.checkNotNull(modelDefaultState, "Model %s returned null as default state", loc);
                builder.add(Pair.of(model, new ModelStateComposition(v.getState(), modelDefaultState, v.isUvLock())));
            }

            if (models.size() == 0) //If all variants are missing, add one with the missing model and default rotation.
            {
                // FIXME: log this?
                IUnbakedModel missing = ModelLoaderRegistry.getMissingModel();
                models.add(missing);
                builder.add(Pair.of(missing, TRSRTransformation.identity()));
            }

            defaultState = new MultiModelState(builder.build());
        }

        private WeightedRandomModel(List<Variant> variants, List<ResourceLocation> locations, Set<ResourceLocation> textures, List<IUnbakedModel> models, IModelState defaultState)
        {
            this.variants = variants;
            this.locations = locations;
            this.textures = textures;
            this.models = models;
            this.defaultState = defaultState;
        }

        @Override
        public Collection<ResourceLocation> getDependencies()
        {
            return ImmutableList.copyOf(locations);
        }

        @Override
        public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
        {
            return ImmutableSet.copyOf(textures);
        }

        @Nullable
        @Override
        public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format)
        {
            if(!Attributes.moreSpecific(format, Attributes.DEFAULT_BAKED_FORMAT))
            {
                throw new IllegalArgumentException("can't bake vanilla weighted models to the format that doesn't fit into the default one: " + format);
            }
            if(variants.size() == 1)
            {
                IUnbakedModel model = models.get(0);
                return model.bake(bakery, spriteGetter, new BasicState(MultiModelState.getPartState(sprite.getState(), model, 0), sprite.isUvLock()), format);
            }
            WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
            for(int i = 0; i < variants.size(); i++)
            {
                IUnbakedModel model = models.get(i);
                builder.add(model.bake(bakery, spriteGetter, new BasicState(MultiModelState.getPartState(sprite.getState(), model, i), sprite.isUvLock()), format), variants.get(i).getWeight());
            }
            return builder.build();
        }

        @Override
        public IModelState getDefaultState()
        {
            return defaultState;
        }

        @Override
        public WeightedRandomModel retexture(ImmutableMap<String, String> textures)
        {
            if (textures.isEmpty())
                return this;

            // rebuild the texture list taking into account new textures
            Set<ResourceLocation> modelTextures = Sets.newHashSet();
            // also recreate the MultiModelState so IModelState data is properly applied to the retextured model
            ImmutableList.Builder<Pair<IUnbakedModel, IModelState>> builder = ImmutableList.builder();
            List<IUnbakedModel> retexturedModels = Lists.newArrayList();
            for(int i = 0; i < this.variants.size(); i++)
            {
                IUnbakedModel retextured = this.models.get(i).retexture(textures);
                modelTextures.addAll(retextured.getTextures(ModelLoader.defaultModelGetter(), new HashSet<>()));
                retexturedModels.add(retextured);
                builder.add(Pair.of(retextured, this.variants.get(i).getState()));
            }

            return new WeightedRandomModel(this.variants, this.locations, modelTextures, retexturedModels, new MultiModelState(builder.build()));
        }
    }

    protected IUnbakedModel getMissingModel()
    {
        if (missingModel == null)
        {
            try
            {
                missingModel = VanillaLoader.INSTANCE.loadModel(new ResourceLocation(MODEL_MISSING.getNamespace(), MODEL_MISSING.getPath()));
            }
            catch(Exception e)
            {
                throw new RuntimeException("Missing the missing model, this should never happen");
            }
        }
        return missingModel;
    }

    protected final class BakedModelCacheKey
    {
        private final VanillaModelWrapper model;
        private final ModelBakery bakery;
        private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
        private final IModelState state;
        private final boolean uvlock;
        private final VertexFormat format;

        public BakedModelCacheKey(VanillaModelWrapper model, ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, IModelState state, boolean uvlock, VertexFormat format)
        {
            this.model = model;
            this.bakery = bakery;
            this.bakedTextureGetter = bakedTextureGetter;
            this.state = state;
            this.uvlock = uvlock;
            this.format = format;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            BakedModelCacheKey that = (BakedModelCacheKey) o;
            return Objects.equal(model, that.model) && Objects.equal(bakery, that.bakery) && Objects.equal(bakedTextureGetter, that.bakedTextureGetter) && Objects.equal(state, that.state) && uvlock == that.uvlock && Objects.equal(format, that.format);
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(model, state, format, bakedTextureGetter);
        }
    }

    /**
     * Adapter from the vanilla json model loader to a custom model loader
     */
    protected static enum VanillaLoader implements ICustomModelLoader
    {
        INSTANCE;

        @Nullable
        private ModelLoader loader;
        private LoadingCache<BakedModelCacheKey, IBakedModel> modelCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterWrite(100, TimeUnit.MILLISECONDS).build(new CacheLoader<BakedModelCacheKey, IBakedModel>() {
            @Override
            public IBakedModel load(BakedModelCacheKey key) throws Exception
            {
                return key.model.bakeImpl(key.bakery, key.bakedTextureGetter, key.state, key.uvlock, key.format);
            }
        });

        void setLoader(ModelLoader loader)
        {
            this.loader = loader;
        }

        @Nullable
        ModelLoader getLoader()
        {
            return loader;
        }

        // NOOP, handled by ModelLoader itself
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {}

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return true;
        }

        @Override
        public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception
        {
            if(modelLocation.equals(MODEL_MISSING) && loader.missingModel != null)
            {
                return loader.getMissingModel();
            }

            String trimmedPath = modelLocation.getPath();
            if(modelLocation.getPath().startsWith("models/"))
            {
                trimmedPath = trimmedPath.substring("models/".length());
            }
            ResourceLocation armatureLocation = new ResourceLocation(modelLocation.getNamespace(), "armatures/" + trimmedPath + ".json");
            ModelBlockAnimation animation = ModelBlockAnimation.loadVanillaAnimation(loader.resourceManager, armatureLocation);

            ResourceLocation trimmedLocation = new ResourceLocation(modelLocation.getNamespace(), trimmedPath);
            BlockModel model = loader.loadModel(trimmedLocation);
            IUnbakedModel iModel = loader.new VanillaModelWrapper(modelLocation, model, false, animation);
            if(loader.missingModel == null && modelLocation.equals(MODEL_MISSING))
            {
                loader.missingModel = iModel;
            }
            return iModel;
        }

        @Override
        public String toString()
        {
            return "VanillaLoader.INSTANCE";
        }
    }

    // Temporary to compile things
    public static final class White {
        public static final ResourceLocation LOCATION = new ResourceLocation("white");
        public static final TextureAtlasSprite INSTANCE = MissingTextureSprite.func_217790_a();
    }

    /**
     * 16x16 pure white sprite.
     */
    /*/ TODO Custom TAS
    public static final class White extends TextureAtlasSprite
    {
        public static final ResourceLocation LOCATION = new ResourceLocation("white");
        public static final White INSTANCE = new White();

        private White()
        {
            super(LOCATION.toString());
            this.width = this.height = 16;
        }

        @Override
        public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location)
        {
            return true;
        }

        @Override
        public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter)
        {
            BufferedImage image = new BufferedImage(this.getIconWidth(), this.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setBackground(Color.WHITE);
            graphics.clearRect(0, 0, this.getIconWidth(), this.getIconHeight());
            int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
            pixels[0] = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels[0], 0, image.getWidth());
            this.clearFramesTextureData();
            this.framesTextureData.add(pixels);
            return false;
        }

        public void register(TextureMap map)
        {
            map.setTextureEntry(White.INSTANCE);
        }
    }
*/
    @SuppressWarnings("serial")
    public static class ItemLoadingException extends ModelLoaderRegistry.LoaderException
    {
        private final Exception normalException;
        private final Exception blockstateException;

        public ItemLoadingException(String message, Exception normalException, Exception blockstateException)
        {
            super(message);
            this.normalException = normalException;
            this.blockstateException = blockstateException;
        }
    }

    /**
     * Internal, do not use.
     */
    public void onPostBakeEvent(Map<ResourceLocation, IBakedModel> modelRegistry)
    {
        IBakedModel missingModel = modelRegistry.get(MODEL_MISSING);
        for(Map.Entry<ResourceLocation, Exception> entry : loadingExceptions.entrySet())
        {
            // ignoring pure ResourceLocation arguments, all things we care about pass ModelResourceLocation
            if(entry.getKey() instanceof ModelResourceLocation)
            {
                LOGGER.debug(MODELLOADING, ()-> new ModelLoaderErrorMessage((ModelResourceLocation)entry.getKey(), entry.getValue()));
                final ModelResourceLocation location = (ModelResourceLocation)entry.getKey();
                final IBakedModel model = modelRegistry.get(location);
                if(model == null)
                {
                    modelRegistry.put(location, missingModel);
                }
            }
        }
        loadingExceptions.clear();
        isLoading = false;
    }

    /**
     * Helper method for registering all itemstacks for given item to map to universal bucket model.
     *//*
    public static void setBucketModelDefinition(Item item) {
        ModelLoader.setCustomMeshDefinition(item, stack -> ModelDynBucket.LOCATION);
        ModelBakery.registerItemVariants(item, ModelDynBucket.LOCATION);
    }
*/

    private static enum DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite>
    {
        INSTANCE;

        @Override
        public TextureAtlasSprite apply(ResourceLocation location)
        {
            return Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());
        }
    }

    private static final Function<ResourceLocation, IUnbakedModel> DEFAULT_MODEL_GETTER = ModelLoaderRegistry::getModelOrMissing;

    /**
     * Get the default texture getter the models will be baked with.
     */
    public static Function<ResourceLocation, TextureAtlasSprite> defaultTextureGetter()
    {
        return DefaultTextureGetter.INSTANCE;
    }

    public static Function<ResourceLocation, IUnbakedModel> defaultModelGetter()
    {
        return DEFAULT_MODEL_GETTER;
    }

    /**
     * Exposes the blockstate json loader as a custom model loader
     */
    protected static enum VariantLoader implements ICustomModelLoader
    {
        INSTANCE;

        private ModelLoader loader;

        void setLoader(ModelLoader loader)
        {
            this.loader = loader;
        }

        // NOOP, handled in loader
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {}

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation instanceof ModelResourceLocation;
        }

        @Override
        public IUnbakedModel loadModel(ResourceLocation modelLocation)
        {
            return loader.getUnbakedModel(modelLocation);
        }

        @Override
        public String toString()
        {
            return "VariantLoader.INSTANCE";
        }
    }
}
