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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.StaticTagHelper;
import net.minecraft.tags.StaticTags;
import net.minecraft.tags.Tag.Named;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagContainer;
import net.minecraft.tags.TagLoader;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeTagHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static Set<ResourceLocation> customTagTypeNames = Collections.emptySet();
    private static boolean tagTypesSet = false;

    @Nullable
    private static <T extends IForgeRegistryEntry<T>> StaticTagHelper<T> getTagHelper(IForgeRegistry<T> registry)
    {
        return (StaticTagHelper<T>) StaticTags.get(registry.getRegistryName());
    }

    private static void validateRegistrySupportsTags(IForgeRegistry<?> registry)
    {
        //Note: We also check against getTagHelper in case someone decides to use the helpers for tag creation for types supported by vanilla
        if (getTagHelper(registry) == null && (!(registry instanceof ForgeRegistry) || ((ForgeRegistry<?>) registry).getTagFolder() == null))
        {
            throw new IllegalArgumentException("Registry " + registry.getRegistryName() + " does not support tag types.");
        }
    }

    /**
     * Helper method that creates a named tag for a forge registry, erroring if the registry doesn't support custom tag types. If the custom tag types
     * have not been set yet, this method falls back and creates the tag reference delaying adding it to the tag registry to allow for statically
     * initializing and referencing the tag.
     * @param registry Registry the tag is for
     * @param name     Name of the tag
     * @param <T>      Type of the registry
     * @return A named tag
     */
    public static <T extends IForgeRegistryEntry<T>> Named<T> makeWrapperTag(IForgeRegistry<T> registry, ResourceLocation name)
    {
        validateRegistrySupportsTags(registry);
        if (tagTypesSet)
        {
            StaticTagHelper<T> tagRegistry = getTagHelper(registry);
            if (tagRegistry == null) throw new IllegalArgumentException("Registry " + registry.getRegistryName() + " does not support tag types.");
            return tagRegistry.bind(name.toString());
        }
        return StaticTagHelper.createDelayedTag(registry.getRegistryName(), name);
    }

    /**
     * Helper method that creates an optional tag for a forge registry, erroring if the registry doesn't support custom tag types. If the custom tag types
     * have not been set yet, this method falls back and creates the tag reference delaying adding it to the tag registry to allow for statically
     * initializing and referencing the tag.
     * @param registry Registry the tag is for
     * @param name     Name of the tag
     * @param <T>      Type of the registry
     * @return An optional tag
     */
    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> createOptionalTag(IForgeRegistry<T> registry, ResourceLocation name)
    {
        return createOptionalTag(registry, name, null);
    }

    /**
     * Helper method that creates an optional tag for a forge registry, erroring if the registry doesn't support custom tag types. If the custom tag types
     * have not been set yet, this method falls back and creates the tag reference delaying adding it to the tag registry to allow for statically
     * initializing and referencing the tag.
     * @param registry Registry the tag is for
     * @param name     Name of the tag
     * @param defaults Default values for the optional tag
     * @param <T>      Type of the registry
     * @return An optional tag
     */
    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> createOptionalTag(IForgeRegistry<T> registry, ResourceLocation name, @Nullable Set<Supplier<T>> defaults)
    {
        validateRegistrySupportsTags(registry);
        if (tagTypesSet)
        {
            StaticTagHelper<T> tagRegistry = getTagHelper(registry);
            if (tagRegistry == null) throw new IllegalArgumentException("Registry " + registry.getRegistryName() + " does not support tag types.");
            return tagRegistry.createOptional(name, defaults);
        }
        return StaticTagHelper.createDelayedOptional(registry.getRegistryName(), name, defaults);
    }

    /**
     * Helper method for creating named tags for custom forge registries. If the custom tag types have not been set yet, this method falls back and creates
     * the tag reference delaying adding it to the tag registry to allow for statically initializing and referencing the tag.
     * @param registryName Name of the registry the tag is for
     * @param name         Name of the tag
     * @param <T>          Type of the registry
     * @return A named tag
     * @implNote This method only errors instantly if tag types have already been set, otherwise the error is delayed until after registries finish initializing
     * and we can validate if the custom registry really does support custom tags.
     */
    public static <T extends IForgeRegistryEntry<T>> Named<T> makeWrapperTag(ResourceLocation registryName, ResourceLocation name)
    {
        if (tagTypesSet)
        {
            IForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(registryName);
            if (registry == null) throw new IllegalArgumentException("Could not find registry named: " + registryName);
            return makeWrapperTag(registry, name);
        }
        return StaticTagHelper.createDelayedTag(registryName, name);
    }

    /**
     * Helper method for creating optional tags for custom forge registries. If the custom tag types have not been set yet, this method falls back and creates
     * the tag reference delaying adding it to the tag registry to allow for statically initializing and referencing the tag.
     * @param registryName Name of the registry the tag is for
     * @param name         Name of the tag
     * @param <T>          Type of the registry
     * @return An optional tag
     * @implNote This method only errors instantly if tag types have already been set, otherwise the error is delayed until after registries finish initializing
     * and we can validate if the custom registry really does support custom tags.
     */
    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> createOptionalTag(ResourceLocation registryName, ResourceLocation name)
    {
        return createOptionalTag(registryName, name, null);
    }

    /**
     * Helper method for creating optional tags for custom forge registries. If the custom tag types have not been set yet, this method falls back and creates
     * the tag reference delaying adding it to the tag registry to allow for statically initializing and referencing the tag.
     * @param registryName Name of the registry the tag is for
     * @param name         Name of the tag
     * @param defaults     Default values for the optional tag
     * @param <T>          Type of the registry
     * @return An optional tag
     * @implNote This method only errors instantly if tag types have already been set, otherwise the error is delayed until after registries finish initializing
     * and we can validate if the custom registry really does support custom tags.
     */
    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> createOptionalTag(ResourceLocation registryName, ResourceLocation name, @Nullable Set<Supplier<T>> defaults)
    {
        if (tagTypesSet)
        {
            IForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(registryName);
            if (registry == null) throw new IllegalArgumentException("Could not find registry named: " + registryName);
            return createOptionalTag(registry, name, defaults);
        }
        return StaticTagHelper.createDelayedOptional(registryName, name, defaults);
    }

    /**
     * Gets the all the registry names of registries that support custom tag types.
     */
    public static Set<ResourceLocation> getCustomTagTypeNames()
    {
        return customTagTypeNames;
    }

    /**
     * Sets the set containing the resource locations representing the registry name of each forge registry that supports custom tag types.
     *
     * @apiNote Internal: Calling this manually <strong>WILL</strong> cause a crash to occur as it can only be called once, and is done so by
     * forge after all registries have been initialized.
     */
    public static void setCustomTagTypes(Set<ResourceLocation> customTagTypesNames)
    {
        if (tagTypesSet) throw new RuntimeException("Custom tag types have already been set, this method should only be called by forge, and after registries are initialized");
        tagTypesSet = true;
        customTagTypeNames = ImmutableSet.copyOf(customTagTypesNames);
        //Add the static references for custom tag types to the proper tag registries
        // Note: If this ends up being a hotspot due to lots of mods having lots of statically registered tags
        // that get loaded/registered before the new registry event is fired/processed everywhere then this
        // potentially should end up being moved into an async processor.
        StaticTagHelper.performDelayedAdd();
    }

    /**
     * Creates a map for custom tag type to tag reader
     *
     * @apiNote Internal: For use by TagManager
     */
    public static Map<ResourceLocation, TagLoader<?>> createCustomTagTypeReaders()
    {
        LOGGER.debug("Gathering custom tag collection reader from types.");
        ImmutableMap.Builder<ResourceLocation, TagLoader<?>> builder = ImmutableMap.builder();
        for (ResourceLocation registryName : customTagTypeNames)
        {
            ForgeRegistry<?> registry = RegistryManager.ACTIVE.getRegistry(registryName);
            if (registry != null && registry.getTagFolder() != null)
            {
                builder.put(registryName, new TagLoader<>(rl -> Optional.ofNullable(registry.getValue(rl)), "tags/" + registry.getTagFolder()));
            }
        }
        return builder.build();
    }

    /**
     * Wraps the forge registry if it supports tags into the internal registry for use in serialization
     *
     * @apiNote Internal: For use in TagContainer
     */
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unchecked"})
    public static <T> Optional<? extends Registry<T>> getWrapperRegistry(ResourceKey<? extends Registry<T>> key, Optional<? extends Registry<T>> vanillaReg)
    {
        if (vanillaReg.isPresent())
            return vanillaReg;

        ForgeRegistry<?> reg = RegistryManager.ACTIVE.getRegistry(key.location());
        if (reg == null || reg.getTagFolder() == null)
            return Optional.empty();

        if (reg.getDefaultKey() == null)
            return Optional.of((Registry<T>) GameData.getWrapper(reg.getRegistryKey(), Lifecycle.stable()));
        return Optional.of((Registry<T>) GameData.getWrapper(reg.getRegistryKey(), Lifecycle.stable(), "ignored"));
    }

    /**
     * Helper to reinject missing optional tags.
     *
     * @apiNote Internal
     */
    public static TagContainer reinjectOptionalTags(TagContainer tagCollectionSupplier)
    {
        TagContainer.Builder builder = new TagContainer.Builder();
        //noinspection unchecked,rawtypes
        StaticTags.visitHelpers(h -> builder.add(h.getKey(), h.reinjectOptionalTags(tagCollectionSupplier.getOrEmpty((ResourceKey) h.getKey()))));
        return builder.build();
    }
}