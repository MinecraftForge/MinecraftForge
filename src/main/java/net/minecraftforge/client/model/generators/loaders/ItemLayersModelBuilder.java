/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.LinkedHashSet;
import java.util.Set;

public class ItemLayersModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
    public static <T extends ModelBuilder<T>> ItemLayersModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper)
    {
        return new ItemLayersModelBuilder<>(parent, existingFileHelper);
    }

    private final Set<Integer> fullbright = new LinkedHashSet<>();

    protected ItemLayersModelBuilder(T parent, ExistingFileHelper existingFileHelper)
    {
        super(new ResourceLocation("forge:item-layers"), parent, existingFileHelper);
    }

    public ItemLayersModelBuilder<T> fullbright(int layer)
    {
        Preconditions.checkArgument(layer >= 0, "layer must be >= 0");
        fullbright.add(layer);
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        json = super.toJson(json);

        JsonArray parts = new JsonArray();
        for(int entry : fullbright)
        {
            parts.add(entry);
        }
        json.add("fullbright_layers", parts);

        return json;
    }
}
