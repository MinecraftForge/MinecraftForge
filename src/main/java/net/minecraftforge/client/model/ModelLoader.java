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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.MultipartBakedModel;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantList;
import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import net.minecraft.client.renderer.block.model.multipart.Multipart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.texture.ITextureMapPopulator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.model.animation.AnimationItemOverrideList;
import net.minecraftforge.client.model.animation.IAnimatedModel;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.model.animation.IClip;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

public final class ModelLoader extends ModelBakery
{
    private static boolean firstLoad = Boolean.parseBoolean(System.getProperty("fml.skipFirstModelBake", "true"));
    private final Map<ModelResourceLocation, IModel> stateModels = Maps.newHashMap();
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

    @Override
    public IRegistry<ModelResourceLocation, IBakedModel> setupModelRegistry()
    {
        isLoading = true;
        loadBlocks();
        loadVariantItemModels();
        missingModel = ModelLoaderRegistry.getMissingModel();
        stateModels.put(MODEL_MISSING, missingModel);

        final Set<ResourceLocation> textures = Sets.newHashSet(ModelLoaderRegistry.getTextures());
        textures.remove(TextureMap.LOCATION_MISSING_TEXTURE);
        textures.addAll(LOCATIONS_BUILTIN_TEXTURES);

        textureMap.loadSprites(resourceManager, new ITextureMapPopulator()
        {
            public void registerSprites(TextureMap map)
            {
                for(ResourceLocation t : textures)
                {
                    map.registerSprite(t);
                }
            }
        });

        IBakedModel missingBaked = missingModel.bake(missingModel.getDefaultState(), DefaultVertexFormats.ITEM, DefaultTextureGetter.INSTANCE);
        Map<IModel, IBakedModel> bakedModels = Maps.newHashMap();
        HashMultimap<IModel, ModelResourceLocation> models = HashMultimap.create();
        Multimaps.invertFrom(Multimaps.forMap(stateModels), models);

        if (firstLoad)
        {
            firstLoad = false;
            for (ModelResourceLocation mrl : stateModels.keySet())
            {
                bakedRegistry.putObject(mrl, missingBaked);
            }
            return bakedRegistry;
        }

        ProgressBar bakeBar = ProgressManager.push("ModelLoader: baking", models.keySet().size());

        for(IModel model : models.keySet())
        {
            bakeBar.step("[" + Joiner.on(", ").join(models.get(model)) + "]");
            if(model == getMissingModel())
            {
                bakedModels.put(model, missingBaked);
            }
            else
            {
                bakedModels.put(model, model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, DefaultTextureGetter.INSTANCE));
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
        List<Block> blocks = Lists.newArrayList(Iterables.filter(Block.REGISTRY, new Predicate<Block>()
        {
            public boolean apply(Block block)
            {
                return block.getRegistryName() != null;
            }
        }));
        Collections.sort(blocks, new Comparator<Block>()
        {
            public int compare(Block b1, Block b2)
            {
                return b1.getRegistryName().toString().compareTo(b2.getRegistryName().toString());
            }
        });
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
    protected void registerVariant(ModelBlockDefinition definition, ModelResourceLocation location)
    {
        IModel model;
        try
        {
            model = ModelLoaderRegistry.getModel(location);
        }
        catch(Exception e)
        {
            storeException(location, e);
            model = getMissingModel();
        }
        stateModels.put(location, model);
    }

    @Override
    protected void registerMultipartVariant(ModelBlockDefinition definition, Collection<ModelResourceLocation> locations)
    {
        for (ModelResourceLocation location : locations)
        {
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
        return new ModelBlockDefinition(new ArrayList<ModelBlockDefinition>());
    }

    @Override
    protected void loadItemModels()
    {
        // register model for the universal bucket, if it exists
        if(FluidRegistry.isUniversalBucketEnabled())
        {
            setBucketModelDefinition(ForgeModContainer.getInstance().universalBucket);
        }

        registerVariantNames();

        List<Item> items = Lists.newArrayList(Iterables.filter(Item.REGISTRY, new Predicate<Item>()
        {
            public boolean apply(Item item)
            {
                return item.getRegistryName() != null;
            }
        }));
        Collections.sort(items, new Comparator<Item>()
        {
            public int compare(Item i1, Item i2)
            {
                return i1.getRegistryName().toString().compareTo(i2.getRegistryName().toString());
            }
        });

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
                    model = ModelLoaderRegistry.getModel(file);
                }
                catch(Exception normalException)
                {
                    // try blockstate json if the item model is missing
                    FMLLog.fine("Item json isn't found for '" + memory + "', trying to load the variant from the blockstate json");
                    try
                    {
                        model = ModelLoaderRegistry.getModel(memory);
                    }
                    catch (Exception blockstateException)
                    {
                        exception = new ItemLoadingException("Could not load item model either from the normal location " + file + " or from the blockstate", normalException, blockstateException);
                    }
                }
                stateModels.put(memory, model);
                if(exception != null)
                {
                    storeException(memory, exception);
                }
            }
        }
        ProgressManager.pop(itemBar);

        // replace vanilla bucket models if desired. done afterwards for performance reasons
        if(ForgeModContainer.replaceVanillaBucketModel)
        {
            // ensure the bucket model is loaded
            if(!stateModels.containsKey(ModelDynBucket.LOCATION))
            {
                // load forges blockstate json for it
                try
                {
                    registerVariant(getModelBlockDefinition(ModelDynBucket.LOCATION), ModelDynBucket.LOCATION);
                }
                catch (Exception exception)
                {
                    FMLLog.getLogger().error("Could not load the forge bucket model from the blockstate", exception);
                    return;
                }
            }

            // empty bucket
            for(String s : getVariantNames(Items.BUCKET))
            {
                ModelResourceLocation memory = getInventoryVariant(s);
                IModel model = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation("forge", "item/bucket"));
                // only on successful load, otherwise continue using the old model
                if(model != getMissingModel())
                {
                    stateModels.put(memory, model);
                }
            }

            setBucketModel(Items.WATER_BUCKET);
            setBucketModel(Items.LAVA_BUCKET);
            // milk bucket only replaced if some mod adds milk
            if(FluidRegistry.isFluidRegistered("milk"))
            {
                // can the milk be put into a bucket?
                Fluid milk = FluidRegistry.getFluid("milk");
                FluidStack milkStack = new FluidStack(milk, Fluid.BUCKET_VOLUME);
                IFluidHandler bucketHandler = FluidUtil.getFluidHandler(new ItemStack(Items.BUCKET));
                if (bucketHandler != null && bucketHandler.fill(milkStack, false) == Fluid.BUCKET_VOLUME)
                {
                    setBucketModel(Items.MILK_BUCKET);
                }
            }
            else
            {
                // milk bucket if no milk fluid is present
                for(String s : getVariantNames(Items.MILK_BUCKET))
                {
                    ModelResourceLocation memory = getInventoryVariant(s);
                    IModel model = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation("forge", "item/bucket_milk"));
                    // only on successful load, otherwise continue using the old model
                    if(model != getMissingModel())
                    {
                        stateModels.put(memory, model);
                    }
                }
            }
        }
    }

    private void setBucketModel(Item item)
    {
        for(String s : getVariantNames(item))
        {
            ModelResourceLocation memory = getInventoryVariant(s);
            IModel model = stateModels.get(ModelDynBucket.LOCATION);
            if(model != null)
            {
                stateModels.put(memory, model);
            }
        }
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

    private final class VanillaModelWrapper implements IRetexturableModel, IModelSimpleProperties, IModelUVLock, IAnimatedModel
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

        public IBakedModel bake(IModelState state, final VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
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
            Map<TransformType, TRSRTransformation> tMap = Maps.newHashMap();
            tMap.putAll(IPerspectiveAwareModel.MapWrapper.getTransforms(transforms));
            tMap.putAll(IPerspectiveAwareModel.MapWrapper.getTransforms(state));
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
            final TRSRTransformation baseState = modelState.apply(Optional.<IModelPart>absent()).or(TRSRTransformation.identity());
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

            return new IPerspectiveAwareModel.MapWrapper(builder.makeBakedModel(), perState)
            {
                private final ItemOverrideList overrides = new AnimationItemOverrideList(VanillaModelWrapper.this, modelState, format, bakedTextureGetter, super.getOverrides());

                @Override
                public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
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
                Iterator<Entry<EnumFacing, BlockPartFace>> itr = part.mapFaces.entrySet().iterator();
                while (itr.hasNext())
                {
                    Entry<EnumFacing, BlockPartFace> entry = itr.next();
                    if (removed.contains(entry.getValue().texture))
                        itr.remove();
                }
            }

            return new VanillaModelWrapper(location, newModel, uvlock, animation);
        }

        @Override
        public Optional<? extends IClip> getClip(String name)
        {
            if(animation.getClips().containsKey(name))
            {
                return Optional.<IClip>fromNullable(animation.getClips().get(name));
            }
            return Optional.absent();
        }

        public IModelState getDefaultState()
        {
            return ModelRotation.X0_Y0;
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
        private final List<ResourceLocation> locations = new ArrayList<ResourceLocation>();
        private final Set<ResourceLocation> textures = Sets.newHashSet();
        private final List<IModel> models = new ArrayList<IModel>();
        private final IModelState defaultState;

        public WeightedRandomModel(ResourceLocation parent, VariantList variants) throws Exception
        {
            this.variants = variants.getVariantList();
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
                builder.add(Pair.of(model, v.getState()));
            }

            if (models.size() == 0) //If all variants are missing, add one with the missing model and default rotation.
            {
                // FIXME: log this?
                IModel missing = ModelLoaderRegistry.getMissingModel();
                models.add(missing);
                builder.add(Pair.<IModel, IModelState>of(missing, TRSRTransformation.identity()));
            }

            defaultState = new MultiModelState(builder.build());
        }

        public Collection<ResourceLocation> getDependencies()
        {
            return ImmutableList.copyOf(locations);
        }

        public Collection<ResourceLocation> getTextures()
        {
            return ImmutableSet.copyOf(textures);
        }

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

        public IModelState getDefaultState()
        {
            return defaultState;
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

    protected static enum VanillaLoader implements ICustomModelLoader
    {
        INSTANCE;

        private ModelLoader loader;

        void setLoader(ModelLoader loader)
        {
            this.loader = loader;
        }

        ModelLoader getLoader()
        {
            return loader;
        }

        // NOOP, handled in loader
        public void onResourceManagerReload(IResourceManager resourceManager) {}

        public boolean accepts(ResourceLocation modelLocation)
        {
            return true;
        }

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
        public boolean load(IResourceManager manager, ResourceLocation location)
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
            map.setTextureEntry(White.LOCATION.toString(), White.INSTANCE);
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
        IBakedModel missingModel = modelRegistry.getObject(MODEL_MISSING);
        Map<String, Integer> modelErrors = Maps.newHashMap();
        Set<ResourceLocation> printedBlockStateErrors = Sets.newHashSet();
        Multimap<ModelResourceLocation, IBlockState> reverseBlockMap = null;
        Multimap<ModelResourceLocation, String> reverseItemMap = null;
        if(enableVerboseMissingInfo)
        {
            reverseBlockMap = HashMultimap.create();
            for(Map.Entry<IBlockState, ModelResourceLocation> entry : blockModelShapes.getBlockStateMapper().putAllStateModelLocations().entrySet())
            {
                reverseBlockMap.put(entry.getValue(), entry.getKey());
            }
            reverseItemMap = HashMultimap.create();
            for(Item item : GameData.getItemRegistry().typeSafeIterable())
            {
                for(String s : getVariantNames(item))
                {
                    ModelResourceLocation memory = getInventoryVariant(s);
                    reverseItemMap.put(memory, item.getRegistryName().toString());
                }
            }
        }

        for(Map.Entry<ResourceLocation, Exception> entry : loadingExceptions.entrySet())
        {
            // ignoring pure ResourceLocation arguments, all things we care about pass ModelResourceLocation
            if(entry.getKey() instanceof ModelResourceLocation)
            {
                ModelResourceLocation location = (ModelResourceLocation)entry.getKey();
                IBakedModel model = modelRegistry.getObject(location);
                if(model == null || model == missingModel)
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
                            FMLLog.getLogger().error(errorMsg + ", normal location exception: ", ex.normalException);
                            FMLLog.getLogger().error(errorMsg + ", blockstate location exception: ", ex.blockstateException);
                        }
                        else
                        {
                            FMLLog.getLogger().error(errorMsg, entry.getValue());
                        }
                        ResourceLocation blockstateLocation = new ResourceLocation(location.getResourceDomain(), location.getResourcePath());
                        if(loadingExceptions.containsKey(blockstateLocation) && !printedBlockStateErrors.contains(blockstateLocation))
                        {
                            FMLLog.getLogger().error("Exception loading blockstate for the variant " + location + ": ", loadingExceptions.get(blockstateLocation));
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
                    FMLLog.severe("Model definition for location %s not found", missing);
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
                FMLLog.severe("Suppressed additional %s model loading errors for domain %s", e.getValue() - verboseMissingInfoCount, e.getKey());
            }
        }
        isLoading = false;
    }

    private static final Map<RegistryDelegate<Block>, IStateMapper> customStateMappers = Maps.newHashMap();

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
        for (Entry<RegistryDelegate<Block>, IStateMapper> e : customStateMappers.entrySet())
        {
            shapes.registerBlockWithStateMapper(e.getKey().get(), e.getValue());
        }
    }

    private static final Map<RegistryDelegate<Item>, ItemMeshDefinition> customMeshDefinitions = com.google.common.collect.Maps.newHashMap();
    private static final Map<Pair<RegistryDelegate<Item>, Integer>, ModelResourceLocation> customModels = com.google.common.collect.Maps.newHashMap();

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
        ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return ModelDynBucket.LOCATION;
            }
        });
        ModelBakery.registerItemVariants(item, ModelDynBucket.LOCATION);
    }

    /**
     * Internal, do not use.
     */
    public static void onRegisterItems(ItemModelMesher mesher)
    {
        for (Map.Entry<RegistryDelegate<Item>, ItemMeshDefinition> e : customMeshDefinitions.entrySet())
        {
            mesher.register(e.getKey().get(), e.getValue());
        }
        for (Entry<Pair<RegistryDelegate<Item>, Integer>, ModelResourceLocation> e : customModels.entrySet())
        {
            mesher.register(e.getKey().getLeft().get(), e.getKey().getRight(), e.getValue());
        }
    }

    private static enum DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite>
    {
        INSTANCE;

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
            catch(MissingVariantException e)
            {
                if(definition.hasMultipartData())
                {
                    return new MultipartModel(new ResourceLocation(variant.getResourceDomain(), variant.getResourcePath()), definition.getMultipartData());
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

        // FIXME: represent selectors as dependencies?
        @Override
        public Collection<ResourceLocation> getDependencies()
        {
            return ImmutableSet.of();
        }

        @Override
        public Collection<ResourceLocation> getTextures()
        {
            return ImmutableSet.of();
        }

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
        public IModelState getDefaultState()
        {
            return TRSRTransformation.identity();
        }
    }
}
