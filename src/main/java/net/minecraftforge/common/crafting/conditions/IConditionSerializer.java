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

import net.minecraft.resources.ResourceLocation;

public interface IConditionSerializer<T extends ICondition>
{
    void write(JsonObject json, T value);

    T read(JsonObject json);

    ResourceLocation getID();

    default JsonObject getJson(T value)
    {
        JsonObject json = new JsonObject();
        this.write(json, value);
        json.addProperty("type", value.getID().toString());
        return json;
    }
}
