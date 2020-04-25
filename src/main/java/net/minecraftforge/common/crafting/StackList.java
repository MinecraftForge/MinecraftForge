/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.common.crafting;

import java.util.Collection;
import java.util.Collections;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient.IItemList;

public class StackList implements IItemList
{
    private Collection<ItemStack> items;
    public StackList(Collection<ItemStack> items)
    {
        this.items = Collections.unmodifiableCollection(items);
    }

    @Override
    public Collection<ItemStack> getStacks()
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
