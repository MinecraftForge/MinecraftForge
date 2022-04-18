/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import java.util.Collection;
import java.util.Collections;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient.Value;

public class MultiItemValue implements Value
{
    private Collection<ItemStack> items;
    public MultiItemValue(Collection<ItemStack> items)
    {
        this.items = Collections.unmodifiableCollection(items);
    }

    @Override
    public Collection<ItemStack> getItems()
    {
        return items;
    }

    @Override
    public JsonObject serialize()
    {
        if (items.size() == 1)
            return toJson(items.iterator().next());

        JsonObject ret = new JsonObject();
        JsonArray array = new JsonArray();
        items.forEach(stack -> array.add(toJson(stack)));
        ret.add("items", array);
        return ret;
    }

    private JsonObject toJson(ItemStack stack)
    {
        JsonObject ret = new JsonObject();
        ret.addProperty("item", stack.getItem().getRegistryName().toString());
        if (stack.getCount() != 1)
            ret.addProperty("count", stack.getCount());
        if (stack.getTag() != null)
            ret.addProperty("nbt", stack.getTag().toString()); //TODO: Better serialization?
        return ret;
    }

}
