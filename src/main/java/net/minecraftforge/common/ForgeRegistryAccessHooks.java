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

package net.minecraftforge.common;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryResourceAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.world.RegistryAccessExtension;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains hooks for RegistryAccess creation, injection and loading.
 */
public class ForgeRegistryAccessHooks
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker INJECT = MarkerManager.getMarker("INJECT");
    private static final Marker LOAD = MarkerManager.getMarker("LOAD");

    /**
     * Forge use only. Called during construction of new RegistryAccess.RegistryHolder instances to
     * add a new, empty, MappedRegistry instance for each RegistryAccessExtension. These registries
     * may be populated at a later stage with the extensions' builtin defaults.
     */
    public static Map<? extends ResourceKey<? extends Registry<?>>, ? extends MappedRegistry<?>> extendHolderRegistries(Map<? extends ResourceKey<? extends Registry<?>>, ? extends MappedRegistry<?>> registries)
    {
        if (ForgeRegistries.REGISTRY_ACCESS_EXTENSIONS.isEmpty())
        {
            return registries;
        }

        var copy = new HashMap<ResourceKey<? extends Registry<?>>, MappedRegistry<?>>(registries);
        for (var entry : ForgeRegistries.REGISTRY_ACCESS_EXTENSIONS)
        {
            var registry = new MappedRegistry<>(entry.getRegistryKey(), Lifecycle.stable());
            copy.put(entry.getRegistryKey(), registry);
        }

        return copy;
    }

    /**
     * Forge use only. Called by RegistryAccess#builtin to populate the RegistryHolder's registries with
     * the default (ie builtin) registry entries.
     * Mirrors what {@link RegistryAccess#builtin} does for vanilla elements.
     */
    public static void injectBuiltins(RegistryAccess.RegistryHolder vanillaHolder, RegistryResourceAccess.InMemoryStorage storage)
    {
        if (ForgeRegistries.REGISTRY_ACCESS_EXTENSIONS.isEmpty())
        {
            return;
        }

        // The 'builtin' RegistryHolder is used during serialization of registry entries into the InMemoryStorage.
        // This RegistryHolder must be populated with the 'builtin' elements (ie those registered to the
        // BuiltinRegistries for vanilla elements and those registered to the RegistryAccessExtension's deferred
        // register for mod elements). The vanilla BUILTIN RegistryHolder may be populated before mod extensions
        // have been registered, so we create a new RegistryHolder and populate it with vanilla's registry
        // entries and then add in those from the RegistryAccessExtensions registries.
        var extendedHolder = createExtendedRegistryHolder(vanillaHolder);

        LOGGER.info(INJECT, "Injecting builtin elements for RegistryAccessExtensions");

        // Loop over each extension and add their default registered elements to the storage.
        for (var extension : ForgeRegistries.REGISTRY_ACCESS_EXTENSIONS)
        {
            injectBuiltinElements(extension, extendedHolder, storage);
        }
    }

    /**
     * Forge use only. Called by RegistryAccess#load to load RegistryAccessExtension elements that have
     * been provided in data form.
     * Mirrors what {@link RegistryAccess#load} does for vanilla elements.
     */
    public static void loadExtensions(RegistryAccess registries, RegistryReadOps<?> readOps)
    {
        if (ForgeRegistries.REGISTRY_ACCESS_EXTENSIONS.isEmpty())
        {
            return;
        }

        LOGGER.info(LOAD, "Loading data for RegistryAccessExtension elements");

        // Mirrors the behaviour or RegistryAccess#load - loop over each registry type and
        // decode into the RegistryAccess.
        for (var extension : ForgeRegistries.REGISTRY_ACCESS_EXTENSIONS)
        {
            loadElements(extension, registries, readOps);
        }
    }

    /**
     * Helper for loading the RegistryAccessExtension from data into the RegistryAccess instance.
     * Mirrors {@link RegistryAccess#readRegistry}.
     */
    private static <T extends IForgeRegistryEntry<T>> void loadElements(RegistryAccessExtension<T> extension, RegistryAccess registries, RegistryReadOps<?> readOps)
    {
        var registry = (MappedRegistry<T>) registries.ownedRegistryOrThrow(extension.getRegistryKey());

        var result = readOps.decodeElements(registry, registry.key(), extension.getCodec());
        result.error().ifPresent((p_175499_) -> {
            throw new JsonParseException("Error loading registry data: " + p_175499_.message());
        });

        printRegistryContents(LOAD, registry);
    }

    /**
     * Helper for adding the default registry entries of a RegistryAccessExtension into the InMemoryStorage.
     * Mirrors {@link RegistryAccess#addBuiltinElements}.
     */
    private static <T extends IForgeRegistryEntry<T>> void injectBuiltinElements(RegistryAccessExtension<T> extension, RegistryAccess.RegistryHolder builtin, RegistryResourceAccess.InMemoryStorage storage)
    {
        var registry = builtin.ownedRegistryOrThrow(extension.getRegistryKey());

        for (var entry : registry.entrySet())
        {
            var value = entry.getValue();
            int id = registry.getId(value);
            var lifecycle = registry.lifecycle(value);
            storage.add(builtin, entry.getKey(), extension.getCodec(), id, value, lifecycle);
        }

        printRegistryContents(INJECT, registry);
    }

    /**
     * Creates a new RegistryAccess.RegistryHolder who's registries are populated with both vanilla's
     * builtin registry elements and those registered to each RegistryAccessExtension. The returned
     * RegistryHolder is used for cross-component reference lookups when inlining things such as
     * Biome feature lists.
     */
    private static RegistryAccess.RegistryHolder createExtendedRegistryHolder(RegistryAccess.RegistryHolder vanillaBuiltin)
    {
        var extendedBuiltin = new RegistryAccess.RegistryHolder();
        // Copy the vanilla builtin registries so that extension components can cross-reference vanilla components.
        for (var data : RegistryAccess.knownRegistries())
        {
            copy(data.key(), vanillaBuiltin, extendedBuiltin);
        }

        // Copy the registered elements of each RegistryAccessExtension. These form the default/builtin values of the component.
        for (var extension : ForgeRegistries.REGISTRY_ACCESS_EXTENSIONS)
        {
            copy(extension, extendedBuiltin);
        }

        return extendedBuiltin;
    }

    /*
     * Helper for copying the entries of a Registry from one RegistryAccess to another.
     */
    private static <T> void copy(ResourceKey<? extends Registry<T>> key, RegistryAccess source, RegistryAccess destination)
    {
        var input = source.ownedRegistryOrThrow(key);
        var output = destination.ownedRegistryOrThrow(key);

        for (var entry : input.entrySet())
        {
            output.register(entry.getKey(), entry.getValue(), input.lifecycle(entry.getValue()));
        }
    }

    /*
     * Helper for copying the entries of a RegistryAccessExtension's Registry to a RegistryAccess.
     */
    private static <T extends IForgeRegistryEntry<T>> void copy(RegistryAccessExtension<T> source, RegistryAccess destination)
    {
        var input = RegistryManager.ACTIVE.getRegistry(source.getRegistryKey());

        // The mod may not have configured a registry for the extension; in which case only json
        // definitions of the component will be loaded.
        if (input == null) return;

        var output = destination.ownedRegistryOrThrow(source.getRegistryKey());

        for (var entry : input.getEntries())
        {
            output.register(entry.getKey(), entry.getValue(), source.getDefaultElementLifecycle());
        }
    }

    private static void printRegistryContents(Marker marker, Registry<?> registry)
    {
        LOGGER.debug(marker, "Registry: {}", registry.key().location());

        for (var key : registry.keySet())
        {
            LOGGER.debug(marker, "- {}", key);
        }
    }
}
