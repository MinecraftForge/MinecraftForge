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
        stateModels.put(MODEL_MISSING, getModel(new ResourceLocation(MODEL_MISSING.getResourceDomain(), MODEL_MISSING.getResourcePath())));
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
        Function<ResourceLocation, TextureAtlasSprite> textureGetter = Functions.forMap(sprites, textureMap.getMissingSprite());
        for(Entry<ModelResourceLocation, IModel> e : stateModels.entrySet())
        {
            bakedRegistry.putObject(e.getKey(), e.getValue().bake(e.getValue().getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter));
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
        if(variants != null && !variants.getVariants().isEmpty())
        {
            try
            {
                stateModels.put(location, new WeightedRandomModel(variants));
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
                IModel model = getModel(file);
                if(model == null || model == getMissingModel())
                {
                    missingVariants.add(memory);
                }
                else stateModels.put(memory, model);
            }
        }
    }

    public IModel getModel(ResourceLocation location)
    {
        if(!ModelLoaderRegistry.loaded(location)) loadAnyModel(location);
        return ModelLoaderRegistry.getModel(location);
    }

    @Override
    protected ResourceLocation getModelLocation(ResourceLocation model)
    {
        return new ResourceLocation(model.getResourceDomain(), model.getResourcePath() + ".json");
    }

    private void loadAnyModel(ResourceLocation location)
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

    private class VanillaModelWrapper implements IModel
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
                IModel parent = getModel(model.getParentLocation());
                if(parent instanceof VanillaModelWrapper)
                {
                    model.parent = ((VanillaModelWrapper) parent).model;
                }
                else
                {
                    throw new IllegalStateException("vanilla model" + model + "can't have non-vanilla parent");
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

        public WeightedRandomModel(Variants variants)
        {
            this.variants = variants.getVariants();
            ImmutableMap.Builder<IModelPart, TRSRTransformation> builder = ImmutableMap.builder();
            for(Variant v : (List<Variant>)variants.getVariants())
            {
                ResourceLocation loc = v.getModelLocation();
                locations.add(loc);
                IModel model = new WeightedPartWrapper(getModel(loc));
                models.add(model);
                builder.put(model, new TRSRTransformation(v.getRotation()));
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
        return getModel(new ResourceLocation(MODEL_MISSING.getResourceDomain(), MODEL_MISSING.getResourcePath()));
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

        public IModel loadModel(ResourceLocation modelLocation)
        {
            try
            {
                return loader.new VanillaModelWrapper(modelLocation, loader.loadModel(modelLocation));
            }
            catch(IOException e)
            {
                if(loader.isLoading)
                {
                    // holding error until onPostBakeEvent
                }
                else FMLLog.log(Level.ERROR, e, "Exception loading model %s with vanilla loader, skipping", modelLocation);
                return loader.getMissingModel();
            }
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
        for(ModelResourceLocation missing : missingVariants)
        {
            if(modelRegistry.getObject(missing) == null)
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
