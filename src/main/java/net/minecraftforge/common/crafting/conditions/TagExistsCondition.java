/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

public class TagExistsCondition implements ICondition
{
    private static final ResourceLocation NAME = new ResourceLocation("forge", "tag_exists");
    private final TagKey<Item> tag;

    public TagExistsCondition(String location)
    {
        this(new ResourceLocation(location));
    }

    public TagExistsCondition(String namespace, String path)
    {
        this(new ResourceLocation(namespace, path));
    }

    public TagExistsCondition(ResourceLocation tag)
    {
        this.tag = TagKey.create(Registry.ITEM_REGISTRY, tag);
    }

    @Override
    public ResourceLocation getID()
    {
        return NAME;
    }

    @Override
    public boolean test(ICondition.IContext context)
    {
        return !context.getTag(tag).getValues().isEmpty();
    }

    @Override
    public boolean test()
    {
        return test(IContext.EMPTY);
    }

    @Override
    public String toString()
    {
        return "tag_exists(\"" + tag.location() + "\")";
    }

    public static class Serializer implements IConditionSerializer<TagExistsCondition>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, TagExistsCondition value)
        {
            json.addProperty("tag", value.tag.location().toString());
        }

        @Override
        public TagExistsCondition read(JsonObject json)
        {
            return new TagExistsCondition(new ResourceLocation(GsonHelper.getAsString(json, "tag")));
        }

        @Override
        public ResourceLocation getID()
        {
            return TagExistsCondition.NAME;
        }
    }
}
