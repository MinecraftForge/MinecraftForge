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

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;

public class JsonContext
{
    private String modId;
    private Map<String, Ingredient> constants = Maps.newHashMap();

    public JsonContext(String modId)
    {
        this.modId = modId;
    }

    public String getModId()
    {
        return this.modId;
    }

    public String appendModId(String data)
    {
        if (data.indexOf(':') == -1)
            return modId + ":" + data;
        return data;
    }

    @Nullable
    public Ingredient getConstant(String name)
    {
        return constants.get(name);
    }

    void loadConstants(JsonObject... jsons)
    {
        for (JsonObject json : jsons)
        {
            if (!CraftingHelper.processConditions(json, "conditions", this))
                continue;
            if (!json.has("ingredient"))
                throw new JsonSyntaxException("Constant entry must contain 'ingredient' value");
            constants.put(JsonUtils.getString(json, "name"), CraftingHelper.getIngredient(json.get("ingredient"), this));
        }

    }
}
