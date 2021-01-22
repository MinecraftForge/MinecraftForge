/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;

import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
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
            return new ItemExistsCondition(new ResourceLocation(JSONUtils.getString(json, "item")));
        }

        @Override
        public ResourceLocation getID()
        {
            return ItemExistsCondition.NAME;
        }
    }
}
