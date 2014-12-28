package net.minecraftforge.client.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variant;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variants;
import net.minecraft.client.renderer.texture.IIconCreator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.item.Item;
import net.minecraft.util.IRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.GameData;

import org.apache.logging.log4j.Level;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class ModelLoader extends ModelBakery
{
    private final Map<ModelResourceLocation, IModel> stateModels = new HashMap<ModelResourceLocation, IModel>();
    private final Set<ResourceLocation> resolveTextures = new HashSet<ResourceLocation>();
    private final Set<ResourceLocation> uvLocked = new HashSet<ResourceLocation>();
    private final Set<ResourceLocation> textures = new HashSet<ResourceLocation>();
    private final Set<ResourceLocation> loadingModels = new HashSet<ResourceLocation>();

    public ModelLoader(IResourceManager manager, TextureMap map, BlockModelShapes shapes)
    {
        super(manager, map, shapes);
        VanillaLoader.instance.setLoader(this);
        ModelLoaderRegistry.clearModelCache();
    }

    @Override
    public IRegistry setupModelRegistry()
    {
        loadBlocks();
        loadItems();
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
            bakedRegistry.putObject(e.getKey(), e.getValue().bake(e.getValue().getDefaultTransformation(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter));
        }
        return bakedRegistry;
    }

    private void loadBlocks()
    {
        Map<IBlockState, ModelResourceLocation> stateMap = blockModelShapes.getBlockStateMapper().putAllStateModelLocations();
        loadVariants(stateMap.values());
    }

    @Override
    protected void registerVariant(ModelBlockDefinition definition, ModelResourceLocation location)
    {
        Variants variants = definition.getVariants(location.getVariant());
        if(!variants.getVariants().isEmpty())
        {
            stateModels.put(location, new WeightedRandomModel(variants));
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
                resolveTextures.add(ModelLoaderRegistry.getActualLocation(file));
                IModel model = getModel(file);
                if(model != null) stateModels.put(memory, model);
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

    private IBakedModel bakeModel(ModelBlock model, IModelTransformation transformation, boolean uvLocked)
    {
        if(!(transformation instanceof ModelRotation))
            throw new UnsupportedOperationException("can only bake vanilla models with vanilla transformations");
        return bakeModel(model, (ModelRotation)transformation, uvLocked);
    }

    private class VanillaModelWrapper implements IModel {
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

            if(!resolveTextures.contains(location)) return Collections.emptyList();

            ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
            builder.add(new ResourceLocation(model.resolveTextureName("particle")));

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
            if(location.getResourcePath().startsWith("models/block/") || !ModelLoader.this.isBuiltinModel(model.getRootModel()))
            {
                builder.addAll(ModelLoader.this.getTextureLocations(model));
            }
            return builder.build();
        }

        public IFlexibleBakedModel bake(IModelTransformation transformation, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            if(!Attributes.moreSpecific(format, Attributes.DEFAULT_BAKED_FORMAT))
            {
                throw new IllegalArgumentException("can't bake vanilla models to the format that doesn't fit into the default one: " + format);
            }
            ModelBlock model = this.model;
            if(hasItemModel(model)) model = makeItemModel(model);
            if(isCustomRenderer(model)) return new IFlexibleBakedModel.Wrapper(new BuiltInModel(new ItemCameraTransforms(model.getThirdPersonTransform(), model.getFirstPersonTransform(), model.getHeadTransform(), model.getInGuiTransform())), Attributes.DEFAULT_BAKED_FORMAT);
            return new IFlexibleBakedModel.Wrapper(bakeModel(model, transformation, uvLocked.contains(location)), Attributes.DEFAULT_BAKED_FORMAT);
        }

        @Override
        public IModelTransformation getDefaultTransformation()
        {
            return ModelRotation.X0_Y0;
        }
    }

    private class WeightedRandomModel implements IModel {
        private final List<Variant> variants;
        private final List<ResourceLocation> locations = new ArrayList<ResourceLocation>();

        public WeightedRandomModel(Variants variants)
        {
            this.variants = variants.getVariants();
            for(Variant v : (List<Variant>)variants.getVariants())
            {
                ResourceLocation loc = v.getModelLocation();
                resolveTextures.add(ModelLoaderRegistry.getActualLocation(loc));
                locations.add(loc);
                if(v.isUvLocked()) uvLocked.add(ModelLoaderRegistry.getActualLocation(loc));
                getModel(loc);
            }
        }

        public Collection<ResourceLocation> getDependencies()
        {
            return ImmutableList.copyOf(locations);
        }

        public Collection<ResourceLocation> getTextures()
        {
            ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
            for(ResourceLocation loc : locations)
            {
                builder.addAll(getModel(loc).getTextures());
            }
            return builder.build();
        }

        public IFlexibleBakedModel bake(IModelTransformation transformation, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            if(!Attributes.moreSpecific(format, Attributes.DEFAULT_BAKED_FORMAT))
            {
                throw new IllegalArgumentException("can't bake vanilla weighted models to the format that doesn't fit into the default one: " + format);
            }
            if(variants.size() == 1)
            {
                Variant v = variants.get(0);
                IModel model = getModel(v.getModelLocation());
                return model.bake(v.getRotation(), format, bakedTextureGetter);
            }
            WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
            for(Variant v : variants)
            {
                IModel model = getModel(v.getModelLocation());
                builder.add(model.bake(v.getRotation(), format, bakedTextureGetter), v.getWeight());
            }
            return new IFlexibleBakedModel.Wrapper(builder.build(), Attributes.DEFAULT_BAKED_FORMAT);
        }

        public IModelTransformation getDefaultTransformation()
        {
            return ModelRotation.X0_Y0;
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
                FMLLog.log(Level.ERROR, e, "Exception loading model %s with vanilla loader, skipping", modelLocation);
                return loader.getMissingModel();
            }
        }
    }
}
