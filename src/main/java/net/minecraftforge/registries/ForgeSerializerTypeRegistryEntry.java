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

package net.minecraftforge.registries;

import com.google.common.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;

import javax.annotation.Nullable;

/**
 * This class acts as a superclass for all classes that would usually extend {@link SerializerType} to make them Forge-Registry compatible.
 * Having this class means {@link net.minecraft.world.level.storage.loot.entries.LootPoolEntryType},
 * {@link net.minecraft.world.level.storage.loot.functions.LootItemFunctionType} and others don't need to implement {@link IForgeRegistryEntry} themselves.
 * @param <V> The type extending this class
 * @param <T> The type to be serialized
 */
public class ForgeSerializerTypeRegistryEntry<V extends IForgeRegistryEntry<V>, T> extends SerializerType<T> implements IForgeRegistryEntry<V>
{

    public ForgeSerializerTypeRegistryEntry(Serializer<? extends T> serializer)
    {
        super(serializer);
    }

    @SuppressWarnings("serial")
    private final TypeToken<V> token = new TypeToken<V>(getClass()){};
    public final IRegistryDelegate<V> delegate = new RegistryDelegate<>((V)this, (Class<V>)token.getRawType());
    private ResourceLocation registryName = null;

    public final V setRegistryName(String name)
    {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        this.registryName = checkRegistryName(name);
        return (V)this;
    }

    //Helper functions
    public final V setRegistryName(ResourceLocation name){ return setRegistryName(name.toString()); }

    public final V setRegistryName(String modID, String name){ return setRegistryName(modID + ":" + name); }

    @Nullable
    public final ResourceLocation getRegistryName()
    {
        if (delegate.name() != null) return delegate.name();
        return registryName != null ? registryName : null;
    }

    public final Class<V> getRegistryType() { return (Class<V>)token.getRawType(); }

    /**
     * This will assert that the registry name is valid and warn about potential registry overrides
     * It is important as it detects cases where modders unintentionally register objects with the "minecraft" namespace, leading to dangerous errors later.
     * @param name The registry name
     * @return A verified "correct" registry name
     */
    ResourceLocation checkRegistryName(String name)
    {
        return GameData.checkPrefix(name, true);
    }

}
