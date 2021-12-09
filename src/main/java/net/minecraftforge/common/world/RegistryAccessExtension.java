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

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

/**
 * Defines an additional world-gen component to load as part of the RegistryAccess registries. The extension
 * consists of a DeferredRegister to which default/builtin entries can be registered, and a Codec providing
 * serialization/deserialization of the component type to and from json (or other data formats). Such
 * components are server-side only.
 * <p>
 * The DeferredRegister held in this RegistryAccessExtension must be registered to the owning mod's
 * event bus to function correctly.
 *
 * @param <T> The type of world-gen component.
 */
public class RegistryAccessExtension<T extends IForgeRegistryEntry<T>> extends ForgeRegistryEntry<RegistryAccessExtension<?>>
{
    private final ResourceKey<Registry<T>> registryKey;
    private final Codec<T> elementCodec;
    private final DeferredRegister<T> register;
    private final Supplier<IForgeRegistry<T>> registry;

    /**
     * Creates a new RegistryAccessExtension with the given registry name, element type, and element Codec.
     * This constructor automatically makes the backing Registry for the DeferredRegister.
     *
     * @param name  The registry name of the extension. The namespace of this ResourceLocation will be used as
     *              the modid of the internally held DeferredRegister, and the path of the ResourceLocation will
     *              define the filepath the data in datapacks.
     * @param type  The registry entry type (must extend IForgeRegistryEntry).
     * @param codec The codec for serializing/deserializing the entry to/from data formats (namely json).
     */
    public RegistryAccessExtension(ResourceLocation name, Class<T> type, Codec<T> codec)
    {
        setRegistryName(name);
        this.registryKey = ResourceKey.createRegistryKey(name);
        this.elementCodec = codec;
        this.register = DeferredRegister.create(type, name.getNamespace());
        this.registry = register.makeRegistry(name.getPath(), () -> new RegistryBuilder<T>().disableSaving().disableSync());
    }

    /**
     * Creates a new RegistryAccessExtension from the given IForgeRegistry and element Codec.
     * The registry name of the RegistryAccessExtension is derived from the name of the provided IForgeRegistry.
     *
     * @param registry The registry that elements will be registered to.
     * @param codec    The codec for serializing/deserializing the entry to/from data formats (namely json).
     */
    public RegistryAccessExtension(IForgeRegistry<T> registry, Codec<T> codec)
    {
        setRegistryName(registry.getRegistryName());
        this.registryKey = ResourceKey.createRegistryKey(registry.getRegistryName());
        this.elementCodec = codec;
        this.register = DeferredRegister.create(registry, registry.getRegistryName().getNamespace());
        this.registry = Suppliers.memoize(() -> registry);
    }

    public Codec<T> getCodec()
    {
        return elementCodec;
    }

    public ResourceKey<? extends Registry<T>> getRegistryKey()
    {
        return registryKey;
    }

    public DeferredRegister<T> getRegister()
    {
        return register;
    }

    public Supplier<IForgeRegistry<T>> getRegistry()
    {
        return registry;
    }
}
