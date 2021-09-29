/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Transformation;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;

/**
 * Central hub for custom model loaders.
 */
public class ModelLoaderRegistry
{
    public static final String WHITE_TEXTURE = "forge:white";

    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final Map<ResourceLocation, IModelLoader<?>> loaders = Maps.newHashMap();
    private static volatile boolean registryFrozen = false;

    // Forge built-in loaders
    public static void init()
    {
        registerLoader(new ResourceLocation("minecraft","elements"), VanillaProxy.Loader.INSTANCE);
        registerLoader(new ResourceLocation("forge","obj"), OBJLoader.INSTANCE);
        registerLoader(new ResourceLocation("forge","bucket"), DynamicBucketModel.Loader.INSTANCE);
        registerLoader(new ResourceLocation("forge","composite"), CompositeModel.Loader.INSTANCE);
        registerLoader(new ResourceLocation("forge","multi-layer"), MultiLayerModel.Loader.INSTANCE);
        registerLoader(new ResourceLocation("forge","item-layers"), ItemLayerModel.Loader.INSTANCE);
        registerLoader(new ResourceLocation("forge", "separate-perspective"), SeparatePerspectiveModel.Loader.INSTANCE);

        // TODO: Implement as new model loaders
        //registerLoader(new ResourceLocation("forge:b3d"), new ModelLoaderAdapter(B3DLoader.INSTANCE));
        //registerLoader(new ResourceLocation("forge:fluid"), new ModelLoaderAdapter(ModelFluid.FluidLoader.INSTANCE));
    }

    /**
     * INTERNAL METHOD, DO NOT CALL
     */
    public static void onModelLoadingStart()
    {
        // Minecraft recreates the ModelBakery on resource reload, but this should only run once during init.
        if (!registryFrozen)
        {
            net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.client.event.ModelRegistryEvent());
            registryFrozen = true;
        }
    }

    /**
     * Makes system aware of your loader.
     * <b>Must be called from within {@link ModelRegistryEvent}</b>
     */
    public static void registerLoader(ResourceLocation id, IModelLoader<?> loader)
    {
        if (registryFrozen)
            throw new IllegalStateException("Can not register model loaders after models have started loading. Please use ModelRegistryEvent to register your loaders.");

        synchronized(loaders)
        {
            loaders.put(id, loader);
            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(loader);
        }
    }

    public static IModelGeometry<?> getModel(ResourceLocation loaderId, JsonDeserializationContext deserializationContext, JsonObject data)
    {
        try
        {
            if (!loaders.containsKey(loaderId))
            {
                throw new IllegalStateException(String.format("Model loader '%s' not found. Registered loaders: %s", loaderId,
                        loaders.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(", "))));
            }

            IModelLoader<?> loader = loaders.get(loaderId);

            return loader.read(deserializationContext, data);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    @Nullable
    public static IModelGeometry<?> deserializeGeometry(JsonDeserializationContext deserializationContext, JsonObject object) {
        if (!object.has("loader")) {
            return null;
        }

        ResourceLocation loader = new ResourceLocation(GsonHelper.getAsString(object,"loader"));
        return getModel(loader, deserializationContext, object);
    }

    /* Explanation:
     * This takes anything that looks like a valid resourcepack texture location, and tries to extract a resourcelocation out of it.
     *  1. it will ignore anything up to and including an /assets/ folder,
     *  2. it will take the next path component as a namespace,
     *  3. it will match but skip the /textures/ part of the path,
     *  4. it will take the rest of the path up to but excluding the .png extension as the resource path
     * It's a best-effort situation, to allow model files exported by modelling software to be used without post-processing.
     * Example:
     *   C:\Something\Or Other\src\main\resources\assets\mymodid\textures\item\my_thing.png
     *   ........................................--------_______----------_____________----
     *                                                 <namespace>        <path>
     * Result after replacing '\' to '/': mymodid:item/my_thing
     */
    private static final Pattern FILESYSTEM_PATH_TO_RESLOC =
            Pattern.compile("(?:.*[\\\\/]assets[\\\\/](?<namespace>[a-z_-]+)[\\\\/]textures[\\\\/])?(?<path>[a-z_\\\\/-]+)\\.png");

    public static Material resolveTexture(@Nullable String tex, IModelConfiguration owner)
    {
        if (tex == null)
            return blockMaterial(WHITE_TEXTURE);
        if (tex.startsWith("#"))
            return owner.resolveTexture(tex);

        // Attempt to convert a common (windows/linux/mac) filesystem path to a ResourceLocation.
        // This makes no promises, if it doesn't work, too bad, fix your mtl file.
        Matcher match = FILESYSTEM_PATH_TO_RESLOC.matcher(tex);
        if (match.matches())
        {
            String namespace = match.group("namespace");
            String path = match.group("path").replace("\\", "/");
            if (namespace != null)
                return blockMaterial(new ResourceLocation(namespace, path));
            return blockMaterial(path);
        }

        return blockMaterial(tex);
    }

    @SuppressWarnings("deprecation")
    public static Material blockMaterial(String location)
    {
        return new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(location));
    }

    @SuppressWarnings("deprecation")
    public static Material blockMaterial(ResourceLocation location)
    {
        return new Material(TextureAtlas.LOCATION_BLOCKS, location);
    }

    @Nullable
    public static ModelState deserializeModelTransforms(JsonDeserializationContext deserializationContext, JsonObject modelData)
    {
        if (!modelData.has("transform"))
            return null;

        return deserializeTransform(deserializationContext, modelData.get("transform")).orElse(null);
    }

    public static Optional<ModelState> deserializeTransform(JsonDeserializationContext context, JsonElement transformData)
    {
        if (!transformData.isJsonObject())
        {
            try
            {
                Transformation base = context.deserialize(transformData, Transformation.class);
                return Optional.of(new SimpleModelState(ImmutableMap.of(), base.blockCenterToCorner()));
            }
            catch (JsonParseException e)
            {
                throw new JsonParseException("transform: expected a string, object or valid base transformation, got: " + transformData);
            }
        }
        else
        {
            JsonObject transform = transformData.getAsJsonObject();
            EnumMap<ItemTransforms.TransformType, Transformation> transforms = Maps.newEnumMap(ItemTransforms.TransformType.class);

            deserializeTRSR(context, transforms, transform, "thirdperson", ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
            deserializeTRSR(context, transforms, transform, "thirdperson_righthand", ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
            deserializeTRSR(context, transforms, transform, "thirdperson_lefthand", ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND);

            deserializeTRSR(context, transforms, transform, "firstperson", ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
            deserializeTRSR(context, transforms, transform, "firstperson_righthand", ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
            deserializeTRSR(context, transforms, transform, "firstperson_lefthand", ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND);

            deserializeTRSR(context, transforms, transform, "head", ItemTransforms.TransformType.HEAD);
            deserializeTRSR(context, transforms, transform, "gui", ItemTransforms.TransformType.GUI);
            deserializeTRSR(context, transforms, transform, "ground", ItemTransforms.TransformType.GROUND);
            deserializeTRSR(context, transforms, transform, "fixed", ItemTransforms.TransformType.FIXED);

            int k = transform.entrySet().size();
            if(transform.has("matrix")) k--;
            if(transform.has("translation")) k--;
            if(transform.has("rotation")) k--;
            if(transform.has("scale")) k--;
            if(transform.has("post-rotation")) k--;
            if(transform.has("origin")) k--;
            if(k > 0)
            {
                throw new JsonParseException("transform: allowed keys: 'thirdperson', 'firstperson', 'gui', 'head', 'matrix', 'translation', 'rotation', 'scale', 'post-rotation', 'origin'");
            }
            Transformation base = Transformation.identity();
            if(!transform.entrySet().isEmpty())
            {
                base = context.deserialize(transform, Transformation.class);
            }
            ModelState state = new SimpleModelState(Maps.immutableEnumMap(transforms), base);
            return Optional.of(state);
        }
    }

    private static void deserializeTRSR(JsonDeserializationContext context, EnumMap<ItemTransforms.TransformType, Transformation> transforms, JsonObject transform, String name, ItemTransforms.TransformType itemCameraTransform)
    {
        if(transform.has(name))
        {
            Transformation t = context.deserialize(transform.remove(name), Transformation.class);
            transforms.put(itemCameraTransform, t.blockCenterToCorner());
        }
    }

    public static BakedModel bakeHelper(BlockModel blockModel, ModelBakery modelBakery, BlockModel otherModel, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation, boolean guiLight3d)
    {
        BakedModel model;
        IModelGeometry<?> customModel = blockModel.customData.getCustomGeometry();
        ModelState customModelState = blockModel.customData.getCustomModelState();
        if (customModelState != null)
            modelTransform = new CompositeModelState(modelTransform, customModelState, modelTransform.isUvLocked());

        if (customModel != null)
            model = customModel.bake(blockModel.customData, modelBakery, spriteGetter, modelTransform, blockModel.getOverrides(modelBakery, otherModel, spriteGetter), modelLocation);
        else
        {
            // handle vanilla item models here, since vanilla has a shortcut for them
            if (blockModel.getRootModel() == ModelBakery.GENERATION_MARKER) {
                model = ITEM_MODEL_GENERATOR.generateBlockModel(spriteGetter, blockModel).bake(modelBakery, blockModel, spriteGetter, modelTransform, modelLocation, guiLight3d);
            }
            else
            {
                model = blockModel.bakeVanilla(modelBakery, otherModel, spriteGetter, modelTransform, modelLocation, guiLight3d);
            }
        }

        if (customModelState != null && !model.doesHandlePerspectives())
            model = new PerspectiveMapWrapper(model, customModelState);

        return model;
    }

    public static class VanillaProxy implements ISimpleModelGeometry<VanillaProxy>
    {
        private final List<BlockElement> elements;

        public VanillaProxy(List<BlockElement> list)
        {
            this.elements = list;
        }

        @Override
        public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation)
        {
            for(BlockElement blockpart : elements) {
                for(Direction direction : blockpart.faces.keySet()) {
                    BlockElementFace blockpartface = blockpart.faces.get(direction);
                    TextureAtlasSprite textureatlassprite1 = spriteGetter.apply(owner.resolveTexture(blockpartface.texture));
                    if (blockpartface.cullForDirection == null) {
                        modelBuilder.addGeneralQuad(BlockModel.makeBakedQuad(blockpart, blockpartface, textureatlassprite1, direction, modelTransform, modelLocation));
                    } else {
                        modelBuilder.addFaceQuad(
                                modelTransform.getRotation().rotateTransform(blockpartface.cullForDirection),
                                BlockModel.makeBakedQuad(blockpart, blockpartface, textureatlassprite1, direction, modelTransform, modelLocation));
                    }
                }
            }
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
        {
            Set<Material> textures = Sets.newHashSet();

            for(BlockElement part : elements) {
                for(BlockElementFace face : part.faces.values()) {
                    Material texture = owner.resolveTexture(face.texture);
                    if (Objects.equals(texture, MissingTextureAtlasSprite.getLocation().toString())) {
                        missingTextureErrors.add(Pair.of(face.texture, owner.getModelName()));
                    }

                    textures.add(texture);
                }
            }

            return textures;
        }

        public static class Loader implements IModelLoader<VanillaProxy>
        {
            public static final Loader INSTANCE = new Loader();

            private Loader()
            {
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager)
            {

            }

            @Override
            public VanillaProxy read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
            {
                List<BlockElement> list = this.getModelElements(deserializationContext, modelContents);
                return new VanillaProxy(list);
            }

            private List<BlockElement> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object) {
                List<BlockElement> list = Lists.newArrayList();
                if (object.has("elements")) {
                    for(JsonElement jsonelement : GsonHelper.getAsJsonArray(object, "elements")) {
                        list.add(deserializationContext.deserialize(jsonelement, BlockElement.class));
                    }
                }

                return list;
            }
        }
    }

    public static class ExpandedBlockModelDeserializer extends BlockModel.Deserializer
    {
        public static final Gson INSTANCE = (new GsonBuilder())
                .registerTypeAdapter(BlockModel.class, new ExpandedBlockModelDeserializer())
                .registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer())
                .registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer())
                .registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer())
                .registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer())
                .registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer())
                .registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer())
                .registerTypeAdapter(Transformation.class, new TransformationHelper.Deserializer())
                .create();

        public BlockModel deserialize(JsonElement element, Type targetType, JsonDeserializationContext deserializationContext) throws JsonParseException {
            BlockModel model = super.deserialize(element, targetType, deserializationContext);
            JsonObject jsonobject = element.getAsJsonObject();
            IModelGeometry<?> geometry = deserializeGeometry(deserializationContext, jsonobject);

            List<BlockElement> elements = model.getElements();
            if (geometry != null) {
                elements.clear();
                model.customData.setCustomGeometry(geometry);
            }

            ModelState modelState = deserializeModelTransforms(deserializationContext, jsonobject);
            if (modelState != null)
            {
                model.customData.setCustomModelState(modelState);
            }

            if (jsonobject.has("visibility"))
            {
                JsonObject visibility = GsonHelper.getAsJsonObject(jsonobject, "visibility");
                for(Map.Entry<String, JsonElement> part : visibility.entrySet())
                {
                    model.customData.visibilityData.setVisibilityState(part.getKey(), part.getValue().getAsBoolean());
                }
            }

            return model;
        }
    }
}
