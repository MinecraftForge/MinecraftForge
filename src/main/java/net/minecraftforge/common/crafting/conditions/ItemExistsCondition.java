/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;

import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemExistsCondition implements ICondition
{
    private static final ResourceLocation NAME = new ResourceLocation("forge", "item_exists");
    private final ResourceLocation item;

    public ItemExistsCondition(String location)
    {
        this(new ResourceLocation(location));
    }

    public ItemExistsCondition(String namespace, String path)
    {
        this(new ResourceLocation(namespace, path));
    }

    public ItemExistsCondition(ResourceLocation item)
    {
        this.item = item;
    }

    @Override
    public ResourceLocation getID()
    {
        return NAME;
    }

    @Override
    public boolean test()
    {
        return ForgeRegistries.ITEMS.containsKey(item);
    }

    @Override
    public String toString()
    {
        return "item_exists(\"" + item + "\")";
    }

    public static class Serializer implements IConditionSerializer<ItemExistsCondition>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, ItemExistsCondition value)
        {
            json.addProperty("item", value.item.toString());
        }

        @Override
        public ItemExistsCondition read(JsonObject json)
        {
            return new ItemExistsCondition(new ResourceLocation(GsonHelper.getAsString(json, "item")));
        }

        @Override
        public ResourceLocation getID()
        {
            return ItemExistsCondition.NAME;
        }
    }
}
