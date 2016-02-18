package net.minecraftforge.client.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
     * @param vanillaGSON ModelBlockDefinition's GSON reader.
     *
     * @return Model definition including variants for all known combinations.
     */
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
                            boolean uvLock = var.getUvLock().or(false);
                            boolean smooth = var.getSmooth().or(true);
                            boolean gui3d = var.getGui3d().or(true);
                            int weight = var.getWeight().or(1);

                            if (var.getModel() != null && var.getSubmodels().size() == 0 && var.getTextures().size() == 0 && var.getCustomData().size() == 0 && var.getState().orNull() instanceof ModelRotation)
                                mcVars.add(new ModelBlockDefinition.Variant(var.getModel(), (ModelRotation)var.getState().get(), uvLock, weight));
                            else
                                mcVars.add(new ForgeVariant(var.getModel(), var.getState().or(TRSRTransformation.identity()), uvLock, smooth, gui3d, weight, var.getTextures(), var.getOnlyPartsVariant(), var.getCustomData()));
                        }
                        variants.add(new ModelBlockDefinition.Variants(entry.getKey(), mcVars));
                    }

                    return new ModelBlockDefinition(variants);

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
        private final IModelState state;
        private final boolean uvLock;
        private final boolean smooth;
        private final boolean gui3d;
        private final ImmutableMap<String, String> textures;
        private final ResourceLocation model;
        private final ImmutableMap<String, String> customData;

        @Deprecated // remove in 1.9
        public SubModel(IModelState state, boolean uvLock, ImmutableMap<String, String> textures, ResourceLocation model, ImmutableMap<String, String> customData)
        {
            this(state, uvLock, true, true, textures, model, customData);
        }

        public SubModel(IModelState state, boolean uvLock, boolean smooth, boolean gui3d, ImmutableMap<String, String> textures, ResourceLocation model, ImmutableMap<String, String> customData)
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
        public ResourceLocation getModelLocation() { return model; }
        public ImmutableMap<String, String> getCustomData() { return customData; }
    }

    private static class ForgeVariant extends ModelBlockDefinition.Variant implements ISmartVariant
    {
        private final ImmutableMap<String, String> textures;
        private final ImmutableMap<String, SubModel> parts;
        private final ImmutableMap<String, String> customData;
        private final boolean smooth;
        private final boolean gui3d;
        private final IModelState state;

        public ForgeVariant(ResourceLocation model, IModelState state, boolean uvLock, boolean smooth, boolean gui3d, int weight, ImmutableMap<String, String> textures, ImmutableMap<String, SubModel> parts, ImmutableMap<String, String> customData)
        {
            super(model == null ? new ResourceLocation("builtin/missing") : model, state instanceof ModelRotation ? (ModelRotation)state : ModelRotation.X0_Y0, uvLock, weight);
            this.textures = textures;
            this.parts = parts;
            this.customData = customData;
            this.state = state;
            this.smooth = smooth;
            this.gui3d = gui3d;
        }

        private IModel runModelHooks(IModel base, boolean smooth, boolean gui3d, ImmutableMap<String, String> textureMap, ImmutableMap<String, String> customData)
        {
            if (!customData.isEmpty() && base instanceof IModelCustomData)
            {
                base = ((IModelCustomData<?>)base).process(customData);
            }

            if (!textureMap.isEmpty() && base instanceof IRetexturableModel)
            {
                base = ((IRetexturableModel<?>)base).retexture(textureMap);
            }

            if (base instanceof IModelSimpleProperties<?>)
            {
                base = ((IModelSimpleProperties<?>) base).smoothLighting(smooth).gui3d(gui3d);
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
                base = runModelHooks(base, smooth, gui3d, textures, customData);

                if (size <= 0)
                    return base;
            }

            // Apply rotation of base model to submodels.
            // If baseRot is non-null, then that rotation will be applied instead of the base model's rotation.
            // This is used to allow replacing base model with a submodel when there is no base model for a variant.
            IModelState baseTr = getState();
            ImmutableMap.Builder<String, Pair<IModel, IModelState>> models = ImmutableMap.builder();
            for (Entry<String, SubModel> entry : parts.entrySet())
            {
                SubModel part = entry.getValue();

                IModel model = null;
                try
                {
                    model = loader.getModel(part.getModelLocation());
                }
                catch (IOException e)
                {
                    FMLLog.warning("Unable to load block sub-model: \'" + part.getModelLocation() /*+ "\' for variant: \'" + parent*/ + "\': " + e.toString());
                    model = loader.getMissingModel(); // Will make it look weird, but that is good.
                }

                IModelState partState = new ModelStateComposition(baseTr, part.getState());
                if (part.isUVLock()) partState = new ModelLoader.UVLock(partState);

                models.put(entry.getKey(), Pair.<IModel, IModelState>of(runModelHooks(model, part.smooth, part.gui3d, part.getTextures(), part.getCustomData()), partState));
            }

            return new MultiModel(getModelLocation(), hasBase ? base : null, baseTr, models.build());
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
