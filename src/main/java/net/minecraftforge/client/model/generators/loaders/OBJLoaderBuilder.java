/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.PackType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class OBJLoaderBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
    public static <T extends ModelBuilder<T>> OBJLoaderBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper)
    {
        return new OBJLoaderBuilder<>(parent, existingFileHelper);
    }

    private ResourceLocation modelLocation;
    private Boolean detectCullableFaces;
    private Boolean diffuseLighting;
    private Boolean flipV;
    private Boolean ambientToFullbright;
    private ResourceLocation materialLibraryOverrideLocation;

    protected OBJLoaderBuilder(T parent, ExistingFileHelper existingFileHelper)
    {
        super(new ResourceLocation("forge:obj"), parent, existingFileHelper);
    }

    public OBJLoaderBuilder<T> modelLocation(ResourceLocation modelLocation)
    {
        Preconditions.checkNotNull(modelLocation, "modelLocation must not be null");
        Preconditions.checkArgument(existingFileHelper.exists(modelLocation, PackType.CLIENT_RESOURCES),
                "OBJ Model %s does not exist in any known resource pack", modelLocation);
        this.modelLocation = modelLocation;
        return this;
    }

    public OBJLoaderBuilder<T> detectCullableFaces(boolean detectCullableFaces)
    {
        this.detectCullableFaces = detectCullableFaces;
        return this;
    }

    public OBJLoaderBuilder<T> diffuseLighting(boolean diffuseLighting)
    {
        this.diffuseLighting = diffuseLighting;
        return this;
    }

    public OBJLoaderBuilder<T> flipV(boolean flipV)
    {
        this.flipV = flipV;
        return this;
    }

    public OBJLoaderBuilder<T> ambientToFullbright(boolean ambientToFullbright)
    {
        this.ambientToFullbright = ambientToFullbright;
        return this;
    }

    public OBJLoaderBuilder<T> overrideMaterialLibrary(ResourceLocation materialLibraryOverrideLocation)
    {
        Preconditions.checkNotNull(materialLibraryOverrideLocation, "materialLibraryOverrideLocation must not be null");
        Preconditions.checkArgument(existingFileHelper.exists(materialLibraryOverrideLocation, PackType.CLIENT_RESOURCES),
                "OBJ Model %s does not exist in any known resource pack", materialLibraryOverrideLocation);
        this.materialLibraryOverrideLocation = materialLibraryOverrideLocation;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        json = super.toJson(json);

        Preconditions.checkNotNull(modelLocation, "modelLocation must not be null");

        json.addProperty("model", modelLocation.toString());

        if (detectCullableFaces != null)
            json.addProperty("detectCullableFaces", detectCullableFaces);

        if (diffuseLighting != null)
            json.addProperty("diffuseLighting", diffuseLighting);

        if (flipV != null)
            json.addProperty("flip-v", flipV);

        if (ambientToFullbright != null)
            json.addProperty("ambientToFullbright", ambientToFullbright);

        if (materialLibraryOverrideLocation != null)
            json.addProperty("materialLibraryOverride", materialLibraryOverrideLocation.toString());

        return json;
    }
}
