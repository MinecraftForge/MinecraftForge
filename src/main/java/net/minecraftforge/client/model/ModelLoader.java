package net.minecraftforge.client.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
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
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ModelLoader extends ModelBakery
{
    private final Map<ModelResourceLocation, IModel> stateModels = new HashMap<ModelResourceLocation, IModel>();
    private final Set<ResourceLocation> textures = new HashSet<ResourceLocation>();
    private final Set<ResourceLocation> loadingModels = new HashSet<ResourceLocation>();
    private final Set<ModelResourceLocation> missingVariants = Sets.newHashSet();
    private IModel missingModel = null;

    private boolean isLoading = false;
    public boolean isLoading()
    {
        return isLoading;
    }

    public ModelLoader(IResourceManager manager, TextureMap map, BlockModelShapes shapes)
    {
        super(manager, map, shapes);
        VanillaLoader.instance.setLoader(this);
        ModelLoaderRegistry.clearModelCache();
    }

    @Override
    public IRegistry setupModelRegistry()
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
                    sprites.put(t, map.registerSprite(t));
                }
            }
        });
        sprites.put(new ResourceLocation("missingno"), textureMap.getMissingSprite());
        Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>()
        {
            public TextureAtlasSprite apply(ResourceLocation location)
            {
                return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
            }
        };
        IFlexibleBakedModel missingBaked = missingModel.bake(missingModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
        for (Entry<ModelResourceLocation, IModel> e : stateModels.entrySet())
        {
            if(e.getValue() == getMissingModel())
            {
                bakedRegistry.putObject(e.getKey(), missingBaked);
            }
            else
            {
                bakedRegistry.putObject(e.getKey(), e.getValue().bake(e.getValue().getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter));
            }
        }
        return bakedRegistry;
    }

    private void loadBlocks()
    {
        Map<IBlockState, ModelResourceLocation> stateMap = blockModelShapes.getBlockStateMapper().putAllStateModelLocations();
        Collection<ModelResourceLocation> variants = Lists.newArrayList(stateMap.values());
        variants.add(new ModelResourceLocation("minecraft:item_frame", "normal")); //Vanilla special cases item_frames so must we
        variants.add(new ModelResourceLocation("minecraft:item_frame", "map"));
        loadVariants(variants);
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

    private void loadItems()
    {
        registerVariantNames();
        for(Item item : GameData.getItemRegistry().typeSafeIterable())
        {
            for(String s : (List<String>)getVariantNames(item))
            {
                ResourceLocation file = getItemLocation(s);
                ModelResourceLocation memory = new ModelResourceLocation(s, "inventory");
                IModel model = null;
                try
                {
                    model = getModel(file);
                }
                catch (IOException e)
                {
                    // Handled by our finally block.
                }
                finally
                {
                    if (model == null || model == getMissingModel())
                    {
                        FMLLog.fine("Item json isn't found for '" + memory + "', trying to load the variant from the blockstate json");
                        registerVariant(getModelBlockDefinition(memory), memory);
                    }
                    else stateModels.put(memory, model);
                }
            }
        }
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
        IModel model = ModelLoaderRegistry.getModel(location);
        for(ResourceLocation dep : model.getDependencies())
        {
            getModel(dep);
        }
        textures.addAll(model.getTextures());
        loadingModels.remove(location);
    }

    private class VanillaModelWrapper implements IRetexturableModel
    {
        private final ResourceLocation location;
        private final ModelBlock model;

        public VanillaModelWrapper(ResourceLocation location, ModelBlock model)
        {
            this.location = location;
            this.model = model;
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

            ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

            if(hasItemModel(model))
            {
                for(String s : (List<String>)ItemModelGenerator.LAYERS)
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
            for(String s : (Iterable<String>)model.textures.values())
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
            if(hasItemModel(model)) model = makeItemModel(model);
            if(model == null) return getMissingModel().bake(state, format, bakedTextureGetter);
            if(isCustomRenderer(model)) return new IFlexibleBakedModel.Wrapper(new BuiltInModel(new ItemCameraTransforms(model.getThirdPersonTransform(), model.getFirstPersonTransform(), model.getHeadTransform(), model.getInGuiTransform())), Attributes.DEFAULT_BAKED_FORMAT);
            return new IFlexibleBakedModel.Wrapper(bakeModel(model, state.apply(this), state instanceof UVLock), Attributes.DEFAULT_BAKED_FORMAT);
        }

        public IModelState getDefaultState()
        {
            return ModelRotation.X0_Y0;
        }

        @Override
        public IModel retexture(ImmutableMap<String, String> textures)
        {
            if (textures.isEmpty())
                return this;

            List<BlockPart> elements = Lists.newArrayList(); //We have to duplicate this so we can edit it below.
            for (BlockPart part : (List<BlockPart>)this.model.getElements())
            {
                elements.add(new BlockPart(part.positionFrom, part.positionTo, Maps.newHashMap(part.mapFaces), part.partRotation, part.shade));
            }

            ModelBlock neweModel = new ModelBlock(this.model.getParentLocation(), elements,
                Maps.newHashMap(this.model.textures), this.model.isAmbientOcclusion(), this.model.isGui3d(), //New Textures man VERY IMPORTANT
                new ItemCameraTransforms(this.model.getThirdPersonTransform(), this.model.getFirstPersonTransform(), this.model.getHeadTransform(), this.model.getInGuiTransform()));
            neweModel.name = this.model.name;
            neweModel.parent = this.model.parent;

            Set<String> removed = Sets.newHashSet();

            for (Entry<String, String> e : textures.entrySet())
            {
                if ("".equals(e.getValue()))
                {
                    removed.add(e.getKey());
                    neweModel.textures.remove(e.getKey());
                }
                else
                    neweModel.textures.put(e.getKey(), e.getValue());
            }

            // Map the model's texture references as if it was the parent of a model with the retexture map as its textures.
            Map<String, String> remapped = Maps.newHashMap();

            for (Entry<String, String> e : (Set<Entry<String, String>>)neweModel.textures.entrySet())
            {
                if (e.getValue().startsWith("#"))
                {
                    String key = e.getValue().substring(1);
                    if (neweModel.textures.containsKey(key))
                        remapped.put(e.getKey(), (String)neweModel.textures.get(key));
                }
            }

            neweModel.textures.putAll(remapped);

            //Remove any faces that use a null texture, this is for performance reasons, also allows some cool layering stuff.
            for (BlockPart part : (List<BlockPart>)neweModel.getElements())
            {
                Iterator<Entry<EnumFacing, BlockPartFace>> itr = part.mapFaces.entrySet().iterator();
                while (itr.hasNext())
                {
                    Entry<EnumFacing, BlockPartFace> entry = itr.next();
                    if (removed.contains(entry.getValue().texture))
                        itr.remove();
                }
            }

            return new VanillaModelWrapper(location, neweModel);
        }
    }

    public static class UVLock implements IModelState
    {
        private final IModelState state;

        public UVLock(IModelState state)
        {
            this.state = state;
        }

        public TRSRTransformation apply(IModelPart part)
        {
            return state.apply(part);
        }
    }

    // Weighted models can contain multiple copies of 1 model with different rotations - this is to make it work with IModelState (different copies will be different objects).
    private static class WeightedPartWrapper implements IModel
    {
        private final IModel model;

        public WeightedPartWrapper(IModel model)
        {
            this.model = model;
        }

        public Collection<ResourceLocation> getDependencies()
        {
            return model.getDependencies();
        }

        public Collection<ResourceLocation> getTextures()
        {
            return model.getTextures();
        }

        public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            return model.bake(state, format, bakedTextureGetter);
        }

        public IModelState getDefaultState()
        {
            return model.getDefaultState();
        }
    }

    private class WeightedRandomModel implements IModel
    {
        private final List<Variant> variants;
        private final List<ResourceLocation> locations = new ArrayList<ResourceLocation>();
        private final List<IModel> models = new ArrayList<IModel>();
        private final IModelState defaultState;

        @Deprecated public WeightedRandomModel(Variants variants){ this(null, variants); } // Remove 1.9
        public WeightedRandomModel(ModelResourceLocation parent, Variants variants)
        {
            this.variants = variants.getVariants();
            ImmutableMap.Builder<IModelPart, TRSRTransformation> builder = ImmutableMap.builder();
            for (Variant v : (List<Variant>)variants.getVariants())
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
                    textures.addAll(model.getTextures()); // Kick this, just in case.
                }

                model = new WeightedPartWrapper(model);
                models.add(model);
                builder.put(model, new TRSRTransformation(v.getRotation()));
            }

            if (models.size() == 0) //If all variants are missing, add one with the missing model and default rotation.
            {
                IModel missing = getMissingModel();
                models.add(missing);
                builder.put(missing, new TRSRTransformation(ModelRotation.X0_Y0));
            }

            defaultState = new MapModelState(builder.build());

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
                return model.bake(addUV(v.isUvLocked(), state.apply(model)), format, bakedTextureGetter);
            }
            WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
            for(int i = 0; i < variants.size(); i++)
            {
                IModel model = models.get(i);
                Variant v =  variants.get(i);
                builder.add(model.bake(addUV(v.isUvLocked(), state.apply(model)), format, bakedTextureGetter), variants.get(i).getWeight());
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
        private final WeightedBakedModel parent;
        private final VertexFormat format;

        public FlexibleWeightedBakedModel(WeightedBakedModel parent, VertexFormat format)
        {
            super(parent.models);
            this.parent = parent;
            this.format = format;
        }

        public VertexFormat getFormat()
        {
            return format;
        }
    }

    private boolean isBuiltinModel(ModelBlock model)
    {
        return model == MODEL_GENERATED || model == MODEL_COMPASS || model == MODEL_CLOCK || model == MODEL_ENTITY;
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
            return loader.new VanillaModelWrapper(modelLocation, loader.loadModel(modelLocation));
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
            loadSprite(images, null);
            return false;
        }

        public void register(TextureMap map)
        {
            map.setTextureEntry(White.loc.toString(), White.instance);
        }
    }

    public void onPostBakeEvent(IRegistry modelRegistry)
    {
        Object missingModel = modelRegistry.getObject(MODEL_MISSING);
        for(ModelResourceLocation missing : missingVariants)
        {
            Object model = modelRegistry.getObject(missing);
            if(model == null || model == missingModel)
            {
                FMLLog.severe("Model definition for location %s not found", missing);
            }
        }
        isLoading = false;
    }

    private static final Map<RegistryDelegate<Block>, IStateMapper> customStateMappers = Maps.newHashMap();

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

    public static void setCustomModelResourceLocation(Item item, int metadata, ModelResourceLocation model)
    {
        customModels.put(Pair.of(item.delegate, metadata), model);
    }

    public static void setCustomMeshDefinition(Item item, ItemMeshDefinition meshDefinition)
    {
        customMeshDefinitions.put(item.delegate, meshDefinition);
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
}
