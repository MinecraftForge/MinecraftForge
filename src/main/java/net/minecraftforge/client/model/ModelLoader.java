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

import static net.minecraftforge.fml.Logging.MODELLOADING;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.BuiltInModel;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemModelGenerator;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelBlock;
import net.minecraft.client.renderer.model.ModelBlockDefinition;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.model.Variant;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.model.WeightedBakedModel;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.AnimationItemOverrideList;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.model.animation.IClip;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.client.ClientModLoader;
import net.minecraftforge.logging.ModelLoaderErrorMessage;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;

public final class ModelLoader extends ModelBakery
{
    public ModelLoader(IResourceManager manager, TextureMap map)
    {
        super(manager, map);
        ModelLoaderRegistry.clearModelCache(manager);
    }
/*
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
                IUnbakedModel model = ModelLoaderRegistry.getMissingModel();
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
*/
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

    public static final class VanillaModelWrapper extends ModelBlock
    {
        private final ResourceLocation location;
        private final boolean uvlock;
        private final ModelBlockAnimation animation;

        public VanillaModelWrapper(ResourceLocation location, ModelBlock model, boolean uvlock, ModelBlockAnimation animation)
        {
            super(model.getParentLocation(), model.getElements(), model.textures, model.ambientOcclusion, model.isGui3d(), model.getAllTransforms(), model.getOverrides());
            this.location = location;
            this.uvlock = uvlock;
            this.animation = animation;
        }

        @Override
        public IBakedModel bake(Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, IModelState state, boolean uvlock, VertexFormat format)
        {
            return VanillaLoader.INSTANCE.modelCache.getUnchecked(new BakedModelCacheKey(this, modelGetter, bakedTextureGetter, state, uvlock, format));
        }

        public IBakedModel bakeImpl(Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, IModelState state, boolean uvlock, VertexFormat format)
        {
            if(!Attributes.moreSpecific(format, Attributes.DEFAULT_BAKED_FORMAT))
            {
                throw new IllegalArgumentException("can't bake vanilla models to the format that doesn't fit into the default one: " + format);
            }

            List<TRSRTransformation> newTransforms = Lists.newArrayList();
            for(int i = 0; i < getElements().size(); i++)
            {
                BlockPart part = getElements().get(i);
                newTransforms.add(animation.getPartTransform(state, part, i));
            }

            ItemCameraTransforms transforms = getAllTransforms();
            Map<TransformType, TRSRTransformation> tMap = Maps.newEnumMap(TransformType.class);
            tMap.putAll(PerspectiveMapWrapper.getTransforms(transforms));
            tMap.putAll(PerspectiveMapWrapper.getTransforms(state));
            IModelState perState = new SimpleModelState(ImmutableMap.copyOf(tMap), state.apply(Optional.empty()));

            if(this == ModelBakery.MODEL_GENERATED)
            {
                return new ItemLayerModel(modelGetter, this).bake(modelGetter, bakedTextureGetter, perState, uvlock, format);
            }
            if(this == ModelBakery.MODEL_ENTITY) return new BuiltInModel(transforms, getOverrides(this, modelGetter, bakedTextureGetter));
            return bakeNormal(this, perState, state, newTransforms, format, modelGetter, bakedTextureGetter, uvlock);
        }

        private IBakedModel bakeNormal(ModelBlock model, IModelState perState, final IModelState modelState, List<TRSRTransformation> newTransforms, final VertexFormat format, final Function<ResourceLocation, IUnbakedModel> modelGetter, final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean uvLocked)
        {
            final TRSRTransformation baseState = modelState.apply(Optional.empty()).orElse(TRSRTransformation.identity());
            TextureAtlasSprite particle = bakedTextureGetter.apply(new ResourceLocation(model.resolveTextureName("particle")));
            SimpleBakedModel.Builder builder = (new SimpleBakedModel.Builder(model, model.getOverrides(model, modelGetter, bakedTextureGetter))).setTexture(particle);
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
                    if(rot == null) rot = new BlockPartRotation(new Vector3f(), EnumFacing.Axis.Y, 0, false);
                    part = new BlockPart(part.positionFrom, part.positionTo, part.mapFaces, rot, part.shade);
                }
                for(Map.Entry<EnumFacing, BlockPartFace> e : part.mapFaces.entrySet())
                {
                    TextureAtlasSprite textureatlassprite1 = bakedTextureGetter.apply(new ResourceLocation(model.resolveTextureName(e.getValue().texture)));

                    if (e.getValue().cullFace == null || !TRSRTransformation.isInteger(transformation.getMatrixVec()))
                    {
                        builder.addGeneralQuad(ModelBlock.makeBakedQuad(part, e.getValue(), textureatlassprite1, e.getKey(), transformation, uvLocked));
                    }
                    else
                    {
                        builder.addFaceQuad(baseState.rotate(e.getValue().cullFace), ModelBlock.makeBakedQuad(part, e.getValue(), textureatlassprite1, e.getKey(), transformation, uvLocked));
                    }
                }
            }

            return new PerspectiveMapWrapper(builder.build(), perState)
            {
                private final ItemOverrideList overrides = new AnimationItemOverrideList(VanillaModelWrapper.this, modelState, format, modelGetter, bakedTextureGetter, super.getOverrides());

                @Override
                public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand, IModelData modelData)
                {
                    IModelState newState = modelData.getData(Properties.AnimationProperty);
                    if(newState != null)
                    {
                        return VanillaModelWrapper.this.bake(modelGetter, bakedTextureGetter, new ModelStateComposition(modelState, newState), uvlock, format).getQuads(state, side, rand, modelData);
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
            for (BlockPart part : this.getElements())
            {
                elements.add(new BlockPart(part.positionFrom, part.positionTo, Maps.newHashMap(part.mapFaces), part.partRotation, part.shade));
            }

            ModelBlock newModel = new ModelBlock(this.getParentLocation(), elements,
                Maps.newHashMap(this.textures), this.isAmbientOcclusion(), this.isGui3d(), //New Textures man VERY IMPORTANT
                getAllTransforms(), Lists.newArrayList(getOverrides()));
            newModel.name = this.name;
            newModel.parent = this.parent;

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
            if(ambientOcclusion == value)
            {
                return this;
            }
            ModelBlock newModel = new ModelBlock(getParentLocation(), getElements(), textures, value, isGui3d(), getAllTransforms(), Lists.newArrayList(getOverrides()));
            newModel.parent = parent;
            newModel.name = name;
            return new VanillaModelWrapper(location, newModel, uvlock, animation);
        }

        @Override
        public VanillaModelWrapper gui3d(boolean value)
        {
            if(isGui3d() == value)
            {
                return this;
            }
            ModelBlock newModel = new ModelBlock(getParentLocation(), getElements(), textures, ambientOcclusion, value, getAllTransforms(), Lists.newArrayList(getOverrides()));
            newModel.parent = parent;
            newModel.name = name;
            return new VanillaModelWrapper(location, newModel, uvlock, animation);
        }
    }

    private static final class WeightedRandomModel extends VariantList
    {
        private final ResourceLocation parent;
        private final List<IUnbakedModel> models;
        private final IModelState defaultState = getDefaultState();

        public WeightedRandomModel(ResourceLocation parent, VariantList variants) throws Exception
        {
            super(variants.getVariantList());
            this.parent = parent;
            this.models = new ArrayList<>();
//            ImmutableList.Builder<Pair<IUnbakedModel, IModelState>> builder = ImmutableList.builder();
//            for (Variant v : getVariantList())
//            {
//                ResourceLocation loc = v.getModelLocation();
//                /*
//                 * Vanilla eats this, which makes it only show variants that have models.
//                 * But that doesn't help debugging, so throw the exception
//                 */
//                IUnbakedModel model;
//                if(loc.equals(MODEL_MISSING))
//                {
//                    // explicit missing location, happens if blockstate has "model"=null
//                    model = ModelLoaderRegistry.getMissingModel();
//                }
//                else
//                {
//                    model = ModelLoaderRegistry.getModel(loc);
//                }
//
//                // FIXME: is this the place? messes up dependency and texture resolution
//                model = v.process(model);
//                for(ResourceLocation location : model.getOverrideLocations())
//                {
//                    ModelLoaderRegistry.getModelOrMissing(location);
//                }
//                //FMLLog.getLogger().error("Exception resolving indirect dependencies for model" + loc, e);
//
//                models.add(model);
//
//                IModelState modelDefaultState = model.getDefaultState();
//                Preconditions.checkNotNull(modelDefaultState, "Model %s returned null as default state", loc);
//                builder.add(Pair.of(model, new ModelStateComposition(v.getState(), modelDefaultState)));
//            }
//
//            if (models.size() == 0) //If all variants are missing, add one with the missing model and default rotation.
//            {
//                // FIXME: log this?
//                IUnbakedModel missing = ModelLoaderRegistry.getMissingModel();
//                models.add(missing);
//                builder.add(Pair.of(missing, TRSRTransformation.identity()));
//            }
//
//            defaultState = new MultiModelState(builder.build());
        }

        @Override
        public IModelState getDefaultState()
        {
            return defaultState;
        }

//        @Override
//        public WeightedRandomModel retexture(ImmutableMap<String, String> textures)
//        {
//            if (textures.isEmpty())
//                return this;
//
//            // rebuild the texture list taking into account new textures
//            Set<ResourceLocation> modelTextures = Sets.newHashSet();
//            // also recreate the MultiModelState so IModelState data is properly applied to the retextured model
//            ImmutableList.Builder<Pair<IUnbakedModel, IModelState>> builder = ImmutableList.builder();
//            List<IUnbakedModel> retexturedModels = Lists.newArrayList();
//            for(int i = 0; i < getVariantList().size(); i++)
//            {
//                IUnbakedModel retextured = this.models.get(i).retexture(textures);
//                modelTextures.addAll(retextured.getTextures(ModelLoader.defaultModelGetter(), new HashSet<>()));
//                retexturedModels.add(retextured);
//                builder.add(Pair.of(retextured, getVariantList().get(i).getState()));
//            }
//
//            return new WeightedRandomModel(this.parent, this);
//        }
    }

    protected static final class BakedModelCacheKey
    {
        private final VanillaModelWrapper model;
        private final Function<ResourceLocation, IUnbakedModel> modelGetter;
        private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
        private final IModelState state;
        private final boolean uvlock;
        private final VertexFormat format;

        public BakedModelCacheKey(VanillaModelWrapper model, Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, IModelState state, boolean uvlock, VertexFormat format)
        {
            this.model = model;
            this.modelGetter = modelGetter;
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
            return Objects.equal(model, that.model) && Objects.equal(modelGetter, that.modelGetter) && Objects.equal(bakedTextureGetter, that.bakedTextureGetter) && Objects.equal(state, that.state) && uvlock == that.uvlock && Objects.equal(format, that.format);
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

        private LoadingCache<BakedModelCacheKey, IBakedModel> modelCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterWrite(100, TimeUnit.MILLISECONDS).build(new CacheLoader<BakedModelCacheKey, IBakedModel>() {
            @Override
            public IBakedModel load(BakedModelCacheKey key) throws Exception
            {
                return key.model.bakeImpl(key.modelGetter, key.bakedTextureGetter, key.state, key.uvlock, key.format);
            }
        });

        // NOOP, handled in loader
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {}

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return true;
        }

        @Override
        public IUnbakedModel loadModel(Function<ResourceLocation, IUnbakedModel> modelGetter, ResourceLocation modelLocation) throws Exception
        {
            String modelPath = modelLocation.getPath();
            if(modelLocation.getPath().startsWith("models/"))
            {
                modelPath = modelPath.substring("models/".length());
            }
            ResourceLocation armatureLocation = new ResourceLocation(modelLocation.getNamespace(), "armatures/" + modelPath + ".json");
            ModelBlockAnimation animation = ModelBlockAnimation.loadVanillaAnimation(Minecraft.getInstance().getResourceManager(), armatureLocation);
            IUnbakedModel model = modelGetter.apply(modelLocation);
            if (!(model instanceof ModelBlock))
            {
                throw new IllegalArgumentException("Model " + modelLocation + " was not a vanilla model");
            }
            IUnbakedModel iModel = new VanillaModelWrapper(modelLocation, (ModelBlock) model, false, animation);
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
        public static final TextureAtlasSprite INSTANCE = MissingTextureSprite.getSprite();
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

    /*
    // TODO replace these if necessary, IStateMapper and ItemMeshDefinition are gone
    private static final Map<IRegistryDelegate<Block>, IStateMapper> customStateMappers = Maps.newHashMap();

    /**
     * Adds a custom IBlockState -> model variant logic.
     *//*
    public static void setCustomStateMapper(Block block, IStateMapper mapper)
    {
        customStateMappers.put(block.delegate, mapper);
    }

    /**
     * Internal, do not use.
     *//*
    public static void onRegisterAllBlocks(BlockModelShapes shapes)
    {
        for (Entry<IRegistryDelegate<Block>, IStateMapper> e : customStateMappers.entrySet())
        {
            shapes.registerBlockWithStateMapper(e.getKey().get(), e.getValue());
        }
    }
*/
    private static final Map<Pair<IRegistryDelegate<Item>, Integer>, ModelResourceLocation> customModels = com.google.common.collect.Maps.newHashMap();

    /**
     * Adds a simple mapping from Item + metadata to the model variant.
     * Registers the variant with the ModelBakery too.
     *//*
    public static void setCustomModelResourceLocation(Item item, int metadata, ModelResourceLocation model)
    {
        customModels.put(Pair.of(item.delegate, metadata), model);
        ModelBakery.registerItemVariants(item, model);
    }

    /**
     * Adds generic ItemStack -> model variant logic.
     * You still need to manually call ModelBakery.registerItemVariants with all values that meshDefinition can return.
     *//*
    public static void setCustomMeshDefinition(Item item, ItemMeshDefinition meshDefinition)
    {
        customMeshDefinitions.put(item.delegate, meshDefinition);
    }

    /**
     * Helper method for registering all itemstacks for given item to map to universal bucket model.
     *//*
    public static void setBucketModelDefinition(Item item) {
        ModelLoader.setCustomMeshDefinition(item, stack -> ModelDynBucket.LOCATION);
        ModelBakery.registerItemVariants(item, ModelDynBucket.LOCATION);
    }
*/
    /**
     * Internal, do not use.
     */
    public static void onRegisterItems(ItemModelMesher mesher)
    {
        for (Entry<Pair<IRegistryDelegate<Item>, Integer>, ModelResourceLocation> e : customModels.entrySet())
        {
            mesher.register(e.getKey().getLeft().get(), e.getValue());
        }
    }

    private static enum DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite>
    {
        INSTANCE;

        @Override
        public TextureAtlasSprite apply(ResourceLocation location)
        {
            return Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());
        }
    }

    /**
     * Get the default texture getter the models will be baked with.
     */
    public static Function<ResourceLocation, TextureAtlasSprite> defaultTextureGetter()
    {
        return DefaultTextureGetter.INSTANCE;
    }
}
