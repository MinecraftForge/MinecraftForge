package net.minecraftforge.client.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.MissingVariantException;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variant;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variants;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.texture.IIconCreator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.model.animation.IAnimatedModel;
import net.minecraftforge.client.model.animation.IClip;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;

@SuppressWarnings("ALL")
public class ModelLoader extends ModelBakery
{
    private final Map<ModelResourceLocation, IModel> stateModels = Maps.newHashMap();
    private final Set<ResourceLocation> textures = Sets.newHashSet();
    private final Set<ResourceLocation> loadingModels = Sets.newHashSet();
    private final Set<ModelResourceLocation> missingVariants = Sets.newHashSet();
    private final Map<ResourceLocation, Exception> loadingExceptions = Maps.newHashMap();
    private IModel missingModel = null;
    private IModel itemModel = new ItemLayerModel(MODEL_GENERATED);

    private boolean isLoading = false;
    public boolean isLoading()
    {
        return isLoading;
    }

    private final boolean enableVerboseMissingInfo = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment") || Boolean.parseBoolean(System.getProperty("forge.verboseMissingModelLogging", "false"));

    public ModelLoader(IResourceManager manager, TextureMap map, BlockModelShapes shapes)
    {
        super(manager, map, shapes);
        VanillaLoader.instance.setLoader(this);
        ModelLoaderRegistry.clearModelCache();
    }

    @Override
    public IRegistry<ModelResourceLocation, IBakedModel> setupModelRegistry()
    {
        isLoading = true;
        loadBlocks();
        loadItems();
        try
        {
            missingModel = getModel(new ResourceLocation(MODEL_MISSING.getResourceDomain(), MODEL_MISSING.getResourcePath()));
        }
        catch (IOException e)
        {
            // If this ever happens things are bad. Should never NOT be able to load the missing model.
            Throwables.propagate(e);
        }
        stateModels.put(MODEL_MISSING, missingModel);
        textures.remove(TextureMap.LOCATION_MISSING_TEXTURE);
        textures.addAll(LOCATIONS_BUILTIN_TEXTURES);
        textureMap.loadSprites(resourceManager, new IIconCreator()
        {
            public void registerSprites(TextureMap map)
            {
                for(ResourceLocation t : textures)
                {
                    map.registerSprite(t);
                }
            }
        });
        IFlexibleBakedModel missingBaked = missingModel.bake(missingModel.getDefaultState(), DefaultVertexFormats.ITEM, DefaultTextureGetter.instance);
        for (Entry<ModelResourceLocation, IModel> e : stateModels.entrySet())
        {
            if(e.getValue() == getMissingModel())
            {
                bakedRegistry.putObject(e.getKey(), missingBaked);
            }
            else
            {
                bakedRegistry.putObject(e.getKey(), e.getValue().bake(e.getValue().getDefaultState(), DefaultVertexFormats.ITEM, DefaultTextureGetter.instance));
            }
        }
        return bakedRegistry;
    }

    private void loadBlocks()
    {
        Map<IBlockState, ModelResourceLocation> stateMap = blockModelShapes.getBlockStateMapper().putAllStateModelLocations();
        List<ModelResourceLocation> variants = Lists.newArrayList(stateMap.values());
        variants.add(new ModelResourceLocation("minecraft:item_frame", "normal")); //Vanilla special cases item_frames so must we
        variants.add(new ModelResourceLocation("minecraft:item_frame", "map"));
        Collections.sort(variants, new Comparator<ModelResourceLocation>()
        {
            public int compare(ModelResourceLocation v1, ModelResourceLocation v2)
            {
                return v1.toString().compareTo(v2.toString());
            }
        });
        ProgressBar blockBar = ProgressManager.push("ModelLoader: blocks", variants.size());
        for(ModelResourceLocation variant : variants)
        {
            loadVariants(ImmutableList.of(variant));
            blockBar.step(variant.toString());
        }
        ProgressManager.pop(blockBar);
    }

    @Override
    protected void registerVariant(ModelBlockDefinition definition, ModelResourceLocation location)
    {
        Variants variants = null;
        try
        {
            variants = definition.getVariants(location.getVariant());
        }
        catch(MissingVariantException e)
        {
            missingVariants.add(location);
        }
        if (variants != null && !variants.getVariants().isEmpty())
        {
            try
            {
                stateModels.put(location, new WeightedRandomModel(location, variants));
            }
            catch(Throwable e)
            {
                throw new RuntimeException(e);
            }
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

    private void loadItems()
    {
        // register model for the universal bucket, if it exists
        if(FluidRegistry.isUniversalBucketEnabled())
        {
            setBucketModelDefinition(ForgeModContainer.getInstance().universalBucket);
        }

        registerVariantNames();
        
        List<String> itemVariants = Lists.newArrayList();
        for(Item item : GameData.getItemRegistry().typeSafeIterable())
        {
            itemVariants.addAll(getVariantNames(item));
        }
        Collections.sort(itemVariants);
        ProgressBar itemBar = ProgressManager.push("ModelLoader: items", itemVariants.size());
        for(String s : itemVariants)
        {
            ResourceLocation file = getItemLocation(s);
            ModelResourceLocation memory = getInventoryVariant(s);
            itemBar.step(memory.toString());
            IModel model = null;
            try
            {
                // default loading
                model = getModel(file);
                if (model == null)
                {
                    model = getMissingModel();
                }
                stateModels.put(memory, model);
            }
            catch (FileNotFoundException e)
            {
                // try blockstate json if the item model is missing
                FMLLog.fine("Item json isn't found for '" + memory + "', trying to load the variant from the blockstate json");
                try
                {
                    registerVariant(getModelBlockDefinition(memory), memory);
                }
                catch (Exception exception)
                {
                    storeException(memory, new Exception("Could not load item model either from the normal location " + file + " or from the blockstate", exception));
                }
            }
            catch (Exception exception)
            {
                storeException(memory, exception);
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
            for(String s : getVariantNames(Items.bucket))
            {
                ModelResourceLocation memory = getInventoryVariant(s);
                try
                {
                    IModel model = getModel(new ResourceLocation("forge", "item/bucket"));
                    // only on successful load, otherwise continue using the old model
                    stateModels.put(memory, model);
                }
                catch(IOException e)
                {
                    // use the original vanilla model
                }
            }

            setBucketModel(Items.water_bucket);
            setBucketModel(Items.lava_bucket);
            // milk bucket only replaced if some mod adds milk
            if(FluidRegistry.isFluidRegistered("milk"))
            {
                // can the milk be put into a bucket?
                Fluid milk = FluidRegistry.getFluid("milk");
                FluidStack milkStack = new FluidStack(milk, FluidContainerRegistry.BUCKET_VOLUME);
                if(FluidContainerRegistry.getContainerCapacity(milkStack, new ItemStack(Items.bucket)) == FluidContainerRegistry.BUCKET_VOLUME)
                {
                    setBucketModel(Items.milk_bucket);
                }
            }
            else
            {
                // milk bucket if no milk fluid is present
                for(String s : getVariantNames(Items.milk_bucket))
                {
                    ModelResourceLocation memory = getInventoryVariant(s);
                    try
                    {
                        IModel model = getModel(new ResourceLocation("forge", "item/bucket_milk"));
                        // only on successful load, otherwise continue using the old model
                        stateModels.put(memory, model);
                    }
                    catch(IOException e)
                    {
                        // use the original vanilla model
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

    public static ModelResourceLocation getInventoryVariant(String s)
    {
        if(s.contains("#"))
        {
            return new ModelResourceLocation(s);
        }
        return new ModelResourceLocation(s, "inventory");
    }

    public IModel getModel(ResourceLocation location) throws IOException
    {
        if(!ModelLoaderRegistry.loaded(location)) loadAnyModel(location);
        return ModelLoaderRegistry.getModel(location);
    }

    @Override
    protected ResourceLocation getModelLocation(ResourceLocation model)
    {
        return new ResourceLocation(model.getResourceDomain(), model.getResourcePath() + ".json");
    }

    private void loadAnyModel(ResourceLocation location) throws IOException
    {
        if(loadingModels.contains(location))
        {
            throw new IllegalStateException("circular model dependencies involving model " + location);
        }
        loadingModels.add(location);
        try
        {
            IModel model = ModelLoaderRegistry.getModel(location);
            resolveDependencies(model);
        }
        finally
        {
            loadingModels.remove(location);
        }
    }

    IModel getVariantModel(ModelResourceLocation location)
    {
        loadVariants(ImmutableList.of(location));
        IModel model = stateModels.get(location);
        if(model == null) model = getMissingModel();
        return model;
    }

    private void resolveDependencies(IModel model) throws IOException
    {
        for (ResourceLocation dep : model.getDependencies())
        {
            if(dep instanceof ModelResourceLocation)
            {
                getVariantModel((ModelResourceLocation)dep);
            }
            else
            {
                getModel(dep);
            }
        }
        textures.addAll(model.getTextures());
    }

    private class VanillaModelWrapper implements IRetexturableModel<VanillaModelWrapper>, IModelSimpleProperties<VanillaModelWrapper>, IAnimatedModel
    {
        private final ResourceLocation location;
        private final ModelBlock model;
        private final ModelBlockAnimation animation;

        public VanillaModelWrapper(ResourceLocation location, ModelBlock model, ModelBlockAnimation animation)
        {
            this.location = location;
            this.model = model;
            this.animation = animation;
        }

        public Collection<ResourceLocation> getDependencies()
        {
            if(model.getParentLocation() == null || model.getParentLocation().getResourcePath().startsWith("builtin/")) return Collections.emptyList();
            return Collections.singletonList(model.getParentLocation());
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
                    try
                    {
                        IModel parent = getModel(model.getParentLocation());
                        if(parent instanceof VanillaModelWrapper)
                        {
                            model.parent = ((VanillaModelWrapper) parent).model;
                        }
                        else
                        {
                            throw new IllegalStateException("vanilla model '" + model + "' can't have non-vanilla parent");
                        }
                    }
                    catch (IOException e)
                    {
                        FMLLog.warning("Could not load vanilla model parent '" + model.getParentLocation() + "' for '" + model + "': " + e.toString());
                        IModel missing = ModelLoader.this.getMissingModel();
                        if (missing instanceof VanillaModelWrapper)
                        {
                            model.parent = ((VanillaModelWrapper)missing).model;
                        }
                        else
                        {
                            throw new IllegalStateException("vanilla model '" + model + "' has missing parent, and missing model is not a vanilla model");
                        }
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
                    // mojang hardcode
                    if(model.getRootModel() == MODEL_COMPASS && !loc.equals(TextureMap.LOCATION_MISSING_TEXTURE))
                    {
                        TextureAtlasSprite.setLocationNameCompass(loc.toString());
                    }
                    else if(model.getRootModel() == MODEL_CLOCK && !loc.equals(TextureMap.LOCATION_MISSING_TEXTURE))
                    {
                        TextureAtlasSprite.setLocationNameClock(loc.toString());
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

        public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
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

            ItemCameraTransforms transforms = model.func_181682_g();
            boolean uvlock = false;
            if(state instanceof UVLock)
            {
                uvlock = true;
                state = ((UVLock)state).getParent();
            }
            Map<TransformType, TRSRTransformation> tMap = Maps.newHashMap();
            tMap.putAll(IPerspectiveAwareModel.MapWrapper.getTransforms(transforms));
            tMap.putAll(IPerspectiveAwareModel.MapWrapper.getTransforms(state));
            IModelState perState = new SimpleModelState(ImmutableMap.copyOf(tMap));

            if(hasItemModel(model))
            {
                return new ItemLayerModel(model).bake(perState, format, bakedTextureGetter);
            }
            if(isCustomRenderer(model)) return new IFlexibleBakedModel.Wrapper(new BuiltInModel(transforms), format);
            return bakeNormal(model, perState, state.apply(Optional.<IModelPart>absent()).or(TRSRTransformation.identity()), newTransforms, format, bakedTextureGetter, uvlock);
        }

        private IFlexibleBakedModel bakeNormal(ModelBlock model, IModelState perState, final TRSRTransformation modelState, List<TRSRTransformation> newTransforms, VertexFormat format, final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked)
        {
            TextureAtlasSprite particle = bakedTextureGetter.apply(new ResourceLocation(model.resolveTextureName("particle")));
            SimpleBakedModel.Builder builder = (new SimpleBakedModel.Builder(model)).setTexture(particle);
            for(int i = 0; i < model.getElements().size(); i++)
            {
                BlockPart part = model.getElements().get(i);
                TRSRTransformation transformation = modelState;
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
                        builder.addFaceQuad(modelState.rotate(e.getValue().cullFace), makeBakedQuad(part, e.getValue(), textureatlassprite1, e.getKey(), transformation, uvLocked));
                    }
                }
            }

            return new ISmartBlockModel.PerspectiveWrapper(new IPerspectiveAwareModel.MapWrapper(new IFlexibleBakedModel.Wrapper(builder.makeBakedModel(), format), perState))
            {
                public IBakedModel handleBlockState(IBlockState state)
                {
                    return VanillaModelWrapper.this.handleBlockState(parent, bakedTextureGetter, modelState, state);
                }
            };
        }

        private IBakedModel handleBlockState(IFlexibleBakedModel model, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, TRSRTransformation modelState, IBlockState state)
        {
            if(state instanceof IExtendedBlockState)
            {
                IExtendedBlockState exState = (IExtendedBlockState)state;
                if(exState.getUnlistedNames().contains(Properties.AnimationProperty))
                {
                    IModelState newState = exState.getValue(Properties.AnimationProperty);
                    if(newState != null)
                    {
                        return VanillaModelWrapper.this.bake(new ModelStateComposition(modelState, newState), model.getFormat(), bakedTextureGetter);
                    }
                }
            }
            return model;
        }

        @Override
        public IModel retexture(ImmutableMap<String, String> textures)
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
                model.func_181682_g());
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

            return new VanillaModelWrapper(location, newModel, animation);
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
            ModelBlock newModel = new ModelBlock(model.getParentLocation(), model.getElements(), model.textures, value, model.isGui3d(), model.func_181682_g());
            newModel.parent = model.parent;
            newModel.name = model.name;
            return new VanillaModelWrapper(location, newModel, animation);
        }

        @Override
        public VanillaModelWrapper gui3d(boolean value)
        {
            if(model.isGui3d() == value)
            {
                return this;
            }
            ModelBlock newModel = new ModelBlock(model.getParentLocation(), model.getElements(), model.textures, model.ambientOcclusion, value, model.func_181682_g());
            newModel.parent = model.parent;
            newModel.name = model.name;
            return new VanillaModelWrapper(location, newModel, animation);
        }
    }

    @Deprecated // rework in 1.9
    public static class UVLock implements IModelState
    {
        private final IModelState parent;

        public UVLock(IModelState parent)
        {
            this.parent = parent;
        }

        public IModelState getParent()
        {
            return parent;
        }

        public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
        {
            return parent.apply(part);
        }
    }

    private class WeightedRandomModel implements IModel
    {
        private final List<Variant> variants;
        private final List<ResourceLocation> locations = new ArrayList<ResourceLocation>();
        private final List<IModel> models = new ArrayList<IModel>();
        private final IModelState defaultState;

        public WeightedRandomModel(ModelResourceLocation parent, Variants variants)
        {
            this.variants = variants.getVariants();
            ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
            for (Variant v : variants.getVariants())
            {
                ResourceLocation loc = v.getModelLocation();
                locations.add(loc);

                IModel model = null;
                try
                {
                    model = getModel(loc);
                }
                catch (Exception e)
                {
                    /*
                     * Vanilla eats this, which makes it only show variants that have models.
                     * But that doesn't help debugging, so we maintain the missing model
                     * so that resource pack makers have a hint that their states are broken.
                     */
                    FMLLog.warning("Unable to load block model: \'" + loc + "\' for variant: \'" + parent + "\': " + e.toString());
                    model = getMissingModel();
                }

                if (v instanceof ISmartVariant)
                {
                    model = ((ISmartVariant)v).process(model, ModelLoader.this);
                    try
                    {
                        resolveDependencies(model);
                    }
                    catch (IOException e)
                    {
                        FMLLog.getLogger().error("Exception resolving indirect dependencies for model" + loc, e);
                    }
                    textures.addAll(model.getTextures()); // Kick this, just in case.
                }

                models.add(model);
                builder.add(Pair.of(model, v.getState()));
            }

            if (models.size() == 0) //If all variants are missing, add one with the missing model and default rotation.
            {
                IModel missing = getMissingModel();
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
            return Collections.emptyList();
        }

        private IModelState addUV(boolean uv, IModelState state)
        {
            if(uv) return new UVLock(state);
            return state;
        }

        public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            if(!Attributes.moreSpecific(format, Attributes.DEFAULT_BAKED_FORMAT))
            {
                throw new IllegalArgumentException("can't bake vanilla weighted models to the format that doesn't fit into the default one: " + format);
            }
            if(variants.size() == 1)
            {
                Variant v = variants.get(0);
                IModel model = models.get(0);
                return model.bake(addUV(v.isUvLocked(), MultiModelState.getPartState(state, model, 0)), format, bakedTextureGetter);
            }
            WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
            for(int i = 0; i < variants.size(); i++)
            {
                IModel model = models.get(i);
                Variant v =  variants.get(i);
                builder.add(model.bake(addUV(v.isUvLocked(), MultiModelState.getPartState(state, model, i)), format, bakedTextureGetter), variants.get(i).getWeight());
            }
            return new FlexibleWeightedBakedModel(builder.build(), Attributes.DEFAULT_BAKED_FORMAT);
        }

        public IModelState getDefaultState()
        {
            return defaultState;
        }
    }

    private static class FlexibleWeightedBakedModel extends WeightedBakedModel implements IFlexibleBakedModel
    {
        private final VertexFormat format;

        public FlexibleWeightedBakedModel(WeightedBakedModel parent, VertexFormat format)
        {
            super(parent.models);
            this.format = format;
        }

        public VertexFormat getFormat()
        {
            return format;
        }
    }

    public IModel getMissingModel()
    {
        if (missingModel == null)
        {
            try
            {
                missingModel = getModel(new ResourceLocation(MODEL_MISSING.getResourceDomain(), MODEL_MISSING.getResourcePath()));
            }
            catch (IOException e)
            {
                // If this ever happens things are bad. Should never NOT be able to load the missing model.
                Throwables.propagate(e);
            }
        }
        return missingModel;
    }

    public IModel getItemModel()
    {
        return itemModel;
    }

    static enum VanillaLoader implements ICustomModelLoader
    {
        instance;

        private ModelLoader loader;

        void setLoader(ModelLoader loader)
        {
            this.loader = loader;
        }

        ModelLoader getLoader()
        {
            return loader;
        }

        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // do nothing, cause loader will store the reference to the resourceManager
        }

        public boolean accepts(ResourceLocation modelLocation)
        {
            return true;
        }

        public IModel loadModel(ResourceLocation modelLocation) throws IOException
        {
            String modelPath = modelLocation.getResourcePath();
            if(modelLocation.getResourcePath().startsWith("models/"))
            {
                modelPath = modelPath.substring("models/".length());
            }
            ResourceLocation armatureLocation = new ResourceLocation(modelLocation.getResourceDomain(), "armatures/" + modelPath + ".json");
            ModelBlockAnimation animation = Animation.INSTANCE.loadVanillaAnimation(armatureLocation);
            return loader.new VanillaModelWrapper(modelLocation, loader.loadModel(modelLocation), animation);
        }
    }

    public static class White extends TextureAtlasSprite
    {
        public static ResourceLocation loc = new ResourceLocation("white");
        public static White instance = new White();

        protected White()
        {
            super(loc.toString());
        }

        @Override
        public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location)
        {
            return true;
        }

        @Override
        public boolean load(IResourceManager manager, ResourceLocation location)
        {
            BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setBackground(Color.WHITE);
            graphics.clearRect(0, 0, 16, 16);
            BufferedImage[] images = new BufferedImage[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1];
            images[0] = image;
            try
            {
                loadSprite(images, null);
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
            return false;
        }

        public void register(TextureMap map)
        {
            map.setTextureEntry(White.loc.toString(), White.instance);
        }
    }

    public void onPostBakeEvent(IRegistry<ModelResourceLocation, IBakedModel> modelRegistry)
    {
        IBakedModel missingModel = modelRegistry.getObject(MODEL_MISSING);
        Map<String, Integer> modelErrors = Maps.newHashMap();
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
                    reverseItemMap.put(memory, item.getRegistryName());
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
                    if(errorCount < 5)
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
                        FMLLog.getLogger().error(errorMsg, entry.getValue());
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
                if(errorCount < 5)
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
            if(e.getValue() >= 5)
            {
                FMLLog.severe("Suppressed additional %s model loading errors for domain %s", e.getValue(), e.getKey());
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
        instance;

        public TextureAtlasSprite apply(ResourceLocation location)
        {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
        }
    }

    public static Function<ResourceLocation, TextureAtlasSprite> defaultTextureGetter()
    {
        return DefaultTextureGetter.instance;
    }
}
