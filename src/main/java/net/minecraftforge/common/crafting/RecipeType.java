/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

public class RecipeType<T extends IRecipe>
{
    private static final Map<ResourceLocation, RecipeType<?>> TYPES = new HashMap<>();

    protected final ResourceLocation id;
    protected final Class<? extends T> baseClass;
    
    private RecipeType(ResourceLocation id, Class<? extends T> baseClass) {
        this.id = id;
        this.baseClass = baseClass;
    }

    public ResourceLocation getId()
    {
        return id;
    }

    /**
     * @return The class that all recipes of this type must extend.
     */
    public Class<? extends T> getBaseClass()
    {
        return baseClass;
    }

    /**
     * @param id The name of the recipe type.
     * @param baseClass The base class of all recipes using this type.
     * @return A recipe type, with the provided ID and base class, or null, if the entry exists and the base class is incorrect.
     */
    @SuppressWarnings("unchecked")
    @Nullable
	public static <T extends IRecipe> RecipeType<T> get(ResourceLocation id, Class<? extends T> baseClass)
    {
        RecipeType<?> type = TYPES.get(id);
        if(type == null)
        {
            type = new RecipeType<>(id, baseClass);
            TYPES.put(id, type);
        }
        else if(type.getBaseClass() != baseClass)
        {
            LogManager.getLogger().error("Attempted to access RecipeType {} with the wrong base class. Provided {}, expected {}.");
            return null;
        }
        return (RecipeType<T>) type;
    }
}
