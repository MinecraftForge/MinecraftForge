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

package net.minecraftforge.common.world;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public abstract class StructurePoolModifierSerializer<T extends IStructurePoolModifier> implements IForgeRegistryEntry<StructurePoolModifierSerializer<?>>
{

    private ResourceLocation registryName = null;

    @SuppressWarnings("unchecked") // Need this wrapper, because generics
    private static <G> Class<G> castClass(Class<?> cls)
    {
        return (Class<G>) cls;
    }

    /**
     * This is where the json structure is deserialized.
     * All properties relevant to the modifier should be read from the json data here.
     *
     * @param location The resource location (if needed)
     * @param json     The full json object
     */
    public abstract T read(ResourceLocation location, JsonObject json);

    /**
     * Write the serializer to json.
     */
    public abstract JsonObject write(T instance);

    public final StructurePoolModifierSerializer<T> setRegistryName(String modID, String name)
    {
        return setRegistryName(modID + ":" + name);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName()
    {
        return this.registryName;
    }

    public StructurePoolModifierSerializer<T> setRegistryName(String name)
    {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        this.registryName = GameData.checkPrefix(name, true);
        return this;
    }

    @Override
    public StructurePoolModifierSerializer<T> setRegistryName(ResourceLocation name)
    {
        return setRegistryName(name.toString());
    }

    @Override
    public Class<StructurePoolModifierSerializer<?>> getRegistryType()
    {
        return castClass(StructurePoolModifierSerializer.class);
    }

}
