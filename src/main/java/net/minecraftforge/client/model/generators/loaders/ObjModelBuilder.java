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

public class ObjModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
    public static <T extends ModelBuilder<T>> ObjModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper)
    {
        return new ObjModelBuilder<>(parent, existingFileHelper);
    }

    private ResourceLocation modelLocation;
    private Boolean automaticCulling;
    private Boolean shadeQuads;
    private Boolean flipV;
    private Boolean emissiveAmbient;
    private ResourceLocation mtlOverride;

    protected ObjModelBuilder(T parent, ExistingFileHelper existingFileHelper)
    {
        super(new ResourceLocation("forge:obj"), parent, existingFileHelper);
    }

    public ObjModelBuilder<T> modelLocation(ResourceLocation modelLocation)
    {
        Preconditions.checkNotNull(modelLocation, "modelLocation must not be null");
        Preconditions.checkArgument(existingFileHelper.exists(modelLocation, PackType.CLIENT_RESOURCES),
                "OBJ Model %s does not exist in any known resource pack", modelLocation);
        this.modelLocation = modelLocation;
        return this;
    }

    public ObjModelBuilder<T> automaticCulling(boolean automaticCulling)
    {
        this.automaticCulling = automaticCulling;
        return this;
    }

    public ObjModelBuilder<T> shadeQuads(boolean shadeQuads)
    {
        this.shadeQuads = shadeQuads;
        return this;
    }

    public ObjModelBuilder<T> flipV(boolean flipV)
    {
        this.flipV = flipV;
        return this;
    }

    public ObjModelBuilder<T> emissiveAmbient(boolean ambientEmissive)
    {
        this.emissiveAmbient = ambientEmissive;
        return this;
    }

    public ObjModelBuilder<T> overrideMaterialLibrary(ResourceLocation mtlOverride)
    {
        Preconditions.checkNotNull(mtlOverride, "mtlOverride must not be null");
        Preconditions.checkArgument(existingFileHelper.exists(mtlOverride, PackType.CLIENT_RESOURCES),
                "OBJ Model %s does not exist in any known resource pack", mtlOverride);
        this.mtlOverride = mtlOverride;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        json = super.toJson(json);

        Preconditions.checkNotNull(modelLocation, "modelLocation must not be null");

        json.addProperty("model", modelLocation.toString());

        if (automaticCulling != null)
            json.addProperty("automatic_culling", automaticCulling);

        if (shadeQuads != null)
            json.addProperty("shade_quads", shadeQuads);

        if (flipV != null)
            json.addProperty("flip_v", flipV);

        if (emissiveAmbient != null)
            json.addProperty("emissive_ambient", emissiveAmbient);

        if (mtlOverride != null)
            json.addProperty("mtl_override", mtlOverride.toString());

        return json;
    }
}
