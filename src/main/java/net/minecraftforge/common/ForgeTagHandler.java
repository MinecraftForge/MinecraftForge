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
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.StaticTagHelper;
import net.minecraft.tags.StaticTags;
import net.minecraft.tags.Tag.Named;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.registries.*;
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
    public static <T extends IForgeRegistryEntry<T>> Named<T> bind(IForgeRegistry<T> registry, ResourceLocation name)
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
    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> bindOptional(IForgeRegistry<T> registry, ResourceLocation name)
    {
        return bindOptional(registry, name, null);
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
    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> bindOptional(IForgeRegistry<T> registry, ResourceLocation name, @Nullable Set<Supplier<T>> defaults)
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
    public static <T extends IForgeRegistryEntry<T>> Named<T> bind(ResourceLocation registryName, ResourceLocation name)
    {
        if (tagTypesSet)
        {
            IForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(registryName);
            if (registry == null) throw new IllegalArgumentException("Could not find registry named: " + registryName);
            return bind(registry, name);
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
    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> bindOptional(ResourceLocation registryName, ResourceLocation name)
    {
        return bindOptional(registryName, name, null);
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
    public static <T extends IForgeRegistryEntry<T>> IOptionalNamedTag<T> bindOptional(ResourceLocation registryName, ResourceLocation name, @Nullable Set<Supplier<T>> defaults)
    {
        if (tagTypesSet)
        {
            IForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(registryName);
            if (registry == null) throw new IllegalArgumentException("Could not find registry named: " + registryName);
            return bindOptional(registry, name, defaults);
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

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unchecked"})
    public static <T, A extends IForgeRegistryEntry<A>> Optional<? extends Registry<T>> getWrapperRegistry(ResourceKey<? extends Registry<T>> key, Optional<? extends Registry<T>> vanillaReg) {
        if (vanillaReg.isPresent())
            return vanillaReg;
        try {
            ResourceKey<? extends Registry<A>> cast = (ResourceKey<? extends Registry<A>>) key;
            if (RegistryManager.ACTIVE.getRegistry(cast).getTagFolder() == null)
                return Optional.empty();
            return Optional.of((Registry<T>) GameData.getAnyWrapper(cast));
        } catch (ClassCastException e) { //if there is an exception that means that the tag was neither from vanilla nor from forge so ignore it
            return Optional.empty();
        }
    }
}