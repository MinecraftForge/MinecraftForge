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

package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CustomLoaderBuilder<T extends ModelBuilder<T>>
{
    protected final ResourceLocation loaderId;
    protected final T parent;
    protected final ExistingFileHelper existingFileHelper;
    protected final Map<String, Boolean> visibility = new LinkedHashMap<>();

    protected CustomLoaderBuilder(ResourceLocation loaderId, T parent, ExistingFileHelper existingFileHelper)
    {
        this.loaderId = loaderId;
        this.parent = parent;
        this.existingFileHelper = existingFileHelper;
    }

    public CustomLoaderBuilder<T> visibility(String partName, boolean show)
    {
        Preconditions.checkNotNull(partName, "partName must not be null");
        this.visibility.put(partName, show);
        return this;
    }

    public T end()
    {
        return parent;
    }

    public JsonObject toJson(JsonObject json)
    {
        json.addProperty("loader", loaderId.toString());

        if (visibility.size() > 0)
        {
            JsonObject visibilityObj = new JsonObject();

            for(Map.Entry<String, Boolean> entry : visibility.entrySet())
            {
                visibilityObj.addProperty(entry.getKey(), entry.getValue());
            }

            json.add("visibility", visibilityObj);
        }

        return json;
    }
}
