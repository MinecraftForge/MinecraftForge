/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.SeparatePerspectiveModel;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class SeparatePerspectiveModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
    public static <T extends ModelBuilder<T>> SeparatePerspectiveModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper)
    {
        return new SeparatePerspectiveModelBuilder<>(parent, existingFileHelper);
    }

    private T base;
    private final Map<String, T> childModels = new LinkedHashMap<>();

    protected SeparatePerspectiveModelBuilder(T parent, ExistingFileHelper existingFileHelper)
    {
        super(new ResourceLocation("forge:separate-perspective"), parent, existingFileHelper);
    }

    public SeparatePerspectiveModelBuilder<T> base(T modelBuilder)
    {
        Preconditions.checkNotNull(modelBuilder, "modelBuilder must not be null");
        base = modelBuilder;
        return this;
    }

    public SeparatePerspectiveModelBuilder<T> perspective(ItemTransforms.TransformType perspective, T modelBuilder)
    {
        Preconditions.checkNotNull(perspective, "layer must not be null");
        Preconditions.checkArgument(SeparatePerspectiveModel.Loader.PERSPECTIVES.containsValue(perspective), "perspective is not included in SeparatePerspectiveModel. New mc version?");
        Preconditions.checkNotNull(modelBuilder, "modelBuilder must not be null");
        childModels.put(SeparatePerspectiveModel.Loader.PERSPECTIVES.inverse().get(perspective), modelBuilder);
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        json = super.toJson(json);

        if (base != null)
        {
            json.add("base", base.toJson());
        }

        JsonObject parts = new JsonObject();
        for(Map.Entry<String, T> entry : childModels.entrySet())
        {
            parts.add(entry.getKey(), entry.getValue().toJson());
        }
        json.add("perspectives", parts);

        return json;
    }
}
