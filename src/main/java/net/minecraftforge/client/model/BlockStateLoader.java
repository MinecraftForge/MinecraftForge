/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import net.minecraftforge.fml.common.FMLLog;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.annotation.Nullable;

public class BlockStateLoader
{
    private static final Gson GSON = (new GsonBuilder())
            .registerTypeAdapter(ForgeBlockStateV1.class,         ForgeBlockStateV1.Deserializer.INSTANCE)
            .registerTypeAdapter(ForgeBlockStateV1.Variant.class, ForgeBlockStateV1.Variant.Deserializer.INSTANCE)
            .registerTypeAdapter(TRSRTransformation.class, ForgeBlockStateV1.TRSRDeserializer.INSTANCE)
            .create();
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
    public static ModelBlockDefinition load(Reader reader, ResourceLocation location, final Gson vanillaGSON)
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

                            if (var.isVanillaCompatible())
                                mcVars.add(new Variant(var.getModel(), (ModelRotation)var.getState().orElse(ModelRotation.X0_Y0), uvLock, weight));
                            else
                                mcVars.add(new ForgeVariant(location, var.getModel(), var.getState().orElse(TRSRTransformation.identity()), uvLock, var.getSmooth(), var.getGui3d(), weight, var.getTextures(), var.getOnlyPartsVariant(), var.getCustomData()));
                        }
                        variants.put(entry.getKey(), new VariantList(mcVars));
                    }

                    return new ModelBlockDefinition(variants, null);

                default: //Unknown version.. try loading it as normal.
                    return vanillaGSON.fromJson(reader, ModelBlockDefinition.class);
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

    //This is here specifically so that we do not have a hard reference to ForgeBlockStateV1.Variant in ForgeVariant
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

    private static class ForgeVariant extends Variant implements ISmartVariant
    {
        private final ResourceLocation blockstateLocation;
        private final ImmutableMap<String, String> textures;
        private final ImmutableMap<String, SubModel> parts;
        private final ImmutableMap<String, String> customData;
        private final Optional<Boolean> smooth;
        private final Optional<Boolean> gui3d;
        private final IModelState state;

        ForgeVariant(ResourceLocation blockstateLocation, @Nullable ResourceLocation model, IModelState state, boolean uvLock, Optional<Boolean> smooth, Optional<Boolean> gui3d, int weight, ImmutableMap<String, String> textures, ImmutableMap<String, SubModel> parts, ImmutableMap<String, String> customData)
        {
            super(model == null ? new ResourceLocation("builtin/missing") : model, state instanceof ModelRotation ? (ModelRotation)state : ModelRotation.X0_Y0, uvLock, weight);
            this.blockstateLocation = blockstateLocation;
            this.textures = textures;
            this.parts = parts;
            this.customData = customData;
            this.state = state;
            this.smooth = smooth;
            this.gui3d = gui3d;
        }

        private IModel runModelHooks(IModel base, Optional<Boolean> smooth, Optional<Boolean> gui3d, boolean uvlock, ImmutableMap<String, String> textureMap, ImmutableMap<String, String> customData)
        {
            base = base.process(customData);
            base = base.retexture(textureMap);
            base = smooth.map(base::smoothLighting).orElse(base);
            base = gui3d.map(base::gui3d).orElse(base);
            base = base.uvlock(uvlock);
            return base;
        }

        /**
         * Used to replace the base model with a re-textured model containing sub-models.
         */
        @Override
        public IModel process(IModel base)
        {
            int size = parts.size();
            // FIXME: should missing base be handled this way?
            boolean hasBase = base != ModelLoaderRegistry.getMissingModel();

            if (hasBase)
            {
                base = runModelHooks(base, smooth, gui3d, this.isUvLock(), textures, customData);

                if (size <= 0)
                    return base;
            }

            ImmutableMap.Builder<String, Pair<IModel, IModelState>> models = ImmutableMap.builder();
            for (Entry<String, SubModel> entry : parts.entrySet())
            {
                SubModel part = entry.getValue();

                final ResourceLocation modelLocation = part.getModelLocation();
                final IModel model;
                if (modelLocation == null)
                {
                    FMLLog.log.error("model not found for variant {} for blockstate {}", entry.getKey(), blockstateLocation);
                    model = ModelLoaderRegistry.getMissingModel(blockstateLocation, new Throwable());
                }
                else
                {
                    model = ModelLoaderRegistry.getModelOrLogError(modelLocation, "Unable to load block sub-model: \'" + modelLocation);
                }

                models.put(entry.getKey(), Pair.of(runModelHooks(model, Optional.of(part.smooth), Optional.of(part.gui3d), part.uvLock, part.getTextures(), part.getCustomData()), part.getState()));
            }

            return new MultiModel(getModelLocation(), hasBase ? base : null, models.build());
        }

        @Override
        public IModelState getState()
        {
            return state;
        }

        @Override
        public String toString()
        {
            StringBuilder buf = new StringBuilder();
            buf.append("TexturedVariant:");
            for (Entry<String, String> e: this.textures.entrySet())
                buf.append(" ").append(e.getKey()).append(" = ").append(e.getValue());
            return buf.toString();
        }
    }
}
