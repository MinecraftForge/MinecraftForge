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

package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
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
