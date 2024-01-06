/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompositeModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
    public static <T extends ModelBuilder<T>> CompositeModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper)
    {
        return new CompositeModelBuilder<>(parent, existingFileHelper);
    }

    private final Map<String, T> childModels = new LinkedHashMap<>();

    protected CompositeModelBuilder(T parent, ExistingFileHelper existingFileHelper)
    {
        super(new ResourceLocation("forge:composite"), parent, existingFileHelper);
    }

    public CompositeModelBuilder<T> submodel(String name, T modelBuilder)
    {
        Preconditions.checkNotNull(name, "name must not be null");
        Preconditions.checkNotNull(modelBuilder, "modelBuilder must not be null");
        childModels.put(name, modelBuilder);
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        json = super.toJson(json);

        JsonObject parts = new JsonObject();
        for(Map.Entry<String, T> entry : childModels.entrySet())
        {
            parts.add(entry.getKey(), entry.getValue().toJson());
        }
        json.add("parts", parts);

        return json;
    }
}
