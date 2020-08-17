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

package net.minecraftforge.common.loot;

import com.google.gson.JsonObject;

import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Abstract base deserializer for LootModifiers. Takes care of Forge registry things.<br/>
 * Modders should extend this class to return their modifier and implement the abstract
 * <code>read</code> method to deserialize from json.
 * @param <T> the Type to deserialize
 */
public abstract class GlobalLootModifierSerializer<T extends IGlobalLootModifier> implements IForgeRegistryEntry<GlobalLootModifierSerializer<?>> {
    private ResourceLocation registryName = null;
    
    public final GlobalLootModifierSerializer<T> setRegistryName(String name) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        this.registryName = GameData.checkPrefix(name, true);
        return this;
    }
    
    //Helpers
    @Override
    public final GlobalLootModifierSerializer<T> setRegistryName(ResourceLocation name){ return setRegistryName(name.toString()); }

    public final GlobalLootModifierSerializer<T> setRegistryName(String modID, String name){ return setRegistryName(modID + ":" + name); }

    @Override
    public final ResourceLocation getRegistryName() {
        return registryName;
    }
    
    /**
     * Most mods will likely not need more than<br/>
     * <code>return new MyModifier(conditionsIn)</code><br/>
     * but any additional properties that are needed will need to be deserialized here.
     * @param name The resource location (if needed)
     * @param json The full json object (including ILootConditions)
     * @param conditionsIn An already deserialized list of ILootConditions
     */
    public abstract T read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition);

    /**
     * Write the serializer to json.
     *
     * Most serializers won't have to do anything else than {@link #makeConditions}
     * Which simply creates the JsonObject from an array of ILootConditions.
     */
    public abstract JsonObject write(T instance);

    /**
     * Helper to create the json object from the conditions.
     * Add any extra properties to the returned json.
     */
    public JsonObject makeConditions(ILootCondition[] conditions) {
        JsonObject json = new JsonObject();
        json.add("conditions", ConditionArraySerializer.field_235679_a_.func_235681_a_(conditions));
        return json;
    }

    /**
     * Used by Forge's registry system.
     */
    @Override
    public final Class<GlobalLootModifierSerializer<?>> getRegistryType() {
        return castClass(GlobalLootModifierSerializer.class);
    }
    
    @SuppressWarnings("unchecked") // Need this wrapper, because generics
    private static <G> Class<G> castClass(Class<?> cls)
    {
        return (Class<G>)cls;
    }
}
