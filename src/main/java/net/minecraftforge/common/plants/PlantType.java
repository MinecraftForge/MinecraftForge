/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.common.plants;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

public class PlantType
{
    private static final Map<ResourceLocation, PlantType> TYPES = new HashMap<>();

    protected final ResourceLocation id;

    private PlantType(ResourceLocation id)
    {
        this.id = id;
    }

    public ResourceLocation getId()
    {
        return id;
    }

    /**
     * @param id
     *            The name of the plant type. Mods creating new plant types should use their domains. Forge uses "minecraft" as the domain.
     * @return A plant type.
     */
    public static PlantType get(ResourceLocation id)
    {
        return TYPES.computeIfAbsent(id, reloc -> new PlantType(reloc));
    }
}
