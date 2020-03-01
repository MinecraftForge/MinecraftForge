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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import net.minecraft.client.renderer.model.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.annotation.Nullable;

@Deprecated // Use the new model loading system and data generators instead.
public class BlockStateLoader
{
    private static final Gson GSON = (new GsonBuilder())
            .registerTypeAdapter(ForgeBlockStateV1.class,         ForgeBlockStateV1.Deserializer.INSTANCE)
            .registerTypeAdapter(ForgeBlockStateV1.Variant.class, ForgeBlockStateV1.Variant.Deserializer.INSTANCE)
            .registerTypeAdapter(TRSRTransformation.class, ForgeBlockStateV1.TRSRDeserializer.INSTANCE)
            .create();
    
    private static final Logger LOGGER = LogManager.getLogger();

    private static long internalGeneratedModelId = 1;

    /**
     * Loads a BlockStates json file.
     * Will attempt to parse it as a Forge Enhanced version if possible.
     * Will fall back to standard loading if marker is not present.
     *
     * Note: This method is NOT thread safe
     *
     * @param reader json read
     * @param location blockstate location
     * @param vanillaGSON ModelBlockDefinition's GSON reader.
     *
     * @return Model definition including variants for all known combinations.
     */
    public static BlockModelDefinition load(Reader reader, ResourceLocation location, final Gson vanillaGSON, ModelBakery bakery, BiConsumer<ResourceLocation, IUnbakedModel> modelConsumer)
    {
        try
        {
            byte[] data = IOUtils.toByteArray(reader, StandardCharsets.UTF_8);
            reader = new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8);

            Marker marker = GSON.fromJson(new String(data, StandardCharsets.UTF_8), Marker.class);  // Read "forge_marker" to determine what to load.

            switch (marker.forge_marker)
            {
                case 1: // Version 1
                    ForgeBlockStateV1 v1 = GSON.fromJson(reader, ForgeBlockStateV1.class);
                    Map<String, VariantList> variants = Maps.newLinkedHashMap();

                    for (Entry<String, Collection<ForgeBlockStateV1.Variant>> entry : v1.variants.asMap().entrySet())
                    {   // Convert Version1 variants into vanilla variants for the ModelBlockDefinition.
                        List<Variant> mcVars = Lists.newArrayList();
                        for (ForgeBlockStateV1.Variant var : entry.getValue())
                        {
                            boolean uvLock = var.getUvLock().orElse(false);
                            int weight = var.getWeight().orElse(1);

                            ResourceLocation modelLocation = var.getModel();
                            if (!var.isVanillaCompatible() && bakery != null)
                            {
                                modelLocation = new ResourceLocation(
                                        "internal",
                                        String.format("%d/%s/%s/%s", internalGeneratedModelId++, location.getNamespace(), location.getPath(),
                                                entry.getKey().replace("=","_").replace(",","_"))
                                );

                                IUnbakedModel model = ForgeVariantHelper.prepareInjectedModel(modelLocation, var.getModel(), var.getSmooth(), var.getGui3d(), var.getTextures(), var.getOnlyPartsVariant(), var.getCustomData());

                                modelConsumer.accept(modelLocation, model);
                            }

                            mcVars.add(new ForgeVariant(modelLocation, var.getState().orElse(ModelRotation.X0_Y0), uvLock, weight));
                        }
                        variants.put(entry.getKey(), new VariantList(mcVars));
                    }

                    return new BlockModelDefinition(variants, null);

                default: //Unknown version.. try loading it as normal.
                    return vanillaGSON.fromJson(reader, BlockModelDefinition.class);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static class Marker
    {
        public int forge_marker = -1;
    }

    //This is here specifically so that we do not have a hard reference to ForgeBlockStateV1.Variant in this code
    public static class SubModel
    {
        private final IModelState state;
        private final boolean uvLock;
        private final boolean smooth;
        private final boolean gui3d;
        private final ImmutableMap<String, String> textures;
        @Nullable
        private final ResourceLocation model;
        private final ImmutableMap<String, String> customData;

        public SubModel(IModelState state, boolean uvLock, boolean smooth, boolean gui3d, ImmutableMap<String, String> textures, @Nullable ResourceLocation model, ImmutableMap<String, String> customData)
        {
            this.state = state;
            this.uvLock = uvLock;
            this.smooth = smooth;
            this.gui3d = gui3d;
            this.textures = textures;
            this.model = model;
            this.customData = customData;
        }

        public IModelState getState() { return state; }
        public boolean isUVLock() { return uvLock; }
        public ImmutableMap<String, String> getTextures() { return textures; }
        @Nullable
        public ResourceLocation getModelLocation() { return model; }
        public ImmutableMap<String, String> getCustomData() { return customData; }
    }

    private static class ForgeVariant extends Variant
    {
        private final IModelState state;

        ForgeVariant(ResourceLocation model, IModelState state, boolean uvLock, int weight)
        {
            super(model, state instanceof ModelRotation ? (ModelRotation)state : ModelRotation.X0_Y0, uvLock, weight);
            this.state = state;
        }

        @Override
        public IModelState getState()
        {
            return state;
        }

        @Override
        public String toString() {
            return "Forge" + super.toString();
        }
    }

    private static class ForgeVariantHelper
    {
        public static IUnbakedModel prepareInjectedModel(ResourceLocation blockstateLocation, @Nullable ResourceLocation modelLocation, Optional<Boolean> smooth, Optional<Boolean> gui3d, ImmutableMap<String, String> textures, ImmutableMap<String, SubModel> parts, ImmutableMap<String, String> customData)
        {
            int size = parts.size();

            IUnbakedModel base = null;
            if (modelLocation != null)
            {
                try
                {
                    base = ModelLoaderRegistry.getModel(modelLocation);

                    if (base != null)
                    {
                        base = base.process(customData);
                        base = base.retexture(textures);
                        base = smooth.map(base::smoothLighting).orElse(base);
                        base = gui3d.map(base::gui3d).orElse(base);

                        if (size <= 0)
                            return base;
                    }
                }
                catch (Exception e)
                {
                    LOGGER.error("Error processing base model for forge blockstates pseudo-model: '" + blockstateLocation, e);
                }
            }

            ImmutableMap.Builder<String, Pair<IUnbakedModel, IModelState>> models = ImmutableMap.builder();
            for (Entry<String, SubModel> entry : parts.entrySet())
            {
                SubModel part = entry.getValue();

                final ResourceLocation location = part.getModelLocation();
                final IUnbakedModel model;
                if (location == null)
                {
                    LOGGER.error("model not found for variant {} for blockstate {}", entry.getKey(), blockstateLocation);
                    model = ModelLoaderRegistry.getMissingModel(blockstateLocation, new Throwable());
                }
                else
                {
                    model = ModelLoaderRegistry.getModelOrLogError(location, "Unable to load block sub-model '" + entry.getKey() + "': '" + location + "'");
                }

                IUnbakedModel base1 = model;
                base1 = base1.process(part.getCustomData());
                base1 = base1.retexture(part.getTextures());
                base1 = Optional.of(part.smooth).map(base1::smoothLighting).orElse(base1);
                base1 = Optional.of(part.gui3d).map(base1::gui3d).orElse(base1);
                models.put(entry.getKey(), Pair.of(base1, part.getState()));
            }

            return new MultiModel(modelLocation, base, models.build());
        }
    }
}
