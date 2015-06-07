package net.minecraftforge.client.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.vecmath.Matrix4f;

import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("deprecation")
public class BlockStateLoader
{
    private static final Gson GSON = (new GsonBuilder())
            .registerTypeAdapter(ForgeBlockStateV1.class,         ForgeBlockStateV1.Deserializer.INSTANCE)
            .registerTypeAdapter(ForgeBlockStateV1.Variant.class, ForgeBlockStateV1.Variant.Deserializer.INSTANCE)
            .create();
    /**
     * Loads a BlockStates json file.
     * Will attempt to parse it as a Forge Enhanced version if possible.
     * Will fall back to standard loading if marker is not present.
     *
     * Note: This method is NOT thread safe
     *
     * @param reader json read
     * @param vanillaGSON ModelBlockDefinition's GSON reader.
     *
     * @return Model definition including variants for all known combinations.
     */
    @SuppressWarnings("rawtypes")
    public static ModelBlockDefinition load(Reader reader, final Gson vanillaGSON)
    {
        try
        {
            byte[] data = IOUtils.toByteArray(reader);
            reader = new InputStreamReader(new ByteArrayInputStream(data), Charsets.UTF_8);

            Marker marker = GSON.fromJson(new String(data), Marker.class);  // Read "forge_marker" to determine what to load.

            switch (marker.forge_marker)
            {
                case 1: // Version 1
                    ForgeBlockStateV1 v1 = GSON.fromJson(reader, ForgeBlockStateV1.class);
                    List<ModelBlockDefinition.Variants> variants = Lists.newArrayList();

                    for (Entry<String, Collection<ForgeBlockStateV1.Variant>> entry : v1.variants.asMap().entrySet())
                    {   // Convert Version1 variants into vanilla variants for the ModelBlockDefinition.
                        List<ModelBlockDefinition.Variant> mcVars = Lists.newArrayList();
                        for (ForgeBlockStateV1.Variant var : entry.getValue())
                        {
                            ModelRotation rot = var.getRotation().or(ModelRotation.X0_Y0);
                            boolean uvLock = var.getUvLock().or(false);
                            int weight = var.getWeight().or(1);

                            if (var.getModel() != null && var.getSubmodels().size() == 0 && var.getTextures().size() == 0)
                                mcVars.add(new ModelBlockDefinition.Variant(var.getModel(), rot, uvLock, weight));
                            else
                                mcVars.add(new ForgeVariant(var.getModel(), rot, uvLock, weight, var.getTextures(), var.getOnlyPartsVariant(), var.getCustomData()));
                        }
                        variants.add(new ModelBlockDefinition.Variants(entry.getKey(), mcVars));
                    }

                    return new ModelBlockDefinition((Collection)variants); //Damn lists being collections!

                default: //Unknown version.. try loading it as normal.
                    return vanillaGSON.fromJson(reader, ModelBlockDefinition.class);
            }
        }
        catch (IOException e)
        {
            Throwables.propagate(e);
        }
        return null;
    }

    public static class Marker
    {
        public int forge_marker = -1;
    }

    //This is here specifically so that we do not have a hard reference to ForgeBlockStateV1.Variant in ForgeVariant
    public static class SubModel
    {
        private final ModelRotation rotation;
        private final boolean uvLock;
        private final ImmutableMap<String, String> textures;
        private final ResourceLocation model;
        private final ImmutableMap<String, String> customData;

        public SubModel(ModelRotation rotation, boolean uvLock, ImmutableMap<String, String> textures, ResourceLocation model, ImmutableMap<String, String> customData)
        {
            this.rotation = rotation;
            this.uvLock = uvLock;
            this.textures = textures;
            this.model = model;
            this.customData = customData;
        }

        public ModelRotation getRotation() { return rotation; }
        public boolean isUVLock() { return uvLock; }
        public ImmutableMap<String, String> getTextures() { return textures; }
        public ResourceLocation getModelLocation() { return model; }
        public ImmutableMap<String, String> getCustomData() { return customData; }
    }

    private static class ForgeVariant extends ModelBlockDefinition.Variant implements ISmartVariant
    {
        private final ImmutableMap<String, String> textures;
        private final ImmutableMap<String, SubModel> parts;
        private final ImmutableMap<String, String> customData;

        public ForgeVariant(ResourceLocation model, ModelRotation rotation, boolean uvLock, int weight, ImmutableMap<String, String> textures, ImmutableMap<String, SubModel> parts, ImmutableMap<String, String> customData)
        {
            super(model == null ? new ResourceLocation("builtin/missing") : model, rotation, uvLock, weight);
            this.textures = textures;
            this.parts = parts;
            this.customData = customData;
        }

        protected IModel runModelHooks(IModel base, ImmutableMap<String, String> textureMap, ImmutableMap<String, String> customData)
        {
            if (!customData.isEmpty())
            {
                if (base instanceof IModelCustomData)
                    base = ((IModelCustomData)base).process(customData);
                else
                    throw new RuntimeException("Attempted to add custom data to a model that doesn't need it: " + base);
            }

            if (!textureMap.isEmpty())
            {
                if (base instanceof IRetexturableModel)
                    base = ((IRetexturableModel)base).retexture(textureMap);
                else
                    throw new RuntimeException("Attempted to retexture a non-retexturable model: " + base);
            }

            return base;
        }

        /**
         * Used to replace the base model with a retextured model containing submodels.
         */
        @Override
        public IModel process(IModel base, ModelLoader loader)
        {
            int size = parts.size();
            boolean hasBase = base != loader.getMissingModel();

            if (hasBase)
            {
                base = runModelHooks(base, textures, customData);

                if (size <= 0)
                    return base;
            }

            // Apply rotation of base model to submodels.
            // If baseRot is non-null, then that rotation will be applied instead of the base model's rotation.
            // This is used to allow replacing base model with a submodel when there is no base model for a variant.
            ModelRotation baseRot = getRotation();
            ImmutableMap.Builder<String, Pair<IModel, IModelState>> models = ImmutableMap.builder();
            for (Entry<String, SubModel> entry : parts.entrySet())
            {
                SubModel part = entry.getValue();

                Matrix4f matrix = new Matrix4f(baseRot.getMatrix());
                matrix.mul(part.getRotation().getMatrix());
                IModelState partState = new TRSRTransformation(matrix);
                if (part.isUVLock()) partState = new ModelLoader.UVLock(partState);

                models.put(entry.getKey(), Pair.of(runModelHooks(loader.getModel(part.getModelLocation()), part.getTextures(), part.getCustomData()), partState));
            }

            return new MultiModel(hasBase ? base : null, baseRot, models.build());
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
