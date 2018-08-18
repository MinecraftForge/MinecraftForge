/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.BuiltInModel;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.MissingVariantException;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.MultipartBakedModel;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantList;
import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import net.minecraft.client.renderer.block.model.multipart.Multipart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.model.animation.AnimationItemOverrideList;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.model.animation.IClip;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;

import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ModelLoader extends ModelBakery
{
    private final Map<ModelResourceLocation, IModel> stateModels = Maps.newHashMap();
    private final Map<ModelResourceLocation, ModelBlockDefinition> multipartDefinitions = Maps.newHashMap();
    private final Map<ModelBlockDefinition, IModel> multipartModels = Maps.newHashMap();
    // TODO: nothing adds to missingVariants, remove it?
    private final Set<ModelResourceLocation> missingVariants = Sets.newHashSet();
    private final Map<ResourceLocation, Exception> loadingExceptions = Maps.newHashMap();
    private IModel missingModel = null;

    private boolean isLoading = false;
    public boolean isLoading()
    {
        return isLoading;
    }

    private final boolean enableVerboseMissingInfo = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment") || Boolean.parseBoolean(System.getProperty("forge.verboseMissingModelLogging", "false"));
    private final int verboseMissingInfoCount = Integer.parseInt(System.getProperty("forge.verboseMissingModelLoggingCount", "5"));

    public ModelLoader(IResourceManager manager, TextureMap map, BlockModelShapes shapes)
    {
        super(manager, map, shapes);
        VanillaLoader.INSTANCE.setLoader(this);
        VariantLoader.INSTANCE.setLoader(this);
        ModelLoaderRegistry.clearModelCache(manager);
    }

    @Nonnull
    @Override
    public IRegistry<ModelResourceLocation, IBakedModel> setupModelRegistry()
    {
        if (FMLClientHandler.instance().hasError()) // skip loading models if we're just going to show a fatal error screen
            return bakedRegistry;

        isLoading = true;
        loadBlocks();
        loadVariantItemModels();
        missingModel = ModelLoaderRegistry.getMissingModel();
        stateModels.put(MODEL_MISSING, missingModel);

        final Set<ResourceLocation> textures = Sets.newHashSet(ModelLoaderRegistry.getTextures());
        textures.remove(TextureMap.LOCATION_MISSING_TEXTURE);
        textures.addAll(LOCATIONS_BUILTIN_TEXTURES);

        textureMap.loadSprites(resourceManager, map -> textures.forEach(map::registerSprite));

        IBakedModel missingBaked = missingModel.bake(missingModel.getDefaultState(), DefaultVertexFormats.ITEM, DefaultTextureGetter.INSTANCE);
        Map<IModel, IBakedModel> bakedModels = Maps.newHashMap();
        HashMultimap<IModel, ModelResourceLocation> models = HashMultimap.create();
        Multimaps.invertFrom(Multimaps.forMap(stateModels), models);

        ProgressBar bakeBar = ProgressManager.push("ModelLoader: baking", models.keySet().size());

        for(IModel model : models.keySet())
        {
            String modelLocations = "[" + Joiner.on(", ").join(models.get(model)) + "]";
            bakeBar.step(modelLocations);
            if(model == getMissingModel())
            {
                bakedModels.put(model, missingBaked);
            }
            else
            {
                try
                {
                    bakedModels.put(model, model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, DefaultTextureGetter.INSTANCE));
                }
                catch (Exception e)
                {
                    FMLLog.log.error("Exception baking model for location(s) {}:", modelLocations, e);
                    bakedModels.put(model, missingBaked);
                }
            }
        }

        ProgressManager.pop(bakeBar);

        for (Entry<ModelResourceLocation, IModel> e : stateModels.entrySet())
        {
            bakedRegistry.putObject(e.getKey(), bakedModels.get(e.getValue()));
        }
        return bakedRegistry;
    }

    // NOOP, replaced by dependency resolution
    @Override
    protected void loadVariantModels() {}

    // NOOP, replaced by dependency resolution
    @Override
    protected void loadMultipartVariantModels() {}

    @Override
    protected void loadBlocks()
    {
        List<Block> blocks = StreamSupport.stream(Block.REGISTRY.spliterator(), false)
                .filter(block -> block.getRegistryName() != null)
                .sorted(Comparator.comparing(b -> b.getRegistryName().toString()))
                .collect(Collectors.toList());
        ProgressBar blockBar = ProgressManager.push("ModelLoader: blocks", blocks.size());

        BlockStateMapper mapper = this.blockModelShapes.getBlockStateMapper();

        for(Block block : blocks)
        {
            blockBar.step(block.getRegistryName().toString());
            for(ResourceLocation location : mapper.getBlockstateLocations(block))
            {
                loadBlock(mapper, block, location);
            }
        }
        ProgressManager.pop(blockBar);
    }

    @Override
    protected void registerVariant(@Nullable ModelBlockDefinition definition, ModelResourceLocation location)
    {
        IModel model;
        try
        {
            model = ModelLoaderRegistry.getModel(location);
        }
        catch(Exception e)
        {
            storeException(location, e);
            model = ModelLoaderRegistry.getMissingModel(location, e);
        }
        stateModels.put(location, model);
    }

    @Override
    protected void registerMultipartVariant(ModelBlockDefinition definition, Collection<ModelResourceLocation> locations)
    {
        for (ModelResourceLocation location : locations)
        {
            multipartDefinitions.put(location, definition);
            registerVariant(null, location);
        }
    }

    private void storeException(ResourceLocation location, Exception exception)
    {
        loadingExceptions.put(location, exception);
    }

    @Override
    protected ModelBlockDefinition getModelBlockDefinition(ResourceLocation location)
    {
        try
        {
            return super.getModelBlockDefinition(location);
        }
        catch (Exception exception)
        {
            storeException(location, new Exception("Could not load model definition for variant " + location, exception));
        }
        return new ModelBlockDefinition(new ArrayList<>());
    }

    @Override
    protected void loadItemModels()
    {
        registerVariantNames();

        List<Item> items = StreamSupport.stream(Item.REGISTRY.spliterator(), false)
                .filter(item -> item.getRegistryName() != null)
                .sorted(Comparator.comparing(i -> i.getRegistryName().toString()))
                .collect(Collectors.toList());

        ProgressBar itemBar = ProgressManager.push("ModelLoader: items", items.size());
        for(Item item : items)
        {
            itemBar.step(item.getRegistryName().toString());
            for(String s : getVariantNames(item))
            {
                ResourceLocation file = getItemLocation(s);
                ModelResourceLocation memory = getInventoryVariant(s);
                IModel model = ModelLoaderRegistry.getMissingModel();
                Exception exception = null;
                try
                {
                    model = ModelLoaderRegistry.getModel(memory);
                }
                catch (Exception blockstateException)
                {
                    try
                    {
                        model = ModelLoaderRegistry.getModel(file);
                        ModelLoaderRegistry.addAlias(memory, file);
                    }
                    catch (Exception normalException)
                    {
                        exception = new ItemLoadingException("Could not load item model either from the normal location " + file + " or from the blockstate", normalException, blockstateException);
                    }
                }
                if (exception != null)
                {
                    storeException(memory, exception);
                    model = ModelLoaderRegistry.getMissingModel(memory, exception);
                }
                stateModels.put(memory, model);
            }
        }
        ProgressManager.pop(itemBar);
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

    @Override
    protected ResourceLocation getModelLocation(ResourceLocation model)
    {
        return new ResourceLocation(model.getResourceDomain(), model.getResourcePath() + ".json");
    }

    private final class VanillaModelWrapper implements IModel
    {
        private final ResourceLocation location;
        private final ModelBlock model;
        private final boolean uvlock;
        private final ModelBlockAnimation animation;

        public VanillaModelWrapper(ResourceLocation location, ModelBlock model, boolean uvlock, ModelBlockAnimation animation)
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
            for(ResourceLocation dep : model.getOverrideLocations())
            {
                if(!location.equals(dep))
                {
                    set.add(dep);
                    // TODO: check if this can go somewhere else, random access to global things is bad
                    stateModels.put(getInventoryVariant(dep.toString()), ModelLoaderRegistry.getModelOrLogError(dep, "Could not load override model " + dep + " for model " + location));
                }
            }
            if(model.getParentLocation() != null && !model.getParentLocation().getResourcePath().startsWith("builtin/"))
            {
                set.add(model.getParentLocation());
            }
            return ImmutableSet.copyOf(set);
        }

        @Override
        public Collection<ResourceLocation> getTextures()
        {
            // setting parent here to make textures resolve properly
            if(model.getParentLocation() != null)
            {
                if(model.getParentLocation().getResourcePath().equals("builtin/generated"))
                {
                    model.parent = MODEL_GENERATED;
                }
                else
                {
                    IModel parent = ModelLoaderRegistry.getModelOrLogError(model.getParentLocation(), "Could not load vanilla model parent '" + model.getParentLocation() + "' for '" + model);
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

            if(hasItemModel(model))
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
            return builder.build();
        }

        @Override
        public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            return VanillaLoader.INSTANCE.modelCache.getUnchecked(new BakedModelCacheKey(this, state, format, bakedTextureGetter));
        }

        public IBakedModel bakeImpl(IModelState state, final VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            if(!Attributes.moreSpecific(format, Attributes.DEFAULT_BAKED_FORMAT))
            {
                throw new IllegalArgumentException("can't bake vanilla models to the format that doesn't fit into the default one: " + format);
            }
            ModelBlock model = this.model;
            if(model == null) return getMissingModel().bake(getMissingModel().getDefaultState(), format, bakedTextureGetter);

            List<TRSRTransformation> newTransforms = Lists.newArrayList();
            for(int i = 0; i < model.getElements().size(); i++)
            {
                BlockPart part = model.getElements().get(i);
                newTransforms.add(animation.getPartTransform(state, part, i));
            }

            ItemCameraTransforms transforms = model.getAllTransforms();
            Map<TransformType, TRSRTransformation> tMap = Maps.newEnumMap(TransformType.class);
            tMap.putAll(PerspectiveMapWrapper.getTransforms(transforms));
            tMap.putAll(PerspectiveMapWrapper.getTransforms(state));
            IModelState perState = new SimpleModelState(ImmutableMap.copyOf(tMap));

            if(hasItemModel(model))
            {
                return new ItemLayerModel(model).bake(perState, format, bakedTextureGetter);
            }
            if(isCustomRenderer(model)) return new BuiltInModel(transforms, model.createOverrides());
            return bakeNormal(model, perState, state, newTransforms, format, bakedTextureGetter, uvlock);
        }

        private IBakedModel bakeNormal(ModelBlock model, IModelState perState, final IModelState modelState, List<TRSRTransformation> newTransforms, final VertexFormat format, final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked)
        {
            final TRSRTransformation baseState = modelState.apply(Optional.empty()).orElse(TRSRTransformation.identity());
            TextureAtlasSprite particle = bakedTextureGetter.apply(new ResourceLocation(model.resolveTextureName("particle")));
            SimpleBakedModel.Builder builder = (new SimpleBakedModel.Builder(model, model.createOverrides())).setTexture(particle);
            for(int i = 0; i < model.getElements().size(); i++)
            {
                if(modelState.apply(Optional.of(Models.getHiddenModelPart(ImmutableList.of(Integer.toString(i))))).isPresent())
                {
                    continue;
                }
                BlockPart part = model.getElements().get(i);
                TRSRTransformation transformation = baseState;
                if(newTransforms.get(i) != null)
                {
                    transformation = transformation.compose(newTransforms.get(i));
                    BlockPartRotation rot = part.partRotation;
                    if(rot == null) rot = new BlockPartRotation(new org.lwjgl.util.vector.Vector3f(), EnumFacing.Axis.Y, 0, false);
                    part = new BlockPart(part.positionFrom, part.positionTo, part.mapFaces, rot, part.shade);
                }
                for(Map.Entry<EnumFacing, BlockPartFace> e : part.mapFaces.entrySet())
                {
                    TextureAtlasSprite textureatlassprite1 = bakedTextureGetter.apply(new ResourceLocation(model.resolveTextureName(e.getValue().texture)));

                    if (e.getValue().cullFace == null || !TRSRTransformation.isInteger(transformation.getMatrix()))
                    {
                        builder.addGeneralQuad(makeBakedQuad(part, e.getValue(), textureatlassprite1, e.getKey(), transformation, uvLocked));
                    }
                    else
                    {
                        builder.addFaceQuad(baseState.rotate(e.getValue().cullFace), makeBakedQuad(part, e.getValue(), textureatlassprite1, e.getKey(), transformation, uvLocked));
                    }
                }
            }

            return new PerspectiveMapWrapper(builder.makeBakedModel(), perState)
            {
                private final ItemOverrideList overrides = new AnimationItemOverrideList(VanillaModelWrapper.this, modelState, format, bakedTextureGetter, super.getOverrides());

                @Override
                public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
                {
                    if(state instanceof IExtendedBlockState)
                    {
                        IExtendedBlockState exState = (IExtendedBlockState)state;
                        if(exState.getUnlistedNames().contains(Properties.AnimationProperty))
                        {
                            IModelState newState = exState.getValue(Properties.AnimationProperty);
                            IExtendedBlockState newExState = exState.withProperty(Properties.AnimationProperty, null);
                            if(newState != null)
                            {
                                return VanillaModelWrapper.this.bake(new ModelStateComposition(modelState, newState), format, bakedTextureGetter).getQuads(newExState, side, rand);
                            }
                        }
                    }
                    return super.getQuads(state, side, rand);
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
            for (BlockPart part : this.model.getElements())
            {
                elements.add(new BlockPart(part.positionFrom, part.positionTo, Maps.newHashMap(part.mapFaces), part.partRotation, part.shade));
            }

            ModelBlock newModel = new ModelBlock(this.model.getParentLocation(), elements,
                Maps.newHashMap(this.model.textures), this.model.isAmbientOcclusion(), this.model.isGui3d(), //New Textures man VERY IMPORTANT
                model.getAllTransforms(), Lists.newArrayList(model.getOverrides()));
            newModel.name = this.model.name;
            newModel.parent = this.model.parent;

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
            for (BlockPart part : newModel.getElements())
            {
                part.mapFaces.entrySet().removeIf(entry -> removed.contains(entry.getValue().texture));
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
            ModelBlock newModel = new ModelBlock(model.getParentLocation(), model.getElements(), model.textures, value, model.isGui3d(), model.getAllTransforms(), Lists.newArrayList(model.getOverrides()));
            newModel.parent = model.parent;
            newModel.name = model.name;
            return new VanillaModelWrapper(location, newModel, uvlock, animation);
        }

        @Override
        public VanillaModelWrapper gui3d(boolean value)
        {
            if(model.isGui3d() == value)
            {
                return this;
            }
            ModelBlock newModel = new ModelBlock(model.getParentLocation(), model.getElements(), model.textures, model.ambientOcclusion, value, model.getAllTransforms(), Lists.newArrayList(model.getOverrides()));
            newModel.parent = model.parent;
            newModel.name = model.name;
            return new VanillaModelWrapper(location, newModel, uvlock, animation);
        }

        @Override
        public IModel uvlock(boolean value)
        {
            if(uvlock == value)
            {
                return this;
            }
            return new VanillaModelWrapper(location, model, value, animation);
        }
    }

    private static final class WeightedRandomModel implements IModel
    {
        private final List<Variant> variants;
        private final List<ResourceLocation> locations;
        private final Set<ResourceLocation> textures;
        private final List<IModel> models;
        private final IModelState defaultState;

        public WeightedRandomModel(ResourceLocation parent, VariantList variants) throws Exception
        {
            this.variants = variants.getVariantList();
            this.locations = new ArrayList<>();
            this.textures = Sets.newHashSet();
            this.models = new ArrayList<>();
            ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
            for (Variant v : this.variants)
            {
                ResourceLocation loc = v.getModelLocation();
                locations.add(loc);

                /*
                 * Vanilla eats this, which makes it only show variants that have models.
                 * But that doesn't help debugging, so throw the exception
                 */
                IModel model;
                if(loc.equals(MODEL_MISSING))
                {
                    // explicit missing location, happens if blockstate has "model"=null
                    model = ModelLoaderRegistry.getMissingModel();
                }
                else
                {
                    model = ModelLoaderRegistry.getModel(loc);
                }

                // FIXME: is this the place? messes up dependency and texture resolution
                model = v.process(model);
                for(ResourceLocation location : model.getDependencies())
                {
                    ModelLoaderRegistry.getModelOrMissing(location);
                }
                //FMLLog.getLogger().error("Exception resolving indirect dependencies for model" + loc, e);
                textures.addAll(model.getTextures()); // Kick this, just in case.

                models.add(model);

                IModelState modelDefaultState = model.getDefaultState();
                Preconditions.checkNotNull(modelDefaultState, "Model %s returned null as default state", loc);
                builder.add(Pair.of(model, new ModelStateComposition(v.getState(), modelDefaultState)));
            }

            if (models.size() == 0) //If all variants are missing, add one with the missing model and default rotation.
            {
                // FIXME: log this?
                IModel missing = ModelLoaderRegistry.getMissingModel();
                models.add(missing);
                builder.add(Pair.of(missing, TRSRTransformation.identity()));
            }

            defaultState = new MultiModelState(builder.build());
        }

        private WeightedRandomModel(List<Variant> variants, List<ResourceLocation> locations, Set<ResourceLocation> textures, List<IModel> models, IModelState defaultState)
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
        public Collection<ResourceLocation> getTextures()
        {
            return ImmutableSet.copyOf(textures);
        }

        @Override
        public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            if(!Attributes.moreSpecific(format, Attributes.DEFAULT_BAKED_FORMAT))
            {
                throw new IllegalArgumentException("can't bake vanilla weighted models to the format that doesn't fit into the default one: " + format);
            }
            if(variants.size() == 1)
            {
                IModel model = models.get(0);
                return model.bake(MultiModelState.getPartState(state, model, 0), format, bakedTextureGetter);
            }
            WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
            for(int i = 0; i < variants.size(); i++)
            {
                IModel model = models.get(i);
                builder.add(model.bake(MultiModelState.getPartState(state, model, i), format, bakedTextureGetter), variants.get(i).getWeight());
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
            ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
            List<IModel> retexturedModels = Lists.newArrayList();
            for(int i = 0; i < this.variants.size(); i++)
            {
                IModel retextured = this.models.get(i).retexture(textures);
                modelTextures.addAll(retextured.getTextures());
                retexturedModels.add(retextured);
                builder.add(Pair.of(retextured, this.variants.get(i).getState()));
            }

            return new WeightedRandomModel(this.variants, this.locations, modelTextures, retexturedModels, new MultiModelState(builder.build()));
        }
    }

    protected IModel getMissingModel()
    {
        if (missingModel == null)
        {
            try
            {
                missingModel = VanillaLoader.INSTANCE.loadModel(new ResourceLocation(MODEL_MISSING.getResourceDomain(), MODEL_MISSING.getResourcePath()));
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
        private final IModelState state;
        private final VertexFormat format;
        private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

        public BakedModelCacheKey(VanillaModelWrapper model, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            this.model = model;
            this.state = state;
            this.format = format;
            this.bakedTextureGetter = bakedTextureGetter;
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
            return Objects.equal(model, that.model) && Objects.equal(state, that.state) && Objects.equal(format, that.format) && Objects.equal(bakedTextureGetter, that.bakedTextureGetter);
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(model, state, format, bakedTextureGetter);
        }
    }

    protected static enum VanillaLoader implements ICustomModelLoader
    {
        INSTANCE;

        @Nullable
        private ModelLoader loader;
        private LoadingCache<BakedModelCacheKey, IBakedModel> modelCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterWrite(100, TimeUnit.MILLISECONDS).build(new CacheLoader<BakedModelCacheKey, IBakedModel>() {
            @Override
            public IBakedModel load(BakedModelCacheKey key) throws Exception
            {
                return key.model.bakeImpl(key.state, key.format, key.bakedTextureGetter);
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

        // NOOP, handled in loader
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {}

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return true;
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception
        {
            if(modelLocation.equals(MODEL_MISSING) && loader.missingModel != null)
            {
                return loader.getMissingModel();
            }
            String modelPath = modelLocation.getResourcePath();
            if(modelLocation.getResourcePath().startsWith("models/"))
            {
                modelPath = modelPath.substring("models/".length());
            }
            ResourceLocation armatureLocation = new ResourceLocation(modelLocation.getResourceDomain(), "armatures/" + modelPath + ".json");
            ModelBlockAnimation animation = ModelBlockAnimation.loadVanillaAnimation(loader.resourceManager, armatureLocation);
            ModelBlock model = loader.loadModel(modelLocation);
            IModel iModel = loader.new VanillaModelWrapper(modelLocation, model, false, animation);
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

    /**
     * 16x16 pure white sprite.
     */
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

    private static class ItemLoadingException extends ModelLoaderRegistry.LoaderException
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
    public void onPostBakeEvent(IRegistry<ModelResourceLocation, IBakedModel> modelRegistry)
    {
        if (!isLoading) return;

        IBakedModel missingModel = modelRegistry.getObject(MODEL_MISSING);
        Map<String, Integer> modelErrors = Maps.newHashMap();
        Set<ResourceLocation> printedBlockStateErrors = Sets.newHashSet();
        Multimap<ModelResourceLocation, IBlockState> reverseBlockMap = null;
        Multimap<ModelResourceLocation, String> reverseItemMap = HashMultimap.create();
        if(enableVerboseMissingInfo)
        {
            reverseBlockMap = HashMultimap.create();
            for(Map.Entry<IBlockState, ModelResourceLocation> entry : blockModelShapes.getBlockStateMapper().putAllStateModelLocations().entrySet())
            {
                reverseBlockMap.put(entry.getValue(), entry.getKey());
            }
            ForgeRegistries.ITEMS.forEach(item ->
            {
                for(String s : getVariantNames(item))
                {
                    ModelResourceLocation memory = getInventoryVariant(s);
                    reverseItemMap.put(memory, item.getRegistryName().toString());
                }
            });
        }

        for(Map.Entry<ResourceLocation, Exception> entry : loadingExceptions.entrySet())
        {
            // ignoring pure ResourceLocation arguments, all things we care about pass ModelResourceLocation
            if(entry.getKey() instanceof ModelResourceLocation)
            {
                ModelResourceLocation location = (ModelResourceLocation)entry.getKey();
                IBakedModel model = modelRegistry.getObject(location);
                if(model == null || model == missingModel || model instanceof FancyMissingModel.BakedModel)
                {
                    String domain = entry.getKey().getResourceDomain();
                    Integer errorCountBox = modelErrors.get(domain);
                    int errorCount = errorCountBox == null ? 0 : errorCountBox;
                    errorCount++;
                    if(errorCount < verboseMissingInfoCount)
                    {
                        String errorMsg = "Exception loading model for variant " + entry.getKey();
                        if(enableVerboseMissingInfo)
                        {
                            Collection<IBlockState> blocks = reverseBlockMap.get(location);
                            if(!blocks.isEmpty())
                            {
                                if(blocks.size() == 1)
                                {
                                    errorMsg += " for blockstate \"" + blocks.iterator().next() + "\"";
                                }
                                else
                                {
                                    errorMsg += " for blockstates [\"" + Joiner.on("\", \"").join(blocks) + "\"]";
                                }
                            }
                            Collection<String> items = reverseItemMap.get(location);
                            if(!items.isEmpty())
                            {
                                if(!blocks.isEmpty()) errorMsg += " and";
                                if(items.size() == 1)
                                {
                                    errorMsg += " for item \"" + items.iterator().next() + "\"";
                                }
                                else
                                {
                                    errorMsg += " for items [\"" + Joiner.on("\", \"").join(items) + "\"]";
                                }
                            }
                        }
                        if(entry.getValue() instanceof ItemLoadingException)
                        {
                            ItemLoadingException ex = (ItemLoadingException)entry.getValue();
                            FMLLog.log.error("{}, normal location exception: ", errorMsg, ex.normalException);
                            FMLLog.log.error("{}, blockstate location exception: ", errorMsg, ex.blockstateException);
                        }
                        else
                        {
                            FMLLog.log.error(errorMsg, entry.getValue());
                        }
                        ResourceLocation blockstateLocation = new ResourceLocation(location.getResourceDomain(), location.getResourcePath());
                        if(loadingExceptions.containsKey(blockstateLocation) && !printedBlockStateErrors.contains(blockstateLocation))
                        {
                            FMLLog.log.error("Exception loading blockstate for the variant {}: ", location, loadingExceptions.get(blockstateLocation));
                            printedBlockStateErrors.add(blockstateLocation);
                        }
                    }
                    modelErrors.put(domain, errorCount);
                }
                if(model == null)
                {
                    modelRegistry.putObject(location, missingModel);
                }
            }
        }
        for(ModelResourceLocation missing : missingVariants)
        {
            IBakedModel model = modelRegistry.getObject(missing);
            if(model == null || model == missingModel)
            {
                String domain = missing.getResourceDomain();
                Integer errorCountBox = modelErrors.get(domain);
                int errorCount = errorCountBox == null ? 0 : errorCountBox;
                errorCount++;
                if(errorCount < verboseMissingInfoCount)
                {
                    FMLLog.log.fatal("Model definition for location {} not found", missing);
                }
                modelErrors.put(domain, errorCount);
            }
            if(model == null)
            {
                modelRegistry.putObject(missing, missingModel);
            }
        }
        for(Map.Entry<String, Integer> e : modelErrors.entrySet())
        {
            if(e.getValue() >= verboseMissingInfoCount)
            {
                FMLLog.log.fatal("Suppressed additional {} model loading errors for domain {}", e.getValue() - verboseMissingInfoCount, e.getKey());
            }
        }
        loadingExceptions.clear();
        missingVariants.clear();
        isLoading = false;
    }

    private static final Map<IRegistryDelegate<Block>, IStateMapper> customStateMappers = Maps.newHashMap();

    /**
     * Adds a custom IBlockState -> model variant logic.
     */
    public static void setCustomStateMapper(Block block, IStateMapper mapper)
    {
        customStateMappers.put(block.delegate, mapper);
    }

    /**
     * Internal, do not use.
     */
    public static void onRegisterAllBlocks(BlockModelShapes shapes)
    {
        for (Entry<IRegistryDelegate<Block>, IStateMapper> e : customStateMappers.entrySet())
        {
            shapes.registerBlockWithStateMapper(e.getKey().get(), e.getValue());
        }
    }

    private static final Map<IRegistryDelegate<Item>, ItemMeshDefinition> customMeshDefinitions = com.google.common.collect.Maps.newHashMap();
    private static final Map<Pair<IRegistryDelegate<Item>, Integer>, ModelResourceLocation> customModels = com.google.common.collect.Maps.newHashMap();

    /**
     * Adds a simple mapping from Item + metadata to the model variant.
     * Registers the variant with the ModelBakery too.
     */
    public static void setCustomModelResourceLocation(Item item, int metadata, ModelResourceLocation model)
    {
        customModels.put(Pair.of(item.delegate, metadata), model);
        ModelBakery.registerItemVariants(item, model);
    }

    /**
     * Adds generic ItemStack -> model variant logic.
     * You still need to manually call ModelBakery.registerItemVariants with all values that meshDefinition can return.
     */
    public static void setCustomMeshDefinition(Item item, ItemMeshDefinition meshDefinition)
    {
        customMeshDefinitions.put(item.delegate, meshDefinition);
    }

    /**
     * Helper method for registering all itemstacks for given item to map to universal bucket model.
     */
    public static void setBucketModelDefinition(Item item) {
        ModelLoader.setCustomMeshDefinition(item, stack -> ModelDynBucket.LOCATION);
        ModelBakery.registerItemVariants(item, ModelDynBucket.LOCATION);
    }

    /**
     * Internal, do not use.
     */
    public static void onRegisterItems(ItemModelMesher mesher)
    {
        for (Map.Entry<IRegistryDelegate<Item>, ItemMeshDefinition> e : customMeshDefinitions.entrySet())
        {
            mesher.register(e.getKey().get(), e.getValue());
        }
        for (Entry<Pair<IRegistryDelegate<Item>, Integer>, ModelResourceLocation> e : customModels.entrySet())
        {
            mesher.register(e.getKey().getLeft().get(), e.getKey().getRight(), e.getValue());
        }
    }

    private static enum DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite>
    {
        INSTANCE;

        @Override
        public TextureAtlasSprite apply(ResourceLocation location)
        {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
        }
    }

    /**
     * Get the default texture getter the models will be baked with.
     */
    public static Function<ResourceLocation, TextureAtlasSprite> defaultTextureGetter()
    {
        return DefaultTextureGetter.INSTANCE;
    }

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
        public IModel loadModel(ResourceLocation modelLocation) throws Exception
        {
            ModelResourceLocation variant = (ModelResourceLocation) modelLocation;
            ModelBlockDefinition definition = loader.getModelBlockDefinition(variant);

            try
            {
                VariantList variants = definition.getVariant(variant.getVariant());
                return new WeightedRandomModel(variant, variants);
            }
            catch (MissingVariantException e)
            {
                if (definition.equals(loader.multipartDefinitions.get(variant)))
                {
                    IModel model = loader.multipartModels.get(definition);
                    if (model == null)
                    {
                        model = new MultipartModel(new ResourceLocation(variant.getResourceDomain(), variant.getResourcePath()), definition.getMultipartData());
                        loader.multipartModels.put(definition, model);
                    }
                    return model;
                }
                throw e;
            }
        }

        @Override
        public String toString()
        {
            return "VariantLoader.INSTANCE";
        }
    }

    private static class MultipartModel implements IModel
    {
        private final ResourceLocation location;
        private final Multipart multipart;
        private final ImmutableMap<Selector, IModel> partModels;

        public MultipartModel(ResourceLocation location, Multipart multipart) throws Exception
        {
            this.location = location;
            this.multipart = multipart;
            ImmutableMap.Builder<Selector, IModel> builder = ImmutableMap.builder();
            for (Selector selector : multipart.getSelectors())
            {
                builder.put(selector, new WeightedRandomModel(location, selector.getVariantList()));
            }
            partModels = builder.build();
        }

        private MultipartModel(ResourceLocation location, Multipart multipart, ImmutableMap<Selector, IModel> partModels)
        {
            this.location = location;
            this.multipart = multipart;
            this.partModels = partModels;
        }

        // FIXME: represent selectors as dependencies?
        // FIXME
        @Override
        public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            MultipartBakedModel.Builder builder = new MultipartBakedModel.Builder();

            for (Selector selector : multipart.getSelectors())
            {
                builder.putModel(selector.getPredicate(multipart.getStateContainer()), partModels.get(selector).bake(partModels.get(selector).getDefaultState(), format, bakedTextureGetter));
            }

            IBakedModel bakedModel = builder.makeMultipartModel();
            return bakedModel;
        }

        @Override
        public IModel retexture(ImmutableMap<String, String> textures)
        {
            if (textures.isEmpty())
                return this;

            ImmutableMap.Builder<Selector, IModel> builder = ImmutableMap.builder();
            for (Entry<Selector, IModel> partModel : this.partModels.entrySet())
            {
                builder.put(partModel.getKey(), partModel.getValue().retexture(textures));
            }

            return new MultipartModel(location, multipart, builder.build());
        }
    }
}
